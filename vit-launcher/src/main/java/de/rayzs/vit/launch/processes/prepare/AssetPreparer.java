package de.rayzs.vit.launch.processes.prepare;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.download.DownloadElement;
import de.rayzs.vit.api.download.DownloadProcess;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.objects.items.*;
import de.rayzs.vit.api.gui.DownloadGUI;
import de.rayzs.vit.api.gui.OptionGUI;
import de.rayzs.vit.api.gui.UninteractableGUI;
import de.rayzs.vit.api.image.DisplayImage;
import de.rayzs.vit.api.image.SystemImages;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import de.rayzs.vit.api.utils.FileUtils;
import de.rayzs.vit.api.utils.StringUtils;
import de.rayzs.vit.bootstrap.OutputLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class AssetPreparer {

    // Format for the images at valorant-api:
    // maps,     id,     displayicon (small) / splash (normal)
    // agent,    id,     displayicon (small)
    private final String IMAGE_URL          = "https://media.valorant-api.com/%s/%s/%s.png";

    private final String IMAGE_TARGET_MAPS  = "maps";
    private final String IMAGE_TARGET_AGENTS = "agents";

    private final String IMAGE_SIZE_BIG     = "splash";
    private final String IMAGE_SIZE_SMALL   = "displayicon";


    // These map images do not have a mini version of the image.
    // Aka no mini map.
    private final String[] SMALL_MAPS_IMAGE_BLACKLIST = new String[] {
            // Tutorial? Or the Range? Not sure
            "/Game/Maps/NPEV2/NPEV2",

            // The range
            "/Game/Maps/Poveglia/Range",
            "/Game/Maps/PovegliaV2/RangeV2",

            // Duel game mode
            "/Game/Maps/Duel/Duel_1/Skirmish_A",
            "/Game/Maps/Duel/Duel_2/Skirmish_B",
            "/Game/Maps/Duel/Duel_3/Skirmish_C",
    };



    private final VITAPI api;

    public AssetPreparer(final VITAPI api) {
        this.api = api;

        System.out.println("Fetching and loading assets...");


        final UninteractableGUI loadingGUI = UninteractableGUI.create(
                "Loading...", "Loading all assets."
        );

        loadingGUI.relocateToLastLocation(300, 300);
        loadingGUI.setAlwaysOnTop(true);


        // Fetch and load all necessary assets.
        final HttpClient client = Request.createClient();


        // Loading all weapons...
        loadingGUI.updateText("Fetching all weapons...");
        System.out.println("Fetching all weapons...");
        loadWeapons(client);


        // Loading all seasons...
        loadingGUI.updateText("Fetching all seasons...");
        System.out.println("Fetching all seasons...");
        loadSeasons(client);


        // Loading all agents...
        loadingGUI.updateText("Fetching all agents...");
        System.out.println("Fetching all agents...");
        loadAgents(client);


        // Loading all maps...
        loadingGUI.updateText("Fetching all maps...");
        System.out.println("Fetching all maps...");
        loadMaps(client);


        // Loading all tiers...
        loadingGUI.updateText("Fetching all tiers...");
        System.out.println("Fetching all tiers...");
        loadTiers(client);


        // Not required anymore, therefore closing the client.
        client.close();


        // Close uninterpretable gui.
        loadingGUI.dispose();



        // Collects all the files which need to be downloaded.

        final Map<FileDir, HashSet<DownloadElement>> images = new HashMap<>();

        boolean requiresToDownload = false;
        for (final FileDir dir : FileDir.values()) {
            for (final DisplayImage image : api.getImageProvider().getImages(dir)) {
                if (image.doesExist()) {
                    continue;
                }

                requiresToDownload = true;
                images.computeIfAbsent(dir, k -> new HashSet<>())
                        .add(image.getDownloadElement());
            }
        }


        // Only shows the download-gui when there's
        // actually something to download.
        if (requiresToDownload) {

            // Some funny VALORANT pickup lines during installation.
            final String[] downloadLines = FileUtils.readResourceInput("data/installation.txt").split("\n");
            final int maxDownloadLines = downloadLines.length;


            // Indicators if VIT was already installed or not. Depending on that,
            // the popup messages will change.
            final File installedFile = FileDir.ROOT.getFile(".installed");
            final boolean installedBefore = installedFile.exists();


            if (!installedBefore) {
                System.out.println("Waiting for confirmation on if VIT should be installed or not...");

                final OptionGUI optionGUI = OptionGUI.create(
                        "Confirmation required!",
                        "Yes", "No",
                        "You are about to install VIT. Would you like to proceed?"
                );

                // When denied, ignore action and close program.
                if (optionGUI.getResponse() == -1) {
                    OutputLogger.shutdown(); // Shutdowns the output logger

                    // Telling the console that the system is going to shut down, since
                    // the installation was denied. Needs to be printed after disabling
                    // the custom OutputLogger, since it disables the logger once the
                    // next console message is sent.
                    System.out.println("Cancelled installation setup!");

                    // Deleting every VIT file.
                    FileUtils.delete(FileDir.ROOT.getFolder());
                    System.exit(0);

                    return;
                }


                System.out.println("Started installation process...");
            } else {
                System.out.println("Downloading missing assets...");
            }


            // Prepare download gui.
            final DownloadGUI downloadGUI = DownloadGUI.create(
                    "Downloading...",
                    "Here are a few pickup lines while downloading everything."
            );


            for (final FileDir dir : FileDir.values()) {

                // Creates a DownloadProcess for the directory and all
                // its assets that still need to be downloaded.
                final DownloadProcess downloadProcess = new DownloadProcess(dir,
                        images.getOrDefault(dir, HashSet.newHashSet(0)
                        ).toArray(new DownloadElement[0])
                );

                // If it's already completed aka empty, it will
                // just skip that DownloadProcess.
                if (downloadProcess.isCompleted()) {
                    continue;
                }


                final AtomicLong lastUpdatedText = new AtomicLong(System.currentTimeMillis());
                final Random random = new Random();

                // Downloads all the missing assets.
                downloadProcess.start(process -> {

                    // Changing the download text after every 8s
                    if (System.currentTimeMillis() - lastUpdatedText.get() > 8000) {
                        lastUpdatedText.set(System.currentTimeMillis());

                        final String downloadText = downloadLines[random.nextInt(maxDownloadLines)];
                        downloadGUI.updateText(
                                downloadText
                        );
                    }

                    downloadGUI.getProgressBar().setValue(Math.round(process.getPercent()));
                });

                // Update all files once downloaded
                api.getImageProvider().getImages(dir).forEach(DisplayImage::updateImage);
            }

            // Close download gui.
            downloadGUI.dispose();


            System.out.println("Finished installation!");


            // Extract the start script.
            FileUtils.exportResourceFile(null, "scripts/start.bat", FileDir.SCRIPTS);


            if (!installedBefore) {


                try {
                    // Creating file which tells VIT that it has been installed before.
                    installedFile.createNewFile();


                    // Create copy of executed jar file into VIT root folder.
                    final File jarFile = new File(this.getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
                    );

                    final File target = FileDir.ROOT.getFile("latest.jar");
                    Files.copy(jarFile.toPath(), target.toPath());

                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }


                System.out.println("Waiting for confirmation on if VIT should create a Desktop shortcut or not...");


                // Create default shortcuts.
                createShortcut(
                        "VIT",
                        "start.bat",
                        System.getenv("APPDATA") + "/Microsoft/Windows/Start Menu/Programs"
                );


                // Create shortcut on Desktop?
                final OptionGUI createShortcutGUI = OptionGUI.create(
                        "Create Desktop shortcut?",
                        "yes", "no",
                        "Would you like to create a shortcut on your Desktop?"
                );


                if (createShortcutGUI.getResponse() == 1) {
                    createShortcut(
                            "VIT",
                            "start.bat",
                            System.getProperty("user.home") + "\\Desktop"
                    );

                    System.out.println("Created Desktop shortcut!");
                } else System.out.println("Denied Desktop shortcut!");


                // Close the shortcut-option gui
                createShortcutGUI.dispose();


                System.out.println("Waiting for confirmation on if VIT should run right away or not...");


                // Create shortcut on Desktop?
                final OptionGUI startNowGUI = OptionGUI.create(
                        "Start VIT now?",
                        "LET'S GO!", "Not now.",
                        "Would you like to start VIT now?"
                );

                if (startNowGUI.getResponse() == -1) {

                    System.out.println("Chose to not run VIT right away!");

                    System.exit(0);
                    return;
                }

                System.out.println("Accepted to run VIT right away after!");

            } else {

                System.out.println("Skipped installation process, since everything is already installed.");

            }

        }



        System.out.println("Finished loading assets!");
    }


    /**
     * Sends a request to the valorant-api
     * and fetches the result and returns
     * it as a JsonObject.
     *
     * @param client Client.
     * @param name Name of the request information.
     * @return JsonObject.
     */
    private JSONObject fetch(final HttpClient client, final String name) {

        // Creating request
        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.API,
                name
        );

        // Send the request.
        final Optional<String> body = request.sendAndGet(client);
        if (body.isEmpty()) {
            // Well, as it already says it, it will throw and exception in case it failed.
            throw new NullPointerException("No body found! URL connection seems to have failed.");
        }

        // Return body as JsonObject
        return new JSONObject(body.get());
    }


    /**
     * Fetches and loads all agents.
     *
     * @param client HttpClient.
     */
    private void loadAgents(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "agents");
        final JSONArray agents = jsonObj.getJSONArray("data");

        for (final Object agentObj : agents) {
            final JSONObject gun = (JSONObject) agentObj;

            final String id = gun.getString("uuid");
            final String name = gun.getString("displayName");

            final Agent agent = Agent.getAgentByName(name);

            if (agent == null) {
                throw new NullPointerException("Agent " + name + " not found!");
            }

            agent.updateAgentId(id);
            api.getImageProvider().getAgents().putName(id, name);

            api.getImageProvider().getAgents().putImage(
                    id,
                    IMAGE_URL.formatted(IMAGE_TARGET_AGENTS, id, IMAGE_SIZE_SMALL)
            );
        }
    }

    /**
     * Fetches and loads all seasons.
     *
     * @param client HttpClient.
     */
    private void loadSeasons(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "seasons");
        final JSONArray seasons = jsonObj.getJSONArray("data");

        final Map<String, String> seasonNames = new HashMap<>();        // id, name
        final Map<String, String> seasonParents = new HashMap<>();      // id, parent id
        final Map<String, SeasonType> seasonTypes = new HashMap<>();    // id, type

        for (final Object seasonObj : seasons) {
            final JSONObject season = (JSONObject) seasonObj;

            final String title = season.optString("title");
            final String name = season.optString("displayName");
            final String seasonTypeName = season.optString("type");

            final SeasonType seasonType = seasonTypeName.contains("Act")
                    ? SeasonType.ACT : SeasonType.EPISODE;


            final String seasonId = season.getString("uuid");
            final String seasonName = title.isEmpty() ? name : title;
            final String parentId = season.optString("parentUuid");

            seasonNames.put(seasonId, seasonName);
            seasonTypes.put(seasonId, seasonType);

            if (!parentId.isEmpty()) {
                seasonParents.put(seasonId, parentId);
            }
        }

        for (final String id : seasonNames.keySet()) {
            final String name = seasonNames.get(id);
            final SeasonType type = seasonTypes.get(id);
            final String parentId = seasonParents.get(id);

            Season parentSeason = null;
            if (parentId != null) {
                final String parentName = seasonNames.get(parentId);
                final SeasonType parentType = seasonTypes.get(parentId);

                parentSeason = new Season(
                        parentId,
                        parentName,
                        parentType,
                        null
                );
            }



            final String fullName = !name.contains("//") && parentSeason != null
                    ? parentSeason.name() + " // " + name
                    : name;


            final Season season = new Season(
                    id,
                    fullName,
                    type,
                    parentSeason
            );
        }
    }


    /**
     * Fetches and loads all maps.
     *
     * @param client HttpClient.
     */
    private void loadMaps(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "maps");
        final JSONArray maps = jsonObj.getJSONArray("data");

        for (final Object mapObj : maps) {
            final JSONObject map = (JSONObject) mapObj;

            final String name = map.getString("displayName");
            final String url = map.getString("mapUrl"); // Using for blacklist
            final String id = map.getString("uuid");


            MatchMap.loadMap(id, url, name);

            api.getImageProvider().getMaps().putName(id, url);

            api.getImageProvider().getMaps().putImage(id,
                    IMAGE_URL.formatted(IMAGE_TARGET_MAPS, id, IMAGE_SIZE_BIG)
            );


            // Check if map actually has a mini image by checking
            // if it's on the blacklist.
            boolean ignore = false;
            for (final String blacklist : SMALL_MAPS_IMAGE_BLACKLIST) {
                if (url.equalsIgnoreCase(blacklist)) {
                    ignore = true;
                    break;
                }
            }

            if (ignore) continue;

            api.getImageProvider().getMaps().putMiniImage(id,
                    IMAGE_URL.formatted(IMAGE_TARGET_MAPS, id, IMAGE_SIZE_SMALL)
            );
        }
    }


    /**
     * Fetches and loads all competitive tiers.
     *
     * @param client HttpClient.
     */
    private void loadTiers(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "competitivetiers");

        final JSONArray acts = jsonObj.getJSONArray("data");
        final JSONObject act = acts.getJSONObject(acts.length() - 1);
        final JSONArray tiers = act.getJSONArray("tiers");

        for (final Object tierObj : tiers) {
            final JSONObject rank = (JSONObject) tierObj;

            final String id = String.valueOf(rank.getInt("tier"));

            final String fullName = rank.getString("tierName");
            final String[] fullNameParts = fullName.split(" ", 2);

            final String rankName = fullNameParts[0];
            final int tierNum = fullNameParts.length == 2
                    ? Integer.parseInt(fullNameParts[1])
                    : 0;

            final Object smallIconObj = rank.get("smallIcon");
            final Object largeIconObj = rank.get("largeIcon");

            if (smallIconObj instanceof String smallIcon && largeIconObj instanceof String largeIcon) {
                final String color = rank.getString("color");
                final String name = rankName + (tierNum == 0 ? "" : " " + ("I").repeat(tierNum));
                final Tier tier = Tier.getTierByName(name);

                if (tier == null) {
                    throw new NullPointerException("Tier " + name + " not found!");
                }

                tier.updateTierColor(color);
                tier.updateTierId(id);

                api.getImageProvider().getTiers().putName(id, name);

                api.getImageProvider().getTiers().putImage(
                        id,
                        largeIcon
                );

                api.getImageProvider().getTiers().putMiniImage(
                        id,
                        smallIcon
                );

            }
        }
    }


    /**
     * Fetches and loads all weapons.
     *
     * @param client HttpClient.
     */
    private void loadWeapons(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "weapons");
        final JSONArray guns = jsonObj.getJSONArray("data");

        for (final Object gunObj : guns) {
            final JSONObject gun = (JSONObject) gunObj;

            final String id = gun.getString("uuid");
            final String name = gun.getString("displayName");
            final String displayIcon = gun.getString("displayIcon");

            api.getImageProvider().getWeaponSkins().putName(id, name);
            api.getImageProvider().getWeaponSkins().putImage(id, displayIcon);

            final Weapon weapon = Weapon.getWeaponByName(name);
            if (weapon == null) {
                throw new NullPointerException("Weapon " + name + " not found!");
            }

            weapon.updateDefaultSkinId(id);

            for (final Object skinObj : gun.getJSONArray("skins")) {
                final JSONObject skin = (JSONObject) skinObj;

                final String skinId = skin.getString("uuid");
                final String skinName = skin.getString("displayName");
                final Object displaySkinIconObj = skin.get("displayIcon");

                if (skinName.equals(name)) {
                    continue;
                }

                // Check if skin name is something like 'Standard Vandal'.
                // If so, then I'll just use the default weapon as image.
                final int removalIndex = StringUtils.searchIndex("Standard ", skinName);
                if (removalIndex != -1) {
                    final String tmpName = skinName.substring(removalIndex);

                    if (Weapon.getWeaponByName(tmpName) != null) {
                        api.getImageProvider().getWeaponSkins().putImage(skinId, displayIcon);
                    }
                }

                api.getImageProvider().getWeaponSkins().putName(skinId, skinName);

                if (! (displaySkinIconObj instanceof String displaySkinIcon)) {
                    final JSONArray chromas = skin.getJSONArray("chromas");
                    final JSONObject firstChroma = (JSONObject) chromas.get(0);
                    final Object displayChromaIconObj = firstChroma.get("displayIcon");

                    if (! (displayChromaIconObj instanceof String displayChromaIcon)) {
                        final JSONArray levels = skin.getJSONArray("levels");
                        final JSONObject firstLevel = (JSONObject) levels.get(0);
                        final String displayLevelIcon = firstLevel.getString("displayIcon");

                        api.getImageProvider().getWeaponSkins().putImage(skinId, displayLevelIcon);
                    } else {
                        api.getImageProvider().getWeaponSkins().putImage(skinId, displayChromaIcon);
                    }

                } else {
                    api.getImageProvider().getWeaponSkins().putImage(skinId, displaySkinIcon);
                }

            }
        }
    }

    /**
     * Creates a shortcut.
     *
     * @param shortcutName Name of the shortcut.
     * @param targetFileName Path of the file which is being executed.
     * @param shortcutDir Directory path where to create the shortcut.
     *
     * @return True successful or not. False otherwise.
     */
    private boolean createShortcut(
            final String shortcutName,
            final String targetFileName,
            String shortcutDir
    ) {

        final String[] scriptLines = prepareScriptLines(
                targetFileName,
                shortcutDir.replace('/', '\\') + "\\" + shortcutName + ".lnk"
        );

        final String script = String.join("\n", scriptLines);

        try {
            // Create a temp script file and execute it the code
            // to create the shortcut using PowerShell.
            final Path tempScript = Files.createTempFile("create_shortcut", ".ps1");
            Files.write(tempScript, script.getBytes());

            final ProcessBuilder pb = new ProcessBuilder(
                    "powershell",
                    "-ExecutionPolicy", "Bypass",
                    "-File",
                    tempScript.toString()
            );

            pb.inheritIO();


            final Process process = pb.start();
            final int exitCode = process.waitFor();

            // Delete temp script file.
            Files.deleteIfExists(tempScript);

            return exitCode == 0;

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * Prepares the script for creating a shortcut
     * using PowerShell.
     *
     * @param targetFileName Target file name.
     * @param shortcutFilePath Shortcut file path.
     *
     * @return Script lines.
     */
    private String[] prepareScriptLines(
            final String targetFileName,
            final String shortcutFilePath
    ) {
        final File targetFile = FileDir.SCRIPTS.getFile(targetFileName);
        final File iconFile = SystemImages.ICON.getImageFile();

        final String targetPath = targetFile.getAbsolutePath()
                .replace('/', '\\');

        final String targetDirectory = targetFile.getParentFile().getAbsolutePath()
                .replace('/', '\\');

        final String iconPath = iconFile.getAbsolutePath()
                .replace('/', '\\');

        return new String[] {
                "$WshShell = New-Object -ComObject WScript.Shell",
                "$Shortcut = $WshShell.CreateShortcut('" + shortcutFilePath + "')",
                "$Shortcut.TargetPath = '" + targetPath + "'",
                "$Shortcut.IconLocation = '" + iconPath + "'",
                "$Shortcut.WorkingDirectory = '" + targetDirectory + "'",
                "$Shortcut.Save()"
        };
    }
}