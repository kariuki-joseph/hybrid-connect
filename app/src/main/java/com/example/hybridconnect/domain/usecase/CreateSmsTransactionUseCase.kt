package com.example.hybridconnect.domain.usecase

import com.example.hybridconnect.domain.enums.TransactionStatus
import com.example.hybridconnect.domain.enums.SmsType
import com.example.hybridconnect.domain.enums.TransactionType
import com.example.hybridconnect.domain.model.Customer
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.model.SmsMessage
import com.example.hybridconnect.domain.model.Transaction
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.utils.getSmsType
import java.util.UUID
import javax.inject.Inject

class CreateSmsTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val customerRepository: CustomerRepository,
) {
    suspend operator fun invoke(customer: Customer, offer: Offer?, sms: SmsMessage): Transaction {
        val smsType  = getSmsType(sms.message)
        val transactionType = when(smsType){
            SmsType.MPESA -> TransactionType.MPESA
            SmsType.TILL -> TransactionType.TILL
            SmsType.SITE_LINK -> TransactionType.SITE_LINK
            SmsType.RECOMMENDATION_TIMEOUT -> TransactionType.TILL
        }

        val transaction = Transaction(
            id = UUID.randomUUID(),
            amount = offer?.price ?: sms.amount,
            time = sms.time,
            message = sms.message,
            status = if (offer == null) TransactionStatus.UNMATCHED else TransactionStatus.SCHEDULED,
            responseMessage = "",
            customer = customer,
            offer = offer,
            type = transactionType
        )
        transactionRepository.createTransaction(transaction)
        updateLastPurchaseTime(customer)
        return transaction
    }

    private suspend fun updateLastPurchaseTime(customer: Customer) {
        customerRepository.updateLastPurchaseTime(customer, System.currentTimeMillis())
    }
}
