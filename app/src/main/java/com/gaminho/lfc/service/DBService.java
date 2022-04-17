package com.gaminho.lfc.service;

import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;

import com.gaminho.lfc.utils.CacheUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Bonnie on 10/04/2022
 */
public abstract class DBService<T extends DatabaseEntity> {

    private final DatabaseReference mReference;
    private final LruCache<String, T> cache;

    public DBService() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance(DBConstants.DB_URL);
        this.mReference = database.getReference().child(getTable());
        this.cache = CacheUtils.getCache(getTClass());
    }

    protected abstract String getTable();
    protected abstract Class<T> getTClass();

    protected DatabaseReference getTableReference() {
        return this.mReference;
    }

    public void initCache() {
        this.getAll(null);
    }

    protected void getAll(FetchingCollectionListener<T> listener) {
        if (cache.size() == 0) {
            this.mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final List<T> entities = new ArrayList<>();
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        entities.add(dsp.getValue(getTClass()));
                    }
                    entities.forEach(entity -> cache.put(entity.buildId(), entity));
                    if (Objects.nonNull(listener)) {
                        listener.onFetched(entities);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DB", "OOPS: " + databaseError.getDetails());
                    if (Objects.nonNull(listener)) {
                        listener.onError(databaseError);
                    }
                }
            });
        } else if (Objects.nonNull(listener)) {
            listener.onFetched(cache.snapshot().values());
        }
    }

    protected void getById(final String id,
                           FetchingListener<T> listener) {
        if (StringUtils.isNotBlank(id) && Objects.nonNull(cache.get(id))) {
            listener.onFetched(cache.get(id));
        } else {
            this.mReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final T entity = dataSnapshot.getValue(getTClass());
                    if (Objects.nonNull(entity)) {
                        cache.put(entity.buildId(), entity);
                    }
                    listener.onFetched(entity);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DB", "OOPS: " + databaseError.getDetails());
                    listener.onError(databaseError);
                }
            });
        }
    }

    protected void saveEntity(final T entity,
                              final OnCompleteListener<Void> listener) {
        this.mReference.child(entity.buildId()).setValue(entity).addOnCompleteListener(task -> {
            cache.put(entity.buildId(), entity);
            if (Objects.nonNull(listener)) {
                listener.onComplete(task);
            }
        });
    }

    public interface FetchingCollectionListener<T extends DatabaseEntity> {
        void onFetched(Collection<T> entities);
        void onError(DatabaseError error);
    }

    public interface FetchingListener<T extends DatabaseEntity> {
        void onFetched(T entity);
        void onError(DatabaseError error);
    }

}
