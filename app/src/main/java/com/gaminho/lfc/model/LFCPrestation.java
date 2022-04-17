package com.gaminho.lfc.model;

import com.google.firebase.database.Exclude;

/**
 * Created by Bonnie on 16/04/2022
 */
public interface LFCPrestation {

    Integer EDITION_SERVICE = 0;
    Integer SHOWCASE = 1;
    Integer JUDGE = 2;

    double getPrestationPrice();
    void setPrestationPrice(double price);

    String getPrestationName();
    String getPrestationDetail();
    int getPrestationType();

    @Exclude
    default String getPrestationId() {
        return getPrestationName() + "@" + getPrestationType();
    }
}
