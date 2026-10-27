package de.rayzs.vit.api.utils;

import de.rayzs.vit.api.objects.items.Team;
import de.rayzs.vit.api.objects.player.Player;
import de.rayzs.vit.api.objects.player.match.Match;
import de.rayzs.vit.api.objects.player.party.Party;
import de.rayzs.vit.api.objects.player.party.PartyColors;
import de.rayzs.vit.api.settings.Settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyEstimator {

    /**
     * Takes an array of players and estimates each of their parties if they're not in one yet.
     * Returns a modified version of the array with all the players inside their estimates parties.
     *
     * @param players Array of players.
     *
     * @return New array of all players with their estimated parties.
     */
    public static Player[] estimateAndModifyPlayerParties(final Player... players) {

        // Ignore in case it's disabled.
        if (! (boolean) Settings.ESTIMATE_PLAYER_PARTIES.read()) return players;


        final Map<String, Player> playerIdsMap = new HashMap<>();        // Player id, Player
        final Map<Player, Party> playerParties = new HashMap<>();        // Player, Party


        // Load all players intro the playerIdsMap.
        for (final Player player : players) {
            playerIdsMap.put(player.id(), player);
        }


        {
            int currentPartyIndex = 0;

            // Put each player into their party.
            for (final Player player : players) {
                final String selfPlayerId = player.id();

                // Ignore in case its already been done.
                if (player.party() != null || playerParties.containsKey(player)) {
                    continue;
                }


                final Set<String> estimatedPartyMembers = estimatePartyMembers(player, player.playedMatches());

                // Remove player ids that are not inside the provided players array.
                estimatedPartyMembers.removeIf(otherPlayerId -> {
                    if (selfPlayerId.equalsIgnoreCase(otherPlayerId)) {
                        return false;
                    }

                    final Player other = playerIdsMap.get(otherPlayerId);
                    return other == null || other.team() != player.team();
                });

                // After filtering out all unnecessary players, we can check
                // whether the set still contains enough data, except the selfPlayerId.
                // If so, we can guarantee that the player has played with people in previous
                // matches that are in the current match even on the same team.
                if (estimatedPartyMembers.size() > 1) {
                    final Party party = new Party(
                            String.valueOf(currentPartyIndex),
                            PartyColors.getPartyColor(currentPartyIndex),
                            new Player[20]
                    );

                    { // Set party members.
                        int partyMemberIndex = 0;
                        for (String partyMemberId : estimatedPartyMembers) {
                            final Player partyMember = playerIdsMap.get(partyMemberId);
                            party.members()[partyMemberIndex++] = partyMember;
                        }
                    }

                    estimatedPartyMembers.forEach(partyMemberId -> {
                        final Player partyMember = playerIdsMap.get(partyMemberId);
                        playerParties.put(partyMember, party);
                    });

                    currentPartyIndex++;
                }
            }
        }


        // Reconstruct player array and apply their fetched parties.
        for (int i = 0; i < players.length; i++) {
            final Player player = players[i];

            players[i] = new Player(
                    player.id(),
                    player.team(),
                    player.name(),
                    player.agent(),
                    player.level(),
                    player.playerCardId(),
                    player.playerTitleId(),
                    player.settings(),
                    player.inventory(),
                    player.competitive(),
                    player.stats(),
                    playerParties.getOrDefault(player, player.party()),
                    player.lastSeenDetails(),
                    player.playedMatches()
            );
        }

        return players;
    }



    /**
     * Estimate party members by checking for previous games
     * where the same players appeared in the same team.
     *
     * @param selfPlayer Player.
     * @param history Player match history.
     * @return Set of player ids of players that were in previous matches on the same side.
     */
    private static Set<String> estimatePartyMembers(final Player selfPlayer, Match[] history) {
        final Set<String> probablePartyMembers = new HashSet<>();


        // Return empty list if there's no match history.
        if (history == null) {
            return probablePartyMembers;
        }


        for (Match match : history) {

            String selfPartyId = null;
            for (Player other : match.players()) {
                if (selfPlayer.id().equalsIgnoreCase( other.id() )) {
                    selfPartyId = other.party().partyId();
                    break;
                }
            }

            for (Player other : match.players()) {
                if (other.party() == null) continue;

                if (other.party().partyId().equalsIgnoreCase(selfPartyId)) {
                    probablePartyMembers.add(other.id());
                }
            }
        }


        return probablePartyMembers;
    }
}