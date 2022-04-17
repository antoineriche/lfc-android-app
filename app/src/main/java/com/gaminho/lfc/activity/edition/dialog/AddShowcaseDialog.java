package com.gaminho.lfc.activity.edition.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.dialog.AddingItemDialog;
import com.gaminho.lfc.databinding.EditionDialogAddShowcaseBinding;
import com.gaminho.lfc.model.ArtistSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Bonnie on 16/04/2022
 */
public class AddShowcaseDialog extends AddingItemDialog<ArtistSet, EditionDialogAddShowcaseBinding> {

    public AddShowcaseDialog(Context context, AddDialogListener<ArtistSet> listener) {
        super(context, listener);
    }

    @Override
    protected EditionDialogAddShowcaseBinding getBinding(Context context) {
        return EditionDialogAddShowcaseBinding.inflate(LayoutInflater.from(context));
    }

    @Override
    protected int getTitleResourceId() {
        return R.string.add_a_showcase;
    }

    @Override
    protected ArtistSet extractItemFromBinding(EditionDialogAddShowcaseBinding binding) {
        final ArtistSet artistSet = new ArtistSet();

        final String artist = binding.etArtist.getText().toString();
        artistSet.setArtist(artist);

        final int duration = StringUtils.isNotBlank(binding.etDuration.getText()) ?
                Integer.parseInt(binding.etDuration.getText().toString()) : 0;
        artistSet.setDuration(duration);

        final double price = StringUtils.isNotBlank(binding.etPrice.getText()) ?
                Double.parseDouble(binding.etPrice.getText().toString()) : 0.0D;
        artistSet.setPrice(price);

        return artistSet;
    }
}
