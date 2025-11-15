package com.teamMate;

import java.util.*;
import java.util.stream.Collectors;

public class TeamBuilder {
    private static final int MAX_SAME_GAME = 2;

    public List<Team> formBalancedTeams(List<Participant> participants, int teamSize) {
        if (participants.isEmpty()) {
            return new ArrayList<>();
        }

        List<Team> teams = new ArrayList<>();
        List<Participant> availableParticipants = new ArrayList<>(participants);

        // Calculate exact team distribution
        int totalParticipants = availableParticipants.size();
        int numFullTeams = totalParticipants / teamSize;      // Full teams
        int leftoverParticipants = totalParticipants % teamSize; // Leftover participants

        System.out.println("Participants: " + totalParticipants + ", Team Size: " + teamSize);
        System.out.println("Forming " + numFullTeams + " full teams + " + leftoverParticipants + " leftover participants");

        // Create only the exact number of full teams needed
        for (int i = 0; i < numFullTeams; i++) {
            Team team = new Team(i + 1);
            team.setRequiredTeamSize(teamSize);
            teams.add(team);
        }

        // Shuffle participants for random distribution
        Collections.shuffle(availableParticipants);

        // Distribute participants to fill teams to exact size
        int participantIndex = 0;

        // Keep distributing until all full teams are exactly filled
        while (participantIndex < availableParticipants.size()) {
            boolean placed = false;

            // Try to place in teams that still have space
            for (Team team : teams) {
                if (team.hasSpace() && participantIndex < availableParticipants.size()) {
                    Participant participant = availableParticipants.get(participantIndex);
                    if (team.addMember(participant)) {
                        participantIndex++;
                        placed = true;
                    }
                }
            }

            // If we couldn't place anyone in this round, break to avoid infinite loop
            if (!placed) {
                break;
            }
        }

        // Create leftover team if we have leftover participants
        if (leftoverParticipants > 0) {
            Team leftoverTeam = new Team(numFullTeams + 1);
            leftoverTeam.setRequiredTeamSize(teamSize); // Still set required size, but it will show as unbalanced

            // Add remaining participants to leftover team
            for (int i = participantIndex; i < availableParticipants.size(); i++) {
                leftoverTeam.addMember(availableParticipants.get(i));
            }

            teams.add(leftoverTeam);
            System.out.println("Created leftover team " + leftoverTeam.getTeamId() +
                    " with " + leftoverTeam.getSize() + " participants (needs " + teamSize + ")");
        }

        // Print formation summary
        printFormationSummary(teams, teamSize);

        return teams;
    }

    private void printFormationSummary(List<Team> teams, int requiredTeamSize) {
        System.out.println("\n=== TEAM FORMATION SUMMARY ===");
        int balancedTeams = 0;
        int unbalancedTeams = 0;

        for (Team team : teams) {
            if (team.isBalancedTeam()) {
                balancedTeams++;
            } else {
                unbalancedTeams++;
            }
        }

        System.out.println("Balanced teams (exactly " + requiredTeamSize + " members): " + balancedTeams);
        System.out.println("Unbalanced teams: " + unbalancedTeams);

        for (Team team : teams) {
            if (!team.isBalancedTeam()) {
                System.out.println("  - Team " + team.getTeamId() + ": " + team.getSize() + "/" + requiredTeamSize + " members");
            }
        }
    }

    // Method to ensure one leader per team
    public void enforceOneLeaderPerTeam(List<Team> teams) {
        for (Team team : teams) {
            if (team.isBalancedTeam()) {
                ensureSingleLeader(team);
            }
        }
    }

    private void ensureSingleLeader(Team team) {
        List<Participant> leaders = team.getMembers().stream()
                .filter(p -> "Leader".equals(p.getPersonalityType()))
                .collect(Collectors.toList());

        // If more than one leader, we'd need to swap with another team
        // For now, just log the situation
        if (leaders.size() > 1) {
            System.out.println("Team " + team.getTeamId() + " has " + leaders.size() + " leaders");
        } else if (leaders.isEmpty()) {
            System.out.println("Team " + team.getTeamId() + " has no leader");
        }
    }
}