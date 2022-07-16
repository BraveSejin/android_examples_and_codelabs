package com.example.inventory.data

import Item
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    //https://developer.android.com/reference/androidx/room/OnConflictStrategy.html?hl=ko

    // insert, update, delete 는 convenience annotion. 나머지는 쿼리! ㅁㄴㅇㄹ
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>
}