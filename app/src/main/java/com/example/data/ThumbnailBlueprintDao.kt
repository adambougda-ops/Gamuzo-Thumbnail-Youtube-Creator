package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailBlueprintDao {
    @Query("SELECT * FROM thumbnail_blueprints ORDER BY timestamp DESC")
    fun getAllBlueprints(): Flow<List<ThumbnailBlueprint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprint(blueprint: ThumbnailBlueprint): Long

    @Query("DELETE FROM thumbnail_blueprints WHERE id = :id")
    suspend fun deleteBlueprint(id: Int)

    @Query("DELETE FROM thumbnail_blueprints")
    suspend fun deleteAllBlueprints()
}
