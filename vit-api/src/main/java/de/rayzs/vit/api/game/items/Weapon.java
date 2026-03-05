package de.rayzs.vit.api.game.items;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.api.image.DisplayImage;

public enum Weapon {

    // Melee
    MELEE       ("Melee"),

    // Short-handed weapons.
    CLASSIC     ("Classic"),
    GHOST       ("Ghost"),
    FRENZY      ("Frenzy"),
    SHERIFF     ("Sheriff"),
    SHORTY      ("Shorty"),

    // Typical
    VANDAL      ("Vandal"),
    PHANTOM     ("Phantom"),
    BULLDOG     ("Bulldog"),

    // Spray and pray
    SPECTRE     ("Spectre"),
    STINGER     ("Stinger"),

    // The outcast
    ODIN        ("Odin"),
    ARES        ("Ares"),
    GUARDIAN    ("Guardian"),

    // Snipers
    OPERATOR    ("Operator"),
    MARSHAL     ("Marshal"),
    OUTLAW      ("Outlaw"),

    // Close range monsters
    JUDGE       ("Judge"),
    BUCKY       ("Bucky");


    private String defaultSkinId;
    private final String weaponName;

    Weapon(final String weaponName) {
        this.weaponName = weaponName;
    }

    /**
     * Returns the weapon name.
     *
     * @return Weapon name.
     */
    public String getWeaponName() {
        return this.weaponName;
    }


    /**
     * Update default skin id. Should only be called once
     * during runtime to set the default weapon skin id fetched from
     * the valorant-api.
     *
     * @param defaultSkinId Default skin id.
     */
    public void updateDefaultWeaponId(final String defaultSkinId) {
        if (this.defaultSkinId != null) {
            throw new IllegalStateException("Default Skin ID is already set!");
        }

        this.defaultSkinId = defaultSkinId;
    }

    /**
     * Get the default skin id. This one needs to be set first after
     * fetching the agent uuid from the valorant-api.
     *
     * @return Default skin id.
     */
    public String getDefaultSkinId() {
        if (this.defaultSkinId == null) {
            throw new IllegalStateException("Default Skin ID is not set yet!");
        }

        return this.defaultSkinId;
    }

    /**
     * Returns the DisplayImage of the default skin.
     *
     * @return DisplayImage.
     */
    public DisplayImage getDefaultSkin() {
        return VIT.get().getImageProvider().getWeaponSkins().getImage(this.defaultSkinId);
    }


    /**
     * Iterates through all weapons and compare their names
     * with the provided input. Returns the weapon if it finds a match,
     * otherwise returns null.
     *
     * @param weaponName Weapon name. Letter casing does not matter!
     * @return Returns matched weapon or null.
     */
    public static Weapon getWeaponByName(final String weaponName) {
        for (final Weapon weapon : Weapon.values()) {
            if (weapon.getWeaponName().equalsIgnoreCase(weaponName)) {
                return weapon;
            }
        }

        return null;
    }
}
