package com.gaminho.lfc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gaminho.lfc.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Bonnie on 16/04/2022
 */
public abstract class SimpleSpinnerAdapter<T> extends ArrayAdapter<T> {

    public SimpleSpinnerAdapter(@NonNull Context context, @NonNull List<T> objects) {
        super(context, R.layout.spinner_simple_item, objects);
        setDropDownViewResource(R.layout.spinner_simple_item);
    }

    public SimpleSpinnerAdapter(@NonNull Context context) {
        super(context, R.layout.spinner_simple_item, new ArrayList<>());
        setDropDownViewResource(R.layout.spinner_simple_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = super.getView(position, convertView, parent);
        final String text = getDisplayText(getItem(position));
        ((TextView) rowView.findViewById(android.R.id.text1)).setText(text);
        return rowView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = super.getDropDownView(position, convertView, parent);
        final String text = getDisplayText(getItem(position));
        ((TextView) rowView.findViewById(android.R.id.text1)).setText(text);
        return rowView;
    }

    public void updateList(final Collection<T> entities) {
        this.clear();
        this.addAll(entities);
        notifyDataSetChanged();
    }

    public abstract String getDisplayText(T entity);
}
