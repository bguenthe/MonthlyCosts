package de.bguenthe.monthlycosts.database

import androidx.room.*

@Dao
interface FixCostsDao {

    @get:Query("select * from fixCosts order by id")
    val all: List<FixCosts>

    @get:Query("select 'fix' as type, sum(fixcosts) as value from fixcosts")
    val sumOfFixCosts: List<MonthlyStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(fixcosts: FixCosts): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(fixcosts: FixCosts)

    @Query("delete from fixcosts")
    fun deleteAll()
}