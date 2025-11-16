package com.teamMate.Interfaces;

import com.teamMate.Participant;
import com.teamMate.Team;

import java.util.List;

public interface TeamFormationStrategy {
    List<Team> formTeams(List<Participant> participants, int teamSize);
}