package com.gaminho.lfc.activity.edition.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.dialog.AddingItemDialog;
import com.gaminho.lfc.adapter.SimpleSpinnerAdapter;
import com.gaminho.lfc.databinding.EditionDialogAddStaffBinding;
import com.gaminho.lfc.model.EditionService;
import com.gaminho.lfc.model.LFCStaff;
import com.gaminho.lfc.model.enumeration.ServiceType;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCStaffService;
import com.google.firebase.database.DatabaseError;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Bonnie on 15/04/2022
 */
public class AddEditionServiceDialog extends AddingItemDialog<EditionService, EditionDialogAddStaffBinding> {

    final LFCStaffService mLFCStaffService;
    private final SimpleSpinnerAdapter<String> servicePeopleAdapter;

    public AddEditionServiceDialog(Context context, AddDialogListener<EditionService> listener) {
        super(context, listener);
        this.mLFCStaffService = new LFCStaffService();
        this.servicePeopleAdapter = new SimpleSpinnerAdapter<String>(context) {
            @Override
            public String getDisplayText(String entity) {
                return entity;
            }
        };
    }

    @Override
    protected EditionDialogAddStaffBinding getBinding(Context context) {
        return EditionDialogAddStaffBinding.inflate(LayoutInflater.from(context));
    }

    @Override
    protected int getTitleResourceId() {
        return R.string.add_a_service;
    }

    @Override
    protected void initView(EditionDialogAddStaffBinding binding,
                            Context context) {
        binding.spinnerStaffPeople.setEnabled(false);
        binding.spinnerStaffPeople.setAdapter(servicePeopleAdapter);

        final List<ServiceType> serviceTypes = Arrays.stream(ServiceType.values()).collect(Collectors.toList());

        final SimpleSpinnerAdapter<ServiceType> adapter = new SimpleSpinnerAdapter<ServiceType>(context, serviceTypes) {
            @Override
            public String getDisplayText(ServiceType entity) {
                return entity.getLabel();
            }
        };

        binding.spinnerStaffType.setAdapter(adapter);
        binding.spinnerStaffType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final ServiceType type = (ServiceType) binding.spinnerStaffType.getSelectedItem();
                if (ServiceType.EXTRA == type ) {
                    binding.llExtra.setVisibility(View.VISIBLE);
                    binding.spinnerStaffPeople.setVisibility(View.GONE);
                } else {
                    binding.llExtra.setVisibility(View.GONE);
                    binding.spinnerStaffPeople.setVisibility(View.VISIBLE);
                    mLFCStaffService.getAllStaffByType(new DBService.FetchingCollectionListener<LFCStaff>() {
                        @Override
                        public void onFetched(Collection<LFCStaff> entities) {
                            final List<String> servicePeople = entities.stream()
                                    .map(LFCStaff::getName)
                                    .collect(Collectors.toList());

                            servicePeopleAdapter.updateList(servicePeople);
                            binding.spinnerStaffPeople.setEnabled(true);
                        }

                        @Override
                        public void onError(DatabaseError error) {
                            binding.spinnerStaffPeople.setEnabled(false);
                        }
                    }, type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                binding.spinnerStaffPeople.setEnabled(false);
            }
        });
    }

    @Override
    protected EditionService extractItemFromBinding(EditionDialogAddStaffBinding binding) {
        final EditionService editionService = new EditionService();
        final ServiceType type = (ServiceType) binding.spinnerStaffType.getSelectedItem();
        editionService.setType(type);

        final String name;
        if (ServiceType.EXTRA == type) {
            name = binding.etExtraName.getText().toString();
        } else {
            name = (String) binding.spinnerStaffPeople.getSelectedItem();
        }

        editionService.setName(name);

        final double price = StringUtils.isNotBlank(binding.etPrice.getText()) ?
                Double.parseDouble(binding.etPrice.getText().toString()) : 0.0D;
        editionService.setPrice(price);

        return editionService;
    }

}
