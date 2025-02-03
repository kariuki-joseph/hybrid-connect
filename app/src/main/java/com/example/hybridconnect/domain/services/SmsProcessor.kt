package com.example.hybridconnect.domain.services

import android.util.Log
import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.exception.InvalidMessageFormatException
import com.example.hybridconnect.domain.exception.RecommendationTimedOutException
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.usecase.CreateSmsTransactionUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetLastTransactionForCustomerUseCase
import com.example.hybridconnect.domain.usecase.GetOfferByPriceUseCase
import com.example.hybridconnect.domain.usecase.GetOrCreateCustomerUseCase
import com.example.hybridconnect.domain.usecase.IncrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.SendAutoReplyMessageUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
private const val TAG = "SmsProcessor"
class SmsProcessor @Inject constructor(
    private val validateMessageUseCase: ValidateMessageUseCase,
    private val getOrCreateCustomerUseCase: GetOrCreateCustomerUseCase,
    private val extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
    private val getOfferByPriceUseCase: GetOfferByPriceUseCase,
    private val createSmsTransactionUseCase: CreateSmsTransactionUseCase,
    private val dialUssdUseCase: DialUssdUseCase,
    private val incrementCustomerBalanceUseCase: IncrementCustomerBalanceUseCase,
    private val prefsRepository: PrefsRepository,
    private val sendAutoReplyMessageUseCase: SendAutoReplyMessageUseCase,
    private val getLastTransactionForCustomerUseCase: GetLastTransactionForCustomerUseCase,
) {
    fun processMessage(message: String, sender: String, simSlot: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                validateMessageUseCase(message, sender, simSlot)
                val sms = extractMessageDetailsUseCase(message)
                val customer = getOrCreateCustomerUseCase(sms.senderPhone, sms.senderName)
                val offer = getOfferByPriceUseCase(sms.amount)

                if(!prefsRepository.isAppActive()){
                    val transaction = Transaction(
                        id = UUID.randomUUID(),
                        amount = sms.amount,
                        time = sms.time,
                        mpesaMessage = sms.message,
                        responseMessage = "",
                        customer = customer,
                        offer = offer,
                    )
                    sendAutoReplyMessageUseCase(transaction, AutoReplyType.OUT_OF_SERVICE)
                    return@launch
                }

                val transaction = createSmsTransactionUseCase(customer, offer, sms)

                if (offer == null) {
                    incrementCustomerBalanceUseCase(customer, sms.amount)
                    sendAutoReplyMessageUseCase(transaction, AutoReplyType.OFFER_UNAVAILABLE)
                }

                offer?.let {
                    dialUssdUseCase(transaction)
                }

            } catch (e: RecommendationTimedOutException){
                Log.e(TAG, e.message, e)
                processRecommendationTimeoutMessage(e.phoneNumber)
            }
            catch (e: InvalidMessageFormatException) {
                println("Invalid message format: ${e.message}")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

   private fun processRecommendationTimeoutMessage(phoneNumber: String){
       CoroutineScope(Dispatchers.IO).launch {
           val customer = getOrCreateCustomerUseCase(phoneNumber, "AlreadyRecommendedCustomer" )
           val transaction = getLastTransactionForCustomerUseCase(customer)
           if(transaction == null) {
               Log.d(TAG, "Last transaction for customer ${customer.name} not found")
               return@launch
           }

           if(transaction.status != TransactionStatus.FAILED){
               Log.d(TAG, "Last transaction status was ${transaction.status} but expected ${TransactionStatus.FAILED}. Aborting")
               return@launch
           }

           transaction.offer?.let {
               dialUssdUseCase(transaction)
           }
       }
    }
}