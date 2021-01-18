package piuk.blockchain.android.coincore.erc20.pax

import com.blockchain.logging.CrashLogger
import com.blockchain.preferences.CurrencyPrefs
import com.blockchain.preferences.WalletStatus
import com.blockchain.nabu.datamanagers.CustodialWalletManager
import com.blockchain.nabu.datamanagers.EligibilityProvider
import com.blockchain.nabu.service.TierService
import com.blockchain.wallet.DefaultLabels
import info.blockchain.balance.CryptoCurrency
import piuk.blockchain.android.coincore.erc20.Erc20TokensBase
import piuk.blockchain.android.coincore.impl.OfflineAccountUpdater
import piuk.blockchain.android.thepit.PitLinking
import piuk.blockchain.androidcore.data.api.EnvironmentConfig
import piuk.blockchain.androidcore.data.ethereum.EthDataManager
import piuk.blockchain.androidcore.data.exchangerate.ExchangeRateDataManager
import piuk.blockchain.androidcore.data.exchangerate.ExchangeRateService
import piuk.blockchain.androidcore.data.fees.FeeDataManager
import piuk.blockchain.androidcore.data.payload.PayloadDataManager

internal class PaxAsset(
    payloadManager: PayloadDataManager,
    ethDataManager: EthDataManager,
    feeDataManager: FeeDataManager,
    custodialManager: CustodialWalletManager,
    exchangeRates: ExchangeRateDataManager,
    historicRates: ExchangeRateService,
    currencyPrefs: CurrencyPrefs,
    labels: DefaultLabels,
    pitLinking: PitLinking,
    crashLogger: CrashLogger,
    tiersService: TierService,
    environmentConfig: EnvironmentConfig,
    walletPreferences: WalletStatus,
    offlineAccounts: OfflineAccountUpdater,
    eligibilityProvider: EligibilityProvider
) : Erc20TokensBase(
    CryptoCurrency.PAX,
    payloadManager,
    ethDataManager,
    feeDataManager,
    walletPreferences,
    custodialManager,
    exchangeRates,
    historicRates,
    currencyPrefs,
    labels,
    pitLinking,
    crashLogger,
    tiersService,
    environmentConfig,
    eligibilityProvider,
    offlineAccounts
)
