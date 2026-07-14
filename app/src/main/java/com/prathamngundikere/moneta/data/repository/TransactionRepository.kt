package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.TransactionDao
import com.prathamngundikere.moneta.data.db.TransactionEntity
import com.prathamngundikere.moneta.data.model.dto.TransactionDetailDto
import com.prathamngundikere.moneta.data.model.dto.TransactionPayloadDto
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val settingsManager: SettingsManager,
    private val retrofitFactory: RetrofitFactory
) {
    private suspend fun getApi() = retrofitFactory.create(
        settingsManager.getCredentials()?.first ?: throw Exception("Missing URL"),
        settingsManager.getCredentials()?.second ?: throw Exception("Missing Key")
    )

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    suspend fun refreshTransactions(): Result<Unit> {
        return try {
            val response = getApi().getTransactions(0, 50)
            if (response.isSuccessful) {
                val dtos = response.body()?.content ?: emptyList()
                val entities = dtos.map {
                    TransactionEntity(it.id, it.merchant, it.transactionDate, it.totalAmount, it.notes)
                }
                transactionDao.insertTransactions(entities)

                // Delete older than 7 days
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
                val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                transactionDao.deleteOlderThan(cutoffDate)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch transactions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTransaction(payload: TransactionPayloadDto): Result<Unit> {
        return try {
            val response = getApi().createTransaction(payload)
            if (response.isSuccessful) {
                refreshTransactions() // Refresh local DB after successful creation
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create transaction"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactionById(id: String): Result<TransactionDetailDto> {
        return try {
            val response = getApi().getTransactionById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Transaction details not found"))
            } else {
                Result.failure(Exception("Failed to fetch transaction details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}