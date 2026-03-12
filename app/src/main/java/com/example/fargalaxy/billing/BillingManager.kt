package com.example.fargalaxy.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.fargalaxy.BuildConfig
import com.example.fargalaxy.data.GameStateRepository
import com.example.fargalaxy.data.UserDataRepository

enum class PremiumProduct(val productId: String) {
    DYING_STAR("dying_star_unlock"),
    CREDITS_PACK("credits_pack_100k")
}

object BillingManager : PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private var isReady: Boolean = false
    private var productDetailsMap: Map<String, ProductDetails> = emptyMap()

    fun initialize(context: Context) {
        if (::billingClient.isInitialized && isReady) return

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isReady = true
                    queryProductDetails()
                    queryExistingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                isReady = false
            }
        })
    }

    private fun queryProductDetails() {
        if (!isReady) return

        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PremiumProduct.DYING_STAR.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PremiumProduct.CREDITS_PACK.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(params) { _, productDetailsList ->
            productDetailsList?.let {
                productDetailsMap = it.associateBy { details -> details.productId }
            }
        }
    }

    /**
     * Restore non-consumable entitlements (e.g., Dying Star) on startup.
     */
    private fun queryExistingPurchases() {
        if (!isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesList.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.products.contains(PremiumProduct.DYING_STAR.productId)) {
                            GameStateRepository.ownShip("dying_star")
                        }
                    }
                }
            }
        }
    }

    fun launchPurchase(activity: Activity, product: PremiumProduct) {
        if (!isReady) {
            Log.w("BillingManager", "launchPurchase: BillingClient not ready")
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    activity,
                    "Billing not ready (debug build). Simulating purchase.",
                    Toast.LENGTH_SHORT
                ).show()
                simulateDebugPurchase(product)
            } else {
                Toast.makeText(
                    activity,
                    "Billing not available. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val productDetails = productDetailsMap[product.productId]
        if (productDetails == null) {
            Log.w(
                "BillingManager",
                "launchPurchase: ProductDetails missing for productId=${product.productId}"
            )
            if (BuildConfig.DEBUG) {
                Toast.makeText(
                    activity,
                    "Product ${product.productId} not configured. Simulating purchase (debug).",
                    Toast.LENGTH_LONG
                ).show()
                simulateDebugPurchase(product)
            } else {
                Toast.makeText(
                    activity,
                    "Product not available.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Debug helper: when running a debug build without Play Store setup, simulate
     * a successful purchase so the rest of the app can be tested.
     */
    private fun simulateDebugPurchase(product: PremiumProduct) {
        when (product) {
            PremiumProduct.CREDITS_PACK -> {
                UserDataRepository.addCredits(100_000)
            }
            PremiumProduct.DYING_STAR -> {
                GameStateRepository.ownShip("dying_star")
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                handlePurchase(purchase)
            }
        }
        // Other response codes (USER_CANCELED, ERROR, etc.) can be logged / handled as needed.
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        val isCreditsPurchase = purchase.products.contains(PremiumProduct.CREDITS_PACK.productId)

        if (isCreditsPurchase) {
            // Consumable: credits pack can be purchased multiple times.
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Grant credits after successful consume.
                    UserDataRepository.addCredits(100_000)
                }
            }
        } else {
            // Non-consumable: Dying Star unlock.
            purchase.products.forEach { productId ->
                when (productId) {
                    PremiumProduct.DYING_STAR.productId -> {
                        GameStateRepository.ownShip("dying_star")
                    }
                }
            }

            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) { _ -> }
            }
        }
    }
}

