package de.rayzs.vit.api.objects.player;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.objects.items.Weapon;
import de.rayzs.vit.api.image.DisplayImage;

import java.util.HashMap;
import java.util.Map;

public class PlayerInventory {

    private final Map<Weapon, DisplayImage> weaponSkins = new HashMap<>();

    public PlayerInventory(final Map<Weapon, String> weaponIds) {
        final VITAPI api = VIT.get();

        for (Map.Entry<Weapon, String> entry : weaponIds.entrySet()) {
            final Weapon weapon = entry.getKey();
            final String skinId = entry.getValue();

            final DisplayImage image = api.getImageProvider().getWeaponSkins().getImage(skinId);
            this.weaponSkins.put(weapon, image);
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
        return weaponSkins.getOrDefault(weapon, weapon.getDefaultSkin());
    }
}
