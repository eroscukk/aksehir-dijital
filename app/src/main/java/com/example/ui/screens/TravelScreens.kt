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
    val favoritePlacesList by viewModel.favoritePlacesState.collectAsState(initial = emptyList())

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
                val isFav = favoritePlacesList.any { it.placeId == activeDetailPlace.id }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.TR) activeDetailPlace.nameTr else activeDetailPlace.nameEn,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.togglePlaceFavorite(activeDetailPlace) },
                        modifier = Modifier.testTag("favorite_place_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (isFav) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                }
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

        // POSTGRESQL & POSTGIS MODE CONTROLLER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("postgis_controller_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (viewModel.postGisModeActive) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) 
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    1.dp, 
                    if (viewModel.postGisModeActive) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🐘",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = Loc.t(lang, "PostgreSQL & PostGIS Uzamsal Motoru", "PostgreSQL & PostGIS Spatial Engine"),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = Loc.t(lang, "Mekanları PostGIS fonksiyonları ile birleştirin", "Connect spots with PostGIS formulas"),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Switch(
                            checked = viewModel.postGisModeActive,
                            onCheckedChange = { 
                                viewModel.postGisModeActive = it 
                                viewModel.updatePostGisOutputs()
                            },
                            modifier = Modifier.testTag("postgis_mode_switch")
                        )
                    }

                    if (viewModel.postGisModeActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = Loc.t(lang, "Aktif Coğrafi Analiz Fonksiyonları (Seçin):", "Active Spatial Functions (Toggle):"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = viewModel.postGisUseLine,
                                onClick = { viewModel.togglePostGisOption("line") },
                                label = { Text("ST_MakeLine", fontSize = 11.sp) },
                                leadingIcon = if (viewModel.postGisUseLine) {
                                    { Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null,
                                modifier = Modifier.testTag("chip_st_makeline")
                            )

                            FilterChip(
                                selected = viewModel.postGisUseCentroid,
                                onClick = { viewModel.togglePostGisOption("centroid") },
                                label = { Text("ST_Centroid", fontSize = 11.sp) },
                                leadingIcon = if (viewModel.postGisUseCentroid) {
                                    { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null,
                                modifier = Modifier.testTag("chip_st_centroid")
                            )

                            FilterChip(
                                selected = viewModel.postGisUseConvexHull,
                                onClick = { viewModel.togglePostGisOption("convexhull") },
                                label = { Text("ST_ConvexHull", fontSize = 11.sp) },
                                leadingIcon = if (viewModel.postGisUseConvexHull) {
                                    { Icon(Icons.Default.Architecture, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null,
                                modifier = Modifier.testTag("chip_st_convexhull")
                            )

                            FilterChip(
                                selected = viewModel.postGisUseBuffer,
                                onClick = { viewModel.togglePostGisOption("buffer") },
                                label = { Text("ST_Buffer", fontSize = 11.sp) },
                                leadingIcon = if (viewModel.postGisUseBuffer) {
                                    { Icon(Icons.Default.Lens, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                } else null,
                                modifier = Modifier.testTag("chip_st_buffer")
                            )
                        }
                    }
                }
            }
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

                        // Local coordinate projections
                        val toX = { lon: Double -> ((lon - 31.40) * size.width / (31.49 - 31.40)).toFloat() }
                        val toY = { lat: Double -> (size.height - ((lat - 38.33) * size.height / (38.50 - 38.33))).toFloat() }

                        // 1. Draw ST_Buffer zones around active spots
                        if (viewModel.postGisModeActive && viewModel.postGisUseBuffer) {
                            viewModel.mapPoints.forEach { pt ->
                                drawCircle(
                                    color = Color(0x1F2196F3),
                                    radius = 55f,
                                    center = androidx.compose.ui.geometry.Offset(toX(pt.longitude), toY(pt.latitude))
                                )
                                drawCircle(
                                    color = Color(0x662196F3),
                                    radius = 55f,
                                    center = androidx.compose.ui.geometry.Offset(toX(pt.longitude), toY(pt.latitude)),
                                    style = Stroke(width = 1.5f)
                                )
                            }
                        }

                        // 2. Draw ST_ConvexHull shaded area
                        if (viewModel.postGisModeActive && viewModel.postGisUseConvexHull && viewModel.mapPoints.size >= 3) {
                            val hullPoints = viewModel.getConvexHullPoints()
                            if (hullPoints.size >= 3) {
                                val path = Path().apply {
                                    val first = hullPoints.first()
                                    moveTo(toX(first.longitude), toY(first.latitude))
                                    for (i in 1 until hullPoints.size) {
                                        val pt = hullPoints[i]
                                        lineTo(toX(pt.longitude), toY(pt.latitude))
                                    }
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0x1F4CAF50)
                                )
                                drawPath(
                                    path = path,
                                    color = Color(0xFF4CAF50),
                                    style = Stroke(width = 2.5f)
                                )
                            }
                        }

                        // 3. Draw ST_MakeLine connections (optimal line)
                        val drawLinesEnabled = !viewModel.postGisModeActive || viewModel.postGisUseLine
                        if (drawLinesEnabled && viewModel.mapPoints.size >= 2) {
                            val routeColor = Color(0xFFF57C00)
                            for (i in 0 until viewModel.mapPoints.size - 1) {
                                val p1 = viewModel.mapPoints[i]
                                val p2 = viewModel.mapPoints[i + 1]

                                drawLine(
                                    color = routeColor,
                                    start = androidx.compose.ui.geometry.Offset(toX(p1.longitude), toY(p1.latitude)),
                                    end = androidx.compose.ui.geometry.Offset(toX(p2.longitude), toY(p2.latitude)),
                                    strokeWidth = 6f
                                )
                            }
                        }

                        // 4. Draw ST_Centroid point
                        if (viewModel.postGisModeActive && viewModel.postGisUseCentroid) {
                            viewModel.getCentroidPoint()?.let { centroid ->
                                val cx = toX(centroid.longitude)
                                val cy = toY(centroid.latitude)
                                drawCircle(
                                    color = Color(0x44FFC107),
                                    radius = 28f,
                                    center = androidx.compose.ui.geometry.Offset(cx, cy)
                                )
                                drawCircle(
                                    color = Color(0xFFFF9800),
                                    radius = 12f,
                                    center = androidx.compose.ui.geometry.Offset(cx, cy)
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

                    // 5. Place Centroid Pin overlay
                    if (viewModel.postGisModeActive && viewModel.postGisUseCentroid) {
                        viewModel.getCentroidPoint()?.let { centroid ->
                            val xOffset = ((centroid.longitude - 31.40) / (31.49 - 31.40)) * 260
                            val yOffset = 210 - (((centroid.latitude - 38.33) / (38.50 - 38.33)) * 200)

                            val xDp = xOffset.coerceIn(10.0, 280.0).dp
                            val yDp = yOffset.coerceIn(10.0, 240.0).dp

                            Box(
                                modifier = Modifier
                                    .offset(x = xDp, y = yDp)
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(17.dp))
                                    .background(Color(0xFFFFC107))
                                    .testTag("centroid_overlay_pin"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "ST_Centroid Point",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
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

        // PostgreSQL & PostGIS ENGINE TERMINAL CONSOLE
        if (viewModel.postGisModeActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("postgis_terminal_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Dark Terminal
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFFFF9800), RoundedCornerShape(5.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Loc.t(lang, "POSTGRESQL / POSTGIS KONSOLU", "POSTGRESQL / POSTGIS CONSOLE"),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                            
                            Text(
                                text = "pgAdmin v8.4",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic PostgreSQL Query block
                        Text(
                            text = Loc.t(lang, "Dinamik SQL Sorgusu (Yol & Haritada Canlı):", "Dynamic SQL Query (Reflects Live Path):"),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // SQL Source Block
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = viewModel.postGisSqlOutput,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // GeoJSON Block
                        Text(
                            text = Loc.t(lang, "Üretilen GeoJSON Verisi (Standart Harita Çıktısı):", "Generated GeoJSON Format (Canonical Map Output):"),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp)
                        ) {
                            Text(
                                text = viewModel.postGisGeoJsonOutput,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Server Execution Trigger
                        Button(
                            onClick = { viewModel.executePostGisQuery() },
                            modifier = Modifier.fillMaxWidth().testTag("execute_postgis_query_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            enabled = !viewModel.postGisQueryExecuting && viewModel.mapPoints.isNotEmpty()
                        ) {
                            if (viewModel.postGisQueryExecuting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Loc.t(lang, "PostgreSQL Sorgusu Yürütülüyor...", "Running pgSQL Spatial Planner..."),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Loc.t(lang, "PostGIS Motorunu Çalıştır (Mekansal Dizin)", "Run Spatial Query on PostgreSQL Client"),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Terminal Log Output
                        if (viewModel.postGisExecutionLog.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = Loc.t(lang, "Sorgu Yürütme Logları & Çıktısı:", "Query Execution Terminal Log:"),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA78BFA)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF020617), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF1E293B))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = viewModel.postGisExecutionLog,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFFDDD6FE)
                                )
                            }
                        }
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
                        HorizontalDivider()
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
    val stories by viewModel.nasreddinStoriesState.collectAsState(initial = emptyList())
    val lifeSections by viewModel.nasreddinLifeSectionsState.collectAsState(initial = emptyList())
    var activeTab by remember { mutableStateOf(0) } // 0: Stories, 1: Biography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("nasreddin_portal_root")
    ) {
        // Sticky Elegant Tab Selector
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text(Loc.t(lang, "Düşündüren Fıkralar", "Witty Stories"), fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Book, contentDescription = "Stories") },
                modifier = Modifier.testTag("portal_stories_tab")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text(Loc.t(lang, "Hoca'nın Hayatı", "His Biography"), fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = "Biography") },
                modifier = Modifier.testTag("portal_biography_tab")
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeTab == 0) {
                // WISDOM OF THE DAY HEADER CARD
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
                        text = Loc.t(lang, "Nasreddin Hoca Fıkraları Kütüphanesi", "Witty Stories Library"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = Loc.t(
                            lang,
                            "Mizah dolu, her biri hayat dersi veren SQLite veri tabanımızda kayıtlı fıkraları okuyun, dinleyin ve etkileşim kurun.",
                            "Read, listen, and interact with tales saved under our SQLite database that yield witty wisdom."
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
                            .testTag("story_item_${story.storyId}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Book, 
                                    contentDescription = "Story", 
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (lang == AppLanguage.TR) story.titleTr else story.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                // LIKED STATE INDICATOR
                                if (story.isLiked) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite, 
                                        contentDescription = "Liked", 
                                        tint = Color.Red,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                // READ STATE INDICATOR
                                if (story.isRead) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle, 
                                        contentDescription = "Read", 
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ChevronRight, 
                                    contentDescription = "Read", 
                                    tint = MaterialTheme.colorScheme.secondary
                                )
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
            } else {
                // BIOGRAPHY VIEW
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Biography",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = Loc.t(lang, "BİR EFSANENİN HAYATI", "LIFE OF A LEGEND"),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = Loc.t(lang, "Gülümseten Bilge Nasreddin Hoca", "Sage of Humorous Wisdom"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                items(lifeSections) { sec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("life_sec_${sec.sectionId}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${sec.orderIndex}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (lang == AppLanguage.TR) sec.titleTr else sec.titleEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TR) sec.contentTr else sec.contentEn,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    // Story Dialog Viewer
    val activeStory = viewModel.selectedStory
    if (activeStory != null) {
        var noteText by remember(activeStory.storyId) { mutableStateOf(activeStory.personalNotes) }

        AlertDialog(
            onDismissRequest = { viewModel.selectedStory = null },
            confirmButton = {
                TextButton(onClick = { viewModel.selectedStory = null }) {
                    Text(Loc.t(lang, "Kapat", "Close"))
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories, 
                            contentDescription = "Story", 
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TR) activeStory.titleTr else activeStory.titleEn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    // Dynamic Database Interactions: Likes and Reads
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activeStory.isLiked,
                            onClick = { viewModel.toggleStoryLike(activeStory.storyId, activeStory.isLiked) },
                            label = { Text(Loc.t(lang, "Beğendim", "Liked")) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (activeStory.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (activeStory.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("story_like_chip")
                        )

                        FilterChip(
                            selected = activeStory.isRead,
                            onClick = { viewModel.toggleStoryRead(activeStory.storyId, activeStory.isRead) },
                            label = { Text(Loc.t(lang, "Okundu", "Read")) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Read",
                                    tint = if (activeStory.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("story_read_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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

                    // PERSISTENT STORY NOTEPAD (Saves to Room Database)
                    Text(
                        text = Loc.t(lang, "Fıkra Hakkındaki Notlarım ve Yorumlarım", "My Notes & Reflections"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = Loc.t(lang, "Yazılan notlar SQLite veritabanına kaydedilir.", "Your notes are saved permanently to the SQLite database."),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text(Loc.t(lang, "Bu fıkraya dair kendi yorumunuzu yazın...", "Write your own reflections on this tale...")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("story_notes_field"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { viewModel.updateStoryPersonalNotes(activeStory.storyId, noteText) },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("story_notes_save_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(Loc.t(lang, "Notu Veritabanına Kaydet", "Save Note to Database"), fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Audio story play
                    val isPlayingThis = viewModel.activeAudioSource == activeStory.storyId && viewModel.isAudioPlaying
                    val playIcon = if (isPlayingThis) Icons.Default.PauseCircle else Icons.Default.PlayCircle

                    Button(
                        onClick = { viewModel.toggleAudioPlayback(activeStory.storyId) },
                        modifier = Modifier.fillMaxWidth().testTag("story_audio_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(playIcon, contentDescription = "Play Story")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPlayingThis) Loc.t(lang, "Hikayeyi Duraklat", "Pause Story Audio")
                                   else Loc.t(lang, "Sesli Hikaye Dinle (${activeStory.audioDuration})", "Listen Audio Story (${activeStory.audioDuration})")
                        )
                    }

                    if (viewModel.activeAudioSource == activeStory.storyId) {
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
    val favoritePlacesList by viewModel.favoritePlacesState.collectAsState(initial = emptyList())

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
                        HorizontalDivider()
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

            if (favoritePlacesList.isNotEmpty()) {
                item {
                    Text(
                        text = Loc.t(lang, "Favori Tarihi Mekanlarım", "My Favorite Spots"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(favoritePlacesList) { fav ->
                    val p = viewModel.repository.places.firstOrNull { it.id == fav.placeId }
                    if (p != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectedPlace = p }
                                .testTag("fav_place_item_${p.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Liked", tint = Color.Red, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == AppLanguage.TR) p.nameTr else p.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = p.audioDurationTr + " " + Loc.t(lang, "dk Sesli Anlatım", "min Audio Guide"),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Details",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
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
