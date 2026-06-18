package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppLanguage
import com.example.ui.TravelStateViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TravelStateViewModel = viewModel()

            MyApplicationTheme(darkTheme = viewModel.isDarkTheme) {
                val lang = viewModel.currentLanguage

                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("app_scaffold_root"),
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = {
                                Text(
                                    text = Loc.t(lang, "Akşehir Gezi & Miras", "Akşehir Travel & Heritage"),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            },
                            actions = {
                                // Real-time language switch button in the app bar for outstanding accessibility
                                TextButton(
                                    onClick = { viewModel.toggleLanguage() },
                                    modifier = Modifier.testTag("app_header_lang_button")
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.TR) "TR 🇹🇷" else "EN 🇬🇧",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Dark/Light mode switch in header for accessibility styling compliance
                                IconButton(
                                    onClick = { viewModel.toggleTheme() },
                                    modifier = Modifier.testTag("app_header_theme_button")
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Theme Toggle",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("app_bottom_nav_bar")
                        ) {
                            val items = listOf("home", "stories", "map", "food", "assistant", "profile")
                            items.forEach { item ->
                                val selected = viewModel.currentTab == item
                                val label = when (item) {
                                    "home" -> Loc.t(lang, "Gezi", "Explore")
                                    "stories" -> Loc.t(lang, "Nükte", "Tales")
                                    "map" -> Loc.t(lang, "Harita", "Routes")
                                    "food" -> Loc.t(lang, "Lezzet", "Flavors")
                                    "assistant" -> Loc.t(lang, "Sesli", "Voice")
                                    "profile" -> Loc.t(lang, "Kimlik", "Portal")
                                    else -> item
                                }
                                val icon = when (item) {
                                    "home" -> if (selected) Icons.Filled.Explore else Icons.Outlined.Explore
                                    "stories" -> if (selected) Icons.Filled.AutoStories else Icons.Outlined.AutoStories
                                    "map" -> if (selected) Icons.Filled.Map else Icons.Outlined.Map
                                    "food" -> if (selected) Icons.Filled.Restaurant else Icons.Outlined.Restaurant
                                    "assistant" -> if (selected) Icons.Filled.Mic else Icons.Outlined.Mic
                                    "profile" -> if (selected) Icons.Filled.AccountBox else Icons.Outlined.AccountBox
                                    else -> Icons.Default.Home
                                }

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { 
                                        viewModel.currentTab = item 
                                        // Reset selected detailed modals to avoid stuck view states
                                        viewModel.selectedPlace = null
                                        viewModel.selectedStory = null
                                        viewModel.selectedFood = null
                                    },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("nav_item_$item")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Crossfade(targetState = viewModel.currentTab, label = "tab_fade") { tab ->
                            when (tab) {
                                "home" -> MainDashboard(viewModel)
                                "stories" -> NasreddinHodjaPortal(viewModel)
                                "map" -> InteractiveMapScreen(viewModel)
                                "food" -> LocalFlavorsScreen(viewModel)
                                "assistant" -> VoiceAssistantScreen(viewModel)
                                "profile" -> ProfileScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
