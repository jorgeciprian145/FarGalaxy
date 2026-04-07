package com.example.fargalaxy.ads

import android.app.Activity
import android.content.Context
import com.example.fargalaxy.data.AdsRepository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.RequestConfiguration

/**
 * AdManager - handles loading and showing interstitial ads.
 *
 * This class itself does NOT enforce frequency rules. That logic lives in [AdsRepository].
 * Call [maybeShowInterstitialOnCareerOrVaultVisit] from MainScreen when user visits
 * Career or Vault pages; it will combine visit-count + daily cap + cooldown rules.
 */
object AdManager {
    private var interstitialAd: InterstitialAd? = null

    // TODO: Replace this with your real AdMob interstitial ad unit ID before release.
    // This is Google's sample test ID and is safe for development.
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4733251926083453/8274858297"

    /**
     * Initialize the Google Mobile Ads SDK.
     * Safe to call multiple times; initialization is idempotent.
     */
    fun initialize(context: Context, onInitialized: () -> Unit = {}) {

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("EB3EF56509E4305D7CEDD508687CBB60"))
                .build()
        )

        MobileAds.initialize(context)

        onInitialized()
    }

    /**
     * Preload an interstitial ad if one is not already loaded.
     */
    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null) {
            return
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    /**
     * Called when user navigates to Career or Vault screen.
     *
     * Behaviour:
     * - First lifetime visit to either screen: no ad.
     * - From the second visit onward, will attempt to show an interstitial IF:
     *   - fewer than 2 ads have been shown today, AND
     *   - at least 4 hours have passed since the last interstitial.
     */
    fun maybeShowInterstitialOnCareerOrVaultVisit(activity: Activity) {
        // Only start considering ads from second visit to these screens
        val hasReachedSecondVisit = AdsRepository.registerCareerOrVaultVisit()
        if (!hasReachedSecondVisit) {
            return
        }

        // Enforce daily cap and cooldown
        if (!AdsRepository.canShowAdNow()) {
            return
        }

        val ad = interstitialAd ?: run {
            // Not yet loaded; request for next time
            loadInterstitialAd(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                // Record that this ad was actually seen by the user
                AdsRepository.recordAdShown()
                // Preload the next one
                loadInterstitialAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                // Try to prepare the next ad
                loadInterstitialAd(activity)
            }
        }

        ad.show(activity)
    }
}

