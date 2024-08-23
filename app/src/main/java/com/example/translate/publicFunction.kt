package com.example.translate

import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface publicFunction {
    companion object {
        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }
    }
}