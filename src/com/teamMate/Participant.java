package com.teamMate;

public class Participant {
    private String studentID;
    private String name;
    private int personalityScore;
    private String personalityType; //leader etc.
    private String preferredGame;
    private String preferredRole; //defender etc.

    //create new participant constructor
    public Participant(String studentID,String name,int personalityScore,String personalityType,String preferredGame,String preferredRole) {
        this.studentID=studentID;
        this.name=name;
        this.personalityScore=personalityScore;
        this.personalityType=personalityType;
        this.preferredGame=preferredGame;
        this.preferredRole=preferredRole;

    }
    //getters for other classes to read this data
    public String getStudentID() {
        return studentID;
    }
    public String getName() {
        return name;
    }
    public int getPersonalityScore() {
        return personalityScore;
    }
    public String getPersonalityType() {
        return personalityType;
    }
    public String getPreferredGame() {
        return preferredGame;
    }
    public String getPreferredRole() {
        return preferredRole;
    }

    //replace the meaningless output into a human-readable format better in debugging
    @Override
    public String toString() {
        return "Participant [studentID=" + studentID + ", name=" + name + ", personalityScore="+personalityScore + ", personalityType="+personalityType + ", preferredGame="+preferredGame + ", preferredRole="+preferredRole + "]";
    }
    //kkk
}
