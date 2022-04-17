package com.gaminho.lfc.service;

import com.gaminho.lfc.model.Liquidity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bonnie on 10/04/2022
 */
public final class LiquidityService extends DBService<Liquidity> {

    public void saveLiquidity(final Liquidity liquidity,
                              final OnCompleteListener<Void> listener) {
        super.saveEntity(liquidity, listener);
    }

    public void getAllLiquidity(FetchingCollectionListener<Liquidity> listener) {
        super.getAll(listener);
    }

    public void getLiquidityById(final String id,
                                 final FetchingListener<Liquidity> listener) {
        super.getById(id, listener);
    }

    public void getLastLiquidity(final FetchingListener<Liquidity> listener) {
        super.getAll(new FetchingCollectionListener<Liquidity>() {
            @Override
            public void onFetched(Collection<Liquidity> entities) {
                if (entities.isEmpty()) {
                    listener.onFetched(null);
                } else {
                    final List<Liquidity> liquidityList = new ArrayList<>(entities);
                    liquidityList.sort(Comparator.comparingLong(edition -> ((Liquidity) edition).getDate()));
                    Collections.reverse(liquidityList);
                    listener.onFetched(liquidityList.get(0));
                }
            }

            @Override
            public void onError(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    @Override
    protected String getTable() {
        return DBConstants.DB_TABLE_LIQUIDITY;
    }

    @Override
    protected Class<Liquidity> getTClass() {
        return Liquidity.class;
    }

}
