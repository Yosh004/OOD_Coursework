package com.teamMate;

import java.util.List;

// This is the main entry point for the application.
public class Main {

    // --- Configuration ---
    private static final String INPUT_FILE = "participants_sample.csv";
    private static final String OUTPUT_FILE = "formed_teams.csv";
    private static final int TEAM_SIZE = 5; // Define team size (e.g., 5-person teams)

    public static void main(String[] args) {
        System.out.println("Starting TeamMate: Intelligent Team Formation System...");

        // 1. Initialize services
        fileService fileService = new fileService();
        TeamBuilder teamBuilder = new TeamBuilder();

        // 2. Read data
        System.out.println("Reading participants from " + INPUT_FILE + "...");
        List<Participant> participants = fileService.readParticipants(INPUT_FILE);

        if (participants.isEmpty()) {
            System.err.println("No participants found. Exiting.");
            return;
        }
        System.out.println("Loaded " + participants.size() + " participants.");

        // 3. Build teams (This is the complex, concurrent part)
        System.out.println("Building " + (participants.size() / TEAM_SIZE) + " teams of " + TEAM_SIZE + "...");
        long startTime = System.currentTimeMillis();

        List<Team> teams = teamBuilder.buildTeams(participants, TEAM_SIZE);

        long endTime = System.currentTimeMillis();
        System.out.println("Team building complete. Took " + (endTime - startTime) + " ms.");

        // 4. Write results
        System.out.println("Writing " + teams.size() + " teams to " + OUTPUT_FILE + "...");
        fileService.writeTeams(teams, OUTPUT_FILE);

        // 5. Print a summary
        System.out.println("\n--- Process Complete ---");
        for (Team team : teams) {
            System.out.println(team); // Uses the toString() method
        }
    }
}