package de.rayzs.vit.api.items;

import java.util.Locale;

public enum Agent {

    // Sentinels
    CHAMBER     ("Chamber",     AgentRole.SENTINEL),
    CYPHER      ("Cypher",      AgentRole.SENTINEL),
    DEADLOCK    ("Deadlock",    AgentRole.SENTINEL),
    KILLJOY     ("Killjoy",     AgentRole.SENTINEL),
    SAGE        ("Sage",        AgentRole.SENTINEL),
    VYSE        ("Vyse",        AgentRole.SENTINEL),
    VETO        ("Veto",        AgentRole.SENTINEL),


    // Duelists
    CLOVE       ("Clove",       AgentRole.DUELIST),
    ISO         ("Iso",         AgentRole.DUELIST),
    JETT        ("Jett",        AgentRole.DUELIST),
    NEON        ("Neon",        AgentRole.DUELIST),
    PHOENIX     ("Phoenix",     AgentRole.DUELIST),
    RAZE        ("Raze",        AgentRole.DUELIST),
    REYNA       ("Reyna",       AgentRole.DUELIST),
    YORU        ("Yoru",        AgentRole.DUELIST),
    WAYLAY      ("Waylay",      AgentRole.DUELIST),

    // Initiators
    BREACH      ("Breach",      AgentRole.INITIATOR),
    FADE        ("Fade",        AgentRole.INITIATOR),
    GEKKO       ("Gekko",       AgentRole.INITIATOR),
    KAYO        ("KAY/O",       AgentRole.INITIATOR),
    SKYE        ("Skye",        AgentRole.INITIATOR),
    SOVA        ("Sova",        AgentRole.INITIATOR),
    TEJO        ("Tejo",        AgentRole.INITIATOR),

    // Controllers
    ASTRA       ("Astra",       AgentRole.CONTROLLER),
    BRIMSTONE   ("Brimstone",   AgentRole.CONTROLLER),
    OMEN        ("Omen",        AgentRole.CONTROLLER),
    VIPER       ("Viper",       AgentRole.CONTROLLER),
    HARBOR      ("Harbor",      AgentRole.CONTROLLER),;


    private String agentId;
    private final String agentName;
    private final AgentRole agentRole;

    Agent(final String agentName, final AgentRole agentRole) {
        this.agentName = agentName;
        this.agentRole = agentRole;
    }

    /**
     * Returns the agent name.
     *
     * @return Agent name.
     */
    public String getAgentName() {
        return this.agentName;
    }

    /**
     * Update agent id. Should only be called once
     * during runtime to set the agent id fetched from
     * the valorant-api.
     *
     * @param agentId Agent id.
     */
    public void updateAgentId(final String agentId) {
        if (this.agentId != null) {
            throw new IllegalStateException("Agent ID is already set!");
        }

        this.agentId = agentId;
    }

    /**
     * Get the agent id. This one needs to be set first after
     * fetching the agent uuid from the valorant-api.
     * It can then be used to easier navigate through
     * each agent's images and other information.
     *
     * @return Agent UUID.
     */
    public String getAgentId() {
        return this.agentId;
    }

    /**
     * Returns the role of the agent.
     *
     * @return Agent role.
     */
    public AgentRole getAgentRole() {
        return agentRole;
    }

    /**
     * Iterates through all agents and compare their names
     * with the provided input. Returns the agent if it finds a match,
     * otherwise returns null.
     *
     * @param agentName Agent name. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Agent getAgentByName(final String agentName) {
        for (final Agent agent : Agent.values()) {
            if (agent.getAgentName().equalsIgnoreCase(agentName)) {
                return agent;
            }
        }

        return null;
    }

    /**
     * Iterates through all agents and compare their ids
     * with the provided input. Returns the agent if it finds a match,
     * otherwise returns null.
     *
     * @param agentId Agent id. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Agent getAgentById(final String agentId) {
        for (final Agent agent : Agent.values()) {
            if (agent.getAgentId().equalsIgnoreCase(agentId)) {
                return agent;
            }
        }

        return null;
    }
}
