package com.teamMate;

public class ClassificationService {

    public static String classifyPersonality(int totalScore) {
        if (totalScore >= 90 && totalScore <= 100) {
            return "Leader";
        } else if (totalScore >= 70 && totalScore <= 89) {
            return "Balanced";
        } else if (totalScore >= 50 && totalScore <= 69) {
            return "Thinker";
        } else {
            throw new IllegalArgumentException("Invalid personality score: " + totalScore +
                    ". Score must be between 50-100.");
        }
    }

    public static int calculatePersonalityScore(int q1, int q2, int q3, int q4, int q5) {
        // Validate input scores
        validateQuestionScore(q1);
        validateQuestionScore(q2);
        validateQuestionScore(q3);
        validateQuestionScore(q4);
        validateQuestionScore(q5);

        int total = q1 + q2 + q3 + q4 + q5;
        return total * 4; // Scale to 100
    }

    private static void validateQuestionScore(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Question score must be between 1-5: " + score);
        }
    }

    public boolean isValidRole(String role) {
        return role != null && (
                role.equals("Strategist") ||
                        role.equals("Attacker") ||
                        role.equals("Defender") ||
                        role.equals("Supporter") ||
                        role.equals("Coordinator")
        );
    }

    public boolean isValidGame(String game) {
        return game != null && !game.trim().isEmpty();
    }
}