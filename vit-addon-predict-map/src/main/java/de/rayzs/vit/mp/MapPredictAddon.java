package de.rayzs.vit.mp;

import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.addon.Addon;
import de.rayzs.vit.api.addon.AddonDescription;
import de.rayzs.vit.api.event.EventAdapter;
import de.rayzs.vit.api.event.events.game.PreGameInitializeEvent;
import de.rayzs.vit.api.event.events.gui.InitializeMainGuiEvent;

public class MapPredictAddon extends Addon {

    public MapPredictAddon(VITAPI api, AddonDescription description) {
        super(api, description);
    }

    @Override
    public void onEnable() {

        api.getEventManager().register(this, new EventAdapter<>(PreGameInitializeEvent.class) {

            @Override
            public void call(PreGameInitializeEvent event) {
                System.out.println("Map: " + event.getMapName());
            }
        });

    }
}
