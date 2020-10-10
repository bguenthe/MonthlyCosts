package de.bguenthe.monthlycosts.repository

import android.content.Context
import android.util.Log
import de.bguenthe.monthlycosts.database.AppDatabase
import de.bguenthe.monthlycosts.database.Income
import java.time.LocalDateTime

class IncomeRepository(val context: Context) {
    val database: AppDatabase = AppDatabase.getDatabase(context)

    private fun deleteAllAndInsertIncome() {
        database.incomeDao().deleteAll()
        database.incomeDao().add(Income("inc", LocalDateTime.of(2018, 9, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2018, 10, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2018, 11, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2018, 12, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 1, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 2, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 3, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 4, 1, 0, 0, 0), 2686.00))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 5, 1, 0, 0, 0), 3048.46))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 6, 1, 0, 0, 0), 3340.85))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 7, 1, 0, 0, 0), 2756.95))
        database.incomeDao().add(Income("inc", LocalDateTime.of(2019, 8, 1, 0, 0, 0), 2756.45))
    }

    fun saveIncome(amount: Double) {
        val localDate = LocalDateTime.of(LocalDateTime.now().year, LocalDateTime.now().month, 1, 0, 0, 0)
        val monthIncome = database.incomeDao().getMonthlyIncome(LocalDateTime.now().year, LocalDateTime.now().monthValue)
        if (monthIncome == null) {
            database.incomeDao().add(Income("inc", localDate, amount))
        } else {
            monthIncome.income = amount
            database.incomeDao().update(monthIncome)
        }
    }

    fun logMessage(message: String) {
        Log.d("FixCostsRepository", message)
    }
}