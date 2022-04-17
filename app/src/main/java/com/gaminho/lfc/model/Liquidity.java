package com.gaminho.lfc.model;

import com.gaminho.lfc.service.DatabaseEntity;
import com.gaminho.lfc.utils.DateParser;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 17/04/2022
 */
@Getter
@Setter
public final class Liquidity implements DatabaseEntity {

    private long date;
    private double bankAccount;
    private int billets500;
    private int billets200;
    private int billets100;
    private int billets50;
    private int billets20;
    private int billets10;
    private int billets5;
    private int coins2;
    private int coins1;
    private int coins05;
    private int coins02;
    private int coins01;

    public double getTotal() {
        return bankAccount
                + 500 * billets500 + 200 * billets200 + 100 * billets100
                + 50 * billets50 + 20 * billets20 + 10 * billets10 + 5 * billets5
                + 2 * coins2 + coins1 + .5 * coins05
                + .2 * coins02 + .1 * coins01;
    }

    @Override
    public String buildId() {
        return DateParser.formatEpochDay(date).replace("/", "-");
    }
}
