package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.image.DisplayImage;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventory {

    private final VITAPI api;
    private final Map<Weapon, DisplayImage> weaponSkins = new HashMap<>();
    private final Map<Weapon, String> weaponSkinNames = new HashMap<>();

    public PlayerInventory(final Map<Weapon, String> weaponIds) {
        this.api = VIT.get();

        for (Map.Entry<Weapon, String> entry : weaponIds.entrySet()) {
            final Weapon weapon = entry.getKey();
            final String skinId = entry.getValue();

            final String skinName = api.getImageProvider().getWeaponSkins().getName(skinId);

            final DisplayImage image = api.getImageProvider().getWeaponSkins().getImage(skinId);
            this.weaponSkins.put(weapon, image);
            this.weaponSkinNames.put(weapon, skinName);
        }
    }

    /**
     * Get the image of the skin the player
     * has for the following weapon.
     *
     * @param weapon Weapon.
     *
     * @return DisplayImage of the weapon skin.
     */
    public DisplayImage getWeaponSkin(final Weapon weapon) {
        final DisplayImage skin = this.weaponSkins.get(weapon);

        if (skin == null) {
            final DisplayImage defaultSkin = weapon.getDefaultSkin();

            this.weaponSkins.put(weapon, defaultSkin);
            return defaultSkin;
        }

        return skin;
    }

    public String getWeaponSkinName(final Weapon weapon) {
        final String skinName = this.weaponSkinNames.get(weapon);

        if (skinName == null) {
            final String defaultSkinName = api
                    .getImageProvider()
                    .getWeaponSkins()
                    .getName(weapon.getDefaultSkinId());

            this.weaponSkinNames.put(weapon, defaultSkinName);
            return defaultSkinName;
        }

        return skinName;
    }
}
