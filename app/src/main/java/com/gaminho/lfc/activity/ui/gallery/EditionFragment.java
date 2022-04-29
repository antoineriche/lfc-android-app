package com.gaminho.lfc.activity.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gaminho.lfc.activity.edition.ActivityEdition2;
import com.gaminho.lfc.activity.edition.EditionActivity;
import com.gaminho.lfc.adapter.LFCEditionAdapter;
import com.gaminho.lfc.databinding.FragmentEditionBinding;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCEditionService;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class EditionFragment extends Fragment implements DBService.FetchingCollectionListener<LFCEdition>,
        LFCEditionAdapter.OnEditionPickListener {

    private FragmentEditionBinding binding;
    private final LFCEditionService editionService = new LFCEditionService();
    private LFCEditionAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditionBinding.inflate(inflater, container, false);
        binding.tvLoadingEditions.setVisibility(View.VISIBLE);

        View root = binding.getRoot();

        binding.addEdition.setOnClickListener(view -> goToEditionActivity(null));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        editionService.getAllEditions(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void goToEditionActivity(final String editionId) {
        final Intent intent = new Intent(getActivity(), ActivityEdition2.class);
        intent.putExtra(EditionActivity.ARG_EDITION_ID, editionId);
        startActivity(intent);
    }

    @Override
    public void onFetched(Collection<LFCEdition> entities) {
        binding.tvLoadingEditions.setVisibility(View.GONE);
        final List<LFCEdition> editions = new ArrayList<>(entities);
        editions.sort(Comparator.comparingInt(edition -> ((LFCEdition) edition).getEdition()));
        mAdapter = new LFCEditionAdapter(editions, this);
        binding.rvEditions.setAdapter(mAdapter);
        binding.rvEditions.setHasFixedSize(true);
        binding.rvEditions.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onError(DatabaseError error) {
        Toast.makeText(getActivity(), "Error while fetching editions: " + error.getDetails(),
                Toast.LENGTH_LONG).show();
        binding.tvLoadingEditions.setText(error.getDetails());
    }

    @Override
    public void onEditionPicked(LFCEdition edition) {
        goToEditionActivity(String.valueOf(edition.getEdition()));
    }
}