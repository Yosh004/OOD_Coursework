package com.teamMate.Test;

import com.teamMate.Participant;
import com.teamMate.Team;
import com.teamMate.Strategies.BalancedTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalancedTeamStrategyTest {

    private final TeamFormationStrategy strategy = new BalancedTeamStrategy();

    @Test
    void testFormTeamsLeaderInEachTeamWhenPossible() {
        List<Participant> participants = new ArrayList<>();

        // 2 leaders + 2 non-leaders, team size 2 → expect 2 teams each with 1 leader
        participants.add(new Participant("P1", "L1", "l1@mail.com",
                "FIFA", 5, "Attacker", 95, "Leader"));
        participants.add(new Participant("P2", "L2", "l2@mail.com",
                "FIFA", 6, "Defender", 92, "Leader"));
        participants.add(new Participant("P3", "N1", "n1@mail.com",
                "Chess", 7, "Supporter", 80, "Balanced"));
        participants.add(new Participant("P4", "N2", "n2@mail.com",
                "Chess", 8, "Strategist", 75, "Thinker"));

        List<Team> teams = strategy.formTeams(participants, 2);

        assertEquals(2, teams.size());
        for (Team t : teams) {
            long leaderCount = t.getMembers().stream()
                    .filter(p -> "Leader".equals(p.getPersonalityType()))
                    .count();
            assertEquals(1, leaderCount);  // one leader per team
        }
    }
}
