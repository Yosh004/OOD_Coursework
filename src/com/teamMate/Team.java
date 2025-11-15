package com.teamMate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Team {
    private int teamId;
    private List<Participant> members;
    private double averageSkill;
    private Set<String> uniqueGames;
    private Set<String> uniqueRoles;
    private Set<String> personalityTypes;

    public Team(int teamId) {
        this.teamId = teamId;
        this.members = new ArrayList<>();
        this.uniqueGames = new HashSet<>();
        this.uniqueRoles = new HashSet<>();
        this.personalityTypes = new HashSet<>();
    }

    public boolean addMember(Participant participant) {
        if (members.add(participant)) {
            uniqueGames.add(participant.getPreferredGame());
            uniqueRoles.add(participant.getPreferredRole());
            personalityTypes.add(participant.getPersonalityType());
            calculateAverageSkill();
            return true;
        }
        return false;
    }

    private void calculateAverageSkill() {
        if (members.isEmpty()) {
            averageSkill = 0;
            return;
        }
        double total = 0;
        for (Participant member : members) {
            total += member.getSkillLevel();
        }
        averageSkill = total / members.size();
    }

    // Getters
    public int getTeamId() { return teamId; }
    public List<Participant> getMembers() { return new ArrayList<>(members); }
    public double getAverageSkill() { return averageSkill; }
    public Set<String> getUniqueGames() { return new HashSet<>(uniqueGames); }
    public Set<String> getUniqueRoles() { return new HashSet<>(uniqueRoles); }
    public Set<String> getPersonalityTypes() { return new HashSet<>(personalityTypes); }
    public int getSize() { return members.size(); }

    public boolean hasLeader() {
        return personalityTypes.contains("Leader");
    }

    public boolean hasThinker() {
        return personalityTypes.contains("Thinker");
    }

    public boolean hasBalanced() {
        return personalityTypes.contains("Balanced");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Team %d (Avg Skill: %.2f, Games: %d, Roles: %d, Personalities: %s)\n",
                teamId, averageSkill, uniqueGames.size(), uniqueRoles.size(), personalityTypes));

        for (Participant member : members) {
            sb.append("  - ").append(member.toString()).append("\n");
        }

        sb.append(String.format("  Team Balance: Leader: %s, Thinker: %s, Balanced: %s",
                hasLeader(), hasThinker(), hasBalanced()));

        return sb.toString();
    }
}