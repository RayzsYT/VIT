package de.rayzs.vit.api.event.events.settings;

import de.rayzs.vit.api.event.Event;
import de.rayzs.vit.api.settings.Settings;

/**
 * Called once a setting inside the settings menu has been changed.
 */
public class UpdatedSettingEvent extends Event {

    private final Settings updatedSetting;

    public UpdatedSettingEvent(final Settings updatedSetting) {
        this.updatedSetting = updatedSetting;
    }

    /**
     * Updated setting.
     *
     * @return Updated setting.
     */
    public Settings getUpdatedSetting() {
        return this.updatedSetting;
    }
}
