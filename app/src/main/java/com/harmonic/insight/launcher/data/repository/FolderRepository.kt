package com.harmonic.insight.launcher.data.repository

import com.harmonic.insight.launcher.data.local.FolderDao
import com.harmonic.insight.launcher.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao,
) {
    fun getAllFolders(): Flow<List<FolderEntity>> = folderDao.getAllFolders()

    fun getAllFolderApps(): Flow<List<FolderAppEntity>> = folderDao.getAllFolderApps()

    suspend fun createFolder(name: String): Long {
        val maxPos = folderDao.getMaxFolderPosition() ?: -1
        return folderDao.insertFolder(FolderEntity(name = name, position = maxPos + 1))
    }

    suspend fun renameFolder(folderId: Long, name: String) {
        folderDao.renameFolder(folderId, name)
    }

    suspend fun deleteFolder(folderId: Long) {
        folderDao.deleteFolder(folderId)
    }

    suspend fun addAppToFolder(folderId: Long, packageName: String) {
        folderDao.addAppToFolderTransactional(folderId, packageName)
    }

    suspend fun removeAppFromFolder(folderId: Long, packageName: String) {
        folderDao.removeFolderApp(folderId, packageName)
    }
}
