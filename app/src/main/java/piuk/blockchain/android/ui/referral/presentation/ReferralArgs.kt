package piuk.blockchain.android.ui.referral.presentation

import com.blockchain.commonarch.presentation.mvi_v2.ModelConfigArgs
import kotlinx.parcelize.Parcelize
import piuk.blockchain.android.ui.referral.domain.model.ReferralData

@Parcelize
data class ReferralArgs(
    val code: String,
    val criteria: List<String>,
    val rewardSubtitle: String,
    val rewardTitle: String
) : ModelConfigArgs.ParcelableArgs {

    companion object {
        const val ARGS_KEY = "ReferralArgs"
    }
}

fun ReferralData.mapArgs(): ReferralArgs = this.run {
    ReferralArgs(
        code = code,
        criteria = criteria,
        rewardSubtitle = rewardSubtitle,
        rewardTitle = rewardTitle
    )
}
