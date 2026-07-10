package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ThumbnailBlueprint
import com.example.ui.ThumbnailViewModel
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.GradientPink
import com.example.ui.theme.HotOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SectionBgColor
import com.example.ui.theme.SectionConceptColor
import com.example.ui.theme.SectionFaceColor
import com.example.ui.theme.SectionGlowColor
import com.example.ui.theme.SectionTextColor
import com.example.ui.theme.SoftGray
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: ThumbnailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            var showSplash by remember { mutableStateOf(true) }

            // Splash Screen timer (2 seconds)
            LaunchedEffect(Unit) {
                delay(2200)
                showSplash = false
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    if (showSplash) {
                        GamuzoSplashScreen()
                    } else {
                        var selectedTab by remember { mutableStateOf(0) }

                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            containerColor = MaterialTheme.colorScheme.background,
                            topBar = {
                                GamuzoHeader(
                                    isDarkTheme = isDarkTheme,
                                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                                )
                            },
                            bottomBar = {
                                SleekBottomNavBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                when (selectedTab) {
                                    0 -> MainDashboardScreen(viewModel = viewModel, isDarkTheme = isDarkTheme)
                                    1 -> ArchiveScreen(viewModel = viewModel)
                                    2 -> StudioTipsScreen()
                                    3 -> ChannelProfileScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamuzoSplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)), // Fixed immersive dark for splash screen
        contentAlignment = Alignment.Center
    ) {
        // Decorative pulsing background glows
        Box(
            modifier = Modifier
                .size(320.dp)
                .alpha(glowAlpha * 0.15f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFE50914), Color.Transparent)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(pulseScale)
        ) {
            // Real gamuzo brand logo
            Image(
                painter = painterResource(id = R.drawable.img_gamuzo_logo_1783698857579),
                contentDescription = "Gamuzo Logo",
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, Color(0xFFE50914), RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "GAMUZO",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "THUMBNAIL YOUTUBE CREATOR",
                color = Color(0xFFE50914),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Color(0xFFE50914),
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun GamuzoHeader(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerBg = if (isDarkTheme) Color(0xFF0F0F0F) else Color(0xFFFFFFFF)
    val dividerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val titleColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBg)
            .border(width = 0.5.dp, color = dividerColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo permanently anchored in the top corner of the main interface
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gamuzo_logo_1783698857579),
                contentDescription = "Gamuzo Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE50914), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = "GAMUZO",
                    color = titleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CREATOR SUITE",
                    color = if (isDarkTheme) Color(0xFFE50914) else Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Theme Support Toggle switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = "وضع المظهر",
                tint = if (isDarkTheme) Color(0xFFE50914) else Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() },
                modifier = Modifier.testTag("theme_switch_toggle"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    uncheckedThumbColor = Color(0xFFF59E0B),
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Composable
fun SleekBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItem(
            icon = Icons.Default.Home,
            label = "الرئيسية",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavBarItem(
            icon = Icons.Default.History,
            label = "الأرشيف",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavBarItem(
            icon = Icons.Default.Brush,
            label = "الاستوديو",
            isSelected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavBarItem(
            icon = Icons.Default.Person,
            label = "حسابي",
            isSelected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else SoftGray,
        label = "tab_color"
    )

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun MainDashboardScreen(
    viewModel: ThumbnailViewModel,
    isDarkTheme: Boolean = true
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val savedBlueprints by viewModel.savedBlueprints.collectAsState()

    val quickPresets = listOf(
        Pair("جلد فورتنايت 💥", "تحدي جلد فورتنايت مع فزعة أسطورية يشلع شلع"),
        Pair("انخرشت برعب كود 🧟", "لعبة رعب انخرشت من وحش في الظلام رياكشن مرعوب"),
        Pair("كليك بيت بـ 1$ 🤑", "تحدي كليك بيت فوز مستحيل بميزانية 1 ليرة صدمة"),
        Pair("فيفا بكجات أسطورية ⚽", "تحدي فيفا فتح بكجات أسطورية فجرنا الحظ ردة فعل صدمة"),
        Pair("سولو سكواد ببجي 🎯", "ببجي موبايل سولو ضد سكواد جلد حماسي مستحيل")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sleek Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gaming_banner),
                    contentDescription = "مخرج الثمنيلز الإبداعي",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Linear gradient matching the theme mode overlays
                val overlayBrush = if (isDarkTheme) {
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF0F0F0F).copy(alpha = 0.95f))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFF9FAFB).copy(alpha = 0.85f))
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(overlayBrush)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Letter card "C" with gradient style from the theme HTML
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFFFF4D4D))))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Gamuzo Suite",
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AI CREATOR PLATFORM",
                                color = if (isDarkTheme) SoftGray else Color(0xFF475569),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "GAMING PRO",
                                color = if (isDarkTheme) Color(0xFFFF8888) else MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Input Box Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صف فكرتك بالعامية (مثال: انخرشت برعب كود) 👇",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF34D399), CircleShape)
                            )
                            Text(
                                text = "المساعد جاهز", 
                                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.inputPrompt,
                            onValueChange = { viewModel.onPromptChange(it) },
                            placeholder = {
                                Text(
                                    "مثال: جلدت الوحش انخراش أسطوري فورتنايت...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("prompt_input_field"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                textDirection = TextDirection.ContentOrRtl,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 13.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        // Voice button from template
                        VoiceRecorderButton(
                            isRecording = uiState.isRecording,
                            progress = uiState.recordingProgress,
                            onClick = { viewModel.startVoiceRecording() },
                            modifier = Modifier.testTag("voice_input_button")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اضغط المايك لتلقي فكرة أسطورية عشوائية فوراً",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )

                        Button(
                            onClick = { viewModel.generateBlueprint(uiState.inputPrompt) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !uiState.isLoading,
                            modifier = Modifier.testTag("generate_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "إنشاء مخطط",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ابتكر المخطط", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. Preset Suggestions
        item {
            Column {
                Text(
                    text = "🔥 أفكار سريعة ومثيرة للـ CTR:",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    items(quickPresets) { preset ->
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onPromptChange(preset.second)
                                    viewModel.generateBlueprint(preset.second)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset.first,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Loading State
        if (uiState.isLoading) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = borderIndicator()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "المخرج الإبداعي Gemini يحلل طلبك...",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Error State
        uiState.error?.let { err ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x11EF4444)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "خطأ", tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تنبيه إبداعي", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 6. Current Blueprint Display Section
        uiState.currentBlueprint?.let { blueprint ->
            item {
                Text(
                    text = "👇 المخطط المقترح الحالي للإنتاج:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                BlueprintCard(
                    blueprint = blueprint,
                    onCopyToClipboard = {
                        val fullText = """
مخطط الصورة المصغرة الاحترافية لليوتيوب:
- العنوان المقترح: ${blueprint.title}
- وصف الفكرة العامة: ${blueprint.visualConcept}
- تعابير الوجه والرياكشن: ${blueprint.faceExpressions}
- عناصر الخلفية والوحوش: ${blueprint.backgroundElements}
- النصوص، الخطوط، والألوان: ${blueprint.textFontsColors}
- الألوان والإضاءة والتوهج: ${blueprint.glowLighting}
                        """.trimIndent()
                        
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Blueprint", fullText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ المخطط الإبداعي بنجاح! 🚀", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ArchiveScreen(
    viewModel: ThumbnailViewModel
) {
    val savedBlueprints by viewModel.savedBlueprints.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "أرشيف المخططات المحفوظة",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "استرجع مخططاتك السابقة وانسخ تفاصيلها لبرنامج التصميم فوراً",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                if (savedBlueprints.isNotEmpty()) {
                    Text(
                        text = "مسح الكل",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.clearAll() }
                            .padding(8.dp)
                    )
                }
            }
        }

        if (savedBlueprints.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "الأرشيف فارغ",
                            tint = SoftGray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد مخططات محفوظة بعد!",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "ابدأ بوصف صورة مصغرة بالعامية ليقوم المخرج الإبداعي Gemini بصياغتها وحفظها هنا تلقائياً.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(savedBlueprints) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectBlueprint(item) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.currentBlueprint?.id == item.id) {
                            CyberPurple.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.currentBlueprint?.id == item.id) CyberPurple else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        val fullText = """
مخطط الصورة المصغرة الاحترافية لليوتيوب:
- العنوان المقترح: ${item.title}
- وصف الفكرة العامة: ${item.visualConcept}
- تعابير الوجه والرياكشن: ${item.faceExpressions}
- عناصر الخلفية والوحوش: ${item.backgroundElements}
- النصوص، الخطوط، والألوان: ${item.textFontsColors}
- الألوان والإضاءة والتوهج: ${item.glowLighting}
                                        """.trimIndent()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Blueprint", fullText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ مخطط ${item.title}!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = NeonGreen, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.deleteBlueprint(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Text(
                            text = "الوصف: " + item.prompt,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudioTipsScreen() {
    val designTips = remember {
        listOf(
            Pair("قاعدة الأثلاث والتركيز 📐", "ضع وجهك دائمًا في الثلث الأيمن أو الأيسر من الصورة لتدع العين البشرية تركز فجأة على تعابير وجهك وتشد الانتباه!"),
            Pair("قاعدة الـ 3 ألوان كحد أقصى 🎨", "لا تدمج أكثر من 3 ألوان متضاربة في صورتك. الأفضل هو: تباين قوي (مثلاً بنفسجي مع أصفر نيون، أو أزرق نيون مع برتقالي حار)."),
            Pair("التكبير والمبالغة بالتعابير 🤯", "الرياكشن على شاشة الهاتف يظهر صغيرًا، كبّر حجم العيون والفم ببرنامج التعديل بنسبة 10% إضافية لإيصال تعبير (الانخراش) أو الصدمة بوضوح!"),
            Pair("خطوط عريضة بظل ثقيل ✍️", "استخدم نصوصًا من كلمة واحدة أو كلمتين كحد أقصى (مثل: جلد!، انخرشت!). استخدم خطًا جافًا وعريضًا، مع حدود سوداء ثقيلة (Stroke) لتظهر فوق الخلفية المعقدة."),
            Pair("الإضاءة الجانبية وتوهج الأطراف ✨", "أضف توهجًا خارجيًا (Rim Light) بلون فوسفوري (أخضر أو بنفسجي) حول خصلات شعرك وأطراف ملابسك لفصلك عن خلفية اللعبة وجعل الثمنيل يبدو سينمائيًا!")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "دليل مخرج الـ CTR الإبداعي للقيمرز 🎬",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تعلم أسرار الصور المصغرة المليونية المتبعة من كبار صناع محتوى الألعاب العالمي:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        items(designTips) { tip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CyberPurple.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "نصيحة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tip.first,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tip.second,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelProfileScreen(
    viewModel: ThumbnailViewModel
) {
    var channelName by remember { mutableStateOf("GamerX_YT") }
    var subscribersCount by remember { mutableStateOf("120,500") }
    val savedBlueprints by viewModel.savedBlueprints.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "حساب صانع المحتوى 🎮",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFFFF4D4D)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "الملف الشخصي",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = channelName,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "العضوية الاحترافية: ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "PRO CREATOR",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f), thickness = 0.5.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${savedBlueprints.size}",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "مخططات مصممة",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = subscribersCount,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "مشترك يوتيوب",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "87.4%",
                                color = NeonGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "متوسط CTR",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إعدادات القناة والتحكم:",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = channelName,
                        onValueChange = { channelName = it },
                        label = { Text("اسم قناتك على اليوتيوب", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    OutlinedTextField(
                        value = subscribersCount,
                        onValueChange = { subscribersCount = it },
                        label = { Text("عدد المشتركين الفعلي", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceRecorderButton(
    isRecording: Boolean,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val buttonColorByState = if (isRecording) {
        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
    } else {
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFFFF4D4D)))
    }

    Box(
        modifier = modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(buttonColorByState)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }
        
        Icon(
            imageVector = if (isRecording) Icons.Default.Mic else Icons.Default.MicNone,
            contentDescription = "تسجيل صوتي",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .alpha(scale)
        )
    }
}

@Composable
fun BlueprintCard(
    blueprint: ThumbnailBlueprint,
    onCopyToClipboard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("blueprint_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title & Copy Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF34D399), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مخطط الصورة المصغرة (جاهز)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = blueprint.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onCopyToClipboard,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("copy_blueprint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ المخطط",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Structured Sections with distinct theme highlight colors
            BlueprintSection(
                title = "الفكرة العامة للصورة المصغرة",
                content = blueprint.visualConcept,
                icon = Icons.Default.Image,
                tint = SectionConceptColor
            )

            BlueprintSection(
                title = "تعابير الوجه والرياكشن المقترح",
                content = blueprint.faceExpressions,
                icon = Icons.Default.Face,
                tint = SectionFaceColor,
                isItalic = true
            )

            BlueprintSection(
                title = "عناصر الخلفية والوحوش والبيئة",
                content = blueprint.backgroundElements,
                icon = Icons.Default.PlayArrow,
                tint = SectionBgColor
            )

            BlueprintSection(
                title = "النصوص، الخطوط المقترحة، والألوان",
                content = blueprint.textFontsColors,
                icon = Icons.Default.TextFormat,
                tint = SectionTextColor,
                isBold = true
            )

            BlueprintSection(
                title = "تأثيرات الإضاءة والتوهج الاحترافية",
                content = blueprint.glowLighting,
                icon = Icons.Default.FlashOn,
                tint = SectionGlowColor
            )
        }
    }
}

@Composable
fun BlueprintSection(
    title: String,
    content: String,
    icon: ImageVector,
    tint: Color,
    isItalic: Boolean = false,
    isBold: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(tint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = title,
                color = tint,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            modifier = if (isItalic) Modifier.alpha(0.9f) else Modifier
        )
    }
}

@Composable
fun borderIndicator(): androidx.compose.foundation.BorderStroke {
    val infiniteTransition = rememberInfiniteTransition(label = "borderPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    return androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = alpha))
}
