package com.harmonic.insight.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.harmonic.insight.launcher.data.local.entity.FolderAppEntity
import com.harmonic.insight.launcher.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FolderDao {

    @Query("SELECT * FROM folders ORDER BY position ASC")
    abstract fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFolder(folder: FolderEntity): Long

    @Query("UPDATE folders SET name = :name WHERE id = :folderId")
    abstract suspend fun renameFolder(folderId: Long, name: String)

    @Query("DELETE FROM folders WHERE id = :folderId")
    abstract suspend fun deleteFolder(folderId: Long)

    @Query("SELECT * FROM folder_apps WHERE folderId = :folderId ORDER BY position ASC")
    abstract fun getFolderApps(folderId: Long): Flow<List<FolderAppEntity>>

    @Query("SELECT * FROM folder_apps ORDER BY position ASC")
    abstract fun getAllFolderApps(): Flow<List<FolderAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFolderApp(folderApp: FolderAppEntity)

    @Query("DELETE FROM folder_apps WHERE folderId = :folderId AND packageName = :packageName")
    abstract suspend fun removeFolderApp(folderId: Long, packageName: String)

    @Query("SELECT COUNT(*) FROM folder_apps WHERE folderId = :folderId")
    abstract suspend fun getFolderAppCount(folderId: Long): Int

    @Query("SELECT MAX(position) FROM folders")
    abstract suspend fun getMaxFolderPosition(): Int?

    @Transaction
    open suspend fun addAppToFolderTransactional(folderId: Long, packageName: String) {
        val count = getFolderAppCount(folderId)
        insertFolderApp(FolderAppEntity(folderId, packageName, count))
    }
}
