package de.bguenthe.monthlycosts.repository

import android.content.Context
import android.util.Log
import de.bguenthe.monthlycosts.database.AppDatabase
import de.bguenthe.monthlycosts.database.FixCosts

class FixCostsRepository(val context: Context) {
    val database: AppDatabase = AppDatabase.getDatabase(context)

    private fun deleteAllAndInsertFixCosts() {
        database.fixCostsDao().deleteAll()
        database.fixCostsDao().add(FixCosts(272.36))
    }

    fun logMessage(message: String) {
        Log.d("FixCostsRepository", message)
    }
}