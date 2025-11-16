package com.teamMate.Test;
import com.teamMate.FileService;
import com.teamMate.Participant;
import com.teamMate.Team;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private final FileService fileService = new FileService();

    @Test
    void testLoadParticipantsFromCSV() throws IOException {
        Path temp = Files.createTempFile("participants_test", ".csv");
        Files.write(temp, (
                "id,name,email,game,skill,role,score,type\n" +
                        "P1,Test,test@mail.com,FIFA,7,Attacker,80,Balanced\n"
        ).getBytes());

        List<Participant> participants =
                fileService.loadParticipantsFromCSV(temp.toString());

        assertEquals(1, participants.size());
        Participant p = participants.get(0);
        assertEquals("P1", p.getId());
        assertEquals("Test", p.getName());
        assertEquals(7, p.getSkillLevel());
    }

    @Test
    void testSaveTeamsToCSV() throws IOException {
        Path temp = Files.createTempFile("teams_test", ".csv");

        Participant p = new Participant("P1", "Test", "test@mail.com",
                "FIFA", 7, "Attacker", 80, "Balanced");
        Team team = new Team(1);
        team.setRequiredTeamSize(1);
        team.addMember(p);

        fileService.saveTeamsToCSV(Collections.singletonList(team), temp.toString());

        List<String> lines = Files.readAllLines(temp);
        assertEquals(2, lines.size()); // header + 1 data line
        assertTrue(lines.get(1).contains("P1"));
    }
}
