package com.k43nqtn.tudienanhviet;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;



public class WordActivity extends AppCompatActivity {

    Context context;

    LinearLayout detailsContainer;
    TextView tvLookupCount;
    Button prevButton, nextButton;
    HorizontalScrollView speechButtonsScroller;
    LinearLayout speechButtonsContainer;
    LinearLayout adContainer;
    AdView adView;
    Values values;
    MediaPlayer mediaPlayer = new MediaPlayer();



//    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3126660852581586~6866555592";
//    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";

//    private Button refresh;
//    private CheckBox startVideoAdsMuted;
//    private TextView videoStatus;

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
        initDatabases();

        detailsContainer = (LinearLayout) findViewById(R.id.details);
        speechButtonsScroller = (HorizontalScrollView) findViewById(R.id.speeches_scroller);
        speechButtonsContainer = (LinearLayout) findViewById(R.id.speeches_container);
        tvLookupCount = (TextView) findViewById(R.id.lookup_count);
        prevButton = (Button) findViewById(R.id.prev_word);
        nextButton = (Button) findViewById(R.id.next_word);
        adContainer = (LinearLayout) findViewById(R.id.ad_container);
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

        initBannerAd(MainActivity.ADMOB_UNIT_ID__BANNER_2, MainActivity.ADMOB_UNIT_ID__BANNER_2B);

//        MobileAds.initialize(this, ADMOB_APP_ID);
//        adContainer = new LinearLayout(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams.setMargins(0, values.DP_20, 0, values.DP_10);
//        adContainer.setLayoutParams(layoutParams);
//        adContainer.setGravity(Gravity.CENTER);
//
//        refreshAd();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void initDatabases() {
        if (MainActivity.dictRdb == null) {
            MainActivity.dictRdb = (new DictDbHelper(context)).getReadableDatabase();
        }
        if (MainActivity.openRdb == null || MainActivity.openWdb == null) {
            OpenDbHelper openDbHelper = new OpenDbHelper(context);
            MainActivity.openRdb = openDbHelper.getReadableDatabase();
            MainActivity.openWdb = openDbHelper.getWritableDatabase();
        }
    }

    private void initBannerAd(final String adUnitId, final String adUnitId2) {

        MobileAds.initialize(context, MainActivity.ADMOB_APP_ID);
        adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(adUnitId);


        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adContainer.removeAllViews();
                adContainer.setVisibility(View.VISIBLE);

                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adContainer.addView(adView);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);

                if (adUnitId2 != null) {
                    initBannerAd(adUnitId2, null);
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void _initBannerAd(final String adUnitId, final String adUnitId2) {
        if (adContainer == null) {
            adContainer = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, values.DP_20, 0, values.DP_5);
            adContainer.setLayoutParams(layoutParams);
            adContainer.setGravity(Gravity.CENTER);
        }


        MobileAds.initialize(context, MainActivity.ADMOB_APP_ID);
        adView = new AdView(context);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(adUnitId);

        if (adView.getParent() == null) {
            adContainer.addView(adView);
        }

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (adContainer.getParent() == null) {
                    if (detailsContainer.getChildCount() > 1) {
                        detailsContainer.addView(adContainer, 1);
                    } else {
                        detailsContainer.addView(adContainer);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                if (adUnitId2 != null) {
                    initBannerAd(adUnitId2, null);
                } else {
                    if (adContainer.getParent() != null) {
                        ((ViewGroup) adContainer.getParent()).removeView(adContainer);
                    }
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

//    private void initNativeAd() {
//        // Facebook audience network - native ads
//        nativeAd = new NativeAd(this, "591787627837331_600886950260732");
//        nativeAd.setAdListener(new AdListener() {
//
//            @Override
//            public void onError(Ad ad, AdError error) {
//                // Ad error callback
////                com.google.android.gms.ads.AdLoader adLoader = new com.google.android.gms.ads.AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
////                        .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
////                            @Override
////                            public void onAppInstallAdLoaded(NativeAppInstallAd appInstallAd) {
////                                // Show the app install ad.
////                            }
////                        })
////                        .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
////                            @Override
////                            public void onContentAdLoaded(NativeContentAd contentAd) {
////                                // Show the content ad.
////                            }
////                        })
////                        .withAdListener(new com.google.android.gms.ads.AdListener() {
////                            @Override
////                            public void onAdFailedToLoad(int errorCode) {
////                                // Handle the failure by logging, altering the UI, and so on.
////                            }
////                        })
////                        .withNativeAdOptions(new NativeAdOptions.Builder()
////                                // Methods in the NativeAdOptions.Builder class can be
////                                // used here to specify individual options settings.
////                                .build())
////                        .build();
////                adLoader.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
//
//
////                ca-app-pub-3126660852581586/7530283772
//                initBannerAd();
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Ad loaded callback
//                if (nativeAd != null) {
//                    nativeAd.unregisterView();
//                }
//
//                // Add the Ad view into the ad container.
//                LinearLayout adContainer = new LinearLayout(context);
//                LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//                adLayoutParams.setMargins(0, values.DP_10, 0, values.DP_10);
//                adContainer.setLayoutParams(adLayoutParams);
//                LayoutInflater inflater = LayoutInflater.from(WordActivity.this);
//                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad, adContainer, false);
//                adContainer.addView(adView);
//                if (detailsContainer.getChildCount() > 1) {
//                    detailsContainer.addView(adContainer, 1);
//                } else {
//                    detailsContainer.addView(adContainer);
//                }
//
//                // Create native UI using the ad metadata.
//                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
//                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
//                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
//                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
//                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
//                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
//
//                // Set the Text.
//                nativeAdTitle.setText(nativeAd.getAdTitle());
//                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//                nativeAdBody.setText(nativeAd.getAdBody());
//                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//
//                // Download and display the ad icon.
//                NativeAd.Image adIcon = nativeAd.getAdIcon();
//                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//                // Download and display the cover image.
//                nativeAdMedia.setNativeAd(nativeAd);
//
//                // Add the AdChoices icon
//                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
//                AdChoicesView adChoicesView = new AdChoicesView(WordActivity.this, nativeAd, true);
//                adChoicesContainer.addView(adChoicesView);
//
//                // Register the Title and CTA button to listen for clicks.
//                List<View> clickableViews = new ArrayList<>();
//                clickableViews.add(nativeAdTitle);
//                clickableViews.add(nativeAdCallToAction);
//                nativeAd.registerViewForInteraction(adContainer,clickableViews);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//            }
//        });
//
//        // Request an ad
//        nativeAd.loadAd();
//    }

//    private void initBannerAd() {
//        // Admob
////        MobileAds.initialize(this, "ca-app-pub-3126660852581586~6866555592");
////        AdView adView = (AdView) findViewById(R.id.adView);
////        AdRequest adRequest = new AdRequest.Builder().build();
////        adView.loadAd(adRequest);
//
//        // Add the Ad container into the Details container.
//        final LinearLayout adContainer = new LinearLayout(context);
//        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        adLayoutParams.setMargins(0, values.DP_20, 0, values.DP_10);
//        adContainer.setLayoutParams(adLayoutParams);
//        adContainer.setGravity(Gravity.CENTER);
//
//        // Instantiate an AdView view
//        bannerAd = new AdView(this, "591787627837331_601880423494718", AdSize.RECTANGLE_HEIGHT_250);
//
//        bannerAd.setAdListener(new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Replace with Admob
////                com.google.android.gms.ads.MobileAds.initialize(context, "ca-app-pub-3126660852581586~6866555592");
////                final com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(context);
////                adView.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
////                adView.setAdUnitId("ca-app-pub-3126660852581586/7530283772");
////
////                adView.setAdListener(new com.google.android.gms.ads.AdListener() {
////                    @Override
////                    public void onAdLoaded() {
////                        // Code to be executed when an ad finishes loading.
////                        adContainer.removeAllViews();
////                        adContainer.addView(adView);
////                    }
////
////                    @Override
////                    public void onAdFailedToLoad(int errorCode) {
////                        // Code to be executed when an ad request fails.
////                        if (adContainer.getParent() != null) {
////                            ((LinearLayout) adContainer.getParent()).removeView(adContainer);
////                        }
////                    }
////
////                    @Override
////                    public void onAdOpened() {
////                        // Code to be executed when an ad opens an overlay that
////                        // covers the screen.
////                    }
////
////                    @Override
////                    public void onAdLeftApplication() {
////                        // Code to be executed when the user has left the app.
////                    }
////
////                    @Override
////                    public void onAdClosed() {
////                        // Code to be executed when when the user is about to return
////                        // to the app after tapping on an ad.
////                    }
////                });
////
////                com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
////                adView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Ad loaded callback
//                // Add the Banner ad into Ad container
//                adContainer.addView(bannerAd);
//                if (detailsContainer.getChildCount() > 1) {
//                    detailsContainer.addView(adContainer, 1);
//                } else {
//                    detailsContainer.addView(adContainer);
//                }
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//            }
//        });
//
//        // Request an ad
//        bannerAd.loadAd();
//    }

    private void renderWordDetailsAsync(String word, String dir) {
        if (dir.compareTo("ev") == 0) {
            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_EV_1,
                    DictDbContract.TITLE_EV_1,
                    detailsContainer
            )).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_EV_2,
                    DictDbContract.TITLE_EV_2,
                    detailsContainer
            )).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_E_1,
                    DictDbContract.TITLE_E_1,
                    detailsContainer
            )).execute();

        } else {
            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_VE_1,
                    DictDbContract.TITLE_VE_1,
                    detailsContainer
            )).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_VE_2,
                    DictDbContract.TITLE_VE_2,
                    detailsContainer
            )).execute();

            (new WordDetailsRenderTask(
                    this,
                    word,
                    MainActivity.dictRdb,
                    DictDbContract.TABLE_VE_3,
                    DictDbContract.TITLE_VE_3,
                    detailsContainer
            )).execute();

        }
    }

    private void setupSpeechButtonsAsync(String word, String lang) {
        SpeechButtonsSetupTask speechButtonsSetupTask = new SpeechButtonsSetupTask(
                this,
                word,
                lang,
                MainActivity.dictRdb,
                mediaPlayer,
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
                MainActivity.openRdb,
                MainActivity.openWdb,
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
                MainActivity.openRdb,
                prevButton,
                nextButton
        );
        myWordSiblingsSetupTask.execute();
    }






//    /**
//     * Populates a {@link UnifiedNativeAdView} object with data from a given
//     * {@link UnifiedNativeAd}.
//     *
//     * @param nativeAd the object containing the ad's assets
//     * @param adView          the view to be populated
//     */
//    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
//        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
//        // have a video asset.
//        VideoController vc = nativeAd.getVideoController();
//
//        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//        // VideoController will call methods on this object when events occur in the video
//        // lifecycle.
//        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//            public void onVideoEnd() {
//                // Publishers should allow native ads to complete video playback before refreshing
//                // or replacing them with another ad in the same UI location.
////                refresh.setEnabled(true);
////                videoStatus.setText("Video status: Video playback has ended.");
//                super.onVideoEnd();
//            }
//        });
//
//        MediaView mediaView = adView.findViewById(R.id.ad_media);
//        ImageView mainImageView = adView.findViewById(R.id.ad_image);
//
//        // Apps can check the VideoController's hasVideoContent property to determine if the
//        // NativeAppInstallAd has a video asset.
//        if (vc.hasVideoContent()) {
//            adView.setMediaView(mediaView);
//            mainImageView.setVisibility(View.GONE);
////            videoStatus.setText(String.format(Locale.getDefault(),
////                    "Video status: Ad contains a %.2f:1 video asset.",
////                    vc.getAspectRatio()));
//        } else {
//            adView.setImageView(mainImageView);
//            mediaView.setVisibility(View.GONE);
//
//            // At least one image is guaranteed.
//            List<NativeAd.Image> images = nativeAd.getImages();
//            mainImageView.setImageDrawable(images.get(0).getDrawable());
//
////            refresh.setEnabled(true);
////            videoStatus.setText("Video status: Ad does not contain a video asset.");
//        }
//
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
//
//        // Some assets are guaranteed to be in every UnifiedNativeAd.
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//
//        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
//        // check before trying to display them.
//        if (nativeAd.getIcon() == null) {
//            adView.getIconView().setVisibility(View.GONE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(
//                    nativeAd.getIcon().getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getPrice() == null) {
//            adView.getPriceView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getPriceView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }
//
//        if (nativeAd.getStore() == null) {
//            adView.getStoreView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getStoreView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
//
//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getAdvertiser() == null) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }
//
//        adView.setNativeAd(nativeAd);
//    }
//
//    /**
//     * Creates a request for a new native ad based on the boolean parameters and calls the
//     * corresponding "populate" method when one is successfully returned.
//     *
//     */
//    private void refreshAd() {
//        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);
//
//        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//            // OnUnifiedNativeAdLoadedListener implementation.
//            @Override
//            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
//                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                        .inflate(R.layout.ad_unified, null);
//                populateUnifiedNativeAdView(unifiedNativeAd, adView);
//                if (adContainer.getParent() == null) {
//                    if (detailsContainer.getChildCount() > 1) {
//                        detailsContainer.addView(adContainer, 1);
//                    } else {
//                        detailsContainer.addView(adContainer);
//                    }
//                }
//                if (adView.getParent() == null) {
//                    adContainer.removeAllViews();
//                    adContainer.addView(adView);
//                }
//            }
//
//        });
//
//        VideoOptions videoOptions = new VideoOptions.Builder()
//                .setStartMuted(true)
//                .build();
//
//        NativeAdOptions adOptions = new NativeAdOptions.Builder()
//                .setVideoOptions(videoOptions)
//                .build();
//
//        builder.withNativeAdOptions(adOptions);
//
//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(context, "Failed to load native ad: "
//                        + errorCode, Toast.LENGTH_SHORT).show();
//            }
//        }).build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());
//
////        videoStatus.setText("");
//    }
}
