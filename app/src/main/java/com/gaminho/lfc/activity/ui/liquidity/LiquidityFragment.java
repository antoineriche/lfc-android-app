package com.gaminho.lfc.activity.ui.liquidity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;

import com.gaminho.lfc.databinding.FragmentLiquidityBinding;
import com.gaminho.lfc.model.Liquidity;
import com.gaminho.lfc.service.DBService;
import com.gaminho.lfc.service.LiquidityService;
import com.gaminho.lfc.utils.DateParser;
import com.gaminho.lfc.utils.ParserUtils;
import com.google.firebase.database.DatabaseError;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

public class LiquidityFragment extends Fragment {

    private FragmentLiquidityBinding binding;
    private double previousTotal;
    private final LiquidityService liquidityService = new LiquidityService();
    private final ObservableField<Liquidity> obsLiquidity = new ObservableField<>();

    private final Observable.OnPropertyChangedCallback fetchLiquidityListener = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            final Liquidity liquidity = obsLiquidity.get();

            fillView(liquidity);
            if (liquidity.getTotal() == extractLiquidityFromView().getTotal()) {
                binding.btnSaveLiquidity.setEnabled(false);
            }
            previousTotal = liquidity.getTotal();
            obsLiquidity.removeOnPropertyChangedCallback(fetchLiquidityListener);
        }
    };

    private void fillView(Liquidity liquidity) {
        binding.totalLiquidity.setText(String.valueOf(liquidity.getTotal()));
        binding.etBankAccount.setText(String.valueOf(liquidity.getBankAccount()));
        binding.etBillets500.setText(String.valueOf(liquidity.getBillets500()));
        binding.etBillets200.setText(String.valueOf(liquidity.getBillets200()));
        binding.etBillets100.setText(String.valueOf(liquidity.getBillets100()));
        binding.etBillets50.setText(String.valueOf(liquidity.getBillets50()));
        binding.etBillets20.setText(String.valueOf(liquidity.getBillets20()));
        binding.etBillets10.setText(String.valueOf(liquidity.getBillets10()));
        binding.etBillets5.setText(String.valueOf(liquidity.getBillets5()));
        binding.etCoins2.setText(String.valueOf(liquidity.getCoins2()));
        binding.etCoins1.setText(String.valueOf(liquidity.getCoins1()));
        binding.etCoins05.setText(String.valueOf(liquidity.getCoins05()));
        binding.etCoins02.setText(String.valueOf(liquidity.getCoins02()));
        binding.etCoins01.setText(String.valueOf(liquidity.getCoins01()));
        binding.date.setText(DateParser.formatEpochDay(liquidity.getDate()));
    }

    private Liquidity extractLiquidityFromView() {
        final Liquidity liquidity = new Liquidity();
        liquidity.setDate(LocalDate.now().toEpochDay());
        liquidity.setBankAccount(ParserUtils.extractDoubleFromTextView(binding.etBankAccount));
        liquidity.setBillets500(ParserUtils.extractIntegerFromTextView(binding.etBillets500));
        liquidity.setBillets200(ParserUtils.extractIntegerFromTextView(binding.etBillets200));
        liquidity.setBillets100(ParserUtils.extractIntegerFromTextView(binding.etBillets100));
        liquidity.setBillets50(ParserUtils.extractIntegerFromTextView(binding.etBillets50));
        liquidity.setBillets20(ParserUtils.extractIntegerFromTextView(binding.etBillets20));
        liquidity.setBillets10(ParserUtils.extractIntegerFromTextView(binding.etBillets10));
        liquidity.setBillets5(ParserUtils.extractIntegerFromTextView(binding.etBillets5));
        liquidity.setCoins2(ParserUtils.extractIntegerFromTextView(binding.etCoins2));
        liquidity.setCoins1(ParserUtils.extractIntegerFromTextView(binding.etCoins1));
        liquidity.setCoins05(ParserUtils.extractIntegerFromTextView(binding.etCoins05));
        liquidity.setCoins02(ParserUtils.extractIntegerFromTextView(binding.etCoins02));
        liquidity.setCoins01(ParserUtils.extractIntegerFromTextView(binding.etCoins01));
        return liquidity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLiquidityBinding.inflate(inflater, container, false);

        binding.btnSaveLiquidity.setOnClickListener(view -> {
            final Liquidity liquidity = extractLiquidityFromView();
            liquidityService.saveLiquidity(liquidity, null);
        });

        this.obsLiquidity.addOnPropertyChangedCallback(fetchLiquidityListener);

        Stream.of(binding.etBankAccount, binding.etBillets500, binding.etBillets200,
                binding.etBillets100, binding.etBillets50, binding.etBillets20, binding.etBillets10,
                binding.etBillets5, binding.etCoins2, binding.etCoins1, binding.etCoins05,
                binding.etCoins02, binding.etCoins01).forEach(editText ->
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        computeTotal();
                    }
                }));

        return binding.getRoot();
    }

    private void computeTotal() {
        final double total = ParserUtils.extractDoubleFromTextView(binding.etBankAccount)
                + ParserUtils.extractIntegerFromTextView(binding.etBillets500) * 500
                + ParserUtils.extractIntegerFromTextView(binding.etBillets200) * 200
                + ParserUtils.extractIntegerFromTextView(binding.etBillets100) * 100
                + ParserUtils.extractIntegerFromTextView(binding.etBillets50) * 50
                + ParserUtils.extractIntegerFromTextView(binding.etBillets20) * 20
                + ParserUtils.extractIntegerFromTextView(binding.etBillets10) * 10
                + ParserUtils.extractIntegerFromTextView(binding.etBillets5) * 5
                + ParserUtils.extractIntegerFromTextView(binding.etCoins2) * 2
                + ParserUtils.extractIntegerFromTextView(binding.etCoins1)
                + ParserUtils.extractIntegerFromTextView(binding.etCoins05) * 0.5
                + ParserUtils.extractIntegerFromTextView(binding.etCoins02) * 0.2
                + ParserUtils.extractIntegerFromTextView(binding.etCoins01) * 0.1;
        binding.totalLiquidity.setText(String.valueOf(total));

        binding.btnSaveLiquidity.setEnabled(previousTotal != total);
    }

    @Override
    public void onResume() {
        super.onResume();
        liquidityService.getLastLiquidity(new DBService.FetchingListener<Liquidity>() {
            @Override
            public void onFetched(Liquidity entity) {
                if (Objects.isNull(entity)) {
                    entity = new Liquidity();
                    entity.setDate(LocalDate.now().toEpochDay());
                }
                obsLiquidity.set(entity);
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}