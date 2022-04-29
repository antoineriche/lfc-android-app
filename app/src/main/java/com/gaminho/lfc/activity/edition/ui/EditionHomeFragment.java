package com.gaminho.lfc.activity.edition.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.edition.ActivityEdition2;
import com.gaminho.lfc.adapter.LFCEditionProgramAdapter;
import com.gaminho.lfc.adapter.SimpleItemTouchHelperCallback;
import com.gaminho.lfc.databinding.FragmentEditionHomeBinding;
import com.gaminho.lfc.model.BreakEvent;
import com.gaminho.lfc.model.EditionProgram;
import com.gaminho.lfc.model.ImprovisationBattle;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.OpenMic;
import com.gaminho.lfc.model.ProgramEvent;
import com.gaminho.lfc.model.enumeration.BattlePhase;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LFCEditionService;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.firebase.database.DatabaseError;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by Bonnie on 29/04/2022
 */
public class EditionHomeFragment extends Fragment {

    private FragmentEditionHomeBinding binding;
    private final List<ProgramEvent> mProgramEvents = new ArrayList<>();
    private final LFCEditionService editionService = new LFCEditionService();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
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

        final LFCEditionProgramAdapter adapter = new LFCEditionProgramAdapter(mProgramEvents,
                LocalTime.of(19, 45));

        final ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvProgram);

        binding.rvProgram.setAdapter(adapter);
        binding.rvProgram.setHasFixedSize(true);
        binding.rvProgram.setLayoutManager(new LinearLayoutManager(requireContext()));

        return binding.getRoot();
    }

    protected void fillView(LFCEdition edition) {

        binding.tvLoadingEdition.setVisibility(View.GONE);
        mProgramEvents.clear();

        if (Objects.nonNull(edition.getProgram())
                && !CollectionUtils.isEmpty(edition.getProgram().getAllEvents())) {
            final List<ProgramEvent> events = edition.getProgram().getAllEvents();
            mProgramEvents.addAll(events);
        } else {
            // Build a program based on showcases, battle and open mic
            mProgramEvents.addAll(edition.getArtistSetList());

            final ImprovisationBattle qualificationBattle = new ImprovisationBattle();
            qualificationBattle.setPhase(BattlePhase.QUALIFICATIONS);
            qualificationBattle.setDuration(45);
            mProgramEvents.add(qualificationBattle);

            final ImprovisationBattle finalBattle = new ImprovisationBattle();
            finalBattle.setPhase(BattlePhase.FINAL);
            finalBattle.setDuration(30);
            mProgramEvents.add(finalBattle);

            final int breakCount = edition.getArtistSetList().size() + 3; // 2 battles + 1 OM
            IntStream.range(0, breakCount).forEach(i -> {
                final BreakEvent breakEvent = new BreakEvent();
                breakEvent.setDuration(10);
                mProgramEvents.add(breakEvent);
            });

            final OpenMic openMic = new OpenMic();
            openMic.setDuration(45);
            mProgramEvents.add(openMic);

            final OpenMic openMic2 = new OpenMic();
            openMic2.setDuration(45);
            mProgramEvents.add(openMic2);
        }

        binding.rvProgram.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_edition_program, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edition_save_program) {
            final LFCEdition edition = ((ActivityEdition2) requireActivity()).getEdition();
            final EditionProgram program = ((LFCEditionProgramAdapter) binding.rvProgram.getAdapter()).extractProgram();
            edition.setProgram(program);
            editionService.saveEdition(edition, task -> Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show());
        }
        return true;
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
