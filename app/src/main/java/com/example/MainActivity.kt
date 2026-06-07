package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Member
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MemberViewModel
import com.example.viewmodel.RegistrationEvent
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

// Navigation screen enum
enum class AppScreen(val titleTamil: String, val titleEnglish: String) {
    HOME("முகப்பு", "Home"),
    ABOUT("அறக்கட்டளை பற்றி", "About Trust"),
    REGISTRATION("பதிவு", "Registration"),
    ADMIN("நிர்வாகம்", "Admin members")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val viewModel: MemberViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    
    // States for inputs to maintain their persistence if screen changes (or we can use ViewModel)
    var regName by remember { mutableStateOf("") }
    var regFatherOrHusband by remember { mutableStateOf("") }
    var regAge by remember { mutableStateOf("") }
    var regMobile by remember { mutableStateOf("") }
    var regAddress by remember { mutableStateOf("") }
    var regDistrict by remember { mutableStateOf("") }
    var regOccupation by remember { mutableStateOf("") }

    // Collect event flows from viewModel for Toast messages
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegistrationEvent.Success -> {
                    Toast.makeText(context, "உறுப்பினர் சேர்க்கை வெற்றிகரமாக முடிந்தது! (Registration Successful!)", Toast.LENGTH_LONG).show()
                    // Reset fields
                    regName = ""
                    regFatherOrHusband = ""
                    regAge = ""
                    regMobile = ""
                    regAddress = ""
                    regDistrict = ""
                    regOccupation = ""
                    // Navigate to Admin page to view list
                    currentScreen = AppScreen.ADMIN
                }
                is RegistrationEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "சிலை நல அறக்கட்டளை",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "STATUE WELFARE TRUST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    // Small language indicator or gold star badge
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "தமிழ்",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { currentScreen = AppScreen.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(AppScreen.HOME.titleTamil, fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ABOUT,
                    onClick = { currentScreen = AppScreen.ABOUT },
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text(AppScreen.ABOUT.titleTamil, fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_about")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.REGISTRATION,
                    onClick = { currentScreen = AppScreen.REGISTRATION },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Register") },
                    label = { Text(AppScreen.REGISTRATION.titleTamil, fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_register")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN,
                    onClick = { currentScreen = AppScreen.ADMIN },
                    icon = { Icon(Icons.Default.List, contentDescription = "Admin Area") },
                    label = { Text(AppScreen.ADMIN.titleTamil, fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(
                        onNavigateToRegister = { currentScreen = AppScreen.REGISTRATION },
                        onNavigateToAbout = { currentScreen = AppScreen.ABOUT }
                    )
                    AppScreen.ABOUT -> AboutScreen()
                    AppScreen.REGISTRATION -> RegistrationScreen(
                        viewModel = viewModel,
                        name = regName, onNameChange = { regName = it },
                        fatherOrHusbandName = regFatherOrHusband, onFatherOrHusbandNameChange = { regFatherOrHusband = it },
                        age = regAge, onAgeChange = { regAge = it },
                        mobileNumber = regMobile, onMobileNumberChange = { regMobile = it },
                        address = regAddress, onAddressChange = { regAddress = it },
                        district = regDistrict, onDistrictChange = { regDistrict = it },
                        occupation = regOccupation, onOccupationChange = { regOccupation = it }
                    )
                    AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// ---------------- HOME SCREEN ----------------

@Composable
fun HomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Custom drawn Golden Trust Logo
        item {
            TrustLogo(modifier = Modifier.testTag("trust_logo"))
        }

        // Trust Name Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "சிலை நல அறக்கட்டளை",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "STATUE WELFARE TRUST",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "அறக்கட்டளை பதிவு எண்: BK4/23/2025",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Warm Welcome Message in a beautifully framed Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Welcome Icon",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "இணையற்ற வரவேற்பு!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "சிலை நல அறக்கட்டளைக்கு தங்களை அன்போடு வரவேற்கிறோம்.\n\nநமது அறக்கட்டளை சமூக நற்பணிகள், மரம் நடுதல், நலிவடைந்த மாணவர்களுக்கான கல்வி உதவி மற்றும் பொது நலப் பணிகளை முழு அர்ப்பணிப்புடன் செய்து வருகிறது.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Fast Action Buttons to navigate (Craft/UX Improvement)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Register Button
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("action_register_now"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "உறுப்பினர் சேர்க்கை",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // About Trust Button
                OutlinedButton(
                    onClick = onNavigateToAbout,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("action_about_now"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "எங்களைப் பற்றி",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Highlight Cards showing activities
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "நமது சேவைகள் (Core Focus)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )

                val highlights = listOf(
                    Triple(Icons.Default.Person, "சமூக சேவை", "Social Service activities for marginalized populations."),
                    Triple(Icons.Default.LocationOn, "மரக்கன்றுகள் நடுதல்", "Mass tree plantation to preserve ecology."),
                    Triple(Icons.Default.Star, "கல்வி உதவி", "Educational assistance & grants for poor students.")
                )

                highlights.forEach { highlight ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = highlight.first,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = highlight.second,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = highlight.third,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ---------------- TRust Logo Canvas Drawing ----------------

@Composable
fun TrustLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(150.dp)
            .shadow(6.dp, shape = CircleShape)
            .background(Color.White, CircleShape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val radius = this.size.minDimension / 2f
            
            // Outer golden rings (representing Welfare prosperity)
            drawCircle(
                color = Color(0xFFD4AF37), // Metallic Gold Theme
                radius = radius,
                style = Stroke(width = 4.5.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF0F5B36), // Deep green inner divider
                radius = radius - 6.dp.toPx(),
                style = Stroke(width = 1.2.dp.toPx())
            )
            
            // Deep emerald backdrop representing trees & social life
            drawCircle(
                color = Color(0xFF0F5B36),
                radius = radius - 8.dp.toPx()
            )
            
            // Shinning sunburst background representation (Golden)
            val rayCount = 16
            val rayLength = radius * 0.7f
            for (i in 0 until rayCount) {
                val angle = (i * 360f / rayCount) * (Math.PI / 180f)
                val startX = center.x + (radius * 0.22f * Math.cos(angle)).toFloat()
                val startY = center.y + (radius * 0.22f * Math.sin(angle)).toFloat()
                val endX = center.x + (rayLength * Math.cos(angle)).toFloat()
                val endY = center.y + (rayLength * Math.sin(angle)).toFloat()
                drawLine(
                    color = Color(0xFFFFD700).copy(alpha = 0.40f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }
            
            // Draw a beautiful golden monument/statue silhouette representing core identity (heritage preservation)
            // Pedestal base (Golden)
            drawRect(
                color = Color(0xFFD4AF37),
                topLeft = Offset(center.x - radius * 0.32f, center.y + radius * 0.35f),
                size = Size(radius * 0.64f, radius * 0.12f)
            )
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(center.x - radius * 0.22f, center.y + radius * 0.22f),
                size = Size(radius * 0.44f, radius * 0.13f)
            )
            
            // Statue silhouette standing tall
            val statuePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(center.x - radius * 0.10f, center.y + radius * 0.22f)
                lineTo(center.x - radius * 0.14f, center.y - radius * 0.22f) // shoulders
                lineTo(center.x + radius * 0.14f, center.y - radius * 0.22f)
                lineTo(center.x + radius * 0.10f, center.y + radius * 0.22f)
                close()
            }
            drawPath(
                path = statuePath,
                color = Color(0xFFD4AF37)
            )
            
            // Statue head
            drawCircle(
                color = Color(0xFFD4AF37),
                radius = radius * 0.08f,
                center = Offset(center.x, center.y - radius * 0.32f)
            )
            
            // Crown/bun
            drawCircle(
                color = Color(0xFFFFE17D),
                radius = radius * 0.04f,
                center = Offset(center.x, center.y - radius * 0.42f)
            )

            // Right arm folder or blessing hand representation
            val armPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(center.x + radius * 0.04f, center.y - radius * 0.08f)
                lineTo(center.x + radius * 0.16f, center.y - radius * 0.16f)
                lineTo(center.x + radius * 0.19f, center.y - radius * 0.08f)
                close()
            }
            drawPath(
                path = armPath,
                color = Color(0xFFFFD700)
            )
        }
    }
}


// ---------------- ABOUT SCREEN ----------------

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    
    // Call Intent trigger
    val dialContact: () -> Unit = {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:9342511821")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "அழைப்பைத் தொடங்க முடியவில்லை", Toast.LENGTH_SHORT).show()
        }
    }

    // Email Intent trigger
    val emailContact: () -> Unit = {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:Krishkavathi@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "சிலை நல அறக்கட்டளை - தகவல்")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "மின்னஞ்சல் செயலியை திறக்க முடியவில்லை", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Core Brand header card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "சிலை நல அறக்கட்டளை",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "STATUE WELFARE TRUST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Official registration details Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "அதிகாரப்பூர்வ விவரங்கள் (Official Information)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))

                    RegistrationDetailRow(
                        label = "பதிவு எண் (Registration No)",
                        value = "BK4/23/2025"
                    )
                    RegistrationDetailRow(
                        label = "தர்பன் ஐடி (Darpan ID)",
                        value = "TN/2025/0779121"
                    )
                    RegistrationDetailRow(
                        label = "PAN எண்",
                        value = "ABLTS8950M"
                    )
                    RegistrationDetailRow(
                        label = "12A பதிவு ARN",
                        value = "935383490251225"
                    )
                }
            }
        }

        // Mission Statement card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "அறக்கட்டளையின் முக்கிய நோக்கங்கள் (Mission)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    
                    Text(
                        text = "• Social Service (சமூக சேவை)\n• Tree Plantation (மரக்கன்றுகள் நடுதல்)\n• Education Support (கல்வி உதவிக்கான திட்டங்கள்)\n• Public Welfare (ஏழை எளியோர் பொது நலன்)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Interactive contact details Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "தொடர்பு கொள்ள (Contact Us)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))

                    // Column for Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "தொலைபேசி எண் (Phone)",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "+91 9342511821",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Button(
                            onClick = dialContact,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_dial")
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("அழைக்க", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Column for Email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "மின்னஞ்சல் (Email)",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Krishkavathi@gmail.com",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = emailContact,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("btn_email")
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email", Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("மின்னஞ்சல்", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun RegistrationDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}


// ---------------- REGISTER SCREEN ----------------

@Composable
fun RegistrationScreen(
    viewModel: MemberViewModel,
    name: String, onNameChange: (String) -> Unit,
    fatherOrHusbandName: String, onFatherOrHusbandNameChange: (String) -> Unit,
    age: String, onAgeChange: (String) -> Unit,
    mobileNumber: String, onMobileNumberChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    district: String, onDistrictChange: (String) -> Unit,
    occupation: String, onOccupationChange: (String) -> Unit
) {
    // Quick district suggestion tag chips
    val districts = listOf(
        "சென்னை", "திருச்சி", "மதுரை", "கோவை", 
        "தஞ்சாவூர்", "திருநெல்வேலி", "சேலம்", "ஈரோடு"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "உறுப்பினர் சேர்க்கை படிவம்",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "உறுப்பினராக இணைய கீழே உள்ள படிவத்தை பூர்த்தி செய்யவும்",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 6.dp)
            )
        }

        // Input Name
        item {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("முழு பெயர் (Full Name) *") },
                placeholder = { Text("முழு பெயரை உள்ளிடவும்") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Input Father / Husband Name
        item {
            OutlinedTextField(
                value = fatherOrHusbandName,
                onValueChange = onFatherOrHusbandNameChange,
                label = { Text("தந்தை / கணவர் பெயர் (Father / Husband Name) *") },
                placeholder = { Text("பெயரை உள்ளிடவும்") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_father_husband"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Input Age & Mobile Number in responsive Row (or Column if tiny)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) onAgeChange(it) },
                    label = { Text("வயது (Age) *") },
                    placeholder = { Text("25") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_age"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { if (it.length <= 15 && it.all { c -> c.isDigit() || c == '+' || c == '-' }) onMobileNumberChange(it) },
                    label = { Text("கைபேசி எண் (Mobile) *") },
                    placeholder = { Text("9342511821") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(2f)
                        .testTag("input_mobile"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }

        // Input Address
        item {
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("முகவரி (Full Address) *") },
                placeholder = { Text("கதவு எண், தெரு மற்றும் ஊர்") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_address"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Input District with auto recommendation chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = district,
                    onValueChange = onDistrictChange,
                    label = { Text("மாவட்டம் (District) *") },
                    placeholder = { Text("எ.கா: திருச்சி") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_district"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "விரைவு தேர்வுகள் (Quick Select):",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 2.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(districts) { dst ->
                        AssistChip(
                            onClick = { onDistrictChange(dst) },
                            label = { Text(dst, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (district == dst) MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f) else Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // Input Occupation
        item {
            OutlinedTextField(
                value = occupation,
                onValueChange = onOccupationChange,
                label = { Text("தொழில் (Occupation) *") },
                placeholder = { Text("விவசாயம் / வணிகம் / வேலை") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_occupation"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    viewModel.registerMember(
                        name = name,
                        fatherOrHusbandName = fatherOrHusbandName,
                        ageStr = age,
                        mobileNumber = mobileNumber,
                        address = address,
                        district = district,
                        occupation = occupation
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_submit"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "பதிவு செய் (Submit Registration)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}


// ---------------- ADMIN SCREEN ----------------

@Composable
fun AdminScreen(viewModel: MemberViewModel) {
    val members by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    // Track selected member for details or deletion
    var memberToDelete by remember { mutableStateOf<Member?>(null) }
    var selectedMemberForDetail by remember { mutableStateOf<Member?>(null) }

    val filteredMembers = remember(members, searchQuery) {
        if (searchQuery.isBlank()) {
            members
        } else {
            members.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.mobileNumber.contains(searchQuery) ||
                it.district.contains(searchQuery, ignoreCase = true) ||
                it.occupation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Modal dialog for deletion confirmation
    if (memberToDelete != null) {
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = { Text("உறுப்பினர் நீக்கம் (Delete Member)", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { 
                Text("மின்னஞ்சல்/படிவத்திலிருந்து '${memberToDelete?.name}' என்ற உறுப்பினரை நீக்க விரும்புகிறீர்களா?\nஇந்த செயல்முறை நிரந்தரமாக நீக்கிவிடும்.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        memberToDelete?.let { viewModel.deleteMember(it) }
                        memberToDelete = null
                    }
                ) {
                    Text("ஆம் (Yes)", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("இல்லை (No)")
                }
            }
        )
    }

    // Modal dialogue for Member Details Dialog (Elite custom detail flow)
    if (selectedMemberForDetail != null) {
        val member = selectedMemberForDetail!!
        AlertDialog(
            onDismissRequest = { selectedMemberForDetail = null },
            title = {
                Column {
                    Text(
                        text = "உறுப்பினர் முழு விவரம்",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Member Card Details",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        DetailInfoItem(label = "முழு பெயர் (Name)", value = member.name)
                    }
                    item {
                        DetailInfoItem(label = "தந்தை / கணவர் பெயர்", value = member.fatherOrHusbandName)
                    }
                    item {
                        DetailInfoItem(label = "வயது (Age)", value = "${member.age} வருடங்கள்")
                    }
                    item {
                        DetailInfoItem(label = "கைபேசி (Mobile)", value = member.mobileNumber)
                    }
                    item {
                        DetailInfoItem(label = "மாவட்டம் (District)", value = member.district)
                    }
                    item {
                        DetailInfoItem(label = "தொழில் (Occupation)", value = member.occupation)
                    }
                    item {
                        DetailInfoItem(label = "முகவரி (Address)", value = member.address)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedMemberForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("சரி (Close)")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "உறுப்பினர்களின் பட்டியல் (Trust Members)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )

        // Statistics bar or search header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "மொத்த உறுப்பினர்கள்: ${members.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (filteredMembers.size != members.size) {
                Text(
                    text = "வடிகட்டப்பட்டவை: ${filteredMembers.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Active search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("உறுப்பினர் பெயர், கைபேசி அல்லது மாவட்டம் தேடவும்...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("admin_search"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (filteredMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (members.isEmpty()) "உறுப்பினர்கள் யாரும் இன்னும் பதிவு செய்யவில்லை!\n(No members registered yet!)"
                               else "தேடலுக்கு பொருந்தும் உறுப்பினர்கள் யாரும் இல்லை.\n(No matches found!)",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("member_list_view"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredMembers) { m ->
                    MemberRowCard(
                        member = m,
                        onClick = { selectedMemberForDetail = m },
                        onDelete = { memberToDelete = m }
                    )
                }
            }
        }
    }
}

@Composable
fun MemberRowCard(
    member: Member,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("member_row_${member.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = member.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "கைபேசி: ${member.mobileNumber}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "மாவட்டம்: ${member.district}  •  தொழில்: ${member.occupation}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick Actions: Tap Details or Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.testTag("member_action_view_${member.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("member_action_delete_${member.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Member",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.15f))
            .padding(10.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
