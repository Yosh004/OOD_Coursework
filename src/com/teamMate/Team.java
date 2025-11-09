package com.teamMate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Team {
    private List<Participant> members;
    private int teamID;

    private static int nextID = 1; //Static counter for unique team ids

    public Team(){
        this.members = new ArrayList<Participant>();
        this.teamID = nextID++;
    }
    public void addMember(Participant p){
        this.members.add(p);
    }
    public List<Participant> getMembers(){
        return this.members;
    }
    public int getTeamID(){
        return this.teamID;
    }
    public int getSize(){
        return members.size();
    }


    public boolean hasRole(String role) {
        for (Participant member : members) {
            if (member.getPreferredRole().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        String memberNames = members.stream()
                .map(Participant::getName)
                .collect(Collectors.joining(", "));
        return "Team #" + teamID + ": [" + memberNames + "]";

    }


}
