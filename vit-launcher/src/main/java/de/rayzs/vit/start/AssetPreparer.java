package de.rayzs.vit.start;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.download.DownloadElement;
import de.rayzs.vit.api.download.DownloadProcess;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.image.DisplayImage;
import de.rayzs.vit.api.items.Agent;
import de.rayzs.vit.api.items.Tier;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.util.*;
import java.util.function.Consumer;

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

    public AssetPreparer(final VITAPI api, final Consumer<DownloadProcess> processConsumer) {
        this.api = api;

        System.out.println("Fetching and loading assets...");


        // Fetch and load all necessary assets.
        final HttpClient client = Request.createClient();

        loadWeapons(client);
        loadAgents(client);
        loadMaps(client);
        loadTiers(client);

        // Not required anymore, therefore closing the client.
        client.close();



        // Collects all the files which need to be downloaded.

        final Map<FileDir, HashSet<DownloadElement>> images = new HashMap<>();
        for (final FileDir dir : FileDir.values()) {
            for (final DisplayImage image : api.getImageProvider().getImages(dir)) {
                if (image.doesExist()) {
                    continue;
                }

                images.computeIfAbsent(dir, k -> new HashSet<>())
                        .add(image.getDownloadElement());
            }
        }

        for (final FileDir dir : FileDir.values()) {

            // Creates a DownloadProcess for the directory and all
            // its assets that still need to be downloaded.
            final DownloadProcess process = new DownloadProcess(dir,
                    images.getOrDefault(dir,
                            HashSet.newHashSet(0)
                    ).toArray(new DownloadElement[0])
            );

            // If it's already completed aka empty, it will
            // just skip that DownloadProcess.
            if (process.isCompleted()) {
                continue;
            }

            // Downloads all the missing assets.
            process.start(processConsumer);
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
     * Fetches and loads all maps.
     *
     * @param client HttpClient.
     */
    private void loadMaps(final HttpClient client) {
        final JSONObject jsonObj = fetch(client, "maps");
        final JSONArray maps = jsonObj.getJSONArray("data");

        for (final Object mapObj : maps) {
            final JSONObject map = (JSONObject) mapObj;

            final String url = map.getString("mapUrl"); // Using for blacklist
            final String name = map.getString("displayName");
            final String id = map.getString("uuid");

            api.getImageProvider().getMaps().putName(id, name);

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

            for (final Object skinObj : gun.getJSONArray("skins")) {
                final JSONObject skin = (JSONObject) skinObj;

                final String skinId = skin.getString("uuid");
                final String skinName = skin.getString("displayName");
                final Object displaySkinIconObj = skin.get("displayIcon");

                if (skinName.equals(name)) {
                    continue;
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
}