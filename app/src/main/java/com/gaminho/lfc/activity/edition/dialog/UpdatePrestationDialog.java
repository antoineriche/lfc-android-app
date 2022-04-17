package com.gaminho.lfc.activity.edition.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.dialog.AddingItemDialog;
import com.gaminho.lfc.databinding.EditionDialogUpdatePrestationBinding;
import com.gaminho.lfc.model.LFCPrestation;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Bonnie on 16/04/2022
 */
public class UpdatePrestationDialog extends AddingItemDialog<LFCPrestation, EditionDialogUpdatePrestationBinding> {

    private final LFCPrestation mLFCPrestation;
    protected final UpdatePrestationListener mListener;

    public UpdatePrestationDialog(Context context,
                                  LFCPrestation prestation,
                                  UpdatePrestationListener listener) {
        super(context, listener);
        this.mListener = listener;
        this.mLFCPrestation = prestation;
    }

    @Override
    protected EditionDialogUpdatePrestationBinding getBinding(Context context) {
        return EditionDialogUpdatePrestationBinding.inflate(LayoutInflater.from(context));
    }

    @Override
    protected int getTitleResourceId() {
        return R.string.update_prestation;
    }

    @Override
    protected void initView(EditionDialogUpdatePrestationBinding binding, Context context) {
        super.initView(binding, context);

        binding.etPrice.setText(String.valueOf(mLFCPrestation.getPrestationPrice()));

        binding.btnRemove.setOnClickListener(v -> {
            this.mListener.onDeletePrestationClick(mLFCPrestation);
            dismiss();
        });
    }

    @Override
    protected LFCPrestation extractItemFromBinding(EditionDialogUpdatePrestationBinding binding) {
        final double price = StringUtils.isNotBlank(binding.etPrice.getText()) ?
                Double.parseDouble(binding.etPrice.getText().toString()) : 0.0D;

        mLFCPrestation.setPrestationPrice(price);
        return mLFCPrestation;
    }

    public interface UpdatePrestationListener extends AddDialogListener<LFCPrestation> {
        void onDeletePrestationClick(LFCPrestation prestation);
    }
}
