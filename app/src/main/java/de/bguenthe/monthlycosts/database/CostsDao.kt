package de.bguenthe.monthlycosts.database

import androidx.room.*

@Dao
interface CostsDao {

    @get:Query("select * from Costs order by id")
    val getAll: List<Costs>

    @get:Query("select * from Costs where mqttsend = 0")
    val allNotSentViaMqtt: List<Costs>

    @get:Query("select count(*) from (select count(*) from Costs where deleted = 0 group by strftime('%Y%m',recordDateTime / 1000, 'unixepoch'))")
    val numberOfMonthsToShow: Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(costs: Costs): Long

    @Query("select * from Costs where id = :taskId")
    fun getCost(taskId: Long): List<Costs>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(costs: Costs)

    @Query("delete from Costs")
    fun deleteAll()

    @Query("update costs set mqttsend = 0")
    fun resendAll()

    @Query("update costs set type = 'drinks' where type = 'trinken_gehen'")
    fun massupdate()

    @Query("update costs set costs = 10 where id = 562")
    fun update1()

    @Query("select * from Costs where id = (select max(id) from costs where deleted = 0 AND CAST(strftime('%Y', recordDateTime / 1000, 'unixepoch') as int) = :year and CAST(strftime('%m', recordDateTime / 1000, 'unixepoch') as int) = :month)")
    fun getLast(year: Int, month: Int): Costs?

    @Query("select * from Costs where deleted = 0 AND CAST(strftime('%Y', recordDateTime / 1000, 'unixepoch') as int) = :year and CAST(strftime('%m', recordDateTime / 1000, 'unixepoch') as int) = :month order by recordDateTime DESC")
    fun getAllCostsByMonth(year: Int, month: Int): List<Costs>

    @Query("select * from Costs where deleted = 0 AND CAST(strftime('%Y', recordDateTime / 1000, 'unixepoch') as int) = :year and CAST(strftime('%m', recordDateTime / 1000, 'unixepoch') as int) = :month and costs > 100 order by recordDateTime DESC")
    fun getAllCostsByMonthgt100(year: Int, month: Int): List<Costs>

    @Query("select type, sum(costs) as value from Costs where deleted = 0 AND CAST(strftime('%Y', recordDateTime / 1000, 'unixepoch') as int) = :year and CAST(strftime('%m', recordDateTime / 1000, 'unixepoch') as int) = :month group by type")
    fun getMonthlySumsPerType(year: Int, month: Int): List<MonthlyStats>

    @Query("select type, sum(costs) as value from Costs where deleted = 0 group by type")
    fun getAllMonthlySums(): List<MonthlyStats>

    @Query("select strftime('%Y%m', recordDateTime / 1000, 'unixepoch') from Costs where deleted = 0 group by strftime('%Y%m', recordDateTime / 1000, 'unixepoch')")
    fun getMonthCount(): List<String>

    @Query("select 'sum' as type, sum(costs) as value from Costs where deleted = 0 AND CAST(strftime('%Y', recordDateTime / 1000, 'unixepoch') as int) = :year and CAST(strftime('%m', recordDateTime / 1000, 'unixepoch') as int) = :month")
    fun getMonthlySumOfCosts(year: Int, month: Int): List<MonthlyStats>

    @Query("select uniqueID from Costs")
    fun getAllUUIds(): List<String>
}