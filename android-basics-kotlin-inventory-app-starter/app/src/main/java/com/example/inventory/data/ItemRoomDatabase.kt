package com.example.inventory.data

import Item
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Specify the Item as the only class with the list of entities.
Set the version as 1. Whenever you change the schema of the database table, you'll have to increase the version number.
Set exportSchema to false, so as not to keep schema version history backups.

 * */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao // database가 DAO를 알아야함.

    companion object {
        @Volatile // 읽기, 쓰기가 캐싱되지 않고 메모리에서 끝남. 따라서 instance는 항상 최신이고 모든 스레드가 똑같이 본다.
        private var INSTANCE: ItemRoomDatabase? = null

        // syncronized : hold only one thread of execution at a time can enter this block of code,
        // which makes sure the database only gets initialized once.
        fun getDatabase(context: Context): ItemRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration() // 마이그레이션 전략
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}