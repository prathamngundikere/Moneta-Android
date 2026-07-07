package com.prathamngundikere.moneta.domain.repository

import com.prathamngundikere.moneta.core.Resource
import com.prathamngundikere.moneta.data.local.entity.TransactionEntity
import com.prathamngundikere.moneta.domain.model.Account
import com.prathamngundikere.moneta.domain.model.Category
import com.prathamngundikere.moneta.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface MonetaRepository {
    suspend fun getAccounts(): Flow<Resource<List<Account>>>
    suspend fun getCategories(): Flow<Resource<List<Category>>>
    suspend fun getItems(): Flow<Resource<List<Item>>>
    suspend fun getAccountStatement(accountId: String): Flow<Resource<List<TransactionEntity>>>
    suspend fun clearOldCache()
}