package com.teamMate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class fileService {
    public List<Participant> readParticipants(String filePath) {
        List<Participant> participants = new ArrayList<>();
        // Using try-with-resources to automatically close the reader
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Skip the header row
            br.readLine();

            // Read every other line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 5) {
                    try {
                        // Assumes CSV structure: StudentID,Name,PersonalityScore,PreferredGame,PreferredRole
                        String studentID = values[0].trim();
                        String name = values[1].trim();
                        int score = Integer.parseInt(values[2].trim());
                        String game = values[3].trim();
                        String role = values[4].trim();

                        // Create and add the new participant
                        participants.add(new Participant(studentID, name, score, game, role));

                    } catch (NumberFormatException e) {
                        // Handle invalid number for score
                        System.err.println("Skipping invalid line (bad score): " + line);
                    } catch (Exception e) {
                        // Handle any other unexpected error on a line
                        System.err.println("Skipping malformed line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            // Handle file not found or read errors
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
        return participants;
    }

    public void writeTeams(List<Team> teams, String filePath) {
        // Using try-with-resources to automatically close the writer
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            // Write the header row
            bw.write("TeamID,StudentID,Name,PersonalityType,PreferredRole,PreferredGame");
            bw.newLine();

            // Write data for each member of each team
            for (Team team : teams) {
                for (Participant member : team.getMembers()) {
                    String line = String.join(",",
                            String.valueOf(team.getTeamID()),
                            member.getStudentID(),
                            member.getName(),
                            member.getPersonalityType(),
                            member.getPreferredRole(),
                            member.getPreferredGame()
                    );
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            // Handle file write errors
            System.err.println("Error writing the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



