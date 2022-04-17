package com.gaminho.lfc.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.gaminho.lfc.model.Location;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Bonnie on 14/04/2022
 */
public final class LocationAdapter extends SimpleSpinnerAdapter<Location> {

    public LocationAdapter(@NonNull Context context) {
        super(context);
    }

    public int findPositionById(final String id) {
        return IntStream.range(0, getCount())
                .filter(index -> StringUtils.equalsIgnoreCase(id, getItem(index).buildId()))
                .findFirst()
                .orElse(0);
    }

    @Override
    public String getDisplayText(Location entity) {
        return entity.getName();
    }
}
