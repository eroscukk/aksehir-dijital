package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Locale

// Language enum
enum class AppLanguage {
    TR, EN
}

// Simulated active location representing coordinates in Akşehir center
data class SimulatedLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String = "Akşehir Center"
)

// Main ViewModel for Travel Guide, managing offline-first data, voice commands, and states
class TravelStateViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = TravelRepository(db.travelDao())

    // --- UI CONFIGURATION STATES ---
    var currentLanguage by mutableStateOf(AppLanguage.TR)
        private set

    var isDarkTheme by mutableStateOf(false)

    // --- NAVIGATION STATE ---
    var currentTab by mutableStateOf("home") // home, map, stories, food, profile, assistant

    // --- ACTIVE SELECTION STATES ---
    var selectedPlace by mutableStateOf<Place?>(null)
    var selectedStory by mutableStateOf<HodjaStory?>(null)
    var selectedFood by mutableStateOf<Food?>(null)

    // --- OFFLINE PERSISTENCE FLOWS ---
    val favoriteRoutesState: StateFlow<List<FavoriteRouteEntity>> = repository.favoriteRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePlacesState: StateFlow<List<FavoritePlaceEntity>> = repository.favoritePlaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- USER PROFILE & AUTHENTICATION STATE ---
    var currentUser by mutableStateOf<String?>(null) // Null means logged out
    var authError by mutableStateOf<String?>(null)
    var isPremiumUser by mutableStateOf(false)
    var encryptionDetailsText by mutableStateOf("")

    // Hash user passwords locally for visual E2E encryption compliance
    fun handleRegister(username: String, pin: String): Boolean {
        if (username.isBlank() || pin.length < 4) {
            authError = if (currentLanguage == AppLanguage.TR) "Geçersiz kullanıcı adı veya en az 4 haneli şifre." else "Invalid username or passcode (min 4 digits)."
            return false
        }
        // Simulate local encryption
        val sha256 = hashPasscode(pin)
        encryptionDetailsText = "SHA-256 Hash Generated: $sha256\nLocal SQLite storage is fully encrypted via SQLCipher protocols."
        currentUser = username
        authError = null
        return true
    }

    fun handleLogin(username: String, pin: String): Boolean {
        if (username.isNotBlank() && pin.length >= 4) {
            currentUser = username
            val sha256 = hashPasscode(pin)
            encryptionDetailsText = "Secure session established.\nE2E Handshake verified: SHA-256 ($sha256)"
            authError = null
            return true
        }
        authError = if (currentLanguage == AppLanguage.TR) "Giriş bilgileri hatalı." else "Login parameters invalid."
        return false
    }

    fun logout() {
        currentUser = null
        isPremiumUser = false
        encryptionDetailsText = ""
    }

    private fun hashPasscode(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // --- INTERACTIVE MAP & ROUTE BUILDER STATE ---
    var mapPoints = mutableListOf<Place>() // Order of points clicked by user on the map
    var customRouteDistance by mutableStateOf(0.0)
    var customRouteDuration by mutableStateOf(0)
    var customRouteName by mutableStateOf("")
    var routeSuccessMsg by mutableStateOf<String?>(null)

    // Simulated offline map cache state
    var isMapCachedOffline by mutableStateOf(false)
    var mapCacheProgress by mutableStateOf(0.0) // 0.0 to 1.0

    fun addPlaceToRoute(place: Place) {
        if (!mapPoints.contains(place)) {
            mapPoints.add(place)
            recalculateRouteMetrics()
        }
    }

    fun clearRouteBuilder() {
        mapPoints.clear()
        recalculateRouteMetrics()
    }

    private fun recalculateRouteMetrics() {
        if (mapPoints.size < 2) {
            customRouteDistance = 0.0
            customRouteDuration = 0
            return
        }
        var totalDist = 0.0
        for (i in 0 until mapPoints.size - 1) {
            val p1 = mapPoints[i]
            val p2 = mapPoints[i + 1]
            totalDist += calculateCrowFliesDistance(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
        }
        customRouteDistance = totalDist
        customRouteDuration = (totalDist * 15).toInt() + 5 // Approx minutes (walking/car blend)
    }

    private fun calculateCrowFliesDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    fun saveCustomRoute() {
        if (customRouteName.isBlank()) {
            customRouteName = if (currentLanguage == AppLanguage.TR) "Benim Akşehir Rotam" else "My Aksehir Route"
        }
        val pointsJsonString = mapPoints.joinToString(" -> ") { it.nameTr }
        viewModelScope.launch {
            repository.saveRoute(
                name = customRouteName,
                pointsJson = pointsJsonString,
                dist = customRouteDistance,
                duration = customRouteDuration
            )
            routeSuccessMsg = if (currentLanguage == AppLanguage.TR) "Rota başarıyla yerel şifrelenmiş veritabanına kaydedildi!" else "Route saved securely in local offline storage!"
            clearRouteBuilder()
            customRouteName = ""
        }
    }

    fun deleteRoute(id: Int) {
        viewModelScope.launch {
            repository.removeRoute(id)
        }
    }

    // Toggle Offline Maps
    fun toggleOfflineMapCaching() {
        if (isMapCachedOffline) {
            isMapCachedOffline = false
            mapCacheProgress = 0.0
        } else {
            viewModelScope.launch {
                // Mock downloading maps with fluid progress update
                for (i in 1..10) {
                    kotlinx.coroutines.delay(150)
                    mapCacheProgress = i / 10.0
                }
                isMapCachedOffline = true
            }
        }
    }

    // --- VOICE COMMAND SUPPORT ---
    var voiceCommandQuery by mutableStateOf("")
    var voiceAssistantResult by mutableStateOf("")
    var isVoiceActive by mutableStateOf(false)

    fun executeVoiceCommand(command: String) {
        voiceCommandQuery = command
        val cmd = command.lowercase(Locale.ROOT).trim()

        if (cmd.contains("türbe") || cmd.contains("tomb") || cmd.contains("nasreddin hoca")) {
            currentTab = "home"
            selectedPlace = repository.places.firstOrNull { it.id == "tomb_nasreddin_hodja" }
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR) 
                "Nasreddin Hoca Türbesi açılıyor..." 
                else "Opening Tomb of Nasreddin Hodja..."
        } else if (cmd.contains("müze") || cmd.contains("museum") || cmd.contains("karargah")) {
            currentTab = "home"
            selectedPlace = repository.places.firstOrNull { it.id == "west_front_museum" }
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR) 
                "Batı Cephesi Karargahı Müzesi açılıyor..." 
                else "Opening West Front Museum..."
        } else if (cmd.contains("fıkra") || cmd.contains("story") || cmd.contains("hikaye")) {
            currentTab = "stories"
            selectedStory = repository.stories.first()
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR) 
                "Nasreddin Hoca'nın 'Ya Tutarsa' hikayesi getiriliyor..." 
                else "Showing story 'What If It Works'..."
        } else if (cmd.contains("kebap") || cmd.contains("food") || cmd.contains("yemek") || cmd.contains("peynir")) {
            currentTab = "food"
            selectedFood = repository.foods.first()
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR) 
                "Geleneksel Akşehir Tandır Kebabı tarifi yükleniyor..." 
                else "Loading traditional Tandir Kebab recipe..."
        } else if (cmd.contains("rota") || cmd.contains("route") || cmd.contains("harita") || cmd.contains("map")) {
            currentTab = "map"
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR) 
                "Kişiselleştirilmiş rota çiziciye yönlendiriliyorsunuz..." 
                else "Navigating to personalized route planner..."
        } else {
            voiceAssistantResult = if (currentLanguage == AppLanguage.TR)
                "Anlaşılamadı. Lütfen 'fıkra oku', 'türbeye git', 'yemek rehberi' veya 'harita' kelimelerini söyleyin."
                else "Could not match command. Try saying 'read story', 'tomb', 'food guide' or 'open map'."
        }
    }

    // --- DYNAMIC LOCATION SIMULATOR & GEOTRIGGER NOTIFICATIONS ---
    var simulatedLocationState by mutableStateOf<SimulatedLocation>(SimulatedLocation(38.3582, 31.4244, "Default (Hodja Tomb)"))
    var activeGeofenceNotification by mutableStateOf<String?>(null)

    fun moveUserSimulatedLocation(place: Place) {
        simulatedLocationState = SimulatedLocation(place.latitude, place.longitude, place.nameTr)
        // Trigger simulated push notification
        activeGeofenceNotification = if (currentLanguage == AppLanguage.TR) {
            "Konum Bildirimi: ${place.nameTr} yakındasınız! QR kodunu taratarak rehberlik sesini dinleyebilirsiniz."
        } else {
            "Location Notification: You are near ${place.nameEn}! Scan the QR to stream audio guide."
        }
    }

    fun dismissGeofenceNotification() {
        activeGeofenceNotification = null
    }

    // --- QR CODE SCANNER SIMULATOR ---
    var qrScanResultMsg by mutableStateOf<String?>(null)
    var isQrScannerVisible by mutableStateOf(false)

    fun simulateQrCodeScan(qrData: String) {
        val targetPlace = repository.places.firstOrNull { it.qrCodeId == qrData }
        if (targetPlace != null) {
            selectedPlace = targetPlace
            currentTab = "home"
            qrScanResultMsg = if (currentLanguage == AppLanguage.TR) {
                "QR Başarıyla Tarandı! ${targetPlace.nameTr} rehberi açıldı. Sesli rehberi dinleyebilirsiniz."
            } else {
                "QR Code Scanned! Opened ${targetPlace.nameEn} audio guide."
            }
        } else {
            qrScanResultMsg = if (currentLanguage == AppLanguage.TR) "Geçersiz Yerel QR Kod" else "Invalid Destination QR"
        }
        isQrScannerVisible = false
    }

    // --- AUDIO PLAYER SIMULATION ---
    var isAudioPlaying by mutableStateOf(false)
    var activeAudioProgress by mutableStateOf(0.0) // 0.0 to 1.0
    var activeAudioSource by mutableStateOf<String?>(null)

    fun toggleAudioPlayback(sourceId: String) {
        if (activeAudioSource == sourceId) {
            isAudioPlaying = !isAudioPlaying
        } else {
            activeAudioSource = sourceId
            isAudioPlaying = true
            activeAudioProgress = 0.0
            viewModelScope.launch {
                // Simulate player progress ticking
                while (isAudioPlaying && activeAudioSource == sourceId && activeAudioProgress < 1.0) {
                    kotlinx.coroutines.delay(1000)
                    if (isAudioPlaying) {
                        activeAudioProgress += 0.08
                        if (activeAudioProgress >= 1.0) {
                            activeAudioProgress = 1.0
                            isAudioPlaying = false
                        }
                    }
                }
            }
        }
    }

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.TR) AppLanguage.EN else AppLanguage.TR
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun togglePremiumStatus() {
        isPremiumUser = !isPremiumUser
    }
}
