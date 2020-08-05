package piuk.blockchain.android.ui.transfer.send

import info.blockchain.balance.CryptoCurrency
import info.blockchain.balance.CryptoValue
import piuk.blockchain.android.coincore.CryptoAccount
import piuk.blockchain.android.coincore.NullAddress
import piuk.blockchain.android.coincore.NullCryptoAccount
import piuk.blockchain.android.coincore.SendTarget
import piuk.blockchain.android.coincore.SendValidationError
import piuk.blockchain.android.ui.base.mvi.MviIntent

sealed class SendIntent : MviIntent<SendState> {

    class Initialise(
        private val account: CryptoAccount,
        private val passwordRequired: Boolean
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            SendState(
                sendingAccount = account,
                errorState = SendErrorState.NONE,
                passwordRequired = passwordRequired,
                currentStep = if (passwordRequired) {
                    SendStep.ENTER_PASSWORD
                } else {
                    SendStep.ENTER_ADDRESS
                },
                nextEnabled = passwordRequired
            )
    }

    class InitialiseWithTarget(
        private val fromAccount: CryptoAccount,
        private val toAccount: SendTarget,
        private val passwordRequired: Boolean
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            SendState(
                sendingAccount = fromAccount,
                sendTarget = toAccount,
                errorState = SendErrorState.NONE,
                passwordRequired = passwordRequired,
                currentStep = if (passwordRequired) {
                    SendStep.ENTER_PASSWORD
                } else {
                    SendStep.ENTER_AMOUNT
                },
                nextEnabled = passwordRequired
            )
    }

    class ValidatePassword(
        val password: String
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = false,
                errorState = SendErrorState.NONE
            )
    }

    class UpdatePasswordIsValidated(
        val password: String
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = true,
                secondPassword = password,
                currentStep = if (oldState.sendTarget == NullAddress) {
                    SendStep.ENTER_ADDRESS
                } else {
                    SendStep.ENTER_AMOUNT
                }
            )
    }

    object UpdatePasswordNotValidated : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = false,
                errorState = SendErrorState.INVALID_PASSWORD,
                secondPassword = ""
            )
    }

    class ValidateInputTargetAddress(
        val targetAddress: String,
        val expectedCrypto: CryptoCurrency
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState = oldState
    }

    class TargetAddressValidated(
        val sendTarget: SendTarget
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                errorState = SendErrorState.NONE,
                sendTarget = sendTarget,
                nextEnabled = true
            )
    }

    class SelectionTargetAddressValidated(
        val sendTarget: SendTarget
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                errorState = SendErrorState.NONE,
                sendTarget = sendTarget,
                currentStep = SendStep.ENTER_AMOUNT
            )
    }

    class TargetAddressInvalid(private val error: SendValidationError) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                errorState = when (error.errorCode) {
                    SendValidationError.ADDRESS_IS_CONTRACT -> SendErrorState.ADDRESS_IS_CONTRACT
                    else -> SendErrorState.INVALID_ADDRESS
                },
                sendTarget = NullCryptoAccount,
                nextEnabled = false
            )
    }

    class TargetSelectionConfirmed(
        val sendTarget: SendTarget
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                errorState = SendErrorState.NONE,
                nextEnabled = false
            )
    }

    class SendAmountChanged(
        val amount: CryptoValue
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = false
            )
    }

    object MaxAmountExceeded : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(errorState = SendErrorState.MAX_EXCEEDED)
    }

    object MinRequired : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(errorState = SendErrorState.MIN_REQUIRED)
    }

    object RequestFee : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState
    }

    object FeeRequestError : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(errorState = SendErrorState.FEE_REQUEST_FAILED)
    }

    class FeeUpdate(
        val fee: CryptoValue
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(feeAmount = fee)
    }

    object RequestTransactionNoteSupport : SendIntent() {
        override fun reduce(oldState: SendState): SendState = oldState
    }

    object TransactionNoteSupported : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(transactionNoteSupported = true)
    }

    class NoteAdded(
        val note: String
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState = oldState.copy(
            note = note,
            noteState = NoteState.UPDATE_SUCCESS
        )
    }

    class UpdateTransactionAmounts(
        val amount: CryptoValue,
        private val maxAvailable: CryptoValue
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = amount.isPositive,
                sendAmount = amount,
                availableBalance = maxAvailable,
                errorState = SendErrorState.NONE
            )
    }

    object PrepareTransaction : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = true,
                currentStep = SendStep.CONFIRM_DETAIL
            )
    }

    object ExecuteTransaction : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = false,
                currentStep = SendStep.IN_PROGRESS,
                transactionInFlight = TransactionInFlightState.IN_PROGRESS
            )
    }

    class FatalTransactionError(
        private val error: Throwable
    ) : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = true,
                transactionInFlight = TransactionInFlightState.ERROR
            )
    }

    object UpdateTransactionComplete : SendIntent() {
        override fun reduce(oldState: SendState): SendState =
            oldState.copy(
                nextEnabled = true,
                transactionInFlight = TransactionInFlightState.COMPLETED
            )
    }

    object ReturnToPreviousStep : SendIntent() {
        override fun reduce(oldState: SendState): SendState {
            val steps = SendStep.values()
            val currentStep = oldState.currentStep.ordinal
            if (currentStep == 0) {
                throw IllegalStateException("Cannot go back")
            }
            val previousStep = steps[currentStep - 1]

            return oldState.copy(
                currentStep = previousStep,
                errorState = SendErrorState.NONE
            )
        }
    }
}
