package com.gaminho.lfc.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bonnie on 29/04/2022
 */
@Getter
@Setter
public class EditionProgram {

    private long startTime;

    private List<OpenMic> openMics = new ArrayList<>();
    private List<ArtistSet> artistSetList = new ArrayList<>();
    private List<ImprovisationBattle> battles = new ArrayList<>();
    private List<BreakEvent> breaks = new ArrayList<>();

    @Exclude
    public List<ProgramEvent> getAllEvents() {
        final List<ProgramEvent> list = new ArrayList<>(openMics);
        list.addAll(artistSetList);
        list.addAll(battles);
        list.addAll(breaks);
        list.sort(Comparator.comparing(ProgramEvent::getStartTime));
        return list;
    }

}
