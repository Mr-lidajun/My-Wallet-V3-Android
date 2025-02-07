package piuk.blockchain.android.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.blockchain.commonarch.presentation.base.HostedBottomSheet
import com.blockchain.componentlib.basic.ImageResource
import com.blockchain.componentlib.sheets.BottomSheetButton
import com.blockchain.componentlib.sheets.BottomSheetOneButton
import com.blockchain.componentlib.sheets.BottomSheetTwoButtons
import com.blockchain.componentlib.sheets.ButtonType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import piuk.blockchain.android.R

class BottomSheetInformation : BottomSheetDialogFragment() {

    interface Host : HostedBottomSheet.Host {
        fun primaryButtonClicked()
        fun secondButtonClicked()
    }

    val host: Host by lazy {
        parentFragment as? Host ?: throw IllegalStateException(
            "Host fragment is not a BottomSheetInformation.Host"
        )
    }

    private val title by lazy { arguments?.getString(TITLE).orEmpty() }
    private val description by lazy { arguments?.getString(DESCRIPTION).orEmpty() }
    private val primaryCtaText by lazy { arguments?.getString(CTA_TEXT_PRIMARY).orEmpty() }
    private val secondaryCtaText by lazy { arguments?.getString(CTA_TEXT_SECONDARY) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity())

        dialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    val secondaryCtaText = secondaryCtaText
                    if (secondaryCtaText == null) {
                        BottomSheetOneButton(
                            title = title,
                            subtitle = description,
                            shouldShowHeaderDivider = false,
                            onCloseClick = { dismiss() },
                            headerImageResource = ImageResource.Local(R.drawable.ic_phone),
                            button = BottomSheetButton(
                                type = ButtonType.PRIMARY,
                                text = primaryCtaText,
                                onClick = {
                                    host.primaryButtonClicked()
                                    super.dismiss()
                                }
                            )
                        )
                    } else {
                        BottomSheetTwoButtons(
                            title = title,
                            subtitle = description,
                            shouldShowHeaderDivider = false,
                            onCloseClick = { dismiss() },
                            headerImageResource = ImageResource.Local(R.drawable.ic_phone),
                            button1 = BottomSheetButton(
                                type = ButtonType.PRIMARY,
                                text = primaryCtaText,
                                onClick = {
                                    host.primaryButtonClicked()
                                    super.dismiss()
                                }
                            ),
                            button2 = BottomSheetButton(
                                type = ButtonType.MINIMAL,
                                text = secondaryCtaText,
                                onClick = {
                                    host.secondButtonClicked()
                                    super.dismiss()
                                }
                            )
                        )
                    }
                }
            }
        )

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val layout =
                d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(layout).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    companion object {
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val CTA_TEXT_PRIMARY = "primary_cta_text"
        private const val CTA_TEXT_SECONDARY = "secondary_cta_text"

        fun newInstance(
            title: String,
            description: String,
            ctaPrimaryText: String,
            ctaSecondaryText: String? = null,
        ): BottomSheetInformation {
            return BottomSheetInformation().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(DESCRIPTION, description)
                    putString(CTA_TEXT_PRIMARY, ctaPrimaryText)
                    putString(CTA_TEXT_SECONDARY, ctaSecondaryText)
                }
            }
        }
    }
}
