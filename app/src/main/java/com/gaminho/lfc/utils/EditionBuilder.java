package com.gaminho.lfc.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.gaminho.lfc.model.ArtistSet;
import com.gaminho.lfc.model.EditionService;
import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.enumeration.ServiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bonnie on 09/04/2022
 */
public final class EditionBuilder {

    public static LFCEdition buildEdition9() {
        final LFCEdition edition = new LFCEdition();
        edition.setEdition(9);
        edition.setDate(LocalDate.of(2022, 4, 28).toEpochDay());
        edition.setLocation("Le Central Perk, cours de la somme");

        final ArtistSet set = new ArtistSet();
        set.setArtist("Cholo");
        set.setDuration(45);
        set.setPrice(70D);

        final ArtistSet set2 = new ArtistSet();
        set2.setArtist("Straight");
        set2.setDuration(45);
        set2.setPrice(60D);
        edition.setArtistSetList(Arrays.asList(set, set2));

        final EditionService photo = new EditionService();
        photo.setType(ServiceType.PHOTO);
        photo.setName("Cassandre");
        photo.setPrice(30D);

        final EditionService video = new EditionService();
        video.setType(ServiceType.VIDEO);
        video.setName("Léa");
        video.setPrice(30D);

        final EditionService light = new EditionService();
        light.setType(ServiceType.LIGHT);
        light.setName("201");
        light.setPrice(30D);

        final EditionService sound = new EditionService();
        sound.setType(ServiceType.SOUND);
        sound.setName("Pale peu");
        sound.setPrice(40D);

        final EditionService dj = new EditionService();
        dj.setType(ServiceType.DJ);
        dj.setName("Kazaam");
        dj.setPrice(50D);

        edition.setEditionServices(Arrays.asList(photo, video, light, sound, dj));
        return edition;
    }

    public static Map<String, Object> buildEditionParamsForFacture(final LFCEdition edition) {
        final Map<String, Object> params = new HashMap<>();
        params.put("edition", edition.getEdition());
        params.put("date", DateParser.formatEpochDay(edition.getDate()));
        params.put("location", edition.getLocation());
        params.put("advertisement", edition.getAdvertisement());
        params.put("artistSets", edition.getArtistSetList());
        params.put("services", edition.getEditionServices());
        params.put("totalPrice", edition.getTotalPrice());
        params.put("today", LocalDateTime.now().format(DateTimeFormatter.ofPattern("'le' dd/MM/yyyy 'à' HH:mm")));
        return params;
    }

    private EditionBuilder() {
    }
}
