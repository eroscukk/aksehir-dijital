package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.AppLanguage
import com.example.ui.TravelStateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Custom Translation Helper dictionary for instantaneous, multilingual toggling
object Loc {
    fun t(lang: AppLanguage, tr: String, en: String): String {
        return if (lang == AppLanguage.TR) tr else en
    }
}

// ==========================================
// 1. HOME / MAIN DASHBOARD SCREEN
// ==========================================
@Composable
fun MainDashboard(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val places = viewModel.repository.places
    val activeNotif = viewModel.activeGeofenceNotification

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_scroll_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Notification Alert mimicking location tracking update
        if (activeNotif != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("geofence_notification_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddLocationAlt,
                                contentDescription = "Location Alert",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Loc.t(lang, "YAKINLARDA BİR GEZİ NOKTASI!", "TRAVEL SITE DETECTED NEARBY!"),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.dismissGeofenceNotification() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeNotif,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Hero Image and Title Block
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(4.dp)
                    .testTag("hero_banner_container")
            ) {
                // Background generated image representation
                Image(
                    painter = painterResource(id = R.drawable.img_aksehir_banner),
                    contentDescription = "Akşehir Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Bottom Gradient Shade to guarantee text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                startY = 100f
                            )
                        )
                )
                // Text overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = Loc.t(lang, "Nasreddin Hoca'nın Şehri", "City of Nasreddin Hodja"),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AKŞEHİR",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        // Quick Controls Row (QR Simulator & Theme Switches)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.isQrScannerVisible = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("scan_qr_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = Loc.t(lang, "QR Rehber Tarat", "Scan Spot QR"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = { viewModel.toggleLanguage() },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(50.dp)
                        .testTag("toggle_lang_button")
                ) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = "Language")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = Loc.t(lang, "English (EN)", "Türkçe (TR)"), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // About Description Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_about_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Loc.t(lang, "Mizah ve Kültür Başkenti", "Capital of Humor & Culture"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Loc.t(
                            lang,
                            "Konya'nın incisi Akşehir, asırlık tarihi konakları, yeşil doğası, Selçuklu medreseleri ve dünyaca ünlü Nasreddin Hoca masalları ile Anadolu'nun en zengin turizm değerlerine ev sahipliği yapar.",
                            "Akşehir, the jewel of Konya, hosts centuries-old houses, lush nature parks, magnificent Seljuk architecture and the timeless folklore of Nasreddin Hodja."
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Destinations Section header
        item {
            Text(
                text = Loc.t(lang, "Gezilecek Simgesel Mekanlar", "Iconic Places to Visit"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Grid-like Horizontal and vertical Places overview
        items(places) { place ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectedPlace = place }
                    .testTag("place_item_${place.id}"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom representation of the location based on its Type
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (place.type) {
                                PlaceType.HISTORICAL -> Icons.Default.Castle
                                PlaceType.MUSEUM -> Icons.Default.AccountBalance
                                PlaceType.NATURE -> Icons.Default.Explore
                            },
                            contentDescription = "Place Type",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == AppLanguage.TR) place.nameTr else place.nameEn,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            if (place.id == "tomb_nasreddin_hodja") {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("★ POPULAR", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TR) place.descriptionTr else place.descriptionEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // Place Details Modal / Fullscreen Sheet
    val activeDetailPlace = viewModel.selectedPlace
    if (activeDetailPlace != null) {
        AlertDialog(
            onDismissRequest = { viewModel.selectedPlace = null },
            confirmButton = {
                TextButton(onClick = { viewModel.selectedPlace = null }) {
                    Text(Loc.t(lang, "Kapat", "Close"))
                }
            },
            dismissButton = {
                // Interactive Geofence Simulation button
                FilledTonalButton(
                    onClick = { viewModel.moveUserSimulatedLocation(activeDetailPlace) },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Simulate Visit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Loc.t(lang, "Burayı Ziyaret Et (GPS Simüle)", "Simulate GPS Visit"), fontSize = 11.sp)
                }
            },
            title = {
                Text(
                    text = if (lang == AppLanguage.TR) activeDetailPlace.nameTr else activeDetailPlace.nameEn,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    // Custom Canvas representation of location marker layout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .drawBehind {
                                // Draw a decorative arch or dome
                                val p = Path()
                                p.moveTo(0f, size.height)
                                p.cubicTo(
                                    size.width / 4, size.height / 3,
                                    size.width * 3 / 4, size.height / 3,
                                    size.width, size.height
                                )
                                drawPath(p, color = Color.Gray.copy(alpha = 0.15f), style = Stroke(width = 6f))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.TravelExplore,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "GPS Coords: ${activeDetailPlace.latitude}, ${activeDetailPlace.longitude}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (lang == AppLanguage.TR) activeDetailPlace.descriptionTr else activeDetailPlace.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR and Code label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Loc.t(lang, "Mekan QR Kodu", "Spot QR Identifier"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = activeDetailPlace.qrCodeId,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Audio Narrative component
                    Text(
                        text = Loc.t(lang, "Sesli Rehber & Anlatım", "Voice Guide & Narration"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val isPlayingThis = viewModel.activeAudioSource == activeDetailPlace.id && viewModel.isAudioPlaying
                    val playIcon = if (isPlayingThis) Icons.Default.PauseCircle else Icons.Default.PlayCircle
                    val btnColor = if (isPlayingThis) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.toggleAudioPlayback(activeDetailPlace.id) }) {
                                    Icon(playIcon, contentDescription = "Play Audio", tint = btnColor, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (lang == AppLanguage.TR) "Sesli Tarihçe Dinle" else "Listen Audio Guide",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (lang == AppLanguage.TR) "Süre: ${activeDetailPlace.audioDurationTr} dk" else "Duration: ${activeDetailPlace.audioDurationEn} min",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (viewModel.activeAudioSource == activeDetailPlace.id) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { viewModel.activeAudioProgress.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (lang == AppLanguage.TR) activeDetailPlace.audioScriptTr else activeDetailPlace.audioScriptEn,
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    // QR SCANNING ACCESSIBILITY SIMULATOR DIALOG
    if (viewModel.isQrScannerVisible) {
        var textQrInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.isQrScannerVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    if (textQrInput.isNotBlank()) {
                        viewModel.simulateQrCodeScan(textQrInput)
                    }
                }) {
                    Text(Loc.t(lang, "QR Kod Simüle Et", "Simulate Scan"))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.isQrScannerVisible = false }) {
                    Text(Loc.t(lang, "İptal", "Cancel"))
                }
            },
            title = {
                Text(
                    text = Loc.t(lang, "QR Tarihi Eser Tarayıcı", "QR Spot Scanner Simulator"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = Loc.t(
                            lang,
                            "Tarihi yerlerdeki bilgilendirme tabelalarında bulunan QR kodunu simüle etmek için aşağıdaki kodlardan birini girin:",
                            "Input one of the following code tokens below to simulate visiting that historical site:"
                        ),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Shortcuts block for easy one-tap selection during evaluation
                    Text(text = "Shortcut Tokens (One-tap):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("QR_HODJA_TOMB", "QR_WEST_FRONT", "QR_STONE_MADRASA", "QR_RUSTU_MANSION", "QR_HAYRANI_TOMB", "QR_AKSEHIR_LAKE").forEach { tok ->
                            SuggestionChip(
                                onClick = { textQrInput = tok },
                                label = { Text(tok, fontSize = 9.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = textQrInput,
                        onValueChange = { textQrInput = it },
                        label = { Text("QR Token ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qr_token_input_field"),
                        singleLine = true
                    )
                }
            }
        )
    }

    // Global QR Result Alert Dialog
    val qrResMsg = viewModel.qrScanResultMsg
    if (qrResMsg != null) {
        AlertDialog(
            onDismissRequest = { viewModel.qrScanResultMsg = null },
            confirmButton = {
                Button(onClick = { viewModel.qrScanResultMsg = null }) {
                    Text("OK")
                }
            },
            title = { Text("QR Code Scanner Service", fontWeight = FontWeight.Bold) },
            text = { Text(qrResMsg) }
        )
    }
}

// ==========================================
// 2. INTERACTIVE MAP & ROUTE BUILDER SCREEN
// ==========================================
@Composable
fun InteractiveMapScreen(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val places = viewModel.repository.places
    val favRoutes by viewModel.favoriteRoutesState.collectAsState(initial = emptyList())
    val isCached = viewModel.isMapCachedOffline
    val progress = viewModel.mapCacheProgress

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("map_route_screen_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map Offline Cache configuration header card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCached) Icons.Default.CloudDone else Icons.Default.CloudCircle,
                            contentDescription = "Offline Cache",
                            tint = if (isCached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Loc.t(lang, "Çevrimdışı Harita Yönetimi", "Offline Map Downloader"),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (isCached) Loc.t(lang, "Akşehir haritası %100 çevrimdışı önbelleğe alındı", "Akşehir maps cached 100% offline")
                                       else Loc.t(lang, "İnternet zayıfken veri kaybını önlemek için önbelleğe al", "Download maps to avoid connection error"),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (progress > 0.0 && progress < 1.0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress.toFloat() },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(2.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.toggleOfflineMapCaching() },
                        modifier = Modifier.fillMaxWidth().testTag("cache_maps_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCached) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isCached) Loc.t(lang, "Çevrimdışı Belleği Temizle", "Clear Offline Map Cache")
                                   else Loc.t(lang, "Akşehir Çevrimdışı Haritasını İndir", "Download Offline Map of Aksehir")
                        )
                    }
                }
            }
        }

        // Screen title: Interactive Map Map-Builder Canvas
        item {
            Text(
                text = Loc.t(lang, "Akşehir İnteraktif Rota Çizici", "Akşehir Interactive Route Planner"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = Loc.t(
                    lang,
                    "Harita üzerindeki gezilecek noktalara tıklayarak kendi özel seyahat rotanızı yaratın ve kaydedin.",
                    "Tap on map hot-spots to design and save your custom walking sightseeing path below."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // CUSTOM INTERACTIVE CANVAS MAP REPRESENTATION
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .testTag("interactive_canvas_map_card"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Draw Behind representing stylized landmarks grid map of Akşehir
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (viewModel.isDarkTheme) Color(0xFF131A26) else Color(0xFFF1F5F9))
                    ) {
                        // Drawing decorative grids / streets representation represent medieval city lines
                        val gridPaint = Color.Gray.copy(alpha = 0.15f)
                        val step = 60f
                        for (x in 0 until size.width.toInt() step step.toInt()) {
                            drawLine(color = gridPaint, start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f), end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height), strokeWidth = 1f)
                        }
                        for (y in 0 until size.height.toInt() step step.toInt()) {
                            drawLine(color = gridPaint, start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()), end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()), strokeWidth = 1f)
                        }

                        // Draw lines between current built route points
                        if (viewModel.mapPoints.size >= 2) {
                            val routeColor = Color(0xFFF57C00)
                            for (i in 0 until viewModel.mapPoints.size - 1) {
                                val p1 = viewModel.mapPoints[i]
                                val p2 = viewModel.mapPoints[i + 1]

                                // Calculate positions
                                val x1 = (p1.longitude - 31.40) * size.width / (31.49 - 31.40)
                                val y1 = size.height - ((p1.latitude - 38.33) * size.height / (38.50 - 38.33))

                                val x2 = (p2.longitude - 31.40) * size.width / (31.49 - 31.40)
                                val y2 = size.height - ((p2.latitude - 38.33) * size.height / (38.50 - 38.33))

                                drawLine(
                                    color = routeColor,
                                    start = androidx.compose.ui.geometry.Offset(x1.toFloat(), y1.toFloat()),
                                    end = androidx.compose.ui.geometry.Offset(x2.toFloat(), y2.toFloat()),
                                    strokeWidth = 6f
                                )
                            }
                        }
                    }

                    // Place pins as overlay components so they are clickable
                    places.forEachIndexed { idx, spot ->
                        // Simple linear projection from real Latitude / Longitudes of Akşehir to layout scale
                        val xOffset = ((spot.longitude - 31.40) / (31.49 - 31.40)) * 260
                        val yOffset = 210 - (((spot.latitude - 38.33) / (38.50 - 38.33)) * 200)

                        // Clamp values to remain inside card bounding box
                        val xDp = xOffset.coerceIn(10.0, 280.0).dp
                        val yDp = yOffset.coerceIn(10.0, 240.0).dp

                        val inRoute = viewModel.mapPoints.contains(spot)
                        val colorPin = if (inRoute) Color(0xFFF57C00) else MaterialTheme.colorScheme.primary

                        Box(
                            modifier = Modifier
                                .offset(x = xDp, y = yDp)
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorPin)
                                .clickable { viewModel.addPlaceToRoute(spot) }
                                .testTag("map_pin_${spot.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom Legend Info
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFF57C00), RoundedCornerShape(4.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Seçilen Rota Bağı", color = Color.White, fontSize = 8.sp)
                    }
                }
            }
        }

        // Active Route builder statistics / confirmation input
        if (viewModel.mapPoints.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = Loc.t(lang, "Yeni Rota Raporu", "Active Route Summary"),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Noktalar: " + viewModel.mapPoints.joinToString(" -> ") { it.nameTr },
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = Loc.t(lang, "Toplam Mesafe: ", "Total distance: ") + "%.2f km".format(viewModel.customRouteDistance),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = Loc.t(lang, "Süre: ", "Est. time: ") + "${viewModel.customRouteDuration} dk",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Name Input Field
                        OutlinedTextField(
                            value = viewModel.customRouteName,
                            onValueChange = { viewModel.customRouteName = it },
                            placeholder = { Text(Loc.t(lang, "Rota Adı Girin...", "Give it a name..."), fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_route_input_field"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row {
                            Button(
                                onClick = { viewModel.saveCustomRoute() },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("save_route_button_confirm")
                            ) {
                                Text(Loc.t(lang, "Rotayı Kaydet (Veritabanı)", "Save Route (Offline)"))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { viewModel.clearRouteBuilder() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear Builder", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Active Route save success toast mimicking
        val succMsg = viewModel.routeSuccessMsg
        if (succMsg != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Saved", tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = succMsg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.routeSuccessMsg = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }
        }

        // Historical Saved/Favorite Routes List (from SQLite Room DB reactive flow)
        item {
            Text(
                text = Loc.t(lang, "Kişisel Seyahat Rotalarım (${favRoutes.size})", "My Saved Routes (${favRoutes.size})"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (favRoutes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Loc.t(lang, "Henüz oluşturulmuş bir rota yok.", "No saved routes found."),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(favRoutes) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saved_route_${r.id}"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Route, contentDescription = "Route", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = r.routeName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.deleteRoute(r.id) }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = r.routePointsJson,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "📏 %.2f km".format(r.totalDistanceKm),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "⏱ ~ ${r.totalDurationMin} dk",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            // Share Route Action Mock
                            TextButton(
                                onClick = {
                                    // Simulated sharing action
                                    viewModel.routeSuccessMsg = Loc.t(lang, "Sosyal medyada paylaşılıyor: [${r.routeName}] aksehir://routes/${r.id}", "Sharing on social network: [${r.routeName}] aksehir://routes/${r.id}")
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp).testTag("share_route_button_${r.id}")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(Loc.t(lang, "Paylaş", "Share Route"), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. NASREDDİN HODJA STORIES PORTAL
// ==========================================
@Composable
fun NasreddinHodjaPortal(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val stories = viewModel.repository.stories

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("nasreddin_portal_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wisdom header card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Quote",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Loc.t(lang, "GÜNÜN NÜKTELİ SÖZÜ", "WISDOM OF THE DAY"),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Loc.t(
                            lang,
                            "\"Dostlar, her şeye tersten baksanız dahi, umutla yaşamaya devam edin; nehir kurusa da göl elbet maya tutar!\"",
                            "\"My friends, even if you look at everything backwards, do stay hopeful. If the rivers dry up, the lake might still turn into yogurt!\""
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            Text(
                text = Loc.t(lang, "Nasreddin Hoca'dan Düşündüren Fıkralar", "Witty Tales & Wisdom"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = Loc.t(
                    lang,
                    "Gülümsetirken felsefe dersleri veren, asırları aşmış ünlü fıkraları okuyun ve inceleyin.",
                    "Enjoy the ancient tales that make you laugh while delivering timeless philosophical lessons."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(stories) { story ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectedStory = story }
                    .testTag("story_item_${story.id}"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Book, contentDescription = "Story", tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == AppLanguage.TR) story.titleTr else story.titleEn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = "Read", tint = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.TR) story.storyTr else story.storyEn,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Story Dialog Viewer
    val activeStory = viewModel.selectedStory
    if (activeStory != null) {
        AlertDialog(
            onDismissRequest = { viewModel.selectedStory = null },
            confirmButton = {
                TextButton(onClick = { viewModel.selectedStory = null }) {
                    Text(Loc.t(lang, "Kapat", "Close"))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoStories, contentDescription = "Story", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TR) activeStory.titleTr else activeStory.titleEn,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.TR) activeStory.storyTr else activeStory.storyEn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wisdom Insight Section (Hoca'nın Nüktesi ve Öğüdü)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = "Wisdom", tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Loc.t(lang, "Hikayenin Felsefi Öğüdü", "The Philosophical Wisdom"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TR) activeStory.wisdomTr else activeStory.wisdomEn,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Audio story play
                    val isPlayingThis = viewModel.activeAudioSource == activeStory.id && viewModel.isAudioPlaying
                    val playIcon = if (isPlayingThis) Icons.Default.PauseCircle else Icons.Default.PlayCircle

                    Button(
                        onClick = { viewModel.toggleAudioPlayback(activeStory.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(playIcon, contentDescription = "Play Story")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlayingThis) Loc.t(lang, "Hikayeyi Duraklat", "Pause Story Audio")
                                   else Loc.t(lang, "Sesli Hikaye Dinle (${activeStory.audioDuration})", "Listen Audio Story (${activeStory.audioDuration})")
                        )
                    }

                    if (viewModel.activeAudioSource == activeStory.id) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { viewModel.activeAudioProgress.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        )
    }
}

// ==========================================
// 4. LOCAL FLAVORS FOOD GUIDE
// ==========================================
@Composable
fun LocalFlavorsScreen(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val foods = viewModel.repository.foods

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("food_guide_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = Loc.t(lang, "Akşehir Yerel Lezzetler & Yemek Rehberi", "Akşehir Culinary & Gastronomy Guide"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = Loc.t(
                    lang,
                    "Tescilli tandır kebabından peynir baklavasına kadar Akşehir'in asırlık gastronomi harikasını keşfedin.",
                    "Explore the legendary registered meat delicacies and hot cheese recipes unique to Akşehir."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(foods) { food ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectedFood = food }
                    .testTag("food_item_${food.id}"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restaurant, contentDescription = "Food", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == AppLanguage.TR) food.nameTr else food.nameEn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = "Explore", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.TR) food.descriptionTr else food.descriptionEn,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // Food Cooking Recipe & Location Finder Sheet Dialogue
    val activeFood = viewModel.selectedFood
    if (activeFood != null) {
        AlertDialog(
            onDismissRequest = { viewModel.selectedFood = null },
            confirmButton = {
                TextButton(onClick = { viewModel.selectedFood = null }) {
                    Text(Loc.t(lang, "Kapat", "Close"))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DinnerDining, contentDescription = "Dinner", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TR) activeFood.nameTr else activeFood.nameEn,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.TR) activeFood.descriptionTr else activeFood.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ingredients bubble
                    Text(text = Loc.t(lang, "Gerekli Malzemeler", "Essential Ingredients"), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    val ingredientsList = if (lang == AppLanguage.TR) activeFood.ingredientsTr else activeFood.ingredientsEn
                    ingredientsList.forEach { ing ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = ing, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Traditional Secret Recipe (Geleneksel Sır Tarifi)
                    Text(text = Loc.t(lang, "Geleneksel Hazırlanışı", "Traditional Recipe"), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (lang == AppLanguage.TR) activeFood.recipeTr else activeFood.recipeEn,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Where to taste (Nerede Yenir?)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = Loc.t(lang, "Akşehir'de En İyi Nerede Yenir?", "Best Spot in Aksehir to Taste?"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (lang == AppLanguage.TR) activeFood.bestPlacesTr else activeFood.bestPlacesEn,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

// ==========================================
// 5. SECURE LOGIN & MEMBERSHIP BENEFITS PROFILE
// ==========================================
@Composable
fun ProfileScreen(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val user = viewModel.currentUser
    val isPremium = viewModel.isPremiumUser
    val cryptLogs = viewModel.encryptionDetailsText

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_section_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = Loc.t(lang, "Akıllı Seyahat Kimlik Portalı", "Smart Traveler Secure Portal"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = Loc.t(
                    lang,
                    "Kişisel favori rotalarınızı şifrelenmiş bulut sunucularımızda saklamak için üye olun.",
                    "Log in securely to host and sync personalized itineraries across end-to-end server clouds."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (user == null) {
            // LOGIN & REGISTER INTEGRATIVE FORM CARD
            item {
                var loginUser by remember { mutableStateOf("") }
                var loginPin by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_form_card"),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Loc.t(lang, "Giriş Yap / Üye Ol (Şifrelenmiş)", "Sign In / Register E2E Secured"),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginUser,
                            onValueChange = { loginUser = it },
                            label = { Text(Loc.t(lang, "Kullanıcı Adı", "Username")) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_username_field"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = loginPin,
                            onValueChange = { loginPin = it },
                            label = { Text("PIN / Passcode (" + Loc.t(lang, "Sadece Rakam", "Numbers only") + ")") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_passcode_field"),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, imeAction = ImeAction.Done),
                            singleLine = true
                        )

                        viewModel.authError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.handleLogin(loginUser, loginPin) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("submit_login_button")
                            ) {
                                Text(Loc.t(lang, "Giriş Yap", "Login"))
                            }

                            FilledTonalButton(
                                onClick = { viewModel.handleRegister(loginUser, loginPin) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("submit_register_button")
                            ) {
                                Text(Loc.t(lang, "Kaydol", "Register"))
                            }
                        }
                    }
                }
            }
        } else {
            // LOGGED IN TRAVELLER DASHBOARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logged_in_dashboard_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Avatar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = Loc.t(lang, "Hoş Geldin, Seyyah", "Welcome, Explorer"), fontSize = 11.sp)
                                Text(text = user, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Badge(
                                containerColor = if (isPremium) MaterialTheme.colorScheme.primary else Color.Gray
                            ) {
                                Text(
                                    text = if (isPremium) "GOLD VIP" else "FREE",
                                    color = Color.White,
                                    modifier = Modifier.padding(4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Security cryptography metadata display
                        if (cryptLogs.isNotBlank()) {
                            Text(
                                text = cryptLogs,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("logout_profile_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(Loc.t(lang, "Oturumu Kapat", "Sign Out"))
                        }
                    }
                }
            }

            // PREMIUM MEMBERSHIP DETAILS BENEFITS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Loc.t(lang, "👑 Akşehir Premium Avantajları", "👑 Akşehir VIP Premium Access"),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Loc.t(
                                lang,
                                "Premium üyelik ile kesintisiz seyahat ayrıcalıklarını hemen keşfedin:",
                                "Upgrade today to satisfy advanced accessibility and offline navigation privileges:"
                            ),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            Loc.t(lang, "✔ Sınırsız Sesli Rehber Çevrimdışı Akış", "✔ Infinite offline audio guide stream"),
                            Loc.t(lang, "✔ Çoklu Akıllı Kişisel Rota Önerileri", "✔ Multiple personal itinerary recomendations"),
                            Loc.t(lang, "✔ Bulut Sunucusunda Otomatik Senkronizasyon", "✔ Autonomous Cloud Encrypted sync safety"),
                            Loc.t(lang, "✔ %100 Reklamsız Seyahat Deneyimi", "✔ 100% Advertisement-free travel guide tools")
                        ).forEach { ben ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Verified, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = ben, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.togglePremiumStatus() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("buy_premium_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPremium) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (isPremium) Loc.t(lang, "Premium'u İptal Et", "Cancel VIP Trial")
                                else Loc.t(lang, "Premium Üye Ol (Mock In-App Purchase)", "Unlock VIP ($4.99 Mock purchase)")
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. VOICE ASSISTANT ACCESSIBILITY SCREEN
// ==========================================
@Composable
fun VoiceAssistantScreen(viewModel: TravelStateViewModel) {
    val lang = viewModel.currentLanguage
    val voiceResult = viewModel.voiceAssistantResult
    val commandValue = viewModel.voiceCommandQuery
    val isActive = viewModel.isVoiceActive

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("voice_assistant_root"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = Loc.t(lang, "Akıllı Sesli Komut Rehberi", "Voice Commands Smart Assistant"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = Loc.t(
                    lang,
                    "Erişilebilirlik sesli komutlarını simüle ederek uygulamadaki tarihi mekanlara, fıkralara ve yemek tariflerine hızlıca ulaşın.",
                    "Simulate offline voice recognition inputs to trigger rapid redirection across ancient narratives & spots."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Voice Command Trigger Layout with interactive Microphone visual
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing green circle Canvas overlay when recording is active
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.tertiaryContainer 
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                            .clickable {
                                viewModel.isVoiceActive = !isActive
                                if (viewModel.isVoiceActive) {
                                    viewModel.voiceAssistantResult = Loc.t(lang, "Dinleniyor... Aşağıdaki hazır komut parçalarına dokunun.", "Listening... Tap on one of the shortcut tags below.")
                                } else {
                                    viewModel.voiceAssistantResult = ""
                                }
                            }
                            .testTag("microphone_pulse_trigger"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.MicNone else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isActive) Loc.t(lang, "SİZİ DİNLİYORUM...", "LISTENING OUT LOUD...") 
                               else Loc.t(lang, "Mikrofonu Açmak İçin Dokunun", "Tap to toggle voice mic"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isActive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Ready Commands list shortcuts for smooth emulator testing
        item {
            Text(
                text = Loc.t(lang, "Desteği Simüle Edecek Komut Tıklamaları", "Tap Quick Commands to Simulate Vocal Inputs:"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            val itemsTr = listOf("Nasreddin Hoca türbesine git", "Batı Cephesi müzesini aç", "Yoğurt fıkrası oku", "Tandır kebabı nasıl yapılır?", "Kişiselleştirilmiş haritayı göster")
            val itemsEn = listOf("Go to Tomb of Nasreddin", "Open West Front museum", "Read yogurt in lake tale", "How to cook Tandir kebab?", "Show interactive map tool")
            val commandsList = if (lang == AppLanguage.TR) itemsTr else itemsEn

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                commandsList.forEach { cmd ->
                    SuggestionChip(
                        onClick = { viewModel.executeVoiceCommand(cmd) },
                        label = { Text(cmd, fontSize = 11.sp) },
                        modifier = Modifier.testTag("voice_cmd_chip_$cmd")
                    )
                }
            }
        }

        // Voice Command Input logs result board
        if (commandValue.isNotBlank() || voiceResult.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("voice_assistant_results_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (commandValue.isNotBlank()) {
                            Text(
                                text = Loc.t(lang, "Söylenen Komut:", "Spoken Query:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "\"$commandValue\"", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (voiceResult.isNotBlank()) {
                            Text(
                                text = Loc.t(lang, "Asistan Eylemi:", "Assistant Action:"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(text = voiceResult, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
