import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.widget.Toast

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


object CameraUtil {

    fun setupImageOrientation(originalFile: File, context: Context): File? {
        val attributes = arrayOf( ExifInterface.TAG_DATETIME, ExifInterface.TAG_EXPOSURE_TIME, ExifInterface.TAG_FLASH, ExifInterface.TAG_FOCAL_LENGTH, ExifInterface.TAG_GPS_ALTITUDE, ExifInterface.TAG_GPS_ALTITUDE_REF, ExifInterface.TAG_GPS_DATESTAMP, ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF, ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF, ExifInterface.TAG_GPS_PROCESSING_METHOD, ExifInterface.TAG_GPS_TIMESTAMP, ExifInterface.TAG_ISO, ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_WHITE_BALANCE)

        var rotate = 0 //Padrão na vertical
        var fotoDestino: File? = null
        val filename = originalFile.absolutePath
        val olfExif = ExifInterface(filename)
        try {
            try {
                val orientation = olfExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: IOException) {
                Toast.makeText(context, "Não foi possível inserir a imagem", Toast.LENGTH_LONG).show()
                return null
            }

            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())

            val bitmap = BitmapFactory.decodeFile(filename)
            val cropped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            fotoDestino = File(filename)
            var os: FileOutputStream? = null

            try {
                os = FileOutputStream(fotoDestino)
                cropped.compress(Bitmap.CompressFormat.JPEG, 100, os)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    os!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Não foi possível inserir a imagem, problema desconhecido", Toast.LENGTH_LONG).show()

        } catch (ome: OutOfMemoryError) {
            ome.printStackTrace()
            Toast.makeText(context, "Não foi possível inserir a imagem, libere mais memória", Toast.LENGTH_LONG).show()
        }

        var newPath = fotoDestino?.path

        val newExif = ExifInterface(newPath)
        for (i in attributes.indices) {
            val value = olfExif.getAttribute(attributes[i])
            if (value != null)
                newExif.setAttribute(attributes[i], value)
        }
        newExif.saveAttributes()

        return fotoDestino
    }
}