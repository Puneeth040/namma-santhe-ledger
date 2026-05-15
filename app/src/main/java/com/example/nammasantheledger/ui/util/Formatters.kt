package com.example.nammasantheledger.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.asRupee(): String = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(this)

fun Long.asDateTime(): String = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(this))
