package com.gaminho.lfc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gaminho.lfc.databinding.EditionElvGroupBinding;
import com.gaminho.lfc.databinding.EditionElvItemBinding;
import com.gaminho.lfc.model.LFCPrestation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bonnie on 16/04/2022
 */
public final class ELVServiceAndShowAdapter extends BaseExpandableListAdapter {

    private final List<LFCPrestation> mLFCPrestations;
    private final List<Integer> prestationTypes;
    private final Map<Integer, List<LFCPrestation>> expandableListDetail;
    private final OnAddPrestationClickListener listener;

    public ELVServiceAndShowAdapter(List<LFCPrestation> prestations,
                                    OnAddPrestationClickListener listener) {
        this.mLFCPrestations = prestations;
        this.expandableListDetail = prestations.stream()
                .collect(Collectors.groupingBy(LFCPrestation::getPrestationType));
        this.prestationTypes = Arrays.asList(LFCPrestation.SHOWCASE, LFCPrestation.EDITION_SERVICE);
        this.listener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        this.expandableListDetail.clear();
        this.expandableListDetail.putAll(mLFCPrestations.stream()
                .collect(Collectors.groupingBy(LFCPrestation::getPrestationType)));
        super.notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        final Integer type = this.prestationTypes.get(groupPosition);
        return this.expandableListDetail.getOrDefault(type, Collections.emptyList()).size();
    }

    @Override
    public Integer getGroup(int groupPosition) {
        return this.prestationTypes.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.prestationTypes.size();
    }

    @Override
    public LFCPrestation getChild(int groupPosition, int itemPosition) {
        final Integer type = this.prestationTypes.get(groupPosition);
        return this.expandableListDetail.get(type).get(itemPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int itemPosition) {
        return itemPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Integer type = getGroup(groupPosition);

        EditionElvGroupBinding groupBinding;
        if (convertView == null) {
            groupBinding = EditionElvGroupBinding.inflate(LayoutInflater.from(parent.getContext()));
            convertView = groupBinding.getRoot();
        } else {
            groupBinding = (EditionElvGroupBinding) convertView.getTag();
        }


        final String title;
        if (LFCPrestation.EDITION_SERVICE.equals(type)) {
            title = "Services";
        } else if (LFCPrestation.SHOWCASE.equals(type)) {
            title = "Showcase";
        } else {
            title = "Autre";
        }
        groupBinding.prestationType.setText(title);
        groupBinding.btnAddPresta.setOnClickListener(v -> listener.onPrestationAddClick(type));

        convertView.setTag(groupBinding);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int itemPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final LFCPrestation prestation = getChild(groupPosition, itemPosition);

        EditionElvItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = EditionElvItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            convertView = itemBinding.getRoot();
        } else {
            itemBinding = (EditionElvItemBinding) convertView.getTag();
        }

        itemBinding.tvName.setText(prestation.getPrestationName());
        itemBinding.tvDesc.setText(prestation.getPrestationDetail());
        itemBinding.tvPrice.setText(String.format(Locale.FRANCE, "%.01f", prestation.getPrestationPrice()));

        convertView.setTag(itemBinding);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public interface OnAddPrestationClickListener {
        void onPrestationAddClick(final int kindOfPrestation);
        void onPrestationClick(final LFCPrestation prestation);
    }
}
