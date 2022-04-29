package com.gaminho.lfc.activity.edition.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gaminho.lfc.activity.edition.ActivityEdition2;
import com.gaminho.lfc.databinding.FragmentEditionHomeBinding;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCEditionService;
import com.gaminho.lfc.utils.DateParser;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Bonnie on 29/04/2022
 */
public class EditionHomeFragment extends Fragment {

    private FragmentEditionHomeBinding binding;
    private final LFCEditionService editionService = new LFCEditionService();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditionHomeBinding.inflate(inflater, container, false);

        final String editionId = ((ActivityEdition2) requireActivity()).getEditionId();
        editionService.getEditionById(editionId, new DBService.FetchingListener<LFCEdition>() {
            @Override
            public void onFetched(LFCEdition entity) {
                ((ActivityEdition2) requireActivity()).setEdition(entity);
                new Handler(Looper.getMainLooper()).postDelayed(() -> fillView(entity), 500);
            }

            @Override
            public void onError(DatabaseError error) {
                quit();
            }
        });

        return binding.getRoot();
    }

    protected void fillView(LFCEdition edition) {
        binding.tvLoadingEdition.setVisibility(View.GONE);
        binding.textHome.setText(DateParser.formatEpochDay(edition.getDate()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void quit() {
        ((ActivityEdition2) requireActivity()).quitEditionActivity();
    }

}
