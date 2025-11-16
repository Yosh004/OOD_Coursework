package com.teamMate.Strategies;

import com.teamMate.Participant;
import com.teamMate.Team;

import java.util.*;
import java.util.stream.Collectors;

public class BalancedTeamStrategy implements TeamFormationStrategy {

    @Override
    public List<Team> formTeams(List<Participant> participants, int teamSize) {
        List<Team> teams = new ArrayList<>();
        List<Participant> availableParticipants = new ArrayList<>(participants);

        int totalParticipants = availableParticipants.size();
        int numFullTeams = totalParticipants / teamSize;
        int leftoverParticipants = totalParticipants % teamSize;

        // Create teams
        for (int i = 0; i < numFullTeams; i++) {
            Team team = new Team(i + 1);
            team.setRequiredTeamSize(teamSize);
            teams.add(team);
        }

        // Distribute leaders first
        List<Participant> leaders = availableParticipants.stream()
                .filter(p -> "Leader".equals(p.getPersonalityType()))
                .collect(Collectors.toList());
        availableParticipants.removeAll(leaders);

        for (int i = 0; i < Math.min(leaders.size(), teams.size()); i++) {
            teams.get(i).addMember(leaders.get(i));
        }

        // Distribute remaining participants
        Collections.shuffle(availableParticipants);
        int teamIndex = 0;
        for (Participant participant : availableParticipants) {
            while (teamIndex < teams.size() && teams.get(teamIndex).isFull()) {
                teamIndex++;
            }
            if (teamIndex < teams.size()) {
                teams.get(teamIndex).addMember(participant);
            }
        }

        // Handle leftovers
        if (leftoverParticipants > 0) {
            Team leftoverTeam = new Team(numFullTeams + 1);
            leftoverTeam.setRequiredTeamSize(teamSize);
            List<Participant> unassigned = getUnassignedParticipants(teams, participants);
            for (int i = 0; i < Math.min(leftoverParticipants, unassigned.size()); i++) {
                leftoverTeam.addMember(unassigned.get(i));
            }
            teams.add(leftoverTeam);
        }

        return teams;
    }

    private List<Participant> getUnassignedParticipants(List<Team> teams, List<Participant> allParticipants) {
        Set<Participant> assigned = teams.stream()
                .flatMap(team -> team.getMembers().stream())
                .collect(Collectors.toSet());
        return allParticipants.stream()
                .filter(p -> !assigned.contains(p))
                .collect(Collectors.toList());
}
}