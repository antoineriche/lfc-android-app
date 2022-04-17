package com.gaminho.lfc.model.enumeration;

import com.x5.util.DataCapsule;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Bonnie on 09/04/2022
 */
@Getter
@RequiredArgsConstructor
public enum ServiceType implements DataCapsule {
    PHOTO("Photo"),
    VIDEO("Video"),
    LIGHT("Lumière"),
    SOUND("Son"),
    DJ("DJ"),
    JUDGE("Jury"),
    EXTRA("Extra");

    private final String label;

    public static ServiceType fromName(final String name) {
        return Arrays.stream(ServiceType.values())
                .filter(type -> type.getLabel().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknwon type"));
    }

    @Override
    public String[] getExports() {
        return new String[]{"getLabel"};
    }

    @Override
    public String getExportPrefix() {
        return "";
    }
}
