package com.artflow.app

import android.os.Bundle
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.graphics.Color as AndroidColor

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
import androidx.compose.ui.graphics.toArgb
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

private val EditorDark = Color(0xFFF7F8FA)
private val EditorPanel = Color(0xFFFFFFFF)
private val EditorMuted = Color(0xFF85858F)
private val EditorWhite = Color(0xFF292633)

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
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
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
                                Icon(
                                    icon,
                                    contentDescription = name,
                                    tint = Accent
                                )
                            },
                            label = { Text(name) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                indicatorColor = Color(0xFFECE7FF),
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            when (selected) {
                "Главная" -> HomeScreen(
                    modifier = Modifier.padding(padding),
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
            .verticalScroll(rememberScrollState())
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

            Text(
                subtitle,
                color = Muted,
                fontSize = 12.sp
            )
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

    var brightness by remember(photoUri) { mutableFloatStateOf(0f) }
    var contrast by remember(photoUri) { mutableFloatStateOf(0f) }
    var saturation by remember(photoUri) { mutableFloatStateOf(0f) }
    var warmth by remember(photoUri) { mutableFloatStateOf(0f) }
    var rotation by remember(photoUri) { mutableIntStateOf(0) }

    var selectedFilter by remember(photoUri) {
        mutableStateOf("Оригинал")
    }

    var selectedTab by remember {
        mutableStateOf("Регулировка")
    }

    var selectedTool by remember {
        mutableStateOf("Яркость")
    }

    var cropRatio by remember {
        mutableStateOf("1:1")
    }

    var cropX by remember { mutableFloatStateOf(0f) }
    var cropY by remember { mutableFloatStateOf(0f) }

    var overlayText by remember { mutableStateOf("") }
    var textSize by remember { mutableFloatStateOf(48f) }
    var textX by remember { mutableFloatStateOf(0f) }
    var textY by remember { mutableFloatStateOf(0f) }
    var textColor by remember { mutableStateOf(Color.White) }

    var showOriginal by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

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

    LaunchedEffect(photoUri) {
        original = null

        if (photoUri != null) {
            original = withContext(Dispatchers.IO) {
                val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                context.contentResolver.openInputStream(photoUri)?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }

                var sample = 1

                while (
                    maxOf(bounds.outWidth, bounds.outHeight) / sample > 3000
                ) {
                    sample *= 2
                }

                val options = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }

                context.contentResolver.openInputStream(photoUri)?.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }
        }
    }

    val preview = remember(original) {
        original?.let { bitmap ->
            val scale = minOf(
                1f,
                1400f / maxOf(
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

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        if (uri != null && original != null) {
            scope.launch {
                saving = true
                message = ""

                try {
                    withContext(Dispatchers.IO) {
                        var edited = createEditedBitmap(
                            original!!,
                            matrix,
                            rotation
                        )

                        if (overlayText.isNotBlank()) {
                            val withText = drawTextOnBitmap(
                                edited,
                                overlayText,
                                textColor.toArgb(),
                                textSize,
                                textX,
                                textY
                            )
                            edited.recycle()
                            edited = withText
                        }

                        context.contentResolver
                            .openOutputStream(uri)
                            ?.use { stream ->
                                edited.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    95,
                                    stream
                                )
                            } ?: error("Не удалось открыть файл")

                        edited.recycle()
                    }

                    message = "Фотография сохранена!"
                } catch (e: Exception) {
                    message = "Ошибка сохранения: ${e.message}"
                } finally {
                    saving = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EditorDark)
    ) {

        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "ARTFLOW",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onChoosePhoto) {
                Text("Другое фото", color = Accent)
            }
        }

        // Закреплённая фотография
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (preview == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(45.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Выбери фотографию", color = Ink)

                    Button(onClick = onChoosePhoto) {
                        Text("Открыть галерею")
                    }
                }
            } else {
                Image(
                    bitmap = preview.asImageBitmap(),
                    contentDescription = "Фото",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = if (showOriginal) {
                        null
                    } else {
                        ColorFilter.colorMatrix(ColorMatrix(matrix))
                    }
                )

                if (overlayText.isNotBlank() && !showOriginal) {
                    Text(
                        text = overlayText,
                        color = textColor,
                        fontSize = textSize.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(
                                x = (textX * 100f).dp,
                                y = (textY * 100f).dp
                            )
                    )
                }
            }
        }

        // Быстрые кнопки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    brightness = 0f
                    contrast = 0f
                    saturation = 0f
                    warmth = 0f
                    selectedFilter = "Оригинал"
                    rotation = 0
                    overlayText = ""
                },
                enabled = original != null
            ) {
                Icon(
                    Icons.Outlined.Refresh,
                    "Сбросить",
                    tint = Accent
                )
            }

            IconButton(
                onClick = { showOriginal = !showOriginal },
                enabled = original != null
            ) {
                Icon(
                    Icons.Outlined.Compare,
                    "До и после",
                    tint = Accent
                )
            }

            IconButton(
                onClick = {
                    rotation = (rotation + 90) % 360
                },
                enabled = original != null
            ) {
                Icon(
                    Icons.Outlined.RotateRight,
                    "Повернуть",
                    tint = Accent
                )
            }

            IconButton(
                onClick = {
                    if (original != null) {
                        showOriginal = false
                        saveLauncher.launch(
                            "ARTFLOW_${System.currentTimeMillis()}.jpg"
                        )
                    }
                },
                enabled = original != null && !saving
            ) {
                Icon(
                    Icons.Outlined.Save,
                    "Сохранить",
                    tint = Accent
                )
            }
        }

        Divider(color = Color(0xFFE8E5F0))

        // Нижняя панель
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(EditorPanel)
        ) {

            // Переключение вкладок
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                listOf("Фильтр", "Регулировка").forEach { tab ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTab = tab
                                message = ""
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            tab,
                            color = if (selectedTab == tab) Accent else Muted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(5.dp))

                        Box(
                            Modifier
                                .fillMaxWidth(0.7f)
                                .height(3.dp)
                                .background(
                                    if (selectedTab == tab) Accent
                                    else Color.Transparent,
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
            }

            // Прокручиваемая область настроек
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp)
            ) {

                if (selectedTab == "Фильтр") {

                    Text(
                        "Выбери стиль",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFECE7FF),
                                    selectedLabelColor = Accent,
                                    containerColor = Color(0xFFF7F5FC),
                                    labelColor = Ink
                                )
                            )
                        }
                    }

                } else {

                    Text(
                        "Настройка изображения",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    EditorSlider(
                        "Яркость",
                        brightness
                    ) { brightness = it }

                    EditorSlider(
                        "Контраст",
                        contrast
                    ) { contrast = it }

                    EditorSlider(
                        "Насыщенность",
                        saturation
                    ) { saturation = it }

                    EditorSlider(
                        "Теплота",
                        warmth
                    ) { warmth = it }
                }

                Spacer(Modifier.height(14.dp))

                Divider(color = Color(0xFFE8E5F0))

                Spacer(Modifier.height(10.dp))

                // Лента инструментов
                val extraTools = listOf(
                    "Обрезка" to Icons.Outlined.Crop,
                    "Текст" to Icons.Outlined.TextFields,
                    "Резкость" to Icons.Outlined.Tune,
                    "Эффекты" to Icons.Outlined.AutoAwesome,
                    "Портрет" to Icons.Outlined.Face,
                    "Стикеры" to Icons.Outlined.EmojiEmotions
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    extraTools.forEach { item ->
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedTool = item.first
                                    message = ""
                                }
                                .background(
                                    if (selectedTool == item.first) {
                                        Color(0xFFECE7FF)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                item.second,
                                contentDescription = item.first,
                                tint = Accent,
                                modifier = Modifier.size(25.dp)
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                item.first,
                                color = Ink,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Обрезка
                if (selectedTool == "Обрезка") {
                    Text(
                        "Формат кадрирования",
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(
                            rememberScrollState()
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1:1", "4:5", "16:9", "9:16").forEach {
                            ratio ->
                            FilterChip(
                                selected = cropRatio == ratio,
                                onClick = {
                                    cropRatio = ratio
                                    cropX = 0f
                                    cropY = 0f
                                },
                                label = { Text(ratio) }
                            )
                        }
                    }

                    Text("Положение по горизонтали", color = Ink)

                    Slider(
                        value = cropX,
                        onValueChange = { cropX = it },
                        valueRange = -1f..1f
                    )

                    Text("Положение по вертикали", color = Ink)

                    Slider(
                        value = cropY,
                        onValueChange = { cropY = it },
                        valueRange = -1f..1f
                    )

                    Button(
                        onClick = {
                            original?.let { bitmap ->
                                val cropped = cropBitmap(
                                    bitmap,
                                    cropRatio,
                                    cropX,
                                    cropY
                                )

                                original = cropped
                            }

                            cropX = 0f
                            cropY = 0f
                            message = "Обрезка применена!"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = original != null
                    ) {
                        Icon(Icons.Outlined.Check, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Применить обрезку")
                    }
                }

                // Текст
                if (selectedTool == "Текст") {
                    Text(
                        "Текст на фотографии",
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = overlayText,
                        onValueChange = { overlayText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Введите надпись") },
                        singleLine = true
                    )

                    Text(
                        "Размер: ${textSize.toInt()}",
                        color = Ink
                    )

                    Slider(
                        value = textSize,
                        onValueChange = { textSize = it },
                        valueRange = 20f..100f
                    )

                    Text("Горизонтальное положение", color = Ink)

                    Slider(
                        value = textX,
                        onValueChange = { textX = it },
                        valueRange = -1f..1f
                    )

                    Text("Вертикальное положение", color = Ink)

                    Slider(
                        value = textY,
                        onValueChange = { textY = it },
                        valueRange = -1f..1f
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(
                            Color.White,
                            Color.Black,
                            Color.Red,
                            Color.Yellow,
                            Color.Blue,
                            Color.Green
                        ).forEach { color ->
                            Box(
                                Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        textColor = color
                                    }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { overlayText = "" },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить текст")
                    }
                }

                // Остальные инструменты
                if (
                    selectedTool == "Резкость" ||
                    selectedTool == "Эффекты" ||
                    selectedTool == "Портрет" ||
                    selectedTool == "Стикеры"
                ) {
                    Text(
                        selectedTool,
                        color = Ink,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Инструмент будет подключён в следующем обновлении.",
                        color = Muted
                    )
                }

                if (message.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(message, color = Accent)
                }

                Spacer(Modifier.height(20.dp))
            }

            // Нижние кнопки в одну строку
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onChoosePhoto,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.AddPhotoAlternate,
                        null,
                        tint = Accent,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Фото", color = Accent, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (original != null) {
                            showOriginal = false
                            saveLauncher.launch(
                                "ARTFLOW_${System.currentTimeMillis()}.jpg"
                            )
                        }
                    },
                    enabled = original != null && !saving,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent
                    )
                ) {
                    Icon(
                        Icons.Outlined.Save,
                        null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (saving) "Сохранение" else "Сохранить",
                        fontSize = 12.sp
                    )
                }
            }
        }
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
                activeTrackColor = Accent,
                inactiveTrackColor = Color(0xFFDCD8EB)
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

        Text(description, color = Muted)
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

    return multiplyColorMatrices(adjustments, preset)
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

fun cropBitmap(
    source: Bitmap,
    ratio: String,
    offsetX: Float,
    offsetY: Float
): Bitmap {

    val targetRatio = when (ratio) {
        "1:1" -> 1f
        "4:5" -> 4f / 5f
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        else -> source.width.toFloat() / source.height.toFloat()
    }

    val sourceRatio =
        source.width.toFloat() / source.height.toFloat()

    var cropWidth = source.width
    var cropHeight = source.height

    if (sourceRatio > targetRatio) {
        cropWidth = (source.height * targetRatio)
            .toInt()
            .coerceIn(1, source.width)
    } else {
        cropHeight = (source.width / targetRatio)
            .toInt()
            .coerceIn(1, source.height)
    }

    val maxX = source.width - cropWidth
    val maxY = source.height - cropHeight

    val left = (
        maxX / 2f + offsetX * maxX / 2f
    ).toInt().coerceIn(0, maxX)

    val top = (
        maxY / 2f + offsetY * maxY / 2f
    ).toInt().coerceIn(0, maxY)

    return Bitmap.createBitmap(
        source,
        left,
        top,
        cropWidth,
        cropHeight
    )
}

fun drawTextOnBitmap(
    source: Bitmap,
    text: String,
    color: Int,
    previewTextSize: Float,
    offsetX: Float,
    offsetY: Float
): Bitmap {

    val output = source.copy(
        Bitmap.Config.ARGB_8888,
        true
    )

    val canvas = Canvas(output)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textAlign = Paint.Align.CENTER

        textSize = (
            previewTextSize * source.width / 360f
        ).coerceIn(12f, 400f)

        typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        setShadowLayer(
            4f,
            2f,
            2f,
            AndroidColor.BLACK
        )
    }

    val x = source.width / 2f +
        offsetX * source.width * 0.35f

    val y = source.height / 2f +
        offsetY * source.height * 0.35f

    canvas.drawText(text, x, y, paint)

    return output
}
