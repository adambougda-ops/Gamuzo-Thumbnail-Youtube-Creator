package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thumbnail_blueprints")
data class ThumbnailBlueprint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val title: String,
    val visualConcept: String,
    val faceExpressions: String,
    val backgroundElements: String,
    val textFontsColors: String,
    val glowLighting: String,
    val timestamp: Long = System.currentTimeMillis()
)
