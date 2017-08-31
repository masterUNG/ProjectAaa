package com.kk.findgirlsandmeetadvice;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {
    public static boolean isGoogle = true; //true false

    public final String[] AllowHtmlAd = new String[]{"Page1.html", "Page2.html", "Page3.html",
            "Page3.1.html", "Page3.2.html", "Page3.3.html", "Page3.4.html",
            "Page3.5.html", "Page4.html", "Page5.html", "Page5.1.html",
            "Page5.2.html", "Page5.3.html" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initWebView();

    }

    WebView webview;

    private void initWebView() {
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/html/index.html");

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webview.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         for (String allowUrl : AllowHtmlAd) {
                                             if (url.toLowerCase().contains(allowUrl.toLowerCase())) {
                                                 adShow(url);
                                                 return true;
                                             }
                                         }

                                         view.loadUrl(url);

                                         return true;
                                     }

                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view, url);
                                     }

                                     @Override
                                     public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                                             error) {
                                         super.onReceivedError(view, request, error);
                                     }
                                 }

        );

        webview.setWebChromeClient(new

                                           WebChromeClient() {
                                               @Override
                                               public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                                                   return super.onConsoleMessage(consoleMessage);
                                               }

                                           }

        );
        // webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        if (isGoogle)

        {
            initBannerAds();
            initInterstitialAds();
        } else

        {
            initFBannerAds();
            initFbInterstitialAds();
        }
    }

    private void initBannerAds() {
        LinearLayout lnLayout = (LinearLayout) findViewById(R.id.activity_main);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_home_footer));
        lnLayout.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initFBannerAds() {
        LinearLayout lnLayout = (LinearLayout) findViewById(R.id.activity_main);

        com.facebook.ads.AdView mAdView = new com.facebook.ads.AdView(this,
                getString(R.string.fb_banner_home_footer), com.facebook.ads.AdSize.BANNER_320_50);
        lnLayout.addView(mAdView);
        mAdView.loadAd();
    }


    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd fBinterstitialAd;

    private void initInterstitialAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        requestNewInterstitial();
    }

    private void initFbInterstitialAds() {
        fBinterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_full_screen));
        fBinterstitialAd.loadAd();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void adShow(final String page) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
//                                webview.loadUrl(page);
                                initInterstitialAds();
                            }
                        });
                        webview.loadUrl(page);
                        mInterstitialAd.show();
                    } else {
                        webview.loadUrl(page);
                    }
                } else {
                    if (fBinterstitialAd != null && fBinterstitialAd.isAdLoaded()) {
                        fBinterstitialAd.setAdListener(new AbstractAdListener() {
                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                super.onInterstitialDismissed(ad);
                                initFbInterstitialAds();
                            }
                        });
                        webview.loadUrl(page);
                        fBinterstitialAd.show();
                    } else {
                        webview.loadUrl(page);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }

//        if (mInterstitialAd != null) {
//            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//            } else if (webview.canGoBack()) {
//                webview.goBack();
//            } else {
//                super.onBackPressed();
//            }
//        } else {
//            if (fBinterstitialAd != null && fBinterstitialAd.isAdLoaded()) {
//                fBinterstitialAd.show();
//            } else if (webview.canGoBack()) {
//                webview.goBack();
//            } else {
//                super.onBackPressed();
//            }
//        }
    }

    public class WebAppInterface {
        Context mContext;

        public WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void adShow(final String page) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mInterstitialAd != null) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    webview.loadUrl("file:///android_asset/html/" + page);
                                    initInterstitialAds();
                                }
                            });
                        }
                    } else {
                        if (fBinterstitialAd != null && fBinterstitialAd.isAdLoaded()) {
                            fBinterstitialAd.show();
                            fBinterstitialAd.setAdListener(new AbstractAdListener() {
                                @Override
                                public void onInterstitialDismissed(Ad ad) {
                                    super.onInterstitialDismissed(ad);
                                    webview.loadUrl("file:///android_asset/html/" + page);
                                    initFbInterstitialAds();
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
