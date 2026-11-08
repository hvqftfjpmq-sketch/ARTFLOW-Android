        Text(
            description,
            color = Muted
        )
    }
}
fun createEditorMatrix(
    brightness: Float,
    contrast: Float,
    saturation: Float,
    warmth: Float
): FloatArray {

    val s = 1f + saturation / 100f
    val c = 1f + contrast / 100f
    val b = brightness * 2.55f

    val sat = floatArrayOf(
        0.213f + 0.787f * s,
        0.715f - 0.715f * s,
        0.072f - 0.072f * s,
        0f, 0f,

        0.213f - 0.213f * s,
        0.715f + 0.285f * s,
        0.072f - 0.072f * s,
        0f, 0f,

        0.213f - 0.213f * s,
        0.715f - 0.715f * s,
        0.072f + 0.928f * s,
        0f, 0f,

        0f, 0f, 0f, 1f, 0f
    )

    val con = floatArrayOf(
        c, 0f, 0f, 0f, 128f * (1f - c) + b,
        0f, c, 0f, 0f, 128f * (1f - c) + b,
        0f, 0f, c, 0f, 128f * (1f - c) + b,
        0f, 0f, 0f, 1f, 0f
    )

    val w = warmth / 100f

    val temp = floatArrayOf(
        1f + w * 0.25f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f - w * 0.25f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    return multiplyColorMatrices(
        temp,
        multiplyColorMatrices(con, sat)
    )
}

fun multiplyColorMatrices(
    a: FloatArray,
    b: FloatArray
): FloatArray {
    val result = FloatArray(20)

    for (row in 0..3) {
        for (col in 0..3) {
            var value = 0f

            for (k in 0..3) {
                value += a[row * 5 + k] *
                    b[k * 5 + col]
            }

            result[row * 5 + col] = value
        }

        var offset = a[row * 5 + 4]

        for (k in 0..3) {
            offset += a[row * 5 + k] *
                b[k * 5 + 4]
        }

        result[row * 5 + 4] = offset
    }

    return result
}

fun createEditedBitmap(
    source: Bitmap,
    matrix: FloatArray,
    rotation: Int
): Bitmap {

    val rotated = rotation % 180 != 0

    val width = if (rotated) source.height else source.width
    val height = if (rotated) source.width else source.height

    val output = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(output)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }

    canvas.translate(width / 2f, height / 2f)
    canvas.rotate(rotation.toFloat())

    canvas.drawBitmap(
        source,
        -source.width / 2f,
        -source.height / 2f,
        paint
    )

    return output
}
