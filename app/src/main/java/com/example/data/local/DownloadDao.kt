package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DownloadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
  @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
  fun getAllDownloads(): Flow<List<DownloadItem>>

  @Query("SELECT * FROM downloads WHERE id = :id")
  suspend fun getDownloadById(id: Long): DownloadItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDownload(item: DownloadItem): Long

  @Update
  suspend fun updateDownload(item: DownloadItem)

  @Query("UPDATE downloads SET downloadedBytes = :downloadedBytes, totalBytes = :totalBytes, speedText = :speedText, status = :status WHERE id = :id")
  suspend fun updateProgress(id: Long, downloadedBytes: Long, totalBytes: Long, speedText: String, status: com.example.data.model.DownloadStatus)

  @Query("UPDATE downloads SET status = :status, localFilePath = :localPath WHERE id = :id")
  suspend fun markCompleted(id: Long, status: com.example.data.model.DownloadStatus, localPath: String)

  @Delete
  suspend fun deleteDownload(item: DownloadItem)

  @Query("DELETE FROM downloads WHERE id = :id")
  suspend fun deleteById(id: Long)
}
