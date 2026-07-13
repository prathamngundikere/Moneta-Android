package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.ItemDao
import com.prathamngundikere.moneta.data.db.ItemEntity
import com.prathamngundikere.moneta.data.model.dto.ItemAssignCategoryRequest
import com.prathamngundikere.moneta.data.model.dto.ItemCreateRequest
import com.prathamngundikere.moneta.data.model.dto.ItemUpdateRequest
import com.prathamngundikere.moneta.data.network.ApiService
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val settingsManager: SettingsManager,
    private val retrofitFactory: RetrofitFactory
) {
    private suspend fun getApi(): ApiService {
        val (url, key) = settingsManager.getCredentials() ?: throw Exception("Missing config")
        return retrofitFactory.create(url, key)
    }

    fun getAllItems(): Flow<List<ItemEntity>> = itemDao.getAllItemsFlow()

    fun getItemById(id: String): Flow<ItemEntity?> = itemDao.getItemByIdFlow(id)

    suspend fun refreshItems(): Result<Unit> {
        return try {
            val api = getApi()
            val response = api.getItems()
            if (response.isSuccessful) {
                val dtos = response.body()?.content ?: emptyList()
                val entities = dtos.map {
                    ItemEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        categoryId = it.category?.id,
                        categoryName = it.category?.name,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
                itemDao.insertItems(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch items from server."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createItem(name: String, description: String): Result<Unit> {
        return try {
            val api = getApi()
            val request = ItemCreateRequest(name, description)
            val response = api.createItem(request)

            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val entity = ItemEntity(
                        dto.id,
                        dto.name,
                        dto.description,
                        dto.category?.id,
                        dto.category?.name,
                        dto.createdAt,
                        dto.updatedAt
                    )
                    itemDao.insertItem(entity)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Item created but response was empty."))
                }
            } else {
                Result.failure(Exception("Failed to create item."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(id: String, name: String, description: String): Result<Unit> {
        return try {
            val api = getApi()
            val request = ItemUpdateRequest(name, description)
            val response = api.updateItem(id, request)

            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val updatedEntity = ItemEntity(
                        dto.id,
                        dto.name,
                        dto.description,
                        dto.category?.id,
                        dto.category?.name,
                        dto.createdAt,
                        updatedAt = dto.updatedAt,
                    )
                    itemDao.updateItem(updatedEntity)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Item updated but response was empty."))
                }
            } else {
                Result.failure(Exception("Failed to update item."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignItemsToCategory(itemIds: List<String>, categoryId: String?): Result<Unit> {
        return try {
            val response = getApi().assignItemsToCategory(
                ItemAssignCategoryRequest(
                    itemIds,
                    categoryId
                )
            )
            if (response.isSuccessful) {
                refreshItems() // Force refresh to update Room DB with new category linkages
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to assign items to category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add local fetch function
    fun getItemsByCategory(categoryId: String): Flow<List<ItemEntity>> = itemDao.getItemsByCategoryIdFlow(categoryId)
}