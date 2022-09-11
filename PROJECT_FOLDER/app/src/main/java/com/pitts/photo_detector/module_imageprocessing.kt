package com.pitts.photo_detector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class module_imageprocessing {
    companion object {
        fun obtainBitmapFromAsset(context: Context, pictureFilePath: String): Bitmap {
            try {
                val inputStream: InputStream = context.assets.open(pictureFilePath)
                val rawBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                return Bitmap.createScaledBitmap(
                    rawBitmap,
                    module_param.bitmapScaleFactor.first,
                    module_param.bitmapScaleFactor.second,
                    true
                )
            } catch (ioException: IOException) {
                throw RuntimeException("MODULE_PYTORCH FILE OPEN FAILED")
            }
        }

        fun obtainBitmapFromFilePath(pictureFilePath: String): Bitmap {
            val rawBitmap = BitmapFactory.decodeFile(pictureFilePath.toString())
            Log.d(
                "Q_MODULE_PYTORCH",
                "OBTAINBITMAPFROMFILEPATH():  ${pictureFilePath.toString()}"
            )
            return Bitmap.createScaledBitmap(
                rawBitmap,
                module_param.bitmapScaleFactor.first,
                module_param.bitmapScaleFactor.second,
                true
            )
        }

        private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        /**
         * https://stackoverflow.com/questions/59588556/androidkotlin-how-getting-assets-file-path-at-pytorch-mobile
         */
        fun assetFilePath(context: Context, asset: String): String {
            val file = File(context.filesDir, asset)

            try {
                val inpStream: InputStream = context.assets.open(asset)
                try {
                    val outStream = FileOutputStream(file, false)
                    val buffer = ByteArray(4 * 1024)
                    var read: Int

                    while (true) {
                        read = inpStream.read(buffer)
                        if (read == -1) {
                            break
                        }
                        outStream.write(buffer, 0, read)
                    }
                    outStream.flush()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                return file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }
    }
}