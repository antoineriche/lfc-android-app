package com.gaminho.lfc.activity.edition.dialog;

import android.app.AlertDialog;
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
        
        setMListener(new LFCDialogListener() {
            @Override
            public void onPositiveClick(AlertDialog alertDialog) {
                final LFCPrestation entity = extractItemFromBinding(mBinding);
                mListener.onItemAddClick(entity, alertDialog);
            }

            @Override
            public void onNeutralClick(AlertDialog alertDialog) {
                mListener.onDeletePrestationClick(mLFCPrestation);
                alertDialog.dismiss();
            }
        });
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
    protected boolean needNeutralButton() {
        return true;
    }

    @Override
    protected int getNeutralButtonResourceId() {
        return R.string.remove_prestation;
    }

    @Override
    protected void initView(EditionDialogUpdatePrestationBinding binding, Context context) {
        super.initView(binding, context);
        binding.etPrice.setText(String.valueOf(mLFCPrestation.getPrestationPrice()));
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
