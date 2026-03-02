package de.rayzs.vit.api.game.items;

public enum AgentRole {

    DUELIST         ("Duelist"),
    INITIATOR       ("Initiator"),
    SENTINEL        ("Sentinel"),
    CONTROLLER      ("Controller");


    private final String roleName;

    AgentRole(final String roleName) {
        this.roleName = roleName;
    }

    /**
     * Returns the agent role name.
     *
     * @return Agent role name.
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * Iterates through all team roles and compare their names
     * with the provided input. Returns the team role if it finds a match,
     * otherwise returns null.
     *
     * @param roleName Team role name. Letter casing does not matter!
     * @return Returns matched team role or null.
     */
    public static AgentRole getRoleByName(final String roleName) {
        for (final AgentRole role : AgentRole.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }

        return null;
    }
}
