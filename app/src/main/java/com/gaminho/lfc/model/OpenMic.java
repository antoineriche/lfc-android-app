package com.gaminho.lfc.model;

import com.google.firebase.database.IgnoreExtraProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 29/04/2022
 */
@Getter
@Setter
@IgnoreExtraProperties
public class OpenMic implements ProgramEvent {

    private int duration;
    private long startTime;

    @Override
    public int getType() {
        return ProgramEvent.OPEN_MIC;
    }

}
