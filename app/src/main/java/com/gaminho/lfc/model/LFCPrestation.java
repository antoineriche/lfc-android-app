package com.gaminho.lfc.model;

/**
 * Created by Bonnie on 16/04/2022
 */
public interface LFCPrestation {

    Integer EDITION_SERVICE = 0;
    Integer SHOWCASE = 1;

    double getPrestationPrice();
    void setPrestationPrice(double price);

    String getPrestationName();
    String getPrestationDetail();
    int getPrestationType();

    default String getPrestationId() {
        return getPrestationName() + "@" + getPrestationType();
    }
}
