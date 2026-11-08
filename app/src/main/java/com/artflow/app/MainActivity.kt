
package com.artflow.app

import android.os.Bundle
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.ColorMatrixColorFilter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Ink = Color(0xFF202124)
private val Muted = Color(0xFF777B82)
private val Accent = Color(0xFF6657D9)
private val Background = Color(0xFFF7F8FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtflowApp()
        }
    }
}

@Composable
fun ArtflowApp() {
    var selected by remember { mutableStateOf("Главная") }
    var selectedPhoto by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedPhoto = uri
            selected = "Фото"
        }
    }

    fun openGallery() {
        photoPicker.launch("image/*")
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Accent,
            background = Background,
            surface = Color.White,
            onSurface = Ink
        )
    ) {
        Scaffold(
            containerColor = Background,
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    val items = listOf(
                        "Главная" to Icons.Outlined.Home,
                        "Фото" to Icons.Outlined.Photo,
                        "AI" to Icons.Outlined.AutoAwesome,
                        "Проекты" to Icons.Outlined.FolderOpen
                    )

                    items.forEach { (name, icon) ->
                        NavigationBarItem(
                            selected = selected == name,
                            onClick = { selected = name },
                            icon = {
                                Icon(icon, contentDescription = name)
                            },
                            label = { Text(name) }
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                "Главная" -> HomeScreen(
                    Modifier.padding(padding),
                    onPhoto = { openGallery() },
                    onAI = { selected = "AI" }
                )

                "Фото" -> PhotoEditorScreen(
                    modifier = Modifier.padding(padding),
                    photoUri = selectedPhoto,
                    onChoosePhoto = { openGallery() }
                )

                "AI" -> ModuleScreen(
                    "AI-студия",
                    "Здесь появятся инструменты искусственного интеллекта."
                )

                else -> ModuleScreen(
                    "Мои проекты",
                    "Здесь будут храниться твои работы."
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPhoto: () -> Unit,
    onAI: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "ARTFLOW",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )
                Text(
                    "Твоя творческая студия",
                    color = Muted,
                    fontSize = 13.sp
                )
            }

            Icon(
                Icons.Outlined.PersonOutline,
                contentDescription = "Профиль",
                tint = Accent,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(35.dp))

        Text(
            "Создавай свободно.",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "Все инструменты творчества в одном приложении.",
            color = Muted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(25.dp))

        ActionCard(
            "Редактировать фото",
            "Цвет, свет, ретушь и эффекты",
            Icons.Outlined.PhotoCamera,
            onPhoto
        )

        Spacer(Modifier.height(12.dp))

        ActionCard(
            "AI-студия",
            "Умная обработка изображений",
            Icons.Outlined.AutoAwesome,
            onAI,
            true
        )

        Spacer(Modifier.height(30.dp))

        Text(
            "Быстрый старт",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(15.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickTool("Цвет", Icons.Outlined.Tune, onPhoto)
            QuickTool("Обрезка", Icons.Outlined.Crop, onPhoto)
            QuickTool("AI", Icons.Outlined.AutoFixHigh, onAI)
        }

        Spacer(Modifier.height(30.dp))

        Text(
            "Недавние проекты",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Collections,
                    contentDescription = null,
                    tint = Muted
                )
                Spacer(Modifier.height(7.dp))
                Text("Пока здесь пусто", color = Muted)
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (highlighted) Color(0xFFF0EDFF)
                else Color.White
            )
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(35.dp)
        )

        Spacer(Modifier.width(15.dp))

        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Ink,
                fontWeight = FontWeight.SemiBold
            )
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }

        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Muted
        )
    }
}

@Composable
fun RowScope.QuickTool(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Accent)
        Spacer(Modifier.height(7.dp))
        Text(title, fontSize = 12.sp, color = Ink)
    }
}

@Composable
fun PhotoEditorScreen(
    modifier: Modifier = Modifier,
    photoUri: Uri?,
    onChoosePhoto: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var original by remember(photoUri) {
        mutableStateOf<Bitmap?>(null)
    }

    var brightness by remember(photoUri) {
        mutableFloatStateOf(0f)
    }
    var contrast by remember(photoUri) {
        mutableFloatStateOf(0f)
    }
    var saturation by remember(photoUri) {
        mutableFloatStateOf(0f)
    }
    var warmth by remember(photoUri) {
        mutableFloatStateOf(0f)
    }
    var rotation by remember(photoUri) {
        mutableIntStateOf(0)
    }
    var selectedFilter by remember(photoUri) {
        mutableStateOf("Оригинал")
    }

    var showOriginal by remember {
        mutableStateOf(false)
    }
    var saving by remember {
        mutableStateOf(false)
    }
    var message by remember {
        mutableStateOf("")
    }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        if (uri != null && original != null) {
            scope.launch {
                saving = true
                message = ""

                try {
                    withContext(Dispatchers.IO) {
                        val matrix = createEditorMatrix(
                            brightness,
                            contrast,
                            saturation,
                            warmth,
                            selectedFilter
                        )

                        val result = createEditedBitmap(
                            original!!,
                            matrix,
                            rotation
                        )

                        context.contentResolver
                            .openOutputStream(uri)
                            ?.use { stream ->
                                result.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    95,
                                    stream
                                )
                            } ?: error("Не удалось открыть файл")

                        result.recycle()
                    }

                    message = "Фотография успешно сохранена!"
                } catch (e: Exception) {
                    message = "Ошибка сохранения: ${e.message}"
                } finally {
                    saving = false
                }
            }
        }
    }

    LaunchedEffect(photoUri) {
        original = null

        if (photoUri != null) {
            original = withContext(Dispatchers.IO) {
                val resolver = context.contentResolver

                val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                resolver.openInputStream(photoUri)?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }

                var sample = 1

                while (
                    maxOf(bounds.outWidth, bounds.outHeight) /
                    sample > 3000
                ) {
                    sample *= 2
                }

                val options = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }

                resolver.openInputStream(photoUri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }
        }
    }

    val matrix = remember(
        brightness,
        contrast,
        saturation,
        warmth,
        selectedFilter
    ) {
        createEditorMatrix(
            brightness,
            contrast,
            saturation,
            warmth,
            selectedFilter
        )
    }

    val preview = remember(original) {
        original?.let { bitmap ->
            val maxSize = 1600f

            val scale = minOf(
                1f,
                maxSize / maxOf(
                    bitmap.width.toFloat(),
                    bitmap.height.toFloat()
                )
            )

            if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * scale).toInt().coerceAtLeast(1),
                    (bitmap.height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                bitmap
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "Фоторедактор",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Text(
            "Создавай фотографии по-своему",
            color = Muted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(18.dp))

        if (preview == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(55.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Выбери фотографию",
                        color = Ink
                    )
                }
            }
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = preview.asImageBitmap(),
                    contentDescription = "Предпросмотр фотографии",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = if (showOriginal) {
                        null
                    } else {
                        ColorFilter.colorMatrix(ColorMatrix(matrix))
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        showOriginal = !showOriginal
                    }
                ) {
                    Icon(Icons.Outlined.Compare, null)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        if (showOriginal) "После"
                        else "До / После"
                    )
                }

                OutlinedButton(
                    onClick = {
                        rotation = (rotation + 90) % 360
                    }
                ) {
                    Icon(Icons.Outlined.RotateRight, null)
                    Text("Повернуть")
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Фильтры",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            Spacer(Modifier.height(10.dp))

            val filters = listOf(
                "Оригинал",
                "Яркий",
                "Тёплый",
                "Холодный",
                "Винтаж",
                "Ч/Б"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                            showOriginal = false
                        },
                        label = {
                            Text(filter)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEAE6FF),
                            selectedLabelColor = Accent
                        )
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Настройки изображения",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            Spacer(Modifier.height(12.dp))

            EditorSlider(
                "Яркость",
                brightness,
                { brightness = it }
            )

            EditorSlider(
                "Контрастность",
                contrast,
                { contrast = it }
            )

            EditorSlider(
                "Насыщенность",
                saturation,
                { saturation = it }
            )

            EditorSlider(
                "Теплота",
                warmth,
                { warmth = it }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    brightness = 0f
                    contrast = 0f
                    saturation = 0f
                    warmth = 0f
                    selectedFilter = "Оригинал"
                    rotation = 0
                    showOriginal = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.RestartAlt, null)
                Spacer(Modifier.width(8.dp))
                Text("Сбросить настройки")
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onChoosePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent
            )
        ) {
            Icon(Icons.Outlined.AddPhotoAlternate, null)
            Spacer(Modifier.width(8.dp))
            Text("Выбрать фотографию")
        }

        if (preview != null) {
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    showOriginal = false
                    saveLauncher.launch(
                        "ARTFLOW_${System.currentTimeMillis()}.jpg"
                    )
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF249B61)
                )
            ) {
                Icon(Icons.Outlined.Save, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (saving) "Сохранение..."
                    else "Сохранить фотографию"
                )
            }
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(message, color = Accent)
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun EditorSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                color = Ink,
                fontWeight = FontWeight.Medium
            )

            Text(
                value.toInt().toString(),
                color = Accent,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -100f..100f,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent
            )
        )
    }
}

@Composable
fun ModuleScreen(
    title: String,
    description: String
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(50.dp)
        )

        Spacer(Modifier.height(18.dp))

        Text(
            title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(10.dp))

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
    warmth: Float,
    filter: String = "Оригинал"
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

    val adjustments = multiplyColorMatrices(
        temp,
        multiplyColorMatrices(con, sat)
    )

    val preset = when (filter) {

        "Яркий" -> floatArrayOf(
            1.12f, 0f, 0f, 0f, -8f,
            0f, 1.12f, 0f, 0f, -8f,
            0f, 0f, 1.12f, 0f, -8f,
            0f, 0f, 0f, 1f, 0f
        )

        "Тёплый" -> floatArrayOf(
            1.12f, 0f, 0f, 0f, 4f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 0.84f, 0f, -2f,
            0f, 0f, 0f, 1f, 0f
        )

        "Холодный" -> floatArrayOf(
            0.88f, 0f, 0f, 0f, -2f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1.14f, 0f, 5f,
            0f, 0f, 0f, 1f, 0f
        )

        "Винтаж" -> floatArrayOf(
            0.92f, 0.04f, 0f, 0f, 12f,
            0.02f, 0.88f, 0.02f, 0f, 7f,
            0f, 0.04f, 0.76f, 0f, -3f,
            0f, 0f, 0f, 1f, 0f
        )

        "Ч/Б" -> floatArrayOf(
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )

        else -> floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    return multiplyColorMatrices(
        adjustments,
        preset
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

    val paint = Paint(
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    ).apply {
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
