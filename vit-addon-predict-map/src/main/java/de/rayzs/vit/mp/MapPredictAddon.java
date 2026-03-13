package de.rayzs.vit.mp;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.event.EventAdapter;
import de.rayzs.vit.api.event.events.game.PreGameInitializeEvent;
import de.rayzs.vit.api.event.events.gui.InitializeMainGuiEvent;
import de.rayzs.vit.api.gui.MainGUI;

public class MapPredictAddon extends Addon {

    public MapPredictAddon(VITAPI api, AddonDescription description) {
        super(api, description);
    }

    private static MainGUI mainGUI;

    @Override
    public void onEnable() {

        api.getEventManager().register(this, new EventAdapter<>(InitializeMainGuiEvent.class) {

            @Override
            public void call(InitializeMainGuiEvent event) {
                mainGUI = event.getGui();
            }
        });

        api.getEventManager().register(this, new EventAdapter<>(PreGameInitializeEvent.class) {

            @Override
            public void call(PreGameInitializeEvent event) {
                System.out.println("Found map: " + event.getMap().mapName());
                mainGUI.setTitle("Predicted Map: " + event.getMap().mapName());
            }
        });

    }
}
