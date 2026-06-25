package com.practicum.playlistmaker.data.playlist

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.domain.playlist.repository.PlaylistImageRepository
import java.io.File
import java.io.FileOutputStream

class PlaylistImageRepositoryImpl(
    private val contentResolver: ContentResolver,
    private val filesDir: java.io.File
) : PlaylistImageRepository {

    override fun saveImageToPrivateStorage(uriString: String): String {
        val uri = Uri.parse(uriString)
        val dir = File(
            filesDir,
            COVERS_DIRECTORY
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
    }

    companion object {
        private const val COVERS_DIRECTORY = "playlist_covers"
        private const val COMPRESSION_QUALITY = 30
    }
}
