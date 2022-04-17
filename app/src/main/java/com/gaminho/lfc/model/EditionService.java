package com.gaminho.lfc.model;

import com.gaminho.lfc.model.enumeration.ServiceType;
import com.google.firebase.database.Exclude;
import com.x5.util.DataCapsule;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 09/04/2022
 */
@Getter
@Setter
public class EditionService implements DataCapsule, LFCPrestation {
    private ServiceType type;
    private String name;
    private double price;

    @Exclude
    @Override
    public String[] getExports() {
        return new String[]{"getType", "getName", "getPrice"};
    }

    @Exclude
    @Override
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
    public String getPrestationName() {
        return name;
    }

    @Exclude
    @Override
    public String getPrestationDetail() {
        return type.getLabel();
    }

    @Exclude
    @Override
    public int getPrestationType() {
        return LFCPrestation.EDITION_SERVICE;
    }
}
