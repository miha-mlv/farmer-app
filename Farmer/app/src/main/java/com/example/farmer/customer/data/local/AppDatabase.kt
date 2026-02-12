package com.example.farmer.customer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.farmer.customer.data.local.dao.BasketDao
import com.example.farmer.customer.data.local.entity.BasketItem

@Database(entities = [BasketItem::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun basketDao(): BasketDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "farmer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}