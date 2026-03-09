package de.rayzs.vit.launch.processes.prepare;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.download.DownloadElement;
import de.rayzs.vit.api.download.DownloadProcess;
import de.rayzs.vit.api.file.FileDir;
import de.rayzs.vit.api.gui.OptionGUI;
import de.rayzs.vit.api.gui.PopupGUI;
import de.rayzs.vit.api.gui.UninteractableGUI;
import de.rayzs.vit.api.request.Request;
import de.rayzs.vit.api.request.RequestDest;
import de.rayzs.vit.api.request.RequestMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.Optional;

public class UpdateChecker {

    private final String url = "https://api.github.com/repos/rayzsyt/vit/releases/latest";

    private boolean updated = true, wantToUpdate = false;

    public UpdateChecker() {

        final Request request = Request.createRequest(
                RequestMethod.GET,
                RequestDest.UPDATE,
                ""
        );

        final Optional<String> result = request.sendAndGet(Request.createClient());

        // In case the request failed.
        if (result.isEmpty()) {
            return;
        }


        final JSONObject data = new JSONObject(result.get());

        // In case there's nothing on GitHub.
        if (!data.has("tag_name")) {
            return;
        }


        final String version = data.getString("tag_name");


        if (VITAPI.getVersion().equalsIgnoreCase(version)) {
            return;
        }


        final OptionGUI option = OptionGUI.create(
                "Update available!",
                "A new version of VIT is available now! Would you like to update?",
                "YESSIIIIR",
                "Not now, thanks."
        );

        if (option.getResponse() != 1) {
            return;
        }


        updated = false;
        wantToUpdate = true;


        final JSONArray assets = data.getJSONArray("assets");

        for (final Object assetObj : assets) {
            final JSONObject asset = (JSONObject) assetObj;

            final String assetName = asset.getString("name");

            if (!assetName.endsWith(".jar")) {
                continue;
            }

            final String downloadUrl = asset.getString("browser_download_url");


            final UninteractableGUI uninteractableGUI = UninteractableGUI.create(
                    "Downloading...",
                    "Installing latest VIT version... Please wait."
            );

            final DownloadElement element = new DownloadElement(url, "latest-updated.jar");
            final DownloadProcess process = new DownloadProcess(FileDir.ROOT, element);

            process.start(a -> {});

            uninteractableGUI.dispose();

            final PopupGUI popupGUI = PopupGUI.create("Completed!",
                    "Okay!",
                    "Update has been installed. Please restart VIT."
            );

            popupGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }

    /**
     * Is VIT running on the latest version
     * or not.
     *
     * @return True if VIT is running on the latest version. False otherwise.
     */
    public boolean isUpdated() {
        return !updated;
    }

    /**
     * If the user wants to update VIT
     * or not.
     *
     * @return True if the user wants to update to the latest VIT version. False otherwise.
     */
    public boolean wantToUpdate() {
        return wantToUpdate;
    }
}
