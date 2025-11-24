package com.teamMate;

import com.teamMate.Strategies.BalancedTeamStrategy;
import com.teamMate.Strategies.RandomTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();
    private static FileService fileService = new FileService();
    private static Scanner scanner = new Scanner(System.in);

    // Polymorphic reference
    private static TeamFormationStrategy teamStrategy = new BalancedTeamStrategy();

    public static void main(String[] args) {
        System.out.println("=== TeamMate: Intelligent Team Formation System ===");
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
        System.out.println("Current Strategy: " + (teamStrategy instanceof BalancedTeamStrategy ? "Balanced" : "Random"));
    }

    private static void changeStrategy() {
        System.out.println("\n=== SELECT FORMATION STRATEGY ===");
        System.out.println("1. Balanced Strategy (One leader per team)");
        System.out.println("2. Random Strategy (Random distribution)");

        int choice = getIntInput("Choose strategy: ");

        if (choice == 1) {
            teamStrategy = new BalancedTeamStrategy();
            System.out.println("------ Using Balanced Team Strategy------");
        } else if (choice == 2) {
            teamStrategy = new RandomTeamStrategy();
            System.out.println("------Using Random Team Strategy--------");
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

        // POLYMORPHIC METHOD CALL
        teams = teamStrategy.formTeams(participants, teamSize);

        System.out.println("\n=== FORMED TEAMS ===");
        for (Team team : teams) {
            System.out.println(team);
            System.out.println();
        }
    }

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
        System.out.println("\n=== ADD NEW PLAYER ===");

        // Generate new ID
        String newId = "P" + String.format("%03d", participants.size() + 1);

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.println("----------Available games--------");
        System.out.println("1-Chess");
        System.out.println("2-FIFA");
        System.out.println("3-Basketball");
        System.out.println("4-CS:GO");
        System.out.println("5-DOTA 2");
        System.out.println("6-Valorant");
        System.out.print("Enter the number of the preferred game: ");
        String game = scanner.nextLine();
        if (game.equals("1")) {
            game="Chess";
        }
        else if (game.equals("2")) {
            game="FIFA";
        }
        else if (game.equals("3")) {
            game="Basketball";
        }
        else if (game.equals("4")) {
            game="CS:GO";
        }
        else if (game.equals("5")) {
            game="DOTA 2";
        }
        else if (game.equals("6")) {
            game="Valorant";
        }
        else{
            System.out.println("Invalid input enter a value from 1 to 6.");
        }

        int skillLevel = getIntInput("Enter skill level (1-10): ");
        if (skillLevel < 1 || skillLevel > 10) {
            System.out.println("Skill level must be between 1-10.");
            return;
        }

        System.out.println("-----Available roles-----");
        System.out.println("1-Strategist");
        System.out.println("2-Attacker");
        System.out.println("3-Defender");
        System.out.println("4-Supporter");
        System.out.println("5-Coordinator");
        System.out.print("Enter the number of the preferred role: ");
        String role = scanner.nextLine();
        if (role.equals("1")) {
            role="Strategist";
        }
        else if (role.equals("2")) {
            role="Attacker";
        }
        else if (role.equals("3")) {
            role="Defender";
        }
        else if (role.equals("4")) {
            role="Supporter";
        }
        else if (role.equals("5")) {
            role="Coordinator";
        }
        else{
            System.out.println("Invalid input enter a value from 1 to 6.");
        }

        // Personality survey
        System.out.println("\n=== PERSONALITY SURVEY ===");
        System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)");

        int q1 = getIntInput("I enjoy taking the lead and guiding others during group activities: ");
        int q2 = getIntInput("I prefer analyzing situations and coming up with strategic solutions: ");
        int q3 = getIntInput("I work well with others and enjoy collaborative teamwork: ");
        int q4 = getIntInput("I am calm under pressure and can help maintain team morale: ");
        int q5 = getIntInput("I like making quick decisions and adapting in dynamic situations: ");

        try {
            // Calculate personality score and type
            int personalityScore = ClassificationService.calculatePersonalityScore(q1, q2, q3, q4, q5);
            String personalityType = ClassificationService.classifyPersonality(personalityScore);

            // Create new participant
            Participant newParticipant = new Participant(newId, name, email, game, skillLevel, role, personalityScore, personalityType);

            // Add to participants list
            participants.add(newParticipant);

            System.out.println("New player added successfully!");
            System.out.println("Player ID: " + newId);
            System.out.println("Preferred Game: "+game);
            System.out.println("Preferred Role: "+role);
            System.out.println("Personality Type: " + personalityType);
            System.out.println("Personality Score: " + personalityScore);

        } catch (Exception e) {
            System.out.println("Error adding player: " + e.getMessage());
        }
    }

    private static void viewTeams() {
        if (teams.isEmpty()) {
            System.out.println("No teams formed yet. Please form teams first.");
            return;
        }

        System.out.println("\n=== CURRENT TEAMS ===");
        for (Team team : teams) {
            System.out.println(team);
            System.out.println();
        }
    }

    private static void viewParticipants() {
        System.out.println("\n=== ALL PARTICIPANTS ===");
        System.out.println("Total participants: " + participants.size());

        // Group by personality type for summary
        Map<String, Long> personalityCounts = participants.stream()
                .collect(Collectors.groupingBy(Participant::getPersonalityType, Collectors.counting()));

        System.out.println("Personality Distribution: " + personalityCounts);

        for (Participant participant : participants) {
            System.out.println(participant);
        }
    }

    private static void saveTeams() {
        if (teams.isEmpty()) {
            System.out.println("No teams to save. Please form teams first.");
            return;
        }

        try {
            fileService.saveTeamsToCSV(teams, "formed_teams.csv");
            System.out.println("✅ Teams successfully saved to formed_teams.csv");
        } catch (Exception e) {
            System.out.println("Error saving teams: " + e.getMessage());
        }
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