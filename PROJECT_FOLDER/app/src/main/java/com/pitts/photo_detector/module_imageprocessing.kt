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
            val inputStream: InputStream
            try {
                inputStream = context.assets.open(pictureFilePath)

            } catch (ioException: IOException) {
                throw RuntimeException("OBTAINBITMAPFROMASSET() IOEXCEPTION")
            }
            var bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            if (module_param.pytorch_param_useScaling) {
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    module_param.pytorch_param_bitmapScaleFactor.first,
                    module_param.pytorch_param_bitmapScaleFactor.second,
                    module_param.pytorch_param_useFilterWhileScaling
                )
            }
            Log.d(
                "Q_IMAGEPROCESSING", "obtainBitmapFromAsset() -file "
                        + pictureFilePath
                        + if (module_param.pytorch_param_useScaling) {
                    StringBuilder()
                        .append(" -resize ")
                        .append(module_param.pytorch_param_bitmapScaleFactor.first)
                        .append(" ")
                        .append(module_param.pytorch_param_bitmapScaleFactor.second)
                        .append(
                            if (module_param.pytorch_param_useFilterWhileScaling) " -filter " else String()
                        )
                        .toString()
                } else String()
            )
            return bitmap
        }

        fun obtainBitmapFromFilePath(pictureFilePath: String): Bitmap {
            val rawBitmap = BitmapFactory.decodeFile(pictureFilePath.toString())
            Log.d(
                "Q_MODULE_PYTORCH",
                "OBTAINBITMAPFROMFILEPATH():  ${pictureFilePath.toString()}"
            )
            return Bitmap.createScaledBitmap(
                rawBitmap,
                module_param.pytorch_param_bitmapScaleFactor.first,
                module_param.pytorch_param_bitmapScaleFactor.second,
                true
            )
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