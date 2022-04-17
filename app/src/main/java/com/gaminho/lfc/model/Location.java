package com.gaminho.lfc.model;

import com.gaminho.lfc.service.DatabaseEntity;
import com.google.firebase.database.IgnoreExtraProperties;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 10/04/2022
 */
@Getter
@Setter
@IgnoreExtraProperties
public class Location implements DatabaseEntity {
    private String name;
    private String address;
    private String city;
    private int zipCode;


    public String buildFullAddress() {
        return Stream.of(name, address, zipCode, city)
                .map(String::valueOf)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String buildId() {
        return StringUtils.isNotBlank(name) ?
                name.toLowerCase().replaceAll("\\s+", "-") : null;
    }
}
