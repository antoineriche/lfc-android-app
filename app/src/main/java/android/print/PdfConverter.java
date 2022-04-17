package android.print;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * Created by Bonnie on 09/04/2022
 */
public class PdfConverter implements Runnable {
    private Context mContext;
    private String mHtmlString;
    private File mPdfFile;
    private PrintAttributes pdfPrintAttrs;
    private boolean mIsCurrentlyConverting;
    private WebView mWebView;
    private PdfConverter.Companion.OnComplete mOnComplete;
    private static final String TAG = "PdfConverter";
    private static PdfConverter sInstance;
    public static final PdfConverter.Companion Companion = new PdfConverter.Companion();

    private PrintAttributes getPdfPrintAttrs() {
        return this.pdfPrintAttrs != null ? this.pdfPrintAttrs : this.getDefaultPrintAttrs();
    }

    private ParcelFileDescriptor getOutputFileDescriptor() {
        try {
            File var10000 = this.mPdfFile;
            var10000.createNewFile();
            return ParcelFileDescriptor.open(this.mPdfFile, 872415232);
        } catch (Exception var2) {
            Log.d("PdfConverter", "Failed to open ParcelFileDescriptor", (Throwable)var2);
            return null;
        }
    }

    private PrintAttributes getDefaultPrintAttrs() {
        return Build.VERSION.SDK_INT < 19 ? null : (new PrintAttributes.Builder()).setMediaSize(PrintAttributes.MediaSize.ISO_A4).setResolution(new PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600)).setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
    }

    public void run() {
        this.mWebView = new WebView(this.mContext);
        WebView var10000 = this.mWebView;
        var10000.setWebViewClient((WebViewClient)(new WebViewClient() {
            public void onPageFinished(@NonNull WebView view, @NonNull String url) {
                if (Build.VERSION.SDK_INT < 19) {
                    throw new RuntimeException("call requires API level 19");
                } else {
                    WebView var10000 = PdfConverter.this.mWebView;
                    final PrintDocumentAdapter documentAdapter = var10000.createPrintDocumentAdapter();
                    documentAdapter.onLayout(null, PdfConverter.this.getPdfPrintAttrs(), null, new PrintDocumentAdapter.LayoutResultCallback() {
                        public void onLayoutFinished(@Nullable PrintDocumentInfo info, boolean changed) {
                            super.onLayoutFinished(info, changed);
                            documentAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, PdfConverter.this.getOutputFileDescriptor(), null, new PrintDocumentAdapter.WriteResultCallback() {
                                public void onWriteFinished(@NonNull PageRange[] pages) {
                                    PdfConverter.Companion.OnComplete var10000 = PdfConverter.this.mOnComplete;
                                    if (var10000 != null) {
                                        var10000.onWriteComplete();
                                    }

                                    PdfConverter.this.destroy();
                                }

                                public void onWriteFailed(@Nullable CharSequence error) {
                                    super.onWriteFailed(error);
                                    PdfConverter.Companion.OnComplete var10000 = PdfConverter.this.mOnComplete;
                                    if (var10000 != null) {
                                        var10000.onWriteFailed();
                                    }

                                }
                            });
                        }
                    }, null);
                }
            }
        }));
        var10000 = this.mWebView;
        var10000.loadDataWithBaseURL("", this.mHtmlString, "text/html", "UTF-8", (String)null);
    }

    public final void convert(@Nullable Context context, @Nullable String htmlString, @Nullable File file, @Nullable PdfConverter.Companion.OnComplete onComplete) throws Exception {
        if (context == null) {
            throw new Exception("context can't be null");
        } else if (htmlString == null) {
            throw new Exception("htmlString can't be null");
        } else if (file == null) {
            throw new Exception("file can't be null");
        } else if (!this.mIsCurrentlyConverting) {
            this.mContext = context;
            this.mHtmlString = htmlString;
            this.mPdfFile = file;
            this.mIsCurrentlyConverting = true;
            this.mOnComplete = onComplete;
            this.runOnUiThread((Runnable)this);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        Context var10002 = this.mContext;
        Handler handler = new Handler(var10002.getMainLooper());
        handler.post(runnable);
    }

    private void destroy() {
        this.mContext = (Context)null;
        this.mHtmlString = (String)null;
        this.mPdfFile = (File)null;
        this.pdfPrintAttrs = (PrintAttributes)null;
        this.mIsCurrentlyConverting = false;
        this.mWebView = (WebView)null;
        this.mOnComplete = (PdfConverter.Companion.OnComplete)null;
    }

    private PdfConverter() {
    }

    // $FF: synthetic method
    public static final void access$setMWebView$p(PdfConverter $this, WebView var1) {
        $this.mWebView = var1;
    }

    // $FF: synthetic method
    public static final void access$setPdfPrintAttrs$p(PdfConverter $this, PrintAttributes var1) {
        $this.pdfPrintAttrs = var1;
    }

    // $FF: synthetic method
    public static final void access$setMOnComplete$p(PdfConverter $this, PdfConverter.Companion.OnComplete var1) {
        $this.mOnComplete = var1;
    }


    public static final class Companion {
        @NonNull
        public final PdfConverter getInstance() {
            if (PdfConverter.sInstance == null) {
                PdfConverter.sInstance = new PdfConverter();
            }

            PdfConverter var10000 = PdfConverter.sInstance;
            return var10000;
        }

        private Companion() {
        }

        public interface OnComplete {
            void onWriteComplete();

            void onWriteFailed();
        }
    }
}


