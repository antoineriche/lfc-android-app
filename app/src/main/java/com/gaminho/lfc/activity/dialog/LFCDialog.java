package com.gaminho.lfc.activity.dialog;

import android.app.AlertDialog;
import android.content.Context;

import androidx.viewbinding.ViewBinding;

import com.gaminho.lfc.R;

import java.util.Objects;

import lombok.Setter;

/**
 * Created by Bonnie on 16/04/2022
 */
@Setter
public abstract class LFCDialog<B extends ViewBinding> {

    private final Context mContext;
    protected final B mBinding;
    private LFCDialogListener mListener;

    private AlertDialog alertDialog;

    public LFCDialog(Context context,
                     LFCDialogListener listener) {
        this.mContext = context;
        this.mBinding = getBinding(context);
        this.mListener = listener;
    }

    public LFCDialog(Context context) {
        this(context, null);
    }

    protected abstract B getBinding(Context context);
    protected abstract int getTitleResourceId();

    protected void initView(B binding, Context context) {
    }

    protected int getNegativeButtonResourceId() {
        return android.R.string.cancel;
    }

    protected int getPositiveButtonResourceId() {
        return android.R.string.ok;
    }

    protected int getNeutralButtonResourceId() {
        return android.R.string.ok;
    }

    protected void dismiss() {
        if (Objects.nonNull(alertDialog)) {
            alertDialog.dismiss();
        }
    }

    protected AlertDialog build() {
        initView(mBinding, mContext);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setView(mBinding.getRoot())
                .setTitle(getTitleResourceId())
                .setNegativeButton(getNegativeButtonResourceId(), null)
                .setPositiveButton(getPositiveButtonResourceId(), null);

        if (needNeutralButton()) {
            builder.setNeutralButton(getNeutralButtonResourceId(), null);
        }

        return builder.create();
    }

    protected boolean needNeutralButton() {
        return false;
    }

    public void show() {
        alertDialog = build();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (Objects.nonNull(mListener)) {
                mListener.onPositiveClick(alertDialog);
            } else {
                alertDialog.dismiss();
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            if (Objects.nonNull(mListener)) {
                mListener.onNegativeClick(alertDialog);
            } else {
                alertDialog.dismiss();
            }
        });

        if (needNeutralButton()) {
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (Objects.nonNull(mListener)) {
                    mListener.onNeutralClick(alertDialog);
                } else {
                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.save);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(R.string.cancel);
    }

    public interface LFCDialogListener {
        void onPositiveClick(AlertDialog alertDialog);
        default void onNegativeClick(AlertDialog alertDialog) {
            alertDialog.dismiss();
        }

        default void onNeutralClick(AlertDialog alertDialog) {
        }
    }

}
