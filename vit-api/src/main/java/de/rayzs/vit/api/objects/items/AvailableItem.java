package de.rayzs.vit.api.objects.items;

public enum AvailableItem {

    SKIN_LEVELS                     ("Skin Levels",             "e7c63390-eda7-46e0-bb7a-a6abdacd2433"),
    SKIN_CHROMAS                    ("Skin Chromas",            "3ad1b2b2-acdb-4524-852f-954a76ddae0a"),
    AGENTS                          ("Agents",                  "01bb38e1-da47-4e6a-9b3d-945fe4655707"),
    CONTRACT_DEFINITIONS            ("Contract Definitions",    "f85cb6f7-33e5-4dc8-b609-ec7212301948"),
    GUN_BUDDIES                     ("Gun Buddies",             "dd3bf334-87f3-40bd-b043-682a57a8dc3a"),
    SPRAYS                          ("Sprays",                  "d5f120f8-ff8c-4aac-92ea-f2b5acbe9475"),
    PLAYER_CARDS                    ("Player Cards",            "3f296c07-64c3-494c-923b-fe692a4fa1bd"),
    PLAYER_TITLES                   ("Player Titles",           "de7caa6b-adf7-4588-bbd1-143831e786c6");



    private final String name, id;

    AvailableItem(final String name, final String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Get name.
     *
     * @return Name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get ID.
     *
     * @return ID.
     */
    public String getId() {
        return this.id;
    }
}
