
package com.artflow.app

import android.os.Bundle
import android.net.Uri
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.layout.ContentScale

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
                NavigationBar(
                    containerColor = Color.White
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
                                Icon(icon, contentDescription = name)
                            },
                            label = {
                                Text(name)
                            }
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

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Collections,
                    contentDescription = null,
                    tint = Muted
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    "Пока здесь пусто",
                    color = Muted
                )
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp)
    ) {
        Text(
            "Фоторедактор",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Твори и редактируй свои фотографии",
            color = Muted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(20.dp))

        if (photoUri == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
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
                        modifier = Modifier.size(65.dp)
                    )

                    Spacer(Modifier.height(15.dp))

                    Text(
                        "Выбери фотографию",
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Загрузи изображение из галереи",
                        color = Muted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White),
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setImageURI(photoUri)
                    }
                },
                update = { imageView ->
                    imageView.setImageURI(photoUri)
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onChoosePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent
            )
        ) {
            Icon(
                Icons.Outlined.AddPhotoAlternate,
                contentDescription = null
            )

            Spacer(Modifier.width(10.dp))

            Text(
                if (photoUri == null) "Выбрать фото"
                else "Выбрать другую фотографию",
                fontSize = 15.sp
            )
        }

        if (photoUri != null) {
            Spacer(Modifier.height(10.dp))

            Text(
                "Фотография загружена. Инструменты обработки добавим следующим этапом.",
                color = Muted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ModuleScreen(title: String, description: String) {
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
