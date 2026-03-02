package de.rayzs.vit.api.items;

import java.util.Locale;

public enum Team {

    DEFEND      ("Defender", "defending"),
    ATTACK      ("Attacker", "attacking");


    private final String teamName, teamId, teamAdjective;

    Team(final String teamName, final String teamAdjective) {
        this.teamId = teamName.toLowerCase(Locale.ROOT);

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
     * Returns the team id which is basically just
     * the team name but in lowercased letters
     * for simpler management.
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
        return teamAdjective;
    }

    /**
     * Iterates through all teams and compare their ids
     * with the provided input. Returns the team if it finds a match,
     * otherwise returns null.
     *
     * @param teamId Team id. Letter casing does not matter!
     * @return Returns matched agent or null.
     */
    public static Team getTeam(final String teamId) {
        for (final Team team : Team.values()) {
            if (team.getTeamId().equalsIgnoreCase(teamId)) {
                return team;
            }
        }

        return null;
    }
}
