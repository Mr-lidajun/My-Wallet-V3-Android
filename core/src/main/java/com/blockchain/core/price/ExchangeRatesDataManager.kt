package com.blockchain.core.price

import com.blockchain.domain.common.model.Seconds
import info.blockchain.balance.AssetInfo
import info.blockchain.balance.Currency
import info.blockchain.balance.FiatCurrency
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.Calendar

enum class HistoricalTimeSpan(val value: Int) {
    DAY(0),
    WEEK(1),
    MONTH(2),
    YEAR(3),
    ALL_TIME(4);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

data class HistoricalRate(
    val timestamp: Seconds,
    val rate: Double
)

typealias HistoricalRateList = List<HistoricalRate>

data class Prices24HrWithDelta(
    val delta24h: Double,
    val previousRate: ExchangeRate,
    val currentRate: ExchangeRate,
    val marketCap: Double? = null,
)

interface ExchangeRates {
    @Deprecated("User the reactive get operations")
    fun getLastCryptoToUserFiatRate(sourceCrypto: AssetInfo): ExchangeRate

    @Deprecated("User the reactive get operations")
    fun getLastFiatToUserFiatRate(sourceFiat: FiatCurrency): ExchangeRate

    @Deprecated("User the reactive get operations")
    fun getLastCryptoToFiatRate(sourceCrypto: AssetInfo, targetFiat: FiatCurrency): ExchangeRate

    @Deprecated("User the reactive get operations")
    fun getLastFiatToFiatRate(sourceFiat: FiatCurrency, targetFiat: FiatCurrency): ExchangeRate

    @Deprecated("User the reactive get operations")
    fun getLastFiatToCryptoRate(sourceFiat: FiatCurrency, targetCrypto: AssetInfo): ExchangeRate
}

interface ExchangeRatesDataManager : ExchangeRates {
    fun init(): Completable

    fun exchangeRate(fromAsset: Currency, toAsset: Currency): Observable<ExchangeRate>
    fun exchangeRateToUserFiat(fromAsset: Currency): Observable<ExchangeRate>

    fun getHistoricRate(fromAsset: Currency, secSinceEpoch: Long): Single<ExchangeRate>
    fun getPricesWith24hDelta(fromAsset: Currency): Observable<Prices24HrWithDelta>
    fun getPricesWith24hDelta(fromAsset: Currency, fiat: Currency): Observable<Prices24HrWithDelta>

    fun getHistoricPriceSeries(
        asset: Currency,
        span: HistoricalTimeSpan,
        now: Calendar = Calendar.getInstance()
    ): Single<HistoricalRateList>

    // Specialised call to historic rates for sparkline caching
    fun get24hPriceSeries(
        asset: Currency
    ): Single<HistoricalRateList>

    val fiatAvailableForRates: List<FiatCurrency>
}
