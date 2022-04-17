package com.gaminho.lfc.activity.edition;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.gaminho.lfc.activity.edition.dialog.AddEditionServiceDialog;
import com.gaminho.lfc.activity.edition.dialog.AddShowcaseDialog;
import com.gaminho.lfc.activity.edition.dialog.UpdatePrestationDialog;
import com.gaminho.lfc.adapter.ELVServiceAndShowAdapter;
import com.gaminho.lfc.adapter.LocationAdapter;
import com.gaminho.lfc.business.Html2Pdf;
import com.gaminho.lfc.databinding.ActivityEditionBinding;
import com.gaminho.lfc.model.ArtistSet;
import com.gaminho.lfc.model.EditionService;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.LFCPrestation;
import com.gaminho.lfc.model.Location;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCEditionService;
import com.gaminho.lfc.service.LocationService;
import com.gaminho.lfc.utils.DateParser;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Created by Bonnie on 11/04/2022
 */
public class EditionActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        OnCompleteListener<Void>,
        DBService.FetchingListener<LFCEdition>,
        Html2Pdf.OnCompleteConversion, ELVServiceAndShowAdapter.OnAddPrestationClickListener {

    public static final String ARG_EDITION_ID = "edition-id";

    private LocalDate currentDate = LocalDate.now();
    private final LFCEditionService editionService = new LFCEditionService();
    private final LocationService locationService = new LocationService();
    private EditText mETEdition;
    private ActivityEditionBinding binding;
    private final ObservableField<LFCEdition> obsEdition = new ObservableField<>();
    private final ObservableField<Collection<Location>> obsLocations = new ObservableField<>();
    private LocationAdapter mLocationAdapter;
    private ELVServiceAndShowAdapter expandableListAdapter;
    private Disposable formObserver;

    private final List<LFCPrestation> mLFCPrestations = new ArrayList<>();

    private final Observable.OnPropertyChangedCallback fetchEditionListener = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            final LFCEdition edition = obsEdition.get();
            if (Objects.isNull(edition)) {
                binding.tvLoadingEdition.setVisibility(View.VISIBLE);
            } else {
                fillView(edition);
                obsEdition.removeOnPropertyChangedCallback(fetchEditionListener);
            }
        }
    };

    private final Observable.OnPropertyChangedCallback fetchLocationListener = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            final Collection<Location> locations = obsLocations.get();
            if (!CollectionUtils.isEmpty(locations)) {
                fillLocationSpinner(new ArrayList<>(locations));
                obsLocations.removeOnPropertyChangedCallback(fetchLocationListener);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mLocationAdapter = new LocationAdapter(this);
        binding.spinnerLocation.setAdapter(mLocationAdapter);
        binding.spinnerLocation.setEnabled(false);

        this.obsLocations.addOnPropertyChangedCallback(fetchLocationListener);
        locationService.getAllLocations(new DBService.FetchingCollectionListener<Location>() {
            @Override
            public void onFetched(Collection<Location> entities) {
                obsLocations.set(entities);
            }

            @Override
            public void onError(DatabaseError error) {
                Toast.makeText(getBaseContext(), "Error while getting locations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveEdition.setEnabled(false);
        binding.btnSaveEdition.setOnClickListener(this);

        binding.btnEditFacture.setOnClickListener(this);

        binding.etPickDate.setOnClickListener(this);

        this.formObserver = io.reactivex.rxjava3.core.Observable
                .combineLatest(RxTextView.textChanges(binding.etEditionNumber),
                        RxTextView.textChanges(binding.etPickDate),
                        (editionNumber, sDate) -> Stream.of(editionNumber, sDate).noneMatch(StringUtils::isBlank))
                .subscribe(isValid -> binding.btnSaveEdition.setEnabled(isValid));

        mETEdition = binding.etEditionNumber;
        this.obsEdition.addOnPropertyChangedCallback(fetchEditionListener);

        if (StringUtils.isNotBlank(getIntent().getStringExtra(ARG_EDITION_ID))) {
            this.obsEdition.set(null);
            editionService.getEditionById(getIntent().getStringExtra(ARG_EDITION_ID), this);
        } else {
            binding.tvLoadingEdition.setVisibility(View.GONE);
        }

        expandableListAdapter = new ELVServiceAndShowAdapter(mLFCPrestations, this);
        binding.elvStaffAndShow.setAdapter(expandableListAdapter);
        binding.elvStaffAndShow.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
            final LFCPrestation prestation = expandableListAdapter.getChild(groupPosition, childPosition);
            onPrestationClick(prestation);
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Objects.nonNull(this.formObserver)) {
            this.formObserver.dispose();
        }
    }

    @Override
    public void onClick(View view) {
        if (binding.etPickDate.equals(view)) {
            new DatePickerDialog(EditionActivity.this, this,
                    currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth())
                    .show();
        } else if (binding.btnSaveEdition.equals(view)) {
            final LFCEdition edition = buildEditionFromCurrentView();
            saveEdition(edition);
        } else if (binding.btnEditFacture.equals(view)) {
            final LFCEdition edition = buildEditionFromCurrentView();
            editEditionFacture(edition, this);
        }
    }

    private void openStaffDialog() {
        new AddEditionServiceDialog(this, (entity, alertDialog) -> {
            mLFCPrestations.add(entity);
            expandableListAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }).show();
    }

    private void openShowcaseDialog() {
        new AddShowcaseDialog(this, (entity, alertDialog) -> {
            mLFCPrestations.add(entity);
            expandableListAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }).show();
    }

    private LFCEdition buildEditionFromCurrentView() {
        final LFCEdition edition = new LFCEdition();
        final int editionCount = Integer.parseInt(mETEdition.getText().toString());
        edition.setEdition(editionCount);

        final LocalDate localDate = LocalDate.parse(binding.etPickDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        edition.setDate(localDate.toEpochDay());

        final String location = ((Location) binding.spinnerLocation.getSelectedItem()).buildId();
        edition.setLocation(location);

        final Map<Integer, List<LFCPrestation>> prestations = mLFCPrestations.stream()
                .collect(Collectors.groupingBy(LFCPrestation::getPrestationType));

        edition.setEditionServices(prestations.getOrDefault(LFCPrestation.EDITION_SERVICE, Collections.emptyList()).stream()
                .map(lfcPrestation -> (EditionService) lfcPrestation)
                .collect(Collectors.toList()));

        edition.setArtistSetList(prestations.getOrDefault(LFCPrestation.SHOWCASE, Collections.emptyList()).stream()
                .map(lfcPrestation -> (ArtistSet) lfcPrestation)
                .collect(Collectors.toList()));

        return edition;
    }

    protected void saveEdition(final LFCEdition edition) {
        editionService.saveEdition(edition, this);
    }

    protected void editEditionFacture(final LFCEdition edition,
                                      final Html2Pdf.OnCompleteConversion completionListener) {
        LFCEditionService.editFacture(edition, this, completionListener);
    }

    protected void fillLocationSpinner(final List<Location> locations) {
        mLocationAdapter.updateList(locations);
        binding.spinnerLocation.setEnabled(!locations.isEmpty());
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        currentDate = LocalDate.of(year, month + 1, day);
        binding.etPickDate.setText(DateParser.formatLocalDate(currentDate));
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(getBaseContext(), "L'édition a été enregistrée", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetched(LFCEdition entity) {
        this.obsEdition.set(entity);
    }

    @Override
    public void onError(DatabaseError error) {
        Toast.makeText(getBaseContext(), "Error while getting edition: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void fillView(LFCEdition edition) {
        binding.etEditionNumber.setText(String.valueOf(edition.getEdition()));
        binding.etEditionNumber.setEnabled(false);

        final LocalDate localDate = LocalDate.ofEpochDay(edition.getDate());
        currentDate = localDate;
        binding.etPickDate.setText(DateParser.formatLocalDate(localDate));

        binding.tvLoadingEdition.setVisibility(View.GONE);

        final LocationAdapter adapter = (LocationAdapter) binding.spinnerLocation.getAdapter();
        if (Objects.nonNull(adapter)
                && StringUtils.isNotBlank(edition.getLocation())) {
            final int position = adapter.findPositionById(edition.getLocation());
            binding.spinnerLocation.setSelection(position, true);
        }

        mLFCPrestations.clear();
        mLFCPrestations.addAll(edition.getEditionServices());
        mLFCPrestations.addAll(edition.getArtistSetList());
    }

    @Override
    public void onSuccess(String pdfName) {
        Toast.makeText(this, "La facture " + pdfName + " a été éditée", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onFailed() {
        Toast.makeText(getBaseContext(), "La facture n'a pas été éditée", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrestationAddClick(int kindOfPrestation) {
        if (LFCPrestation.EDITION_SERVICE.equals(kindOfPrestation)) {
            openStaffDialog();
        } else if (LFCPrestation.SHOWCASE.equals(kindOfPrestation)) {
            openShowcaseDialog();
        }
    }

    @Override
    public void onPrestationClick(LFCPrestation prestation) {
        new UpdatePrestationDialog(this, prestation, new UpdatePrestationDialog.UpdatePrestationListener() {
            @Override
            public void onDeletePrestationClick(LFCPrestation prestation) {
                mLFCPrestations.removeIf(p -> prestation.getPrestationId().equals(p.getPrestationId()));
                expandableListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemAddClick(LFCPrestation entity, AlertDialog alertDialog) {
                mLFCPrestations.stream()
                        .filter(p -> entity.getPrestationId().equals(p.getPrestationId()))
                        .findFirst()
                        .ifPresent(p -> p.setPrestationPrice(entity.getPrestationPrice()));
                expandableListAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        }).show();
    }
}
