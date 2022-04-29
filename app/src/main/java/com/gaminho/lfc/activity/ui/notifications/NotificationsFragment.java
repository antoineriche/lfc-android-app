package com.gaminho.lfc.activity.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gaminho.lfc.activity.edition.ActivityEdition2;
import com.gaminho.lfc.databinding.FragmentNotificationsBinding;
import com.gaminho.lfc.model.LFCEdition;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private LFCEdition mLFCEdition;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mLFCEdition = ((ActivityEdition2) requireActivity()).getEdition();
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}