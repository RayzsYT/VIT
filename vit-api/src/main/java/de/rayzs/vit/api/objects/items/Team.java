package de.rayzs.vit.api.objects.items;

public enum Team {

    DEFEND      ("Defender", "blue",    "defending"),
    ATTACK      ("Attacker", "red",     "attacking");


    private final String teamName, teamId, teamAdjective;

    Team(final String teamName, final String teamId, final String teamAdjective) {
        this.teamName = teamName;
        this.teamId = teamId;
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
     * Get team id based on how the VALORANT api calls it.
     *
     * @return Team id.
     */
    public String getTeamId() {
        return this.teamId;
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
     * @return Returns matched team or null.
     */
    public static Team getTeamByName(final String teamName) {
        for (final Team team : Team.values()) {
            if (team.getTeamName().equalsIgnoreCase(teamName)) {
                return team;
            }
        }

        return null;
    }


    /**
     * Iterates through all teams and compare their ids
     * with the provided input. Returns the team if it finds a match,
     * otherwise returns null.
     *
     * @param teamId Team id. Letter casing does not matter!
     * @return Returns matched team or null.
     */
    public static Team getTeamById(final String teamId) {
        for (final Team team : Team.values()) {
            if (team.getTeamId().equalsIgnoreCase(teamId)) {
                return team;
            }
        }

        return null;
    }
}
