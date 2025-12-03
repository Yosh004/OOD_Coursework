package com.teamMate.Classes;

import com.teamMate.Strategies.BalancedTeamStrategy;
import com.teamMate.Strategies.RandomTeamStrategy;
import com.teamMate.Strategies.TeamFormationStrategy;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    //making lists to store participants and team formed
    private static List<Participant> participants = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();
    private static FileService fileService = new FileService();
    private static Scanner scanner = new Scanner(System.in);

    // Polymorphic reference
    private static TeamFormationStrategy teamStrategy = new BalancedTeamStrategy();

    // Thread pool for concurrent tasks
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        System.out.println("=== TeamMate: Intelligent Team Formation System ===");
        try {
            loadParticipants();

            boolean running = true;
            while (running) {
                displayMenu();
                int choice = getIntInput("Enter your choice: ");
                switch (choice) {
                    case 1 -> formTeams();
                    case 2 -> addNewPlayer();
                    case 3 -> viewTeams();
                    case 4 -> viewParticipants();
                    case 5 -> saveTeams();
                    case 6 -> {
                        running = false;
                        System.out.println("Thank you for using TeamMate!");
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executorService.shutdown(); //close thread pool
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
        System.out.println("6. Exit");
    }

    private static void changeStrategy() {
        System.out.println("\n=== SELECT FORMATION STRATEGY ===");
        System.out.println("1. Balanced Strategy (One leader per team)");
        System.out.println("2. Random Strategy (Random distribution)");
        int choice = getIntInput("Choose strategy: ");
        if (choice == 1) {
            teamStrategy = new BalancedTeamStrategy();
            System.out.println("------ Using Balanced Team Strategy ------");
        } else if (choice == 2) {
            teamStrategy = new RandomTeamStrategy();
            System.out.println("------ Using Random Team Strategy ------");
        } else {
            System.out.println("Invalid choice. Going with Balanced Strategy.");
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
        changeStrategy();

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

        String newId = "P" + String.format("%03d", participants.size() + 1);

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        if (name.equals("")) {
            System.out.println("Invalid name.");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        if (email.equals("")) {
            System.out.println("Invalid email.");
            return;
        }

        String game = selectGame();
        int skillLevel = getValidatedSkillLevel();
        String role = selectRole();

        // Personality Survey
        System.out.println("\n=== PERSONALITY SURVEY ===");
        System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

        final int q1 = getValidatedSurveyInput(
                "I enjoy taking the lead and guiding others during group activities: ");
        final int q2 = getValidatedSurveyInput(
                "I prefer analyzing situations and coming up with strategic solutions: ");
        final int q3 = getValidatedSurveyInput(
                "I work well with others and enjoy collaborative teamwork: ");
        final int q4 = getValidatedSurveyInput(
                "I am calm under pressure and can help maintain team morale: ");
        final int q5 = getValidatedSurveyInput(
                "I like making quick decisions and adapting in dynamic situations: ");

        try {
            CompletableFuture<Integer> scoreFuture = CompletableFuture.supplyAsync(
                    () -> ClassificationService.calculatePersonalityScore(q1, q2, q3, q4, q5),
                    executorService
            );

            int personalityScore = scoreFuture.get();
            String personalityType = ClassificationService.classifyPersonality(personalityScore);

            Participant newParticipant = new Participant(
                    newId, name, email, game, skillLevel, role, personalityScore, personalityType
            );

            participants.add(newParticipant);

            System.out.println("\nNew player added successfully!");
            System.out.println("Player ID: " + newId);
            System.out.println("Name: " + name);
            System.out.println("Preferred Game: " + game);
            System.out.println("Preferred Role: " + role);
            System.out.println("Personality Type: " + personalityType);
            System.out.println("Personality Score: " + personalityScore + "\n");

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("Survey processing was interrupted.");
        } catch (ExecutionException ee) {
            System.out.println("Error calculating personality score: " + ee.getCause().getMessage());
        } catch (Exception e) {
            System.out.println("Error adding player: " + e.getMessage());
        }
    }

    private static String selectGame() {
        while (true) {
            System.out.println("---------- Available games --------");
            System.out.println("1 - Chess");
            System.out.println("2 - FIFA");
            System.out.println("3 - Basketball");
            System.out.println("4 - CS:GO");
            System.out.println("5 - DOTA 2");
            System.out.println("6 - Valorant");
            System.out.print("Enter the number of the preferred game: ");
            String input = scanner.nextLine();
            return switch (input) {
                case "1" -> "Chess";
                case "2" -> "FIFA";
                case "3" -> "Basketball";
                case "4" -> "CS:GO";
                case "5" -> "DOTA 2";
                case "6" -> "Valorant";

                default -> {
                    System.out.println("Invalid input! Automatically assigning to Chess");
                    yield "Chess";
                }
            };
        }
    }

    private static int getValidatedSkillLevel() {
        while (true) {
            int level = getIntInput("Enter skill level (1-10): ");
            if (level >= 1 && level <= 10) {
                return level;
            }
            System.out.println("Invalid skill level! Please enter a number between 1 and 10.");
        }
    }
//team role selection
    private static String selectRole() {
        while (true) {
            System.out.println("----- Available roles -----");
            System.out.println("1 - Strategist");
            System.out.println("2 - Attacker");
            System.out.println("3 - Defender");
            System.out.println("4 - Supporter");
            System.out.println("5 - Coordinator");
            System.out.print("Enter the number of the preferred role: ");
            String input = scanner.nextLine();
            return switch (input) {
                case "1" -> "Strategist";
                case "2" -> "Attacker";
                case "3" -> "Defender";
                case "4" -> "Supporter";
                case "5" -> "Coordinator";
                default -> {
                    System.out.println("Invalid input! Automatically assigning to Strategist");
                    yield "Strategist";
                }
            };
        }
    }

    // Helper for 1–5 survey input
    private static int getValidatedSurveyInput(String prompt) {
        while (true) {
            int value = getIntInput(prompt);
            if (value >= 1 && value <= 5) {
                return value;
            }
            System.out.println("Please enter a number between 1 and 5.");
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

        Map<String, Long> personalityCounts = participants.stream()
                .collect(Collectors.groupingBy(Participant::getPersonalityType, Collectors.counting()));
        System.out.println("Personality Distribution: " + personalityCounts + "\n");

        for (Participant p : participants) {
            System.out.println(p);
        }
    }

    private static void saveTeams() {
        if (teams.isEmpty()) {
            System.out.println("No teams to save. Please form teams first.");
            return;
        }
        try {
            fileService.saveTeamsToCSV(teams, "formed_teams.csv");
            System.out.println("Teams successfully saved to formed_teams.csv");
        } catch (Exception e) {
            System.out.println("Error saving teams: " + e.getMessage());
        }
    }
    // Helper method to read and validate numeric input
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}