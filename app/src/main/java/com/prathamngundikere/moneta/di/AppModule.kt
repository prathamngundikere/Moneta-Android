package com.prathamngundikere.moneta.di

import android.content.Context
import androidx.room.Room
import com.prathamngundikere.moneta.data.db.AccountDao
import com.prathamngundikere.moneta.data.db.MonetaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MonetaDatabase {
        return Room.databaseBuilder(
            context,
            MonetaDatabase::class.java,
            "moneta_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: MonetaDatabase): AccountDao = database.accountDao()
}