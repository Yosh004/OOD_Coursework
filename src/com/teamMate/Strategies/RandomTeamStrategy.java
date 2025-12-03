package com.teamMate.Strategies;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;

import java.util.*;

public class RandomTeamStrategy implements TeamFormationStrategy {

//random assignments no leader priority
    @Override
    public List<Team> formTeams(List<Participant> participants, int teamSize) {
        List<Team> teams = new ArrayList<>();
        List<Participant> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled);

        int totalParticipants = shuffled.size();
        int numFullTeams = totalParticipants / teamSize;
        int leftoverParticipants = totalParticipants % teamSize;

        // Create teams
        for (int i = 0; i < numFullTeams; i++) {
            Team team = new Team(i + 1);
            team.setRequiredTeamSize(teamSize);
            teams.add(team);
        }

        // Simple distribution
        int index = 0;
        for (Team team : teams) {
            while (team.hasSpace() && index < shuffled.size()) {
                team.addMember(shuffled.get(index));
                index++;
            }
        }

        // Handle leftovers
        if (leftoverParticipants > 0) {
            Team leftoverTeam = new Team(numFullTeams + 1);
            leftoverTeam.setRequiredTeamSize(teamSize);
            for (int i = index; i < shuffled.size(); i++) {
                leftoverTeam.addMember(shuffled.get(i));
            }
            teams.add(leftoverTeam);
        }

        return teams;
}
}