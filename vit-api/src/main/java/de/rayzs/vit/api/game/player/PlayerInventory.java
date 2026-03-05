package de.rayzs.vit.api.game.player;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.VITAPI;
import de.rayzs.vit.api.game.items.Weapon;
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

    public DisplayImage getWeaponSkin(final Weapon weapon) {
        return weaponSkins.getOrDefault(weapon, null);
    }
}
