package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.formats.NativeAdOptions;
//import com.google.android.gms.ads.formats.NativeAppInstallAd;
//import com.google.android.gms.ads.formats.NativeContentAd;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.facebook.ads.AdSize;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

public class WordActivity extends AppCompatActivity {

    Context context;
    MyApplication myApplication;

    LinearLayout detailsContainer;
    TextView tvLookupCount;
    Button prevButton, nextButton;
    HorizontalScrollView speechButtonsScroller;
    LinearLayout speechButtonsContainer;
    NativeAd nativeAd;
    AdView bannerAd;
    Values values;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        context = this;
        myApplication = (MyApplication) this.getApplication();
        initDatabases();

        detailsContainer = (LinearLayout) findViewById(R.id.details);
        speechButtonsScroller = (HorizontalScrollView) findViewById(R.id.speeches_scroller);
        speechButtonsContainer = (LinearLayout) findViewById(R.id.speeches_container);
        tvLookupCount = (TextView) findViewById(R.id.lookup_count);
        prevButton = (Button) findViewById(R.id.prev_word);
        nextButton = (Button) findViewById(R.id.next_word);
        values = new Values(context);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String dir = extras.getString("dir");
            String word = extras.getString("word");
            String lastLookupTime = extras.getString("last_lookup_time");

            if (dir != null && word != null) {
                setTitle(word);

                if (lastLookupTime != null && lastLookupTime.compareTo("NOW") == 0) {
                    updateLookupCountAsync(word, dir);
                }

                if (dir.compareTo("ev") == 0) {
                    setupSpeechButtonsAsync(word, "en");
                }

                renderWordDetailsAsync(word, dir);

                setupMyWordSiblingsAsync(word, lastLookupTime);
            }
        }

        initNativeAd();

    }

    private void initDatabases() {
        if (myApplication.dictRdb == null) {
            myApplication.dictRdb = (new DictDbHelper(this)).getReadableDatabase();
        }
        if (myApplication.openRdb == null || myApplication.openWdb == null) {
            OpenDbHelper openDbHelper = new OpenDbHelper(this);
            myApplication.openRdb = openDbHelper.getReadableDatabase();
            myApplication.openWdb = openDbHelper.getWritableDatabase();
        }
    }

    private void initNativeAd() {
        // Facebook audience network - native ads
        nativeAd = new NativeAd(this, "591787627837331_600886950260732");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
//                com.google.android.gms.ads.AdLoader adLoader = new com.google.android.gms.ads.AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
//                        .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
//                            @Override
//                            public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
//                                // Show the app install ad.
//                            }
//                        })
//                        .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//                            @Override
//                            public void onContentAdLoaded(NativeContentAd contentAd) {
//                                // Show the content ad.
//                            }
//                        })
//                        .withAdListener(new com.google.android.gms.ads.AdListener() {
//                            @Override
//                            public void onAdFailedToLoad(int errorCode) {
//                                // Handle the failure by logging, altering the UI, and so on.
//                            }
//                        })
//                        .withNativeAdOptions(new NativeAdOptions.Builder()
//                                // Methods in the NativeAdOptions.Builder class can be
//                                // used here to specify individual options settings.
//                                .build())
//                        .build();
//                adLoader.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());


//                ca-app-pub-3126660852581586/7530283772
                initBannerAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                if (nativeAd != null) {
                    nativeAd.unregisterView();
                }

                // Add the Ad view into the ad container.
                LinearLayout adContainer = new LinearLayout(context);
                LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );
                adLayoutParams.setMargins(0, values.DP_10, 0, values.DP_10);
                adContainer.setLayoutParams(adLayoutParams);
                LayoutInflater inflater = LayoutInflater.from(WordActivity.this);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad, adContainer, false);
                adContainer.addView(adView);
                if (detailsContainer.getChildCount() > 1) {
                    detailsContainer.addView(adContainer, 1);
                } else {
                    detailsContainer.addView(adContainer);
                }

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(WordActivity.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(adContainer,clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

    private void initBannerAd() {
        // Admob
//        MobileAds.initialize(this, "ca-app-pub-3126660852581586~6866555592");
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        // Add the Ad container into the Details container.
        final LinearLayout adContainer = new LinearLayout(context);
        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        adLayoutParams.setMargins(0, values.DP_20, 0, values.DP_10);
        adContainer.setLayoutParams(adLayoutParams);
        adContainer.setGravity(Gravity.CENTER);

        // Instantiate an AdView view
        bannerAd = new AdView(this, "591787627837331_601880423494718", AdSize.RECTANGLE_HEIGHT_250);

        bannerAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Replace with Admob
//                com.google.android.gms.ads.MobileAds.initialize(context, "ca-app-pub-3126660852581586~6866555592");
//                final com.google.android.gms.ads.AdView mAdView = new com.google.android.gms.ads.AdView(context);
//                mAdView.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
//                mAdView.setAdUnitId("ca-app-pub-3126660852581586/7530283772");
//
//                mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
//                    @Override
//                    public void onAdLoaded() {
//                        // Code to be executed when an ad finishes loading.
//                        adContainer.removeAllViews();
//                        adContainer.addView(mAdView);
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(int errorCode) {
//                        // Code to be executed when an ad request fails.
//                        if (adContainer.getParent() != null) {
//                            ((LinearLayout) adContainer.getParent()).removeView(adContainer);
//                        }
//                    }
//
//                    @Override
//                    public void onAdOpened() {
//                        // Code to be executed when an ad opens an overlay that
//                        // covers the screen.
//                    }
//
//                    @Override
//                    public void onAdLeftApplication() {
//                        // Code to be executed when the user has left the app.
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        // Code to be executed when when the user is about to return
//                        // to the app after tapping on an ad.
//                    }
//                });
//
//                com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
//                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                // Add the Banner ad into Ad container
                adContainer.addView(bannerAd);
                if (detailsContainer.getChildCount() > 1) {
                    detailsContainer.addView(adContainer, 1);
                } else {
                    detailsContainer.addView(adContainer);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        bannerAd.loadAd();
    }

    private void renderWordDetailsAsync(String word, String dir) {
        if (dir.compareTo("ev") == 0) {
            (new WordDetailsRenderTask(
                    this,
                    word,
                    myApplication.dictRdb,
                    DictDbContract.TABLE_EV_1,
                    DictDbContract.TITLE_EV_1,
                    detailsContainer)
            ).execute();

//            (new WordDetailsRenderTask(
//                    this,
//                    word,
//                    myApplication.dictRdb,
//                    DictDbContract.TABLE_EV_2,
//                    DictDbContract.TITLE_EV_2,
//                    detailsContainer)
//            ).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    myApplication.dictRdb,
                    DictDbContract.TABLE_E_1,
                    DictDbContract.TITLE_E_1,
                    detailsContainer)
            ).execute();

        } else {
            (new WordDetailsRenderTask(
                    this,
                    word,
                    myApplication.dictRdb,
                    DictDbContract.TABLE_VE_1,
                    DictDbContract.TITLE_VE_1,
                    detailsContainer)
            ).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    myApplication.dictRdb,
                    DictDbContract.TABLE_VE_2,
                    DictDbContract.TITLE_VE_2,
                    detailsContainer)
            ).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    myApplication.dictRdb,
                    DictDbContract.TABLE_VE_3,
                    DictDbContract.TITLE_VE_3,
                    detailsContainer)
            ).execute();

        }
    }

    private void setupSpeechButtonsAsync(String word, String lang) {
        SpeechButtonsSetupTask speechButtonsSetupTask = new SpeechButtonsSetupTask(
                this,
                word,
                lang,
                myApplication.dictRdb,
                speechButtonsScroller,
                speechButtonsContainer
        );
        speechButtonsSetupTask.execute();
    }


    private void updateLookupCountAsync(String word, String dir) {
        LookupCountUpdateTask lookupCountUpdateTask = new LookupCountUpdateTask(
                this,
                word,
                dir,
                myApplication.openRdb,
                myApplication.openWdb,
                tvLookupCount
                );
        lookupCountUpdateTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_back_search:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupMyWordSiblingsAsync(String word, String lastLookupTime) {
        MyWordSiblingsSetupTask myWordSiblingsSetupTask = new MyWordSiblingsSetupTask(
                this,
                word,
                lastLookupTime,
                myApplication.openRdb,
                prevButton,
                nextButton
        );
        myWordSiblingsSetupTask.execute();
    }
}
