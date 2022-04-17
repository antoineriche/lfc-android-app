package com.gaminho.lfc.service;

import androidx.annotation.NonNull;

import com.gaminho.lfc.model.Location;
import com.google.android.gms.tasks.OnCompleteListener;

/**
 * Created by Bonnie on 10/04/2022
 */
public final class LocationService extends DBService<Location> {

    public void saveLocation(final Location location,
                             final OnCompleteListener<Void> listener) {
        super.saveEntity(location.buildId(), location, listener);
    }

    public void getAllLocations(FetchingCollectionListener<Location> listener) {
        super.getAll(listener);
    }

    public void getLocationById(@NonNull final String id,
                                @NonNull final FetchingListener<Location> listener) {
        super.getById(id, listener);
    }

    @Override
    protected String getTable() {
        return DBConstants.DB_TABLE_LOCATION;
    }

    @Override
    protected Class<Location> getTClass() {
        return Location.class;
    }

}
