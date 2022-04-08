package com.blockchain.blockchaincard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.blockchain.blockchaincard.R
import com.blockchain.blockchaincard.domain.models.BlockchainDebitCardProduct
import com.blockchain.blockchaincard.ui.composables.BlockchainCardNavHost
import com.blockchain.blockchaincard.viewmodel.BlockchainCardViewModel
import com.blockchain.blockchaincard.viewmodel.BlockchainDebitCardArgs
import com.blockchain.commonarch.presentation.base.FlowFragment
import com.blockchain.commonarch.presentation.base.updateToolbar
import com.blockchain.commonarch.presentation.mvi_v2.ModelConfigArgs
import com.blockchain.koin.payloadScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.scope.getViewModel

class BlockchainCardFragment : Fragment(), FlowFragment {

    private val viewModel: BlockchainCardViewModel by lazy {
        payloadScope.getViewModel(owner = { ViewModelOwner.from(this) })
    }

    /*
        private val navigator: BlockchainCardNavigationRouter by inject()
    */

    private val modelArgs: ModelConfigArgs by lazy {
        arguments?.getString(BLOCKCHAIN_CARD_ID)?.let { cardId ->
            BlockchainDebitCardArgs.CardArgs(cardId)
        } ?: (arguments?.getParcelable(BLOCKCHAIN_PRODUCT) as? BlockchainDebitCardProduct)?.let { product ->
            BlockchainDebitCardArgs.ProductArgs(product)
        } ?: throw IllegalStateException("Missing card or product data")
    }

    companion object {
        private const val BLOCKCHAIN_CARD_ID = "BLOCKCHAIN_CARD_ID"
        private const val BLOCKCHAIN_PRODUCT = "BLOCKCHAIN_PRODUCT"

        fun newInstance(blockchainDebitCardId: String) =
            BlockchainCardFragment().apply {
                arguments = Bundle().apply {
                    putString(BLOCKCHAIN_CARD_ID, blockchainDebitCardId)
                }
            }

        fun newInstance(blockchainDebitCardProduct: BlockchainDebitCardProduct) =
            BlockchainCardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BLOCKCHAIN_PRODUCT, blockchainDebitCardProduct)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        updateToolbar(
            toolbarTitle = getString(R.string.order_card_title),
            menuItems = emptyList()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                BlockchainCardNavHost(viewModel = viewModel, modelArgs = modelArgs)
            }
        }
    }

    override fun onBackPressed(): Boolean = false
}
