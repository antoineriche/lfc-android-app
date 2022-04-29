package com.gaminho.lfc.service;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import com.gaminho.lfc.business.Html2Pdf;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.Location;
import com.gaminho.lfc.model.enumeration.PDFTemplate;
import com.gaminho.lfc.utils.EditionBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseError;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Bonnie on 10/04/2022
 */
public final class LFCEditionService extends DBService<LFCEdition> {

    private static final LocationService mLocationService = new LocationService();

    public void saveEdition(final LFCEdition edition,
                            final OnCompleteListener<Void> listener) {
        super.saveEntity(edition, listener);
    }

    public void getAllEditions(FetchingCollectionListener<LFCEdition> listener) {
        super.getAll(listener);
    }

    public void getEditionById(final String id,
                               final FetchingListener<LFCEdition> listener) {
        super.getById(id, listener);
    }

    public void deleteEdition(final String id,
                              final DeletionListener listener) {
        super.deleteEntity(id, listener);
    }

    public static void editFacture(final LFCEdition edition,
                                   final Context context,
                                   final Html2Pdf.OnCompleteConversion completionListener) {

        final String pdfName = String.format(Locale.FRANCE, "LFC-facture-%d-%s.pdf",
                edition.getEdition(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        final ObservableBoolean obsReady = new ObservableBoolean(false);
        obsReady.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (obsReady.get()) {
                    final Map<String, Object> params = EditionBuilder.buildEditionParamsForFacture(edition);
                    PDFService.editPDFFromChunk(PDFTemplate.FACTURE, pdfName, params, completionListener, context);
                }
            }
        });

        if (StringUtils.isNotBlank(edition.getLocation())) {
            obsReady.set(false);
            mLocationService.getLocationById(edition.getLocation(), new FetchingListener<Location>() {
                @Override
                public void onFetched(Location entity) {
                    edition.setLocation(entity.buildFullAddress());
                    obsReady.set(true);
                }

                @Override
                public void onError(DatabaseError error) {

                }
            });
        } else {
            obsReady.set(true);
        }
    }

    @Override
    protected String getTable() {
        return DBConstants.DB_TABLE_EDITION;
    }

    @Override
    protected Class<LFCEdition> getTClass() {
        return LFCEdition.class;
    }

}
