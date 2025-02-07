package com.blockchain.domain.eligibility.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductEligibility(
    val product: EligibleProduct,
    val canTransact: Boolean,
    val maxTransactionsCap: TransactionsLimit,
    val reasonNotEligible: ProductNotEligibleReason?
) {
    companion object {
        fun asEligible(product: EligibleProduct): ProductEligibility =
            ProductEligibility(
                product = product,
                canTransact = true,
                maxTransactionsCap = TransactionsLimit.Unlimited,
                reasonNotEligible = null
            )
    }
}

@Serializable
sealed class ProductNotEligibleReason {
    @Serializable
    sealed class InsufficientTier : ProductNotEligibleReason() {
        @Serializable
        object Tier2Required : InsufficientTier()
        @Serializable
        object Tier1TradeLimitExceeded : InsufficientTier()
        @Serializable
        data class Unknown(val message: String) : InsufficientTier()
    }
    @Serializable
    sealed class Sanctions : ProductNotEligibleReason() {
        @Serializable
        object RussiaEU5 : Sanctions()
        @Serializable
        data class Unknown(val message: String) : Sanctions()
    }
    @Serializable
    data class Unknown(val message: String) : ProductNotEligibleReason()
}
