package de.rayzs.vit.api.items;

import java.util.Locale;

public enum Agent {

    // Sentinels
    CHAMBER     ("Chamber",     AgentRole.SENTINEL),
    CYPHER      ("Cypher",      AgentRole.SENTINEL),
    DEADLOCK    ("Deadlock",    AgentRole.SENTINEL),
    KILLJOY     ("Killjoy",     AgentRole.SENTINEL),
    SAGE        ("Sage",        AgentRole.SENTINEL),

    // Duelists
    CLOVE       ("Clove",       AgentRole.DUELIST),
    ISO         ("Iso",         AgentRole.DUELIST),
    JETT        ("Jett",        AgentRole.DUELIST),
    NEON        ("Neon",        AgentRole.DUELIST),
    PHOENIX     ("Phoenix",     AgentRole.DUELIST),
    RAZE        ("Raze",        AgentRole.DUELIST),
    REYNA       ("Reyna",       AgentRole.DUELIST),
    YORU        ("Yoru",        AgentRole.DUELIST),

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


    private final String agentName, agentId;
    private final AgentRole agentRole;

    Agent(final String agentName, final AgentRole agentRole) {
        this.agentId = agentName.toLowerCase(Locale.ROOT);

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
     * Returns agent id which is basically just
     * the agent name but in lowercased letters
     * for simpler management.
     *
     * @return Agent id.
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
     * Iterates through all agents and compare their ids
     * with the provided input. Returns the agent if it finds a match,
     * otherwise returns null.
     *
     * @param agentId Agent name. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Agent getAgent(final String agentId) {
        for (final Agent agent : Agent.values()) {
            if (agent.getAgentId().equalsIgnoreCase(agentId)) {
                return agent;
            }
        }

        return null;
    }
}
