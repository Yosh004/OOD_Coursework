package com.teamMate.Strategies;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;

import java.util.List;
//interface shows the strategy patterns for the team formation system
public interface TeamFormationStrategy {
    List<Team> formTeams(List<Participant> participants, int teamSize);
}