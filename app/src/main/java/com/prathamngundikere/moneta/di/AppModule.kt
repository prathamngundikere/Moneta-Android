package com.prathamngundikere.moneta.di

import android.app.Application
import androidx.room.Room
import com.prathamngundikere.moneta.data.local.MonetaDatabase
import com.prathamngundikere.moneta.data.local.dao.TransactionDao
import com.prathamngundikere.moneta.data.remote.MonetaApiClient
import com.prathamngundikere.moneta.data.repository.MonetaRepositoryImpl
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMonetaDatabase(app: Application): MonetaDatabase {
        return Room.databaseBuilder(
            app,
            MonetaDatabase::class.java,
            "moneta_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: MonetaDatabase) = db.transactionDao

    @Provides
    @Singleton
    fun provideMonetaRepository(
        api: MonetaApiClient,
        dao: TransactionDao
    ): MonetaRepository {
        return MonetaRepositoryImpl(api, dao)
    }
}