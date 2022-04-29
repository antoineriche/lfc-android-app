package com.gaminho.lfc.model;

/**
 * Created by Bonnie on 29/04/2022
 */
public interface ProgramEvent {

    int BATTLE = 0;
    int SHOWCASE = 1;
    int BREAK = 2;
    int OPEN_MIC = 3;

    int getType();
    int getDuration();
    long getStartTime();
    void setStartTime(long startTime);
}
