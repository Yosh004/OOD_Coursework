package com.teamMate;

import com.teamMate.Strategies.BalancedTeamStrategy;
import com.teamMate.Strategies.RandomTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;

import java.util.*;

public class Main {
    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();
    private static FileService fileService = new FileService();
    private static Scanner scanner = new Scanner(System.in);

    // Polymorphic reference
    private static TeamFormationStrategy teamStrategy = new BalancedTeamStrategy();

    public static void main(String[] args) {
        System.out.println("=== TeamMate: Intelligent Team Formation System ===");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();
        if (username.equals("yv") && password.equals("2004")){
            System.out.println("...Hello user.....Welcome to Team Mate!");
        }
        else {
            System.out.println("...You don't have the required permissions to do this!");
            System.exit(0);
        }
        try {
            loadParticipants();

            boolean running = true;
            while (running) {
                displayMenu();
                int choice = getIntInput("Enter your choice: ");

                switch (choice) {
                    case 1:
                        formTeams();
                        break;
                    case 2:
                        addNewPlayer();
                        break;
                    case 3:
                        viewTeams();
                        break;
                    case 4:
                        viewParticipants();
                        break;
                    case 5:
                        saveTeams();
                        break;
                    case 6:
                        changeStrategy(); // New option to change strategy
                        break;
                    case 7:
                        running = false;
                        System.out.println("Thank you for using TeamMate!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Form Teams");
        System.out.println("2. Add New Player");
        System.out.println("3. View Teams");
        System.out.println("4. View Participants");
        System.out.println("5. Save Teams to CSV");
        System.out.println("6. Change Formation Strategy");
        System.out.println("7. Exit");
    }

    // Simple strategy selection using polymorphism
    private static void changeStrategy() {
        System.out.println("\n=== SELECT FORMATION STRATEGY ===");
        System.out.println("1. Balanced Strategy (One leader per team)");
        System.out.println("2. Random Strategy (Random distribution)");

        int choice = getIntInput("Choose strategy: ");

        // POLYMORPHISM: Same reference, different objects
        if (choice == 1) {
            teamStrategy = new BalancedTeamStrategy();
            System.out.println("✅ Using Balanced Team Strategy");
        } else if (choice == 2) {
            teamStrategy = new RandomTeamStrategy();
            System.out.println("✅ Using Random Team Strategy");
        } else {
            System.out.println("Invalid choice. Keeping current strategy.");
        }
    }

    private static void formTeams() {
        if (participants.isEmpty()) {
            System.out.println("No participants available. Please add players first.");
            return;
        }

        int teamSize = getIntInput("Enter team size: ");
        if (teamSize <= 0) {
            System.out.println("Invalid team size.");
            return;
        }

        // POLYMORPHIC METHOD CALL: Same method call, different behavior
        teams = teamStrategy.formTeams(participants, teamSize);

        System.out.println("\n=== FORMED TEAMS ===");
        for (Team team : teams) {
            System.out.println(team);
            System.out.println();
        }
    }

    // ... keep all your existing methods exactly as they were
    private static void loadParticipants() {
        try {
            participants = fileService.loadParticipantsFromCSV("participants_sample.csv");
            System.out.println("Successfully loaded " + participants.size() + " participants.");
        } catch (Exception e) {
            System.out.println("No existing participants file found. Starting with empty list.");
            participants = new ArrayList<>();
        }
    }

    private static void addNewPlayer() {
        // ... your existing addNewPlayer code
    }

    private static void viewTeams() {
        // ... your existing viewTeams code
    }

    private static void viewParticipants() {
        // ... your existing viewParticipants code
    }

    private static void saveTeams() {
        // ... your existing saveTeams code
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
}
}