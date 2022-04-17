package com.gaminho.lfc.activity.dialog;

import android.app.AlertDialog;
import android.content.Context;

import androidx.viewbinding.ViewBinding;

/**
 * Created by Bonnie on 16/04/2022
 */
public abstract class AddingItemDialog<T, B extends ViewBinding> extends LFCDialog<B> {

    protected final AddDialogListener<T> mListener;

    public AddingItemDialog(Context context, AddDialogListener<T> listener) {
        super(context);
        this.mListener = listener;

        setMListener(alertDialog -> {
            final T entity = extractItemFromBinding(mBinding);
            mListener.onItemAddClick(entity, alertDialog);
        });
    }

    protected abstract T extractItemFromBinding(B binding);
    protected void initView(B binding, Context context) {
    }

    public interface AddDialogListener<T> {
        void onItemAddClick(T entity, AlertDialog alertDialog);
    }
}
