package com.example.vkr_todolist.cache.koin

import android.content.Context
import androidx.room.Room
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import com.example.vkr_todolist.cache.room.db.AppDatabase
import com.example.vkr_todolist.cache.room.mapper.EventCacheMapper
import com.example.vkr_todolist.cache.room.mapper.ListCacheMapper
import com.example.vkr_todolist.cache.room.mapper.NoteCacheMapper
import com.example.vkr_todolist.cache.room.mapper.TaskCacheMapper
import com.example.vkr_todolist.cache.source.EventCacheDataSourceImpl
import com.example.vkr_todolist.cache.source.ListCacheDataSourceImpl
import com.example.vkr_todolist.cache.source.NoteCacheDataSourceImpl
import com.example.vkr_todolist.cache.source.ProductivityCacheDataSourceImpl
import com.example.vkr_todolist.cache.source.TaskCacheDataSourceImpl
import com.example.vkr_todolist.data.source.local.EventCacheDataSource
import com.example.vkr_todolist.data.source.local.ListCacheDataSource
import com.example.vkr_todolist.data.source.local.NoteCacheDataSource
import com.example.vkr_todolist.data.source.local.ProductivityCacheDataSource
import com.example.vkr_todolist.data.source.local.TaskCacheDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {
    single { provideDataBase(androidContext()) }
    single { provideEventDao(get()) }
    single { provideListDao(get()) }
    single { provideNoteDao(get()) }
    single { provideTaskDao(get()) }

    single<EventCacheDataSource> { EventCacheDataSourceImpl(get(), get()) }
    single<ListCacheDataSource> { ListCacheDataSourceImpl(get(), get()) }
    single<NoteCacheDataSource> { NoteCacheDataSourceImpl(get(), get()) }
    single<ProductivityCacheDataSource> { ProductivityCacheDataSourceImpl(get()) }
    single<TaskCacheDataSource> { TaskCacheDataSourceImpl(get(), get()) }

    factory { EventCacheMapper(get()) }
    factory { ListCacheMapper() }
    factory { NoteCacheMapper(get()) }
    factory { TaskCacheMapper(get()) }
}

private fun provideDataBase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        CacheConstants.DATABASE_NAME
    ).build()
}

private fun provideEventDao(appDatabase: AppDatabase) = appDatabase.getEventDao()

private fun provideListDao(appDatabase: AppDatabase) = appDatabase.getListDao()

private fun provideNoteDao(appDatabase: AppDatabase) = appDatabase.getNoteDao()

private fun provideTaskDao(appDatabase: AppDatabase) = appDatabase.getTaskDao()