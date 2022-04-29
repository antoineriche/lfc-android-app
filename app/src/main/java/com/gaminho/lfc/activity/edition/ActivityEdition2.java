package com.gaminho.lfc.activity.edition;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gaminho.lfc.R;
import com.gaminho.lfc.activity.MainActivity;
import com.gaminho.lfc.databinding.ActivityEdition2Binding;
import com.gaminho.lfc.model.LFCEdition;

public class ActivityEdition2 extends AppCompatActivity {

    private LFCEdition currentEdition;
    private String editionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEdition2Binding binding = ActivityEdition2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editionId = getIntent().getStringExtra(EditionActivity.ARG_EDITION_ID);
        setTitle("Edition #" + editionId);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_edition2);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void setEdition(LFCEdition edition) {
        this.currentEdition = edition;
    }

    public LFCEdition getEdition() {
        return this.currentEdition;
    }

    public void quitEditionActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public String getEditionId() {
       return getIntent().getStringExtra(EditionActivity.ARG_EDITION_ID);
    }
}