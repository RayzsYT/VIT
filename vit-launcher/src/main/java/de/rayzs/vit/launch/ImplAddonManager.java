package de.rayzs.vit.launch;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.addon.AddonManager;
import de.rayzs.vit.api.file.FileDir;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ImplAddonManager implements AddonManager {


    // Addon id, Addon Info
    private final HashMap<String, AddonInfo> addons = new HashMap<>();
    private final VITAPI api;

    public ImplAddonManager(final VITAPI api) {
        this.api = api;
    }


    @Override
    public void loadAddons() {
        for (final File file : FileDir.ADDONS.getFolder().listFiles()) {
            loadAddonByFile(file.getName());
        }

    }

    @Override
    public void unloadAddons() {
        final Set<Addon> addons = Collections.unmodifiableSet(getLoadedAddons());

        for (Addon addon : addons) {
            unloadAddon(addon);
        }
    }

    @Override
    public void unloadAddon(final Addon addon) {
        final String id = addon.getDescription().getId();
        final AddonInfo addonInfo = addons.get(id);

        addons.remove(id);

        addonInfo.disable();
    }

    @Override
    public Addon loadAddonByFile(final String fileName) {
        final File file = FileDir.ADDONS.getFile(fileName);

        if (!file.exists() || !file.getName().endsWith(".jar")) {
            return null;
        }

        try {
            final AddonInfo addonInfo = new AddonInfo(file);

            final String id = addonInfo.getAddon().getDescription().getId();
            final String main = addonInfo.getAddon().getDescription().getMain();

            if (addons.containsKey(id)) {
                System.err.println("Cannot load addon. Another addon with that id already exists! (" + id + ")");
                return null;
            }


            for (final AddonInfo value : addons.values()) {
                if (main.equals(value.getAddon().getDescription().getMain())) {
                    System.err.println("Cannot load addon. Another addon with that main class already exists! (" + main + ")");
                    return null;
                }
            }

            addons.put(addonInfo.getAddon().getDescription().getId(), addonInfo);
            addonInfo.enable();

            return addonInfo.getAddon();

        } catch (Exception exception) {
            System.err.println("Failed to load addon " + fileName + "!");
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public Addon getAddonByName(final String fileName) {
        final AddonInfo addonInfo = addons.get(fileName);

        if (addonInfo != null) {
            return addonInfo.getAddon();
        }

        return null;
    }

    @Override
    public Set<Addon> getLoadedAddons() {
        return addons.values().stream().map(AddonInfo::getAddon).collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled(Addon addon) {
        return addon.isEnabled();
    }

    @Override
    public boolean isDisabled(Addon addon) {
        return !addon.isEnabled();
    }


    private class AddonInfo {

        private final String fileName;

        private final JarFile jarFile;
        private final ClassLoader classLoader;

        private final Class<?> clazz;

        private final Addon addon;

        public AddonInfo(final File file) throws Exception {
            this.fileName = file.getName();
            this.jarFile = new JarFile(file);


            final AddonDescription description = readAndSetAddonDescription();


            if (description == null) {
                throw new RuntimeException("Failed to load addon description!");
            }


            this.classLoader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() }, getClass().getClassLoader());
            this.clazz = this.classLoader.loadClass(description.getMain());

            this.addon = (Addon) this.clazz
                    .getConstructor(VITAPI.class, AddonDescription.class)
                    .newInstance(api, description);
        }

        private void enable() {
            System.out.println("Loaded addon " + fileName + "!");
            addon.onEnable();
        }

        private void disable() {
            if (!addon.isEnabled()) {
                return;
            }

            try {
                final Field field = Addon.class.getDeclaredField("enabled");

                field.setAccessible(true);
                field.set(addon, false);
                field.setAccessible(false);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            api.getEventManager().unregisterAll(addon);

            addon.onDisable();
            System.err.println("Unloaded addon " + fileName + "!");
        }

        private Addon getAddon() {
            return addon;
        }


        private AddonDescription readAndSetAddonDescription() {
            try {
                final JarEntry entry = jarFile.getJarEntry("addon.json");

                if (entry == null) {
                    return null;
                }

                final InputStream stream = jarFile.getInputStream(entry);
                final String result;

                try (final InputStream inputStream = stream) {
                    result = new String(inputStream.readAllBytes());
                }

                return new AddonDescription(new JSONObject(result));

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return null;
        }
    }
}