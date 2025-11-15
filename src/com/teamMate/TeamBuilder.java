package com.teamMate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TeamBuilder {
    private static final int MAX_SAME_GAME = 2;
    private static final int MIN_DIFFERENT_ROLES = 3;

    public List<Team> formBalancedTeams(List<Participant> participants, int teamSize) {
        if (participants.isEmpty()) {
            return new ArrayList<>();
        }

        List<Team> teams = new ArrayList<>();

        // Create teams
        int numTeams = (int) Math.ceil((double) participants.size() / teamSize);
        for (int i = 0; i < numTeams; i++) {
            teams.add(new Team(i + 1));
        }

        // Separate by personality type for better distribution
        List<Participant> leaders = participants.stream()
                .filter(p -> "Leader".equals(p.getPersonalityType()))
                .sorted((p1, p2) -> Integer.compare(p2.getSkillLevel(), p1.getSkillLevel()))
                .collect(Collectors.toList());

        List<Participant> thinkers = participants.stream()
                .filter(p -> "Thinker".equals(p.getPersonalityType()))
                .sorted((p1, p2) -> Integer.compare(p2.getSkillLevel(), p1.getSkillLevel()))
                .collect(Collectors.toList());

        List<Participant> balanced = participants.stream()
                .filter(p -> "Balanced".equals(p.getPersonalityType()))
                .sorted((p1, p2) -> Integer.compare(p2.getSkillLevel(), p1.getSkillLevel()))
                .collect(Collectors.toList());

        // Phase 1: Distribute one leader to each team
        distributeByPersonality(teams, leaders, 1, teamSize);

        // Phase 2: Distribute thinkers (1-2 per team)
        distributeByPersonality(teams, thinkers, 2, teamSize);

        // Phase 3: Distribute balanced participants to fill remaining spots
        distributeByPersonality(teams, balanced, teamSize, teamSize);

        return teams;
    }

    private void distributeByPersonality(List<Team> teams, List<Participant> participants,
                                         int maxPerTeam, int teamSize) {
        if (participants.isEmpty()) return;

        int participantIndex = 0;
        boolean addedParticipant;

        do {
            addedParticipant = false;

            for (Team team : teams) {
                if (team.getSize() >= teamSize) continue; // Team is full
                if (participantIndex >= participants.size()) break; // No more participants

                Participant participant = participants.get(participantIndex);

                // Check if team can accept this personality type
                if (canAcceptPersonality(participant, team, maxPerTeam) &&
                        isTeamBalancedWithNewMember(participant, team)) {

                    if (team.addMember(participant)) {
                        participantIndex++;
                        addedParticipant = true;
                    }
                }
            }

        } while (addedParticipant && participantIndex < participants.size());

        // If we still have participants and teams with space, try less strict matching
        if (participantIndex < participants.size()) {
            for (int i = participantIndex; i < participants.size(); i++) {
                Participant participant = participants.get(i);

                for (Team team : teams) {
                    if (team.getSize() >= teamSize) continue;

                    if (canAcceptPersonality(participant, team, maxPerTeam) &&
                            team.addMember(participant)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean canAcceptPersonality(Participant participant, Team team, int maxPerTeam) {
        String personality = participant.getPersonalityType();
        long currentCount = team.getMembers().stream()
                .filter(p -> personality.equals(p.getPersonalityType()))
                .count();

        return currentCount < maxPerTeam;
    }

    private boolean isTeamBalancedWithNewMember(Participant participant, Team team) {
        // Check game diversity
        long sameGameCount = team.getMembers().stream()
                .filter(m -> m.getPreferredGame().equals(participant.getPreferredGame()))
                .count();
        if (sameGameCount >= MAX_SAME_GAME) {
            return false;
        }

        // Check role diversity (but be flexible for small teams)
        Set<String> potentialRoles = new HashSet<>(team.getUniqueRoles());
        potentialRoles.add(participant.getPreferredRole());

        int currentSize = team.getSize();
        if (currentSize >= MIN_DIFFERENT_ROLES - 1 && potentialRoles.size() < MIN_DIFFERENT_ROLES) {
            return false;
        }

        return true;
    }

    // Method to add a single participant to existing teams (for new player addition)
    public boolean addParticipantToTeams(Participant newParticipant, List<Team> teams, int teamSize) {
        if (teams.isEmpty()) return false;

        // Try to find the best team for this participant
        for (Team team : teams) {
            if (team.getSize() >= teamSize) continue; // Team is full

            if (canAcceptPersonality(newParticipant, team, getMaxForPersonality(newParticipant.getPersonalityType())) &&
                    isTeamBalancedWithNewMember(newParticipant, team)) {

                return team.addMember(newParticipant);
            }
        }

        // If no ideal team found, try any team with space
        for (Team team : teams) {
            if (team.getSize() < teamSize && team.addMember(newParticipant)) {
                return true;
            }
        }

        return false;
    }

    private int getMaxForPersonality(String personalityType) {
        switch (personalityType) {
            case "Leader": return 1;
            case "Thinker": return 2;
            case "Balanced": return Integer.MAX_VALUE;
            default: return 1;
        }
    }
}