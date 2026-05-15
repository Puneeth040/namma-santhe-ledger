package com.example.nammasantheledger.data.local

import androidx.room.TypeConverter
import com.example.nammasantheledger.data.model.TransactionType

class LedgerTypeConverters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(type: String): TransactionType = TransactionType.valueOf(type)
}
