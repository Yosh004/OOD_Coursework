package com.teamMate.Classes;

import java.util.*;
import java.util.stream.Collectors;

public class Team {
    private int teamId;
    private List<Participant> members;
    private double averageSkill;
    private Set<String> uniqueGames;
    private Set<String> uniqueRoles;
    private Set<String> personalityTypes;
    private int requiredTeamSize;

    public Team(int teamId) {
        this.teamId = teamId;
        this.members = new ArrayList<>();
        this.uniqueGames = new HashSet<>();
        this.uniqueRoles = new HashSet<>();
        this.personalityTypes = new HashSet<>();
        this.requiredTeamSize = 0; // Will be set later
    }

    public void setRequiredTeamSize(int teamSize) {
        this.requiredTeamSize = teamSize;
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

    //  Balanced = has exact required team size
    public boolean isBalancedTeam() {
        return members.size() == requiredTeamSize;
    }

    public boolean isFull() {
        return members.size() >= requiredTeamSize;
    }

    public boolean hasSpace() {
        return members.size() < requiredTeamSize;
    }

    public int getRemainingSpace() {
        return requiredTeamSize - members.size();
    }

    public String getBalanceStatus() {
        if (members.size() == requiredTeamSize) {
            return " BALANCED (" + members.size() + "/" + requiredTeamSize + " members)";
        } else if (members.size() > requiredTeamSize) {
            return " OVERFILLED (" + members.size() + "/" + requiredTeamSize + " members)";
        } else {
            return " UNBALANCED (" + members.size() + "/" + requiredTeamSize + " members)";
        }
    }

    // Getters
    public int getTeamId() { return teamId; }
    public List<Participant> getMembers() { return new ArrayList<>(members); }
    public double getAverageSkill() { return averageSkill; }
    public Set<String> getUniqueGames() { return new HashSet<>(uniqueGames); }
    public Set<String> getUniqueRoles() { return new HashSet<>(uniqueRoles); }
    public Set<String> getPersonalityTypes() { return new HashSet<>(personalityTypes); }
    public int getSize() { return members.size(); }
    public int getRequiredTeamSize() { return requiredTeamSize; }

    // Get detailed personality counts
    public Map<String, Long> getPersonalityCounts() {
        return members.stream()
                .collect(Collectors.groupingBy(Participant::getPersonalityType, Collectors.counting()));
    }

    // Get detailed game counts
    public Map<String, Long> getGameCounts() {
        return members.stream()
                .collect(Collectors.groupingBy(Participant::getPreferredGame, Collectors.counting()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<String, Long> personalityCounts = getPersonalityCounts();
        Map<String, Long> gameCounts = getGameCounts();

        sb.append(String.format("Team %d - %s\n", teamId, getBalanceStatus()));
        sb.append(String.format("  Avg Skill: %.2f, Personalities: %s\n", averageSkill, personalityCounts));
        sb.append(String.format("  Roles: %s, Games: %s\n", uniqueRoles, gameCounts));

        for (Participant member : members) {
            sb.append("  - ").append(member.toString()).append("\n");
        }

        return sb.toString();
    }
}