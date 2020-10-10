package de.bguenthe.monthlycosts.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FixCosts(var fixcosts: Double) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
