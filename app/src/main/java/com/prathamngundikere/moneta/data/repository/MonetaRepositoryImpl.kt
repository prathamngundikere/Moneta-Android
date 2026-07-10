package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.data.local.dao.TransactionDao
import com.prathamngundikere.moneta.data.local.entity.TransactionEntity
import com.prathamngundikere.moneta.data.remote.MonetaApiClient
import com.prathamngundikere.moneta.data.remote.dto.*
import com.prathamngundikere.moneta.domain.model.*
import com.prathamngundikere.moneta.domain.repository.MonetaRepository
import jakarta.inject.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.temporal.ChronoUnit

@Singleton
class MonetaRepositoryImpl @Inject constructor(
    private val api: MonetaApiClient,
    private val dao: TransactionDao
) : MonetaRepository {

    override suspend fun getAccounts(): Flow<Resource<List<Account>>> = flow {
        emit(Resource.Loading())
        try {
            val remoteAccounts: List<AccountDto> = api.get("/api/accounts")
            val accounts = remoteAccounts.map {
                Account(
                    id = it.id ?: "",
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    name = it.name,
                    accountType = AccountType.valueOf(it.accountType),
                    balance = it.balance,
                    dueDate = it.dueDate,
                    currency = it.currency,
                    isActive = it.isActive
                )
            }
            emit(Resource.Success(accounts))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch accounts"))
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val remoteCategories: List<CategoryDto> = api.get("/api/categories")
            val categories = remoteCategories.map { dto ->
                Category(
                    id = dto.id ?: "",
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt,
                    name = dto.name,
                    parentCategory = dto.parentCategory?.let { parent ->
                        Category(parent.id ?: "", parent.createdAt, parent.updatedAt, parent.name)
                    }
                )
            }
            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch categories"))
        }
    }

    override suspend fun getItems(): Flow<Resource<List<Item>>> = flow {
        emit(Resource.Loading())
        try {
            val remoteItems: List<ItemDto> = api.get("/api/items")
            val items = remoteItems.map { dto ->
                Item(
                    id = dto.id ?: "",
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt,
                    name = dto.name,
                    description = dto.description,
                    category = dto.category?.let { cat ->
                        Category(cat.id ?: "", cat.createdAt, cat.updatedAt, cat.name)
                    }
                )
            }
            emit(Resource.Success(items))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch items"))
        }
    }

    override suspend fun getAccountStatement(accountId: String): Flow<Resource<List<TransactionEntity>>> = flow {
        emit(Resource.Loading())
        val oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli()
        val cachedData = dao.getRecentTransactions(accountId, oneWeekAgo).first()
        emit(Resource.Loading(data = cachedData))

        try {
            val remoteTransactions: List<AccountStatementResponseDto> = api.get("/api/accounts/$accountId/transactions")
            val entities = remoteTransactions.map { dto ->
                TransactionEntity(
                    merchant = dto.merchant ?: "Unknown",
                    transactionDate = Instant.parse(dto.transactionDate).toEpochMilli(),
                    amountMoved = dto.amountMoved,
                    accountId = accountId
                )
            }.filter { it.transactionDate >= oneWeekAgo }
            dao.insertTransactions(entities)
            val updatedCache = dao.getRecentTransactions(accountId, oneWeekAgo).first()
            emit(Resource.Success(updatedCache))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Error", data = cachedData))
        }
    }

    override suspend fun clearOldCache() {
        val oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli()
        dao.deleteOldTransactions(oneWeekAgo)
    }

    override suspend fun createAccount(name: String, type: AccountType, balance: Double, currency: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.post("/api/accounts", CreateAccountDto(name, type.name, balance, currency))
            if (response.status.value in 200..299) emit(Resource.Success(Unit))
            else emit(Resource.Error("Error: ${response.status.value}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create account"))
        }
    }

    override suspend fun createCategory(name: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.post("/api/categories", CreateCategoryDto(name))
            if (response.status.value in 200..299) emit(Resource.Success(Unit))
            else emit(Resource.Error("Error: ${response.status.value}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create category"))
        }
    }

    override suspend fun createItem(name: String, description: String?, categoryId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.post("/api/items", CreateItemDto(name, description, ItemCategoryDto(categoryId)))
            if (response.status.value in 200..299) emit(Resource.Success(Unit))
            else emit(Resource.Error("Error: ${response.status.value}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create item"))
        }
    }

    override suspend fun recordTransaction(payload: TransactionPayloadDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.post("/api/transactions", payload)
            if (response.status.value in 200..299) emit(Resource.Success(Unit))
            else emit(Resource.Error("Error: ${response.status.value}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to record transaction"))
        }
    }
}