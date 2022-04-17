package com.gaminho.lfc.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gaminho.lfc.business.Html2Pdf;
import com.gaminho.lfc.model.enumeration.PDFTemplate;
import com.x5.template.Chunk;
import com.x5.template.Theme;
import com.x5.template.providers.AndroidTemplates;

import java.io.File;
import java.util.Map;

/**
 * Created by Bonnie on 14/04/2022
 */
public final class PDFService {

    protected static Chunk initChunkFromTemplate(final PDFTemplate template,
                                                 final Context context) {
        final AndroidTemplates loader = new AndroidTemplates(context);
        return new Theme(loader).makeChunk(template.getTemplateName());
    }

    public static void editPDFFromChunk(final PDFTemplate template,
                                        final String pdfName,
                                        final Map<String, Object> params,
                                        final Html2Pdf.OnCompleteConversion completionListener,
                                        final Context context) {

        final Chunk chunk = initChunkFromTemplate(template, context);
        params.forEach(chunk::set);
        final File outputPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName);

        try {
            final Html2Pdf converter = new Html2Pdf.Companion.Builder()
                    .context(context)
                    .html(chunk.toString())
                    .file(outputPdf)
                    .build();
            converter.convertToPdf(completionListener);
        } catch (Exception e) {
            Log.e("FILE", "can not convert to pdf: " + e.getMessage());
            completionListener.onFailed();
        }
    }

    private PDFService() {
    }
}
