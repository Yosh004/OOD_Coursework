package com.teamMate;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class fileService {

    public List<Participant> loadParticipantsFromCSV(String filename) {
        List<Participant> participants = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                try {
                    Participant participant = parseParticipant(line);
                    if (participant != null) {
                        participants.add(participant);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }

        return participants;
    }

    private Participant parseParticipant(String line) {
        try {
            String[] fields = line.split(",");
            if (fields.length < 8) {
                throw new IllegalArgumentException("Invalid CSV format: " + line);
            }

            String id = fields[0].trim();
            String name = fields[1].trim();
            String email = fields[2].trim();
            String preferredGame = fields[3].trim();
            int skillLevel = Integer.parseInt(fields[4].trim());
            String preferredRole = fields[5].trim();
            int personalityScore = Integer.parseInt(fields[6].trim());
            String personalityType = fields[7].trim();

            // Validate data
            validateParticipantData(id, name, email, skillLevel, personalityScore);

            return new Participant(id, name, email, preferredGame, skillLevel,
                    preferredRole, personalityScore, personalityType);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in line: " + line, e);
        }
    }

    private void validateParticipantData(String id, String name, String email,
                                         int skillLevel, int personalityScore) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Participant ID cannot be empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Participant name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        if (skillLevel < 1 || skillLevel > 10) {
            throw new IllegalArgumentException("Skill level must be between 1-10: " + skillLevel);
        }
        if (personalityScore < 0 || personalityScore > 100) {
            throw new IllegalArgumentException("Personality score must be between 0-100: " + personalityScore);
        }
    }

    public void saveTeamsToCSV(List<Team> teams, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            // Write header
            writer.write("TeamID,ParticipantID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            writer.newLine();

            // Write team data
            for (Team team : teams) {
                for (Participant member : team.getMembers()) {
                    String line = String.format("%d,%s,%s,%s,%s,%d,%s,%d,%s",
                            team.getTeamId(),
                            member.getId(),
                            member.getName(),
                            member.getEmail(),
                            member.getPreferredGame(),
                            member.getSkillLevel(),
                            member.getPreferredRole(),
                            member.getPersonalityScore(),
                            member.getPersonalityType());
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV file: " + e.getMessage(), e);
        }
    }
}