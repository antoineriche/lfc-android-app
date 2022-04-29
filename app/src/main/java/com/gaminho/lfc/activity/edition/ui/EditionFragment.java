package com.gaminho.lfc.activity.edition.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.gaminho.lfc.activity.edition.ActivityEdition2;
import com.gaminho.lfc.model.LFCEdition;

/**
 * Created by Bonnie on 29/04/2022
 */
abstract class EditionFragment<T extends ViewBinding> extends Fragment {

    protected abstract T getBinding(@NonNull LayoutInflater inflater,
                                    ViewGroup container);

    protected abstract void initView(T binding, LFCEdition edition);

    protected T binding;
    protected LFCEdition mLFCEdition;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mLFCEdition = ((ActivityEdition2) requireActivity()).getEdition();
        binding = getBinding(inflater, container);
        initView(binding, mLFCEdition);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void quitActivity() {
        ((ActivityEdition2) requireActivity()).quitEditionActivity();
    }

    public LFCEdition getLFCEdition() {
        return mLFCEdition;
    }
}
