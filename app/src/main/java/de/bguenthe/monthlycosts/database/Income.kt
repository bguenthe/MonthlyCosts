package de.bguenthe.monthlycosts.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import java.time.LocalDateTime

@Entity
class Income(var name: String?, var incomeDateTime: LocalDateTime?, var income: Double) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}