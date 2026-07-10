package com.example.data

import kotlinx.coroutines.flow.Flow

class BlueprintRepository(private val dao: ThumbnailBlueprintDao) {
    val allBlueprints: Flow<List<ThumbnailBlueprint>> = dao.getAllBlueprints()

    suspend fun insert(blueprint: ThumbnailBlueprint): Long {
        return dao.insertBlueprint(blueprint)
    }

    suspend fun delete(id: Int) {
        dao.deleteBlueprint(id)
    }

    suspend fun deleteAll() {
        dao.deleteAllBlueprints()
    }
}
