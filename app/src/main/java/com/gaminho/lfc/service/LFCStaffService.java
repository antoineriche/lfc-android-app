package com.gaminho.lfc.service;

import com.gaminho.lfc.model.LFCStaff;
import com.gaminho.lfc.model.enumeration.ServiceType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Bonnie on 10/04/2022
 */
public final class LFCStaffService extends DBService<LFCStaff> {

    public void init() {
        final LFCStaff staff = new LFCStaff();
        staff.setName("Cassandre");
        staff.setTypes(Arrays.asList(ServiceType.PHOTO, ServiceType.VIDEO));
        this.saveStaff(staff, null);

        staff.setName("Léa");
        staff.setTypes(Arrays.asList(ServiceType.PHOTO, ServiceType.VIDEO));
        this.saveStaff(staff, null);

        staff.setName("Kazaam");
        staff.setTypes(Collections.singletonList(ServiceType.DJ));
        this.saveStaff(staff, null);

        staff.setName("Pale peu");
        staff.setTypes(Collections.singletonList(ServiceType.SOUND));
        this.saveStaff(staff, null);

        staff.setName("201");
        staff.setTypes(Collections.singletonList(ServiceType.LIGHT));
        this.saveStaff(staff, null);

        staff.setName("Rémy");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        this.saveStaff(staff, null);

        staff.setName("Straight");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        this.saveStaff(staff, null);

        staff.setName("NeirDa");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        this.saveStaff(staff, null);

        staff.setName("Alban");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        this.saveStaff(staff, null);

        staff.setName("Rap Indé LaMiff");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        this.saveStaff(staff, null);
    }

    public void saveStaff(final LFCStaff staff,
                          final OnCompleteListener<Void> listener) {
        super.saveEntity(staff.buildId(), staff, listener);
    }

    public void getAllStaff(FetchingCollectionListener<LFCStaff> listener) {
        super.getAll(listener);
    }

    public void getAllStaffByType(FetchingCollectionListener<LFCStaff> listener,
                                  final ServiceType type) {
        super.getAll(new FetchingCollectionListener<LFCStaff>() {
            @Override
            public void onFetched(Collection<LFCStaff> entities) {
                listener.onFetched(entities.stream()
                        .filter(lfcStaff -> lfcStaff.getTypes().contains(type))
                        .collect(Collectors.toList()));
            }

            @Override
            public void onError(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    protected DatabaseReference getTableTypeReference(final ServiceType type) {
        return getTableReference().child(type.name().toLowerCase());
    }

    @Override
    protected String getTable() {
        return DBConstants.DB_TABLE_STAFF;
    }

    @Override
    protected Class<LFCStaff> getTClass() {
        return LFCStaff.class;
    }

}
