package com.gaminho.lfc.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaminho.lfc.R;
import com.gaminho.lfc.databinding.CellProgramBreakBinding;
import com.gaminho.lfc.databinding.CellProgramEventBinding;
import com.gaminho.lfc.model.ArtistSet;
import com.gaminho.lfc.model.BreakEvent;
import com.gaminho.lfc.model.EditionProgram;
import com.gaminho.lfc.model.ImprovisationBattle;
import com.gaminho.lfc.model.OpenMic;
import com.gaminho.lfc.model.ProgramEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

/**
 * Created by Bonnie on 29/04/2022
 */
@RequiredArgsConstructor
public final class LFCEditionProgramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<ProgramEvent> mProgramEvents;
    private final LocalTime startTime;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType != ProgramEvent.BREAK) {
            CellProgramEventBinding binding = CellProgramEventBinding
                    .inflate(LayoutInflater.from(parent.getContext()));
            return new ViewHolderEvent(binding);
        } else {
            CellProgramBreakBinding binding = CellProgramBreakBinding
                    .inflate(LayoutInflater.from(parent.getContext()));
            return new ViewHolderBreak(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType != ProgramEvent.BREAK) {
            ((ViewHolderEvent) holder).bindData(mProgramEvents.get(position));
        } else {
            ((ViewHolderBreak) holder).bindData(position, startTime, mProgramEvents);
        }
    }

    @Override
    public int getItemCount() {
        return mProgramEvents.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mProgramEvents, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mProgramEvents, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mProgramEvents.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView) {
        IntStream.range(0, getItemCount())
                .filter(i -> getItemViewType(i) == ProgramEvent.BREAK)
                .forEach(i -> {
                    final ViewHolderBreak holderBreak = (ViewHolderBreak) recyclerView.findViewHolderForAdapterPosition(i);
                    if (Objects.nonNull(holderBreak)) {
                        final String s = holderBreak.computeTimelapse(i, mProgramEvents, startTime);
                        holderBreak.mBinding.tvTimelapse.setText(s);
                    }
                });
    }

    public static class ViewHolderEvent extends RecyclerView.ViewHolder {
        private final CellProgramEventBinding mBinding;

        ViewHolderEvent(@NonNull CellProgramEventBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bindData(final ProgramEvent programEvent) {
            final String title;
            final String detail;
            if (programEvent instanceof ArtistSet) {
                title = ((ArtistSet) programEvent).getArtist();
                detail = programEvent.getDuration() + "mn";
                this.mBinding.borderLeft.setBackgroundResource(R.color.blue);
            } else if (programEvent instanceof ImprovisationBattle) {
                title = "Battle d'impro - " + ((ImprovisationBattle) programEvent).getPhase();
                detail = programEvent.getDuration() + "mn";
                this.mBinding.borderLeft.setBackgroundResource(R.color.black);
            } else if (programEvent instanceof OpenMic) {
                title = "Open Mic";
                detail = programEvent.getDuration() + "mn";
                this.mBinding.borderLeft.setBackgroundResource(R.color.red);
            } else {
                title = "Whatever";
                detail = "anything";
                this.mBinding.borderLeft.setBackgroundResource(R.color.white);
            }

            this.mBinding.cellArtist.setText(title);
            this.mBinding.cellDuration.setText(detail);
        }
    }

    public static class ViewHolderBreak extends RecyclerView.ViewHolder {
        private final CellProgramBreakBinding mBinding;

        ViewHolderBreak(@NonNull CellProgramBreakBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bindData(final int position,
                      final LocalTime startTime,
                      final List<ProgramEvent> programEvents) {
            mBinding.tvTimelapse.setText(computeTimelapse(position, programEvents, startTime));
        }

        String computeTimelapse(final int position,
                                final List<ProgramEvent> programEvents,
                                final LocalTime startTime) {
            long elapsedTime = IntStream.range(0, position)
                    .mapToLong(i -> programEvents.get(i).getDuration())
                    .sum();
            final LocalTime start = startTime.plusMinutes(elapsedTime);
            final LocalTime end = start.plusMinutes(programEvents.get(position).getDuration());

            return start.format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " - " + end.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        final ProgramEvent programEvent = mProgramEvents.get(position);
        return programEvent.getType();
    }

    public EditionProgram extractProgram() {
        final EditionProgram program = new EditionProgram();

        LocalTime startTime = this.startTime;
        for (ProgramEvent event: mProgramEvents) {
            long start = startTime.toSecondOfDay() >= this.startTime.toSecondOfDay() ?
                    startTime.toSecondOfDay() : startTime.toSecondOfDay() + LocalTime.MAX.toSecondOfDay();

            event.setStartTime(start);
            if (ProgramEvent.SHOWCASE == event.getType()) {
                program.getArtistSetList().add((ArtistSet) event);
            } else if (ProgramEvent.BATTLE == event.getType()) {
                program.getBattles().add((ImprovisationBattle) event);
            } else if (ProgramEvent.BREAK == event.getType()) {
                program.getBreaks().add((BreakEvent) event);
            } else if (ProgramEvent.OPEN_MIC == event.getType()) {
                program.getOpenMics().add((OpenMic) event);
            }

            startTime = startTime.plusMinutes(event.getDuration());
        }

        program.setStartTime(this.startTime.toSecondOfDay());
        return program;
    }
}
