package com.example.nammasantheledger.data

import androidx.lifecycle.LiveData
import com.example.nammasantheledger.data.local.CustomerDao
import com.example.nammasantheledger.data.local.TransactionDao
import com.example.nammasantheledger.data.model.CustomerEntity
import com.example.nammasantheledger.data.model.LedgerTransactionEntity
import com.example.nammasantheledger.data.model.LedgerTransactionWithCustomer
import com.example.nammasantheledger.data.model.TransactionType

class LedgerRepository(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    fun customers(query: String): LiveData<List<CustomerEntity>> =
        if (query.isBlank()) customerDao.getAllCustomers() else customerDao.searchCustomers(query)

    suspend fun addCustomer(name: String, phone: String, address: String?): Long {
        return customerDao.insertCustomer(CustomerEntity(name = name, phone = phone, address = address))
    }

    suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)
    suspend fun getCustomer(id: Long): CustomerEntity? = customerDao.getCustomerById(id)

    suspend fun addTransaction(customerId: Long, amount: Double, type: TransactionType, note: String?) {
        transactionDao.insertTransaction(
            LedgerTransactionEntity(
                customerId = customerId,
                amount = amount,
                type = type,
                note = note,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun totalOutstanding(): LiveData<Double> = transactionDao.getTotalOutstanding()
    fun todaySales(): LiveData<Double> = transactionDao.getTodaySales()
    fun todayPayments(): LiveData<Double> = transactionDao.getTodayPayments()
    fun recentTransactions(): LiveData<List<LedgerTransactionWithCustomer>> = transactionDao.getRecentTransactions()
    fun customerTransactions(customerId: Long): LiveData<List<LedgerTransactionWithCustomer>> =
        transactionDao.getTransactionsForCustomer(customerId)
}
