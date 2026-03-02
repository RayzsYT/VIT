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
