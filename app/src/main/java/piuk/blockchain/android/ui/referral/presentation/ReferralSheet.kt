package piuk.blockchain.android.ui.referral.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import com.blockchain.analytics.Analytics
import com.blockchain.commonarch.presentation.mvi_v2.MVIBottomSheet
import com.blockchain.commonarch.presentation.mvi_v2.NavigationRouter
import com.blockchain.commonarch.presentation.mvi_v2.bindViewModel
import com.blockchain.commonarch.presentation.mvi_v2.disableDragging
import com.blockchain.commonarch.presentation.mvi_v2.withArgs
import com.blockchain.koin.payloadScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.java.KoinJavaComponent.get
import piuk.blockchain.android.ui.referral.domain.model.ReferralData
import piuk.blockchain.android.ui.referral.presentation.composable.ReferralScreen
import piuk.blockchain.android.util.copyToClipboard
import piuk.blockchain.android.util.shareText

class ReferralSheet :
    MVIBottomSheet<ReferralViewState>(),
    NavigationRouter<ReferralNavigationEvent>,
    Analytics by get(Analytics::class.java) {

    private val args: ReferralArgs by lazy {
        arguments?.getParcelable<ReferralArgs>(ReferralArgs.ARGS_KEY) ?: error("missing ReferralArgs")
    }

    private val viewModel: ReferralViewModel by lazy {
        payloadScope.getViewModel(owner = { ViewModelOwner.from(this) })
    }

    override fun onStateUpdated(state: ReferralViewState) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        disableDragging()

        setupViewModel()

        return ComposeView(requireContext()).apply {
            setContent {
                SheetContent()
            }
        }
    }

    private fun setupViewModel() {
        bindViewModel(viewModel = viewModel, navigator = this, args = args)
    }

    @Composable
    private fun SheetContent() {
        with(viewModel.viewState.collectAsState().value) {
            ReferralScreen(
                rewardTitle = rewardTitle,
                rewardSubtitle = rewardSubtitle,
                code = code,
                confirmCopiedToClipboard = confirmCopiedToClipboard,
                criteria = criteria,
                onBackPressed = ::onBackPressed,
                copyToClipboard = ::copyToClipboard,
                shareCode = ::shareCode
            )
        }
    }

    private fun onBackPressed() {
        dismiss()
    }

    private fun copyToClipboard(code: String) {
        context?.copyToClipboard("referralCode", code)
        viewModel.onIntent(ReferralIntents.ConfirmCopiedToClipboard)
    }

    private fun shareCode(code: String) {
        context?.shareText(code)
    }

    override fun route(navigationEvent: ReferralNavigationEvent) {
    }

    companion object {
        fun newInstance(referralData: ReferralData) = ReferralSheet().withArgs(
            key = ReferralArgs.ARGS_KEY,
            args = referralData.mapArgs()
        )
    }
}
