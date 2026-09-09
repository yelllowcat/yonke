package mx.yonkefacil.inventory

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import mx.yonkefacil.inventory.data.Part
import java.io.File
import java.io.FileOutputStream

object LabelPdfExporter {
    fun share(context: Context, parts: List<Part>) {
        val directory = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(directory, "etiquetas-yonke-facil.pdf")
        val document = PdfDocument()
        val pageWidth = 612
        val pageHeight = 792
        val labelsPerPage = 6

        parts.ifEmpty { return }.chunked(labelsPerPage).forEachIndexed { pageIndex, pageParts ->
            val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create())
            val canvas = page.canvas
            val titlePaint = Paint().apply { color = Color.rgb(23, 50, 77); textSize = 26f; isFakeBoldText = true }
            val textPaint = Paint().apply { color = Color.rgb(15, 37, 56); textSize = 16f }
            val yellowPaint = Paint().apply { color = Color.rgb(244, 197, 66) }
            pageParts.forEachIndexed { index, part ->
                val column = index % 2
                val row = index / 2
                val left = 28f + column * 290f
                val top = 28f + row * 250f
                canvas.drawRect(left, top, left + 266f, top + 220f, yellowPaint)
                canvas.drawText(part.reference, left + 15f, top + 38f, titlePaint)
                canvas.drawText(part.nameEs.take(27), left + 15f, top + 68f, textPaint)
                canvas.drawText(part.locationNote.take(30), left + 15f, top + 94f, textPaint)
                canvas.drawBitmap(qrBitmap("yonkefacil://part/${part.id}", 105), left + 80f, top + 105f, null)
            }
            document.finishPage(page)
        }
        FileOutputStream(file).use(document::writeTo)
        document.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Etiquetas QR"))
    }

    private fun qrBitmap(value: String, size: Int): Bitmap {
        val matrix: BitMatrix = MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bitmap ->
            for (x in 0 until size) for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.rgb(23, 50, 77) else Color.rgb(244, 197, 66))
            }
        }
    }
}

