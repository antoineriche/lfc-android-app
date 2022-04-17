package com.gaminho.lfc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaminho.lfc.databinding.CellLfcEditionBinding;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.Location;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LocationService;
import com.gaminho.lfc.utils.DateParser;
import com.google.firebase.database.DatabaseError;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;

/**
 * Created by Bonnie on 13/04/2022
 */
@RequiredArgsConstructor
public class LFCEditionAdapter extends RecyclerView.Adapter<LFCEditionAdapter.ViewHolder> {

    private final List<LFCEdition> mEditions;
    private final OnEditionPickListener pickListener;
    private CellLfcEditionBinding binding;
    private final LocationService mLocationService = new LocationService();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public LFCEditionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        binding = CellLfcEditionBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull LFCEditionAdapter.ViewHolder viewHolder, int position) {
        final LFCEdition edition = mEditions.get(position);
        binding.getRoot().setOnClickListener(view -> pickListener.onEditionPicked(edition));
        binding.cellEditionDate.setText(StringUtils.isNotBlank(edition.getLocation()) ? edition.getLocation() : "-");

        if (StringUtils.isNotBlank(edition.getLocation())) {
            mLocationService.getLocationById(edition.getLocation(), new DBService.FetchingListener<Location>() {
                @Override
                public void onFetched(Location entity) {
                    binding.cellEditionDate.setText(entity.getName());
                }

                @Override
                public void onError(DatabaseError error) {
                }
            });
        }

        binding.cellEditionNumber.setText(String.format(Locale.FRANCE, "Édition #%02d", edition.getEdition()));
        binding.cellEditionDetail1.setText(DateParser.formatEpochDay(edition.getDate()));
    }

    @Override
    public int getItemCount() {
        return mEditions.size();
    }

    public interface OnEditionPickListener {
        void onEditionPicked(LFCEdition edition);
    }
}
