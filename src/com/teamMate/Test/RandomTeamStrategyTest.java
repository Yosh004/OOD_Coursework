package com.teamMate.Test;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;
import com.teamMate.Strategies.RandomTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomTeamStrategyTest {

    private final TeamFormationStrategy strategy = new RandomTeamStrategy();

    @Test
    void testFormTeamsCreatesCorrectNumberOfTeams() {
        List<Participant> participants = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            participants.add(new Participant("P" + i, "Name" + i,
                    "p" + i + "@mail.com", "Game", 5,
                    "Role", 80, "Balanced"));
        }

        List<Team> teams = strategy.formTeams(participants, 2);
        // 5 participants, team size 2 → 2 full teams + 1 leftover
        assertEquals(3, teams.size());
        assertEquals(2, teams.get(0).getRequiredTeamSize());
        assertEquals(2, teams.get(1).getRequiredTeamSize());
    }
    @Test
    void testFormTeamsCreatesCorrectDistribution() {

        List<Participant> participants = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            participants.add(new Participant("P" + i, "Name" + i,
                    "p" + i + "@mail.com", "Game", 5,
                    "Role", 80, "Balanced"));
        }

        List<Team> teams = strategy.formTeams(participants, 2);

        assertEquals(3, teams.size()); // 2 full + 1 leftover

        // Required team size must be applied to all teams
        teams.forEach(t -> assertEquals(2, t.getRequiredTeamSize()));

        // All 5 participants must exist in some team
        int totalAssigned = teams.stream().mapToInt(t -> t.getMembers().size()).sum();
        assertEquals(5, totalAssigned);

        // No team should exceed required size
        teams.forEach(team -> assertTrue(team.getMembers().size() <= 2));
    }
}
