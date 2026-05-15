package com.example.nammasantheledger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.nammasantheledger.data.LedgerRepository
import com.example.nammasantheledger.data.model.CustomerEntity
import com.example.nammasantheledger.data.model.LedgerTransactionWithCustomer
import com.example.nammasantheledger.data.model.TransactionType
import kotlinx.coroutines.launch

class LedgerViewModel(private val repository: LedgerRepository) : ViewModel() {
    private val customerQuery = MutableLiveData("")
    val customers: LiveData<List<CustomerEntity>> = customerQuery.switchMap { repository.customers(it) }

    val totalOutstanding = repository.totalOutstanding()
    val todaySales = repository.todaySales()
    val todayPayments = repository.todayPayments()
    val recentTransactions = repository.recentTransactions()
    val safeOutstanding: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(totalOutstanding) { value = (it ?: 0.0).coerceAtLeast(0.0) }
    }
    val advanceBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(totalOutstanding) { value = ((it ?: 0.0) * -1).coerceAtLeast(0.0) }
    }

    fun setCustomerQuery(query: String) {
        customerQuery.value = query
    }

    fun saveCustomer(id: Long?, name: String, phone: String, address: String?) {
        viewModelScope.launch {
            if (id == null || id == 0L) {
                repository.addCustomer(name, phone, address)
            } else {
                repository.updateCustomer(CustomerEntity(id, name, phone, address))
            }
        }
    }

    fun deleteCustomer(customer: CustomerEntity) = viewModelScope.launch {
        repository.deleteCustomer(customer)
    }

    fun addCredit(customerId: Long, amount: Double, note: String?) = viewModelScope.launch {
        repository.addTransaction(customerId, amount, TransactionType.CREDIT, note)
    }

    fun addPayment(customerId: Long, amount: Double, note: String?) = viewModelScope.launch {
        repository.addTransaction(customerId, amount, TransactionType.PAYMENT, note)
    }

    fun customerById(customerId: Long): LiveData<CustomerEntity?> = liveData {
        emit(repository.getCustomer(customerId))
    }

    fun customerLedger(customerId: Long): LiveData<List<LedgerTransactionWithCustomer>> =
        repository.customerTransactions(customerId)

    fun dailyInsight(): LiveData<String> {
        return MediatorLiveData<String>().apply {
            var sales = 0.0
            var payments = 0.0
            fun recompute() {
                value = if (payments > sales) {
                    "Smart Insight: You collected more payments than credit today."
                } else {
                    "Smart Insight: More credit than collections today, follow up on dues."
                }
            }
            addSource(todaySales) {
                sales = it ?: 0.0
                recompute()
            }
            addSource(todayPayments) {
                payments = it ?: 0.0
                recompute()
            }
        }
    }
}

class LedgerViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LedgerViewModel(repository) as T
}
