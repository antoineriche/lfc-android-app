package com.gaminho.lfc.business;

import android.content.Context;
import android.print.PdfConverter;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * Created by Bonnie on 09/04/2022
 */
public class Html2Pdf {
    private final Context context;
    private final String html;
    private final File file;

    public final void convertToPdf(@NonNull final Html2Pdf.OnCompleteConversion onCompleteConversion) throws Exception {
        PdfConverter.Companion.getInstance().convert(this.context, this.html, this.file, new PdfConverter.Companion.OnComplete() {
            public void onWriteComplete() {
                onCompleteConversion.onSuccess(file.getName());
            }

            public void onWriteFailed() {
                onCompleteConversion.onFailed();
            }
        });
    }

    private Html2Pdf(Context context, String html, File file) {
        this.context = context;
        this.html = html;
        this.file = file;
    }

    public interface OnCompleteConversion {
        void onSuccess(final String pdfName);

        void onFailed();
    }

    public static final class Companion {
        private Companion() {
        }

        public static final class Builder {
            private Context context;
            private String html;
            private File file;

            @NonNull
            public final Html2Pdf.Companion.Builder context(@NonNull Context context) {
                this.context = context;
                return this;
            }

            @NonNull
            public final Html2Pdf.Companion.Builder html(@NonNull String html) {
                this.html = html;
                return this;
            }

            @NonNull
            public final Html2Pdf.Companion.Builder file(@NonNull File file) {
                this.file = file;
                return this;
            }

            @NonNull
            public final Html2Pdf build() {
                return new Html2Pdf(context, html, file);
            }
        }
    }
}
