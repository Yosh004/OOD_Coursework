package com.teamMate.Test;

import com.teamMate.Participant;
import com.teamMate.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    void testAddMemberUpdatesAverageSkillAndSets() {
        Team team = new Team(1);
        team.setRequiredTeamSize(3);

        Participant p1 = new Participant("P1", "A", "a@mail.com",
                "FIFA", 5, "Attacker", 80, "Leader");
        Participant p2 = new Participant("P2", "B", "b@mail.com",
                "Chess", 7, "Defender", 70, "Thinker");

        team.addMember(p1);
        team.addMember(p2);

        assertEquals(2, team.getSize());
        assertEquals(6.0, team.getAverageSkill(), 0.001);
        assertTrue(team.getUniqueGames().contains("FIFA"));
        assertTrue(team.getUniqueGames().contains("Chess"));
        assertTrue(team.getUniqueRoles().contains("Attacker"));
        assertTrue(team.getPersonalityTypes().contains("Leader"));
    }

    @Test
    void testBalanceStatus() {
        Team team = new Team(1);
        team.setRequiredTeamSize(2);

        Participant p1 = new Participant("P1", "A", "a@mail.com",
                "FIFA", 5, "Attacker", 80, "Leader");
        team.addMember(p1);

        assertFalse(team.isBalancedTeam());
        assertTrue(team.hasSpace());
        assertEquals(1, team.getRemainingSpace());
    }
}
