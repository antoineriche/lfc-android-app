package com.gaminho.lfc.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Bonnie on 14/04/2022
 */
@Getter
@RequiredArgsConstructor
public enum PDFTemplate {
    FACTURE("facture");

    private final String templateName;
}
