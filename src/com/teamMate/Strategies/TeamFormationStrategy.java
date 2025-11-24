package com.teamMate.Strategies;

import com.teamMate.Classes.Participant;
import com.teamMate.Classes.Team;

import java.util.List;

public interface TeamFormationStrategy {
    List<Team> formTeams(List<Participant> participants, int teamSize);
}