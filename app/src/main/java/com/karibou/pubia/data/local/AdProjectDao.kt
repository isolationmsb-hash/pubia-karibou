package com.karibou.pubia.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AdProjectDao {

    @Query("SELECT * FROM ad_projects ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<AdProjectEntity>>

    @Query("SELECT * FROM ad_projects WHERE id = :id")
    suspend fun getById(id: Long): AdProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: AdProjectEntity): Long

    @Update
    suspend fun update(project: AdProjectEntity)

    @Delete
    suspend fun delete(project: AdProjectEntity)

    @Query("DELETE FROM ad_projects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
