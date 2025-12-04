package com.teamMate.Test;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;
import com.teamMate.Strategies.BalancedTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalancedTeamStrategyTest {

    private TeamFormationStrategy strategy;
    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        strategy = new BalancedTeamStrategy();
        participants = new ArrayList<>();
    }

    @Test
    void testFormTeamsWithNullOrEmptyInput() {
        // Test null participants
        List<Team> result1 = strategy.formTeams(null, 3);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Test empty participants
        List<Team> result2 = strategy.formTeams(new ArrayList<>(), 3);
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }

    @Test
    void testFormTeamsWithInvalidTeamSize() {
        participants.add(createParticipant("P1", "John", "john@mail.com", "FIFA", 5, "Attacker", 80, "Balanced"));

        List<Team> result = strategy.formTeams(participants, 1); // teamSize < 2
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFormTeamsPerfectLeaderToTeamRatio() {
        // 2 leaders + 2 non-leaders, team size 2 → expect 2 balanced teams
        participants.add(createParticipant("P1", "Leader1", "l1@mail.com", "FIFA", 5, "Attacker", 95, "Leader"));
        participants.add(createParticipant("P2", "Leader2", "l2@mail.com", "FIFA", 6, "Defender", 92, "Leader"));
        participants.add(createParticipant("P3", "NonLeader1", "n1@mail.com", "Chess", 7, "Supporter", 80, "Balanced"));
        participants.add(createParticipant("P4", "NonLeader2", "n2@mail.com", "Chess", 8, "Strategist", 75, "Thinker"));

        List<Team> teams = strategy.formTeams(participants, 2);

        assertEquals(2, teams.size(), "Should create exactly 2 teams");

        // Verify each team has exactly 2 members
        for (Team team : teams) {
            assertEquals(2, team.getMembers().size(), "Each team should have 2 members");
            assertEquals(1, team.getMembers().stream()
                    .filter(p -> "Leader".equalsIgnoreCase(p.getPersonalityType()))
                    .count(), "Each team should have exactly 1 leader");
        }
    }


    @Test
    void testFormTeamsLargeScenario() {
        // Create a larger scenario: 5 leaders + 10 non-leaders, team size 4
        for (int i = 1; i <= 5; i++) {
            participants.add(createParticipant("L" + i, "Leader" + i, "l" + i + "@mail.com",
                    "Game" + i, 6, "Role" + i, 85, "Leader"));
        }

        for (int i = 1; i <= 10; i++) {
            participants.add(createParticipant("NL" + i, "NonLeader" + i, "nl" + i + "@mail.com",
                    "Game" + (i % 3 + 1), 5 + (i % 4), "Role" + (i % 4 + 1), 70 + (i % 20),
                    i % 3 == 0 ? "Thinker" : "Balanced"));
        }

        List<Team> teams = strategy.formTeams(participants, 4);

        int totalParticipants = 15;
        int requiredTeams = (int) Math.ceil((double) totalParticipants / 4); // ceil(15/4) = 4

        // We have 5 leaders, so we'll create 4 main teams + 1 solo leader team = 5 teams total
        assertEquals(5, teams.size(), "Should create 5 teams (4 main + 1 solo)");

        // Verify all participants are assigned
        int totalAssigned = teams.stream().mapToInt(team -> team.getMembers().size()).sum();
        assertEquals(totalParticipants, totalAssigned, "All participants should be assigned");

        // Verify team size constraints
        for (Team team : teams) {
            assertTrue(team.getMembers().size() <= 4, "No team should exceed team size");
            assertTrue(team.getMembers().size() >= 1, "No team should be empty");
        }
    }

    @Test
    void testTeamRequiredSizeIsSetCorrectly() {
        participants.add(createParticipant("P1", "Leader1", "l1@mail.com", "FIFA", 5, "Attacker", 95, "Leader"));
        participants.add(createParticipant("P2", "NonLeader1", "nl1@mail.com", "Chess", 6, "Defender", 80, "Balanced"));

        List<Team> teams = strategy.formTeams(participants, 3); // Team size 3

        for (Team team : teams) {
            assertEquals(3, team.getRequiredTeamSize(), "Team required size should match input parameter");
        }
    }


    // Helper method to create participants safely
    private Participant createParticipant(String id, String name, String email,
                                          String preferredGame, int skillLevel,
                                          String preferredRole, int personalityScore,
                                          String personalityType) {
        Participant participant = new Participant(id, name, email, preferredGame, skillLevel, preferredRole, personalityScore, personalityType);
        return participant;
    }
    @Test
    void testNoDuplicateParticipants() {
        // Prepare a medium dataset: 20 participants
        for (int i = 1; i <= 20; i++) {
            participants.add(createParticipant(
                    "P" + i,
                    "Name" + i,
                    "mail" + i + "@test.com",
                    "Game" + (i % 5),
                    5,
                    "Role" + (i % 5),
                    80,
                    (i % 4 == 0) ? "Leader" : "Balanced"
            ));
        }

        List<Team> teams = strategy.formTeams(participants, 4);

        // Collect all IDs
        List<String> allIds = new ArrayList<>();
        for (Team t : teams) {
            for (Participant p : t.getMembers()) {
                allIds.add(p.getId());
            }
        }

        // Ensure uniqueness
        long distinctCount = allIds.stream().distinct().count();
        assertEquals(distinctCount, allIds.size(), "Duplicate participant detected!");
    }

}