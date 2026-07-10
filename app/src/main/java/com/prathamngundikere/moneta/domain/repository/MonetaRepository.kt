package com.prathamngundikere.moneta.domain.repository

import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.data.local.entity.TransactionEntity
import com.prathamngundikere.moneta.data.remote.dto.TransactionPayloadDto
import com.prathamngundikere.moneta.domain.model.Account
import com.prathamngundikere.moneta.domain.model.AccountType
import com.prathamngundikere.moneta.domain.model.Category
import com.prathamngundikere.moneta.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface MonetaRepository {
    suspend fun getAccounts(): Flow<Resource<List<Account>>>
    suspend fun getCategories(): Flow<Resource<List<Category>>>
    suspend fun getItems(): Flow<Resource<List<Item>>>
    suspend fun getAccountStatement(accountId: String): Flow<Resource<List<TransactionEntity>>>
    suspend fun clearOldCache()

    suspend fun createAccount(name: String, type: AccountType, balance: Double, currency: String): Flow<Resource<Unit>>
    suspend fun createCategory(name: String): Flow<Resource<Unit>>
    suspend fun createItem(name: String, description: String?, categoryId: String): Flow<Resource<Unit>>
    suspend fun recordTransaction(payload: TransactionPayloadDto): Flow<Resource<Unit>>
}