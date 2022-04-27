package com.gaminho.lfc.model;

import com.gaminho.lfc.service.DatabaseEntity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 09/04/2022
 */
@Getter
@Setter
public class LFCEdition implements DatabaseEntity {
    private int edition;
    private long date;
    private double advertisement;
    private String location;
    private List<ArtistSet> artistSetList = new ArrayList<>();
    private List<EditionService> editionServices = new ArrayList<>();

    public double getTotalPrice() {
        return editionServices.stream().mapToDouble(EditionService::getPrice).sum()
                + artistSetList.stream().mapToDouble(ArtistSet::getPrice).sum()
                + advertisement;
    }

    @Override
    public String buildId() {
        return String.valueOf(edition);
    }
}
