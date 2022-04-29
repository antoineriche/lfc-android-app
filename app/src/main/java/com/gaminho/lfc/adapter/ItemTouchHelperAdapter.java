package com.gaminho.lfc.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Bonnie on 29/04/2022
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onMoved(@NonNull RecyclerView recyclerView);

}
