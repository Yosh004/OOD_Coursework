package com.teamMate;

import java.util.*;
import java.util.concurrent.*;

public class TeamBuilder {
    private static final int MAX_SAME_GAME = 2;
    private static final int MIN_DIFFERENT_ROLES = 3;

    public List<Team> formBalancedTeams(List<Participant> participants, int teamSize) {
        List<Team> teams = new ArrayList<>();

        // Create teams
        int numTeams = (int) Math.ceil((double) participants.size() / teamSize);
        for (int i = 0; i < numTeams; i++) {
            teams.add(new Team(i + 1));
        }

        // Sort participants by skill level (descending) for better distribution
        List<Participant> sortedParticipants = new ArrayList<>(participants);
        sortedParticipants.sort((p1, p2) -> Integer.compare(p2.getSkillLevel(), p1.getSkillLevel()));

        // Use ExecutorService for concurrent team formation
        ExecutorService executor = Executors.newFixedThreadPool(teams.size());
        List<Future<Boolean>> futures = new ArrayList<>();

        // Distribute participants to teams with balancing logic
        int teamIndex = 0;
        for (Participant participant : sortedParticipants) {
            final Participant p = participant;
            final int currentTeamIndex = teamIndex;

            Future<Boolean> future = executor.submit(() -> {
                Team team = teams.get(currentTeamIndex);
                return addParticipantToTeam(p, team);
            });

            futures.add(future);
            teamIndex = (teamIndex + 1) % teams.size();
        }

        // Wait for all tasks to complete
        for (Future<Boolean> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error in team formation: " + e.getMessage());
            }
        }

        executor.shutdown();

        return teams;
    }

    private boolean addParticipantToTeam(Participant participant, Team team) {
        // Check if team can accept this participant based on balancing rules
        if (isTeamBalancedWithNewMember(participant, team)) {
            return team.addMember(participant);
        }
        return false;
    }

    private boolean isTeamBalancedWithNewMember(Participant participant, Team team) {
        // Check game diversity
        long sameGameCount = team.getMembers().stream()
                .filter(m -> m.getPreferredGame().equals(participant.getPreferredGame()))
                .count();
        if (sameGameCount >= MAX_SAME_GAME) {
            return false;
        }

        // Check if adding this member maintains role diversity
        Set<String> potentialRoles = new HashSet<>(team.getUniqueRoles());
        potentialRoles.add(participant.getPreferredRole());
        if (potentialRoles.size() < Math.min(MIN_DIFFERENT_ROLES, team.getSize() + 1)) {
            return false;
        }

        // Check personality balance
        return isPersonalityBalanceMaintained(participant, team);
    }

    private boolean isPersonalityBalanceMaintained(Participant participant, Team team) {
        String newPersonality = participant.getPersonalityType();
        Map<String, Long> personalityCounts = new HashMap<>();

        // Count current personalities
        for (Participant member : team.getMembers()) {
            String personality = member.getPersonalityType();
            personalityCounts.put(personality, personalityCounts.getOrDefault(personality, 0L) + 1);
        }

        // Add new personality
        personalityCounts.put(newPersonality, personalityCounts.getOrDefault(newPersonality, 0L) + 1);

        // Check balance rules
        long leaders = personalityCounts.getOrDefault("Leader", 0L);
        long thinkers = personalityCounts.getOrDefault("Thinker", 0L);
        long balanced = personalityCounts.getOrDefault("Balanced", 0L);

        // Ideal: 1 Leader, 1-2 Thinkers, rest Balanced
        if (leaders > 1) return false;
        if (thinkers > 2) return false;

        return true;
    }
}