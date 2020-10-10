package de.bguenthe.monthlycosts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(Costs::class, FixCosts::class, Income::class), version = 6, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun costsDao(): CostsDao

    abstract fun fixCostsDao(): FixCostsDao

    abstract fun incomeDao(): IncomeDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //            database.execSQL("CREATE TABLE fixcosts "
                //                    + " ADD COLUMN uniqueID TEXT");
                //            database.execSQL("CREATE TABLE income "
                //                    + " ADD COLUMN deleted INTEGER DEFAULT 0 not null");
                //database.execSQL("CREATE TABLE IF NOT EXISTS `fixcosts` (`id` integer primary key autoincrement not null, `fixcosts` REAL NOT NULL)")
                //database.execSQL("CREATE TABLE IF NOT EXISTS `income` (`id` integer primary key autoincrement not null , `name` TEXT, `income` REAL NOT NULL, `incomeDateTime` INTEGER)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `income` (`id` integer primary key autoincrement not null , `name` TEXT, `income` REAL NOT NULL, `incomeDateTime` INTEGER)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context, AppDatabase::class.java, "userdatabase")
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_5_6, MIGRATION_5_6)
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}