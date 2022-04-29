package com.gaminho.lfc.activity.edition.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.edition.dialog.AddEditionServiceDialog;
import com.gaminho.lfc.activity.edition.dialog.AddShowcaseDialog;
import com.gaminho.lfc.activity.edition.dialog.UpdatePrestationDialog;
import com.gaminho.lfc.adapter.ELVServiceAndShowAdapter;
import com.gaminho.lfc.adapter.LocationAdapter;
import com.gaminho.lfc.business.Html2Pdf;
import com.gaminho.lfc.databinding.FragmentEditionDashboardBinding;
import com.gaminho.lfc.model.ArtistSet;
import com.gaminho.lfc.model.EditionService;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.LFCPrestation;
import com.gaminho.lfc.model.Location;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCEditionService;
import com.gaminho.lfc.service.LocationService;
import com.gaminho.lfc.utils.DateParser;
import com.gaminho.lfc.utils.ParserUtils;
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
 * Created by Bonnie on 29/04/2022
 */
public class EditionDashboardFragment extends EditionFragment<FragmentEditionDashboardBinding>
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        OnCompleteListener<Void>,
        Html2Pdf.OnCompleteConversion,
        ELVServiceAndShowAdapter.OnAddPrestationClickListener {

    private LocalDate currentDate = LocalDate.now();

    private LocationAdapter mLocationAdapter;
    private final LFCEditionService editionService = new LFCEditionService();
    private final LocationService locationService = new LocationService();
    private final ObservableField<Collection<Location>> obsLocations = new ObservableField<>();
    private ELVServiceAndShowAdapter expandableListAdapter;
    private Disposable formObserver;

    private final List<LFCPrestation> mLFCPrestations = new ArrayList<>();

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected FragmentEditionDashboardBinding getBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentEditionDashboardBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView(FragmentEditionDashboardBinding binding, LFCEdition edition) {

        mLocationAdapter = new LocationAdapter(requireContext());
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
                Toast.makeText(requireActivity(), "Error while getting locations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveEdition.setEnabled(false);
        binding.btnSaveEdition.setOnClickListener(this);
        binding.etPickDate.setOnClickListener(this);

        this.formObserver = io.reactivex.rxjava3.core.Observable
                .combineLatest(RxTextView.textChanges(binding.etEditionNumber),
                        RxTextView.textChanges(binding.etPickDate),
                        (editionNumber, sDate) -> Stream.of(editionNumber, sDate).noneMatch(StringUtils::isBlank))
                .subscribe(binding.btnSaveEdition::setEnabled);

        expandableListAdapter = new ELVServiceAndShowAdapter(mLFCPrestations, this);
        binding.elvStaffAndShow.setAdapter(expandableListAdapter);
        binding.elvStaffAndShow.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
            final LFCPrestation prestation = expandableListAdapter.getChild(groupPosition, childPosition);
            onPrestationClick(prestation);
            return true;
        });

        binding.etEditionNumber.setText(String.valueOf(edition.getEdition()));
        binding.etEditionNumber.setEnabled(false);

        binding.etAdvertisement.setText(String.valueOf(edition.getAdvertisement()));

        final LocalDate localDate = LocalDate.ofEpochDay(edition.getDate());
        currentDate = localDate;
        binding.etPickDate.setText(DateParser.formatLocalDate(localDate));

        final LocationAdapter adapter = (LocationAdapter) binding.spinnerLocation.getAdapter();
        if (Objects.nonNull(adapter)
                && StringUtils.isNotBlank(edition.getLocation())) {
            final int position = adapter.findPositionById(edition.getLocation());
            binding.spinnerLocation.setSelection(position, true);
        }

        mLFCPrestations.clear();
        mLFCPrestations.addAll(edition.getEditionServices());
        mLFCPrestations.addAll(edition.getArtistSetList());
        expandableListAdapter.notifyDataSetChanged();
    }

    protected void fillLocationSpinner(final List<Location> locations) {
        mLocationAdapter.updateList(locations);
        binding.spinnerLocation.setEnabled(!locations.isEmpty());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Objects.nonNull(this.formObserver)) {
            this.formObserver.dispose();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_edition_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit_facture) {
            final LFCEdition edition = buildEditionFromCurrentView();
            editEditionFacture(edition, this);
        } else if (itemId == R.id.action_delete_edition) {
            editionService.deleteEdition(getLFCEdition().buildId(), new DBService.DeletionListener() {
                @Override
                public void onDeleted() {
                    Toast.makeText(requireContext(), "L'édition a été supprimée", Toast.LENGTH_SHORT).show();
                    quitActivity();
                }

                @Override
                public void onDeletionError(DatabaseError error) {
                    Toast.makeText(requireContext(), "Error while deleting edition: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (binding.etPickDate.equals(view)) {
            new DatePickerDialog(requireContext(), this,
                    currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth())
                    .show();
        } else if (binding.btnSaveEdition.equals(view)) {
            final LFCEdition edition = buildEditionFromCurrentView();
            saveEdition(edition);
        }
    }

    private void openStaffDialog() {
        new AddEditionServiceDialog(requireContext(), (entity, alertDialog) -> {
            mLFCPrestations.add(entity);
            expandableListAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }).show();
    }

    private void openShowcaseDialog() {
        new AddShowcaseDialog(requireContext(), (entity, alertDialog) -> {
            mLFCPrestations.add(entity);
            expandableListAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
        }).show();
    }

    private LFCEdition buildEditionFromCurrentView() {
        final LFCEdition edition = new LFCEdition();
        final int editionCount = Integer.parseInt(binding.etEditionNumber.getText().toString());
        edition.setEdition(editionCount);

        final LocalDate localDate = LocalDate.parse(binding.etPickDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        edition.setDate(localDate.toEpochDay());

        final String location = ((Location) binding.spinnerLocation.getSelectedItem()).buildId();
        edition.setLocation(location);

        final double advertisement = ParserUtils.extractDoubleFromTextView(binding.etAdvertisement);
        edition.setAdvertisement(advertisement);

        final Map<Integer, List<LFCPrestation>> prestations = mLFCPrestations.stream()
                .collect(Collectors.groupingBy(LFCPrestation::getPrestationType));

        edition.setEditionServices(prestations.getOrDefault(LFCPrestation.EDITION_SERVICE, Collections.emptyList()).stream()
                .map(lfcPrestation -> (EditionService) lfcPrestation)
                .collect(Collectors.toList()));

        //edition.getEditionServices().addAll(prestations.getOrDefault(LFCPrestation.JUDGE, Collections.emptyList()).stream()
        //        .map(lfcPrestation -> (EditionService) lfcPrestation)
        //        .collect(Collectors.toList()));

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
        LFCEditionService.editFacture(edition, requireContext(), completionListener);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        currentDate = LocalDate.of(year, month + 1, day);
        binding.etPickDate.setText(DateParser.formatLocalDate(currentDate));
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(requireContext(), "L'édition a été enregistrée", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String pdfName) {
        Toast.makeText(requireContext(), "La facture " + pdfName + " a été éditée", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed() {
        Toast.makeText(requireContext(), "La facture n'a pas été éditée", Toast.LENGTH_SHORT).show();
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
        new UpdatePrestationDialog(requireContext(), prestation, new UpdatePrestationDialog.UpdatePrestationListener() {
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
