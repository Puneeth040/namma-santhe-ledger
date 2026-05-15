package com.example.nammasantheledger.ui

import android.content.Context
import com.example.nammasantheledger.data.LedgerRepository
import com.example.nammasantheledger.data.local.AppDatabase

object AppGraph {
    fun repository(context: Context): LedgerRepository {
        val db = AppDatabase.getInstance(context)
        return LedgerRepository(db.customerDao(), db.transactionDao())
    }
}
