package de.rayzs.vit.api.items;

import java.util.Locale;

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


    private final String weaponName, weaponId;

    Weapon(final String weaponName) {
        this.weaponId = weaponName.toLowerCase(Locale.ROOT);

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
     * Returns weapon id which is basically just
     * the weapon name but in lowercased letters
     * for simpler management.
     *
     * @return Weapon id.
     */
    public String getWeaponId() {
        return this.weaponId;
    }


    /**
     * Iterates through all weapons and compare their ids
     * with the provided input. Returns the weapon if it finds a match,
     * otherwise returns null.
     *
     * @param weaponId Weapon id. Letter casing does not matter!
     * @return Returns matched weapon or null.
     */
    public static Weapon getWeapon(final String weaponId) {
        for (final Weapon weapon : Weapon.values()) {
            if (weapon.getWeaponId().equalsIgnoreCase(weaponId)) {
                return weapon;
            }
        }

        return null;
    }
}
