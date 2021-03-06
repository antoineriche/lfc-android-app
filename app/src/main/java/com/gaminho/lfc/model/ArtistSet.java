package com.gaminho.lfc.model;

import com.google.firebase.database.Exclude;
import com.x5.util.DataCapsule;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 09/04/2022
 */
@Getter
@Setter
public class ArtistSet implements DataCapsule, LFCPrestation {
    private String artist;
    private int duration;
    private double price;

    @Override
    @Exclude
    public String[] getExports() {
        return new String[]{"getArtist", "getDuration", "getPrice"};
    }

    @Override
    @Exclude
    public String getExportPrefix() {
        return "";
    }

    @Exclude
    @Override
    public double getPrestationPrice() {
        return price;
    }

    @Exclude
    @Override
    public void setPrestationPrice(double price) {
        setPrice(price);
    }

    @Exclude
    @Override
    public String getPrestationDetail() {
        return duration + "mn";
    }

    @Exclude
    @Override
    public String getPrestationName() {
        return artist;
    }

    @Exclude
    @Override
    public int getPrestationType() {
        return LFCPrestation.SHOWCASE;
    }
}
