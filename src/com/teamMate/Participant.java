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

}
