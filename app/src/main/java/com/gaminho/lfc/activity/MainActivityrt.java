package com.gaminho.lfc.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.edition.EditionActivity;
import com.gaminho.lfc.business.Html2Pdf;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.LFCStaff;
import com.gaminho.lfc.model.Location;
import com.gaminho.lfc.model.enumeration.ServiceType;
import com.gaminho.lfc.service.LFCEditionService;
import com.gaminho.lfc.service.LFCStaffService;
import com.gaminho.lfc.service.LocationService;
import com.gaminho.lfc.utils.EditionBuilder;

import java.util.Arrays;
import java.util.Collections;

public class MainActivityrt extends AppCompatActivity implements View.OnClickListener {

    private final LocationService locationService = new LocationService();
    private final LFCStaffService staffService = new LFCStaffService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);
        findViewById(R.id.add_edition).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }


    public void saveNewLocation() {
        final Location location = new Location();
        location.setCity("Bordeaux");
        location.setName("Le Fiacre");
        location.setZipCode(33000);
        location.setAddress("42 Rue de Cheverus");

        final Location location2 = new Location();
        location2.setCity("Bordeaux");
        location2.setName("Central Perk");
        location2.setZipCode(33800);
        location2.setAddress("26 Cours de la Somme");

        locationService.saveLocation(location, task -> {
            Log.i("DB", "Success? " + task.isSuccessful());
            Log.i("DB", "Complete? " + task.isComplete());
            Log.i("DB", "Cancel? " + task.isCanceled());
        });

        final LFCStaff staff = new LFCStaff();
        staff.setName("Cassandre");
        staff.setTypes(Arrays.asList(ServiceType.PHOTO, ServiceType.VIDEO));
        staffService.saveStaff(staff, null);

        staff.setName("Léa");
        staff.setTypes(Arrays.asList(ServiceType.PHOTO, ServiceType.VIDEO));
        staffService.saveStaff(staff, null);

        staff.setName("Nakama Prod");
        staff.setTypes(Collections.singletonList(ServiceType.VIDEO));
        staffService.saveStaff(staff, null);

        staff.setName("Kazaam");
        staff.setTypes(Collections.singletonList(ServiceType.DJ));
        staffService.saveStaff(staff, null);

        staff.setName("Pale peu");
        staff.setTypes(Collections.singletonList(ServiceType.SOUND));
        staffService.saveStaff(staff, null);

        staff.setName("201");
        staff.setTypes(Collections.singletonList(ServiceType.LIGHT));
        staffService.saveStaff(staff, null);

        staff.setName("Rémy");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);

        staff.setName("Public");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);

        staff.setName("Straight");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);

        staff.setName("NeirDa");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);

        staff.setName("Alban");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);

        staff.setName("Rap Indé LaMiff");
        staff.setTypes(Collections.singletonList(ServiceType.JUDGE));
        staffService.saveStaff(staff, null);
    }

    public void chunk() {

        final LFCEdition edition = EditionBuilder.buildEdition9();
        LFCEditionService.editFacture(edition, this, new Html2Pdf.OnCompleteConversion() {
            @Override
            public void onSuccess(final String fileName) {
                Toast.makeText(getBaseContext(), "La facture " + fileName + " a été générée", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Log.e("PDF", "KO");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_edition:
                final Intent intent = new Intent(this, EditionActivity.class);
                intent.putExtra(EditionActivity.ARG_EDITION_ID, "9");
                startActivity(intent);
                break;
            case R.id.btn:
                chunk();
                break;
            case R.id.btn2:
                saveNewLocation();
                break;
            default:
                break;
        }
    }
}