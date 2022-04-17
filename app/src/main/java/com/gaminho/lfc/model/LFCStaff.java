package com.gaminho.lfc.model;

import com.gaminho.lfc.model.enumeration.ServiceType;
import com.gaminho.lfc.service.DatabaseEntity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 15/04/2022
 */
@Getter
@Setter
public class LFCStaff implements DatabaseEntity {
    private List<ServiceType> types;
    private String name;

    @Override
    public String buildId() {
        return name;
    }
}
