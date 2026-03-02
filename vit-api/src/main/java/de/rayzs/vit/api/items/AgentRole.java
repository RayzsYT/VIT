package de.rayzs.vit.api.items;

import java.util.Locale;

public enum AgentRole {

    DUELIST         ("Duelist"),
    INITIATOR       ("Initiator"),
    SENTINEL        ("Sentinel"),
    CONTROLLER      ("Controller");


    private final String roleName, roleId;

    AgentRole(final String roleName) {
        this.roleId = roleName.toLowerCase(Locale.ROOT);

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
     * Returns role id which is basically just
     * the role name but in lowercased letters
     * for simpler management.
     *
     * @return Role id.
     */
    public String getRoleId() {
        return this.roleId;
    }

    /**
     * Iterates through all team roles and compare their ids
     * with the provided input. Returns the team role if it finds a match,
     * otherwise returns null.
     *
     * @param roleId Team role id. Letter casing does not matter!
     * @return Returns matched team role or null.
     */
    public static AgentRole getRole(final String roleId) {
        for (final AgentRole role : AgentRole.values()) {
            if (role.getRoleId().equalsIgnoreCase(roleId)) {
                return role;
            }
        }

        return null;
    }
}
