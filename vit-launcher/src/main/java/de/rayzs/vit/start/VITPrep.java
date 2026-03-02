package de.rayzs.vit.start;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.download.DownloadElement;
import de.rayzs.vit.api.download.DownloadProcess;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.image.DisplayImage;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.*;

public class VITPrep {

    private final VITAPI api;

    public VITPrep(VITAPI api) {
        this.api = api;

        final HttpClient client = Request.createClient();

        loadWeapons(client);

        client.close();


        Map<FileDir, HashSet<DownloadElement>> images = new HashMap<>();
        for (final FileDir dir : FileDir.values()) {
            for (final DisplayImage image : api.getImageProvider().getImages(dir)) {
                images.computeIfAbsent(dir, k -> new HashSet<>())
                        .add(image.getDownloadElement());

                System.out.println(image.getDownloadElement().fileName());
            }
        }

        for (final FileDir dir : FileDir.values()) {
            final DownloadProcess process = new DownloadProcess(
                    dir,
                    images.getOrDefault(
                            dir,
                            HashSet.newHashSet(0)
                    ).toArray(new DownloadElement[0])
            );

            process.start(p -> {
                try {
                    System.out.write(("\rDownloading " + dir.getFolderName() + ": " + Math.round(p.getPercent()) + "%").getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    public void loadWeapons(final HttpClient client) {
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

    private JSONObject fetch(final HttpClient client, final String name) {
        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.API,
                name
        );

        final Optional<String> body = request.sendAndGet(client);
        if (body.isEmpty()) {
            throw new NullPointerException("No body found! URL connection seems to have failed.");
        }

        return new JSONObject(body.get());
    }
}
