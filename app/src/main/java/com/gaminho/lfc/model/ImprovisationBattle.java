package com.gaminho.lfc.model;

import com.gaminho.lfc.model.enumeration.BattlePhase;
import com.google.firebase.database.IgnoreExtraProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 29/04/2022
 */
@Getter
@Setter
@IgnoreExtraProperties
public class ImprovisationBattle implements ProgramEvent {

    private BattlePhase phase;
    private int duration;
    private long startTime;

    @Override
    public int getType() {
        return ProgramEvent.BATTLE;
    }
}
