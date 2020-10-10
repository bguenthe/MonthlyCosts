package de.bguenthe.monthlycosts.database

import androidx.room.*

@Dao
interface IncomeDao {

    @get:Query("select * from income order by id")
    val all: List<Income>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(income: Income): Long

    @Query("delete from income")
    fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(income: Income)

    @Query("select * from income where CAST(strftime('%Y', incomeDateTime / 1000, 'unixepoch', 'localtime') as int) = :year and CAST(strftime('%m', incomeDateTime / 1000, 'unixepoch', 'localtime') as int) = :month")
    fun getMonthlyIncome(year: Int, month: Int): Income?
}