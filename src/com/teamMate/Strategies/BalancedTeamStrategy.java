package com.teamMate.Strategies;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;

import java.util.*;
import java.util.stream.Collectors;
// strickly one and only leader per group
public class BalancedTeamStrategy implements TeamFormationStrategy {

    @Override
    public List<Team> formTeams(List<Participant> participants, int teamSize) {
        if (participants == null || participants.isEmpty() || teamSize < 2) {
            return new ArrayList<>();
        }

        // Work on a copy to avoid mutating the original list
        List<Participant> availableParticipants = new ArrayList<>(participants);

        // hold ALL teams to be returned (main teams + solo teams)
        List<Team> allTeams = new ArrayList<>();

        // ONLY hold teams that should receive non-leader members
        List<Team> teamsToFill = new ArrayList<>();

        // 1. Collect and sort leaders (single-threaded: NO parallel here)
        List<Participant> allLeaders = availableParticipants.stream()
                .filter(p -> {
                    String type = p.getPersonalityType();
                    return type != null && type.trim().equalsIgnoreCase("leader");
                })
                .sorted((p1, p2) -> Integer.compare(p2.getPersonalityScore(), p1.getPersonalityScore()))
                .collect(Collectors.toList());

        availableParticipants.removeAll(allLeaders);

        int totalParticipants = participants.size();
        int numLeaders = allLeaders.size();

        // 2. Calculate required teams
        int requiredNumTeams = (int) Math.ceil((double) totalParticipants / teamSize);

        // can only form "valid" main teams up to the number of available leaders
        int mainTeamsToForm = Math.min(requiredNumTeams, numLeaders);

        List<Participant> assignedLeaders = new ArrayList<>();

        // 3. Create MAIN TEAMS
        for (int i = 0; i < mainTeamsToForm; i++) {
            Team team = new Team(i + 1);
            team.setRequiredTeamSize(teamSize);

            Participant leader = allLeaders.get(i);
            team.addMember(leader);
            assignedLeaders.add(leader);

            allTeams.add(team);
            teamsToFill.add(team); // Add to the list for distribution
        }

        // 4. Create SOLO LEADER TEAMS for unbalance teams
        List<Participant> unassignedLeaders = new ArrayList<>(allLeaders);
        unassignedLeaders.removeAll(assignedLeaders);

        for (Participant leader : unassignedLeaders) {
            Team soloTeam = new Team(allTeams.size() + 1);
            soloTeam.setRequiredTeamSize(teamSize);
            soloTeam.addMember(leader);
            allTeams.add(soloTeam);
            // they should stay solo
        }

        // 5. Phase 1: distribute non-leaders ONLY to teamsToFill
        distributeWithConstraints(teamsToFill, availableParticipants, teamSize);

        // 6. Phase 2: fill remaining spots ONLY in teamsToFill
        fillRemainingSpots(teamsToFill, availableParticipants, teamSize);

        return allTeams;
    }

    private void distributeWithConstraints(List<Team> teams, List<Participant> available, int teamSize) {
        // Randomize order to avoid bias
        Collections.shuffle(available);

        // Iterate over a copy to safely remove from original list while iterating
        for (Participant participant : new ArrayList<>(available)) {
            Team bestTeam = findBestTeamForParticipant(teams, participant, teamSize);
            if (bestTeam != null && bestTeam.hasSpace()) {
                bestTeam.addMember(participant);
                available.remove(participant); // Remove once assigned ⇒ avoids duplicates
            }
        }
    }

    private Team findBestTeamForParticipant(List<Team> teams, Participant participant, int teamSize) {
        Team bestTeam = null;
        int bestScore = Integer.MIN_VALUE;

        for (Team team : teams) {
            if (!team.hasSpace()) continue;

            // somehow a leader added new avoid creating double-leader teams
            if (isLeader(participant)) {
                boolean hasLeader = team.getMembers().stream().anyMatch(this::isLeader);
                if (hasLeader) continue;
            }

            int teamScore = calculateTeamFitScore(team, participant, teamSize);
            if (teamScore > bestScore) {
                bestScore = teamScore;
                bestTeam = team;
            }
        }

        return bestTeam;
    }

    private boolean isLeader(Participant p) {
        String type = p.getPersonalityType();
        return type != null && type.trim().equalsIgnoreCase("leader");
    }

    private int calculateTeamFitScore(Team team, Participant participant, int teamSize) {
        int score = 0;

        // 1. Game Variety
        long sameGameCount = team.getMembers().stream()
                .filter(m -> Objects.equals(m.getPreferredGame(), participant.getPreferredGame()))
                .count();
        if (sameGameCount >= 2) {
            score -= 20;
        } else if (sameGameCount == 1) {
            score -= 5;
        } else {
            score += 10;
        }

        // 2. Role Diversity
        Set<String> existingRoles = team.getUniqueRoles();
        if (!existingRoles.contains(participant.getPreferredRole())) {
            score += 15;
            int minRolesRequired = Math.min(3, teamSize);
            if (existingRoles.size() < minRolesRequired) {
                score += 10;
            }
        }

        // 3. Personality Mix (Thinker / Balanced)
        Map<String, Long> personalityCounts = team.getPersonalityCounts();
        long thinkerCount = personalityCounts.getOrDefault("Thinker", 0L);
        long balancedCount = personalityCounts.getOrDefault("Balanced", 0L);

        switch (participant.getPersonalityType()) {
            case "Thinker":
                if (thinkerCount < 2) score += 25;
                else score -= 15;
                break;
            case "Balanced":
                if (balancedCount < (teamSize - 2)) score += 10;
                break;
            default:
                // Leaders handled earlier
                break;
        }

        // 4. Skill Balance
        double currentAvgSkill = team.getAverageSkill();
        if (currentAvgSkill < 5.0) {
            score += participant.getSkillLevel() * 2;
        } else if (currentAvgSkill > 7.0) {
            score += (10 - participant.getSkillLevel()) * 2;
        } else {
            score += 5;
        }

        return score;
    }

    private void fillRemainingSpots(List<Team> teams, List<Participant> available, int teamSize) {
        // Stronger skill balancing: assign highest skill first to weakest teams
        available.sort(Comparator.comparingInt(Participant::getSkillLevel).reversed());

        for (Participant participant : new ArrayList<>(available)) {
            if (isLeader(participant)) {
                continue; // safety guard
            }

            Team bestTeam = null;
            double lowestAvgSkill = Double.MAX_VALUE;

            for (Team team : teams) {
                if (team.isFull()) continue;
                double avgSkill = team.getAverageSkill();
                if (avgSkill < lowestAvgSkill) {
                    lowestAvgSkill = avgSkill;
                    bestTeam = team;
                }
            }

            if (bestTeam != null) {
                bestTeam.addMember(participant);
                available.remove(participant); // Remove once assigned ⇒ still no duplicates
            }
        }
    }
}
