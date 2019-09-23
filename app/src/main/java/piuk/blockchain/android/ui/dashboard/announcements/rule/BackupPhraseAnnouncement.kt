package piuk.blockchain.android.ui.dashboard.announcements.rule

import android.support.annotation.VisibleForTesting
import com.blockchain.preferences.WalletStatus
import io.reactivex.Single
import piuk.blockchain.android.R
import piuk.blockchain.android.ui.dashboard.announcements.AnnouncementCard
import piuk.blockchain.android.ui.dashboard.announcements.AnnouncementHost
import piuk.blockchain.android.ui.dashboard.announcements.AnnouncementRule
import piuk.blockchain.android.ui.dashboard.announcements.DismissRecorder
import piuk.blockchain.android.ui.dashboard.announcements.DismissRule

class BackupPhraseAnnouncement(
    dismissRecorder: DismissRecorder,
    private val walletStatus: WalletStatus
) : AnnouncementRule(dismissRecorder) {

    override val dismissKey = DISMISS_KEY

    override fun shouldShow(): Single<Boolean> {
        if (dismissEntry.isDismissed) {
            return Single.just(false)
        }

        return Single.just(walletStatus.isWalletFunded && !walletStatus.isWalletBackedUp)
    }

    override fun show(host: AnnouncementHost) {
        host.showAnnouncementCard(
            card = AnnouncementCard(
                dismissRule = DismissRule.CardPeriodic,
                dismissEntry = dismissEntry,
                titleText = R.string.recovery_card_title,
                bodyText = R.string.recovery_card_body,
                ctaText = R.string.recovery_card_cta,
                iconImage = R.drawable.ic_announce_backup,
                ctaFunction = {
                    host.startFundsBackup()
                },
                dismissFunction = {
                    host.dismissAnnouncementCard(dismissEntry.prefsKey)
                }
            )
        )
    }

    override val name = "backup_funds"

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val DISMISS_KEY = "BackupWalletAuthAnnouncement_DISMISSED"
    }
}
