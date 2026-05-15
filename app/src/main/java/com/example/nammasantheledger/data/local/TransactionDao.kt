package com.example.nammasantheledger.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nammasantheledger.data.model.LedgerTransactionEntity
import com.example.nammasantheledger.data.model.LedgerTransactionWithCustomer

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: LedgerTransactionEntity)

    @Query(
        """
        SELECT t.id, t.customerId, c.name AS customerName, t.amount, t.type, t.note, t.timestamp
        FROM transactions t
        INNER JOIN customers c ON c.id = t.customerId
        ORDER BY t.timestamp DESC
        LIMIT 20
        """
    )
    fun getRecentTransactions(): LiveData<List<LedgerTransactionWithCustomer>>

    @Query(
        """
        SELECT t.id, t.customerId, c.name AS customerName, t.amount, t.type, t.note, t.timestamp
        FROM transactions t
        INNER JOIN customers c ON c.id = t.customerId
        WHERE t.customerId = :customerId
        ORDER BY t.timestamp DESC
        """
    )
    fun getTransactionsForCustomer(customerId: Long): LiveData<List<LedgerTransactionWithCustomer>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END), 0)
        FROM transactions
        """
    )
    fun getTotalOutstanding(): LiveData<Double>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0)
        FROM transactions
        WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        """
    )
    fun getTodaySales(): LiveData<Double>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions
        WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        """
    )
    fun getTodayPayments(): LiveData<Double>
}
