package ru.suhachev.weatherapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hours",
    foreignKeys = [
        ForeignKey(
            entity = ForecastDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["forecastDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("forecastDayId")]
)
data class HourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val forecastDayId: Long,
    val date: String,
    val time: String,
    val temp: String,
    val condition: String,
    val icon: String
) 