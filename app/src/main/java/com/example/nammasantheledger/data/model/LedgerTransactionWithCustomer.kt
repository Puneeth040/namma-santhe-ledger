package com.example.nammasantheledger.data.model

data class LedgerTransactionWithCustomer(
    val id: Long,
    val customerId: Long,
    val customerName: String,
    val amount: Double,
    val type: TransactionType,
    val note: String?,
    val timestamp: Long
)
