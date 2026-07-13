package com.prathamngundikere.moneta.data.repository

import com.prathamngundikere.moneta.data.datastore.SettingsManager
import com.prathamngundikere.moneta.data.db.CategoryDao
import com.prathamngundikere.moneta.data.db.CategoryEntity
import com.prathamngundikere.moneta.data.model.dto.CategoryCreateRequest
import com.prathamngundikere.moneta.data.network.ApiService
import com.prathamngundikere.moneta.data.network.RetrofitFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val settingsManager: SettingsManager,
    private val retrofitFactory: RetrofitFactory
) {
    private suspend fun getApi(): ApiService {
        val (url, key) = settingsManager.getCredentials() ?: throw Exception("Missing config")
        return retrofitFactory.create(url, key)
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategoriesFlow()
    fun getCategoryById(id: String): Flow<CategoryEntity?> = categoryDao.getCategoryByIdFlow(id)

    suspend fun refreshCategories(): Result<Unit> {
        return try {
            val response = getApi().getCategories()
            if (response.isSuccessful) {
                val dtos = response.body()?.content ?: emptyList()
                val entities = dtos.map { CategoryEntity(it.id, it.name, it.createdAt, it.updatedAt) }
                categoryDao.insertCategories(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch categories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategory(name: String): Result<Unit> {
        return try {
            val response = getApi().createCategory(CategoryCreateRequest(name))
            if (response.isSuccessful) {
                response.body()?.let {
                    categoryDao.insertCategory(CategoryEntity(it.id, it.name, it.createdAt, it.updatedAt))
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}