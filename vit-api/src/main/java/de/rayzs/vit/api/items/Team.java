package de.rayzs.vit.api.items;

public enum Team {

    DEFEND      ("Defender", "defending"),
    ATTACK      ("Attacker", "attacking");


    private final String teamName, teamAdjective;

    Team(final String teamName, final String teamAdjective) {
        this.teamName = teamName;
        this.teamAdjective = teamAdjective;
    }

    /**
     * Returns the team name.
     *
     * @return Agent name.
     */
    public String getTeamName() {
        return this.teamName;
    }

    /**
     * Returns the team adjective.
     * Example: defending, attacking
     *
     * @return Team adjective.
     */
    public String getTeamAdjective() {
        return this.teamAdjective;
    }

    /**
     * Iterates through all teams and compare their names
     * with the provided input. Returns the team if it finds a match,
     * otherwise returns null.
     *
     * @param teamName Team name. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Team getTeamByName(final String teamName) {
        for (final Team team : Team.values()) {
            if (team.getTeamName().equalsIgnoreCase(teamName)) {
                return team;
            }
        }

        return null;
    }
}
