package com.teamMate;

//Uses participation classification algorithm and the score from the survey score provided in the doc file

public class ClassificationService {
    public static String classifyPersonality(int score){
        if(score >= 90 && score <= 100){
            return "Leader";
        }
        else if(score >= 70 && score <= 89){
            return "Balanced";
        }
        else if(score >= 50 && score <= 69){
            return "Thinker";
        }
        else{
            return "Undefined";
        }
    }
}
