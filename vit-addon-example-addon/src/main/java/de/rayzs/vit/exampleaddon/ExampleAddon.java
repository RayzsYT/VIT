package de.rayzs.vit.exampleaddon;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.configuration.Configuration;
import de.rayzs.vit.api.event.EventAdapter;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.event.events.system.state.StateChangeEvent;

import java.io.IOException;

public class ExampleAddon extends Addon {

    // Empty constructor. No need to do anything with it.
    // Leave it as it is and do not change the default
    // parameters!
    public ExampleAddon(VITAPI api, AddonDescription description) {
        super(api, description);
    }

    @Override
    public void onEnable() {

        // Load default config
        loadConfig();

        // Read prefix from config
        final String prefix = getConfig().getString("prefix");
        System.out.println(prefix + "Example Addon is enabled");


        // Set a new value to a config.
        config.get().put("lol", "skrr");
        // Apply and save your config changes.
        saveConfig();



        // Another config file
        Configuration configuration = new Configuration(getAddonDir(), "settings.json");
        configuration.get().put("test", true);
        configuration.get().put("name", "Some name");

        try {
            configuration.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Register an event
        api.getEventManager().register(this, new EventAdapter<>(StateChangeEvent.class) {

            @Override
            public void call(StateChangeEvent event) {
                System.out.println("Detected state change! " + event);
            }

        });
    }

    @Override
    public void onDisable() {
        System.out.println("Example Addon is disabled");
    }
}
