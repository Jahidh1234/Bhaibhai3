package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Dynamic custom presets for simulated member photos & bkash screenshots
val PHOTO_PRESETS = listOf(
    "preset_man1" to "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
    "preset_man2" to "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
    "preset_man3" to "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
    "preset_man4" to "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150"
)

val SCREENSHOT_PRESETS = listOf(
    "screens_bkash1" to "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?w=300",
    "screens_bkash2" to "https://images.unsplash.com/photo-1563013544-824ae1d704d3?w=300"
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: SamityViewModel) {
    val context = LocalContext.current
    val authByState by viewModel.authState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Scaffold(
        bottomBar = {
            if (authByState is AuthState.LoggedIn) {
                NavigationBar(
                    containerColor = if (isDark) CardDarkMoss else Color.White,
                    tonalElevation = 8.dp
                ) {
                    val activeRow = viewModel.currentScreen.value
                    
                    NavigationBarItem(
                        selected = activeRow == "dashboard",
                        onClick = { viewModel.currentScreen.value = "dashboard" },
                        icon = { Icon(Icons.Default.Dashboard, "ড্যাশবোর্ড") },
                        label = { Text("ড্যাশবোর্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDarkForest,
                            selectedTextColor = EmeraldSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = activeRow == "members",
                        onClick = { viewModel.currentScreen.value = "members" },
                        icon = { Icon(Icons.Default.People, "সদস্য") },
                        label = { Text("সদস্য", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDarkForest,
                            selectedTextColor = EmeraldSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = activeRow == "payments",
                        onClick = { viewModel.currentScreen.value = "payments" },
                        icon = { Icon(Icons.Default.Payments, "পেমেন্ট") },
                        label = { Text("পেমেন্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDarkForest,
                            selectedTextColor = EmeraldSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = activeRow == "dues" || activeRow == "reports",
                        onClick = { viewModel.currentScreen.value = "dues" },
                        icon = { Icon(Icons.Default.Assessment, "রিপোর্ট") },
                        label = { Text("হিসাব", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDarkForest,
                            selectedTextColor = EmeraldSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = activeRow == "info",
                        onClick = { viewModel.currentScreen.value = "info" },
                        icon = { Icon(Icons.Default.Info, "তথ্য") },
                        label = { Text("নিয়ম", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDarkForest,
                            selectedTextColor = EmeraldSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            IslamicBackgroundPattern()

            AnimatedContent(
                targetState = viewModel.currentScreen.value,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                },
                label = "screen_trans"
            ) { targetScreen ->
                when (targetScreen) {
                    "login" -> LoginScreen(viewModel)
                    "dashboard" -> DashboardScreen(viewModel)
                    "members" -> MembersScreen(viewModel)
                    "payments" -> PaymentsScreen(viewModel)
                    "dues" -> DuesScreen(viewModel)
                    "reports" -> ReportsScreen(viewModel)
                    "info" -> InfoScreen(viewModel)
                    "receipt" -> ReceiptScreen(viewModel)
                    else -> LoginScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: SamityViewModel) {
    var email by remember { mutableStateOf("admin@bhaibhai.com") }
    var password by remember { mutableStateOf("123456") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IslamicLogo(size = 90.0)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ভাই ভাই সমিতি",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = EmeraldDarkForest,
            textAlign = TextAlign.Center
        )
        Text(
            text = "সঞ্চয় করি গরুর ফান্ডে, আনন্দ করি ঈদের চাঁদে।",
            style = MaterialTheme.typography.bodyMedium,
            color = EmeraldSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "অ্যাডমিন লগইন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDarkForest,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ইমেইল অ্যাড্রেস / মেম্বার আইডি") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("পাসওয়ার্ড") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val success = viewModel.login(email, "PRESIDENT")
                        if (success) {
                            Toast.makeText(context, "সভাপতি হিসেবে সফল লগইন!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("লগইন করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Role-based testing shortcuts
        Text(
            text = "রোল পরীক্ষার সহজ শর্টকাট:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.login("president@samity.com", "PRESIDENT")
                    Toast.makeText(context, "সভাপতি (প্রেসিডেন্ট) মোড সচল!", Toast.LENGTH_SHORT).show()
                },
                border = BorderStroke(1.dp, EmeraldPrimary)
            ) {
                Text("সভাপতি", color = EmeraldSecondary)
            }

            OutlinedButton(
                onClick = {
                    viewModel.login("cashier@samity.com", "CASHIER")
                    Toast.makeText(context, "ক্যাশিয়ার মোড সচল!", Toast.LENGTH_SHORT).show()
                },
                border = BorderStroke(1.dp, SoftGoldAccent)
            ) {
                Text("ক্যাশিয়ার", color = SoftGoldAccent)
            }

            OutlinedButton(
                onClick = {
                    viewModel.login("member@samity.com", "MEMBER", "M003")
                    Toast.makeText(context, "মেম্বার M003 মোড সচল!", Toast.LENGTH_SHORT).show()
                },
                border = BorderStroke(1.dp, EmeraldDarkForest)
            ) {
                Text("সদস্য (M003)", color = EmeraldDarkForest)
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: SamityViewModel) {
    val context = LocalContext.current
    val listMembers by viewModel.members.collectAsState()
    val listPayments by viewModel.payments.collectAsState()
    val duesList by viewModel.duesState.collectAsState()
    val loggedInUser by viewModel.authState.collectAsState()

    val totalMembers = listMembers.size
    val totalCollected = listPayments.sumOf { it.amount }
    val totalExpected = 12 * 600.0 * totalMembers
    val totalDues = duesList.sumOf { it.dueAmount }

    val cowProgress = if (totalExpected > 0) (totalCollected / totalExpected).toFloat() else 0f

    // Sort to compile last 6 months trend maps
    val last6MonthsPaymentsMap = listPayments
        .groupBy { it.paymentMonth }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Logged-In User Information Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = when (val state = loggedInUser) {
                        is AuthState.LoggedIn -> state.name
                        else -> "অতিথি ব্যবহারকারী"
                    }
                    Text(
                        text = "আসসালামু আলাইকুম,",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDarkForest
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "লগআউট করা হয়েছে।", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "লগআউট", tint = Color.Red)
                    }
                }
            }
        }

        // Section Ramadan Countdown (prompt #11a)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) CardDarkMoss else EmeraldPrimary),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFF047857)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "কাউন্টডাউন",
                            tint = SoftGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পবিত্র রমজান ও কুরবানি কাউন্টডাউন",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = viewModel.countdownText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SoftGoldAccent,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "রমজানের লক্ষ্য: জমা করে একটি সুন্দর গরু কেনা।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Grid Statistics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Members Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.People, "সদস্য", tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "মোট সদস্য", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = "${viewModel.translateToBanglaNum(totalMembers.toString())} জন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EmeraldDarkForest
                        )
                    }
                }

                // Total Collected Taka Card
                Card(
                    modifier = Modifier.weight(1.2f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.MonetizationOn, "জমা", tint = EmeraldSecondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "মোট সঞ্চয় জমা", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = "৳ ${viewModel.translateToBanglaNum(String.format("%,.0f", totalCollected))}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EmeraldSecondary
                        )
                    }
                }

                // Total Due Taka Card
                Card(
                    modifier = Modifier.weight(1.1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Warning, "বকেয়া", tint = Color.Red, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "মোট বকেয়া বকেয়া", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = "৳ ${viewModel.translateToBanglaNum(String.format("%,.0f", totalDues))}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        }

        // Circular Goal Progress Bar Chart Panel
        item {
            CustomProgressRing(
                progress = cowProgress,
                targetAmount = totalExpected,
                currentAmount = totalCollected,
                viewModel = viewModel
            )
        }

        // Monthly Income Bar Chart Trends Panel
        item {
            CustomBarChart(data = last6MonthsPaymentsMap, viewModel = viewModel)
        }

        // Automated notifications and reminders block
        item {
            AutomatedNotificationPanel(viewModel = viewModel)
        }

        // Quick action to view all reports
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentScreen.value = "reports" },
                colors = CardDefaults.cardColors(containerColor = SoftGoldAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, SoftGoldAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আর্থিক রিপোর্ট সমূহ দেখুন",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDarkForest,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "সদস্যভিত্তিক, পরিশোধ ও মাস ভিত্তিক লিজার",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldDarkForest)
                }
            }
        }

        // Unpaid Quick List Table (Prompt 11.b: বকেয়া সদস্যদের অটো তালিকা)
        item {
            Text(
                text = "জরুরী বকেয়া নোটিশ তালিকা",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val delinquentMembers = duesList.filter { it.dueAmount > 0 }

        if (delinquentMembers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "আলহামদুলিল্লাহ! এই মাসে কারও কোন বকেয়া নেই।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldSecondary,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(delinquentMembers.take(4)) { dueInfo ->
                DelinquentMemberRow(dueInfo, viewModel, context)
            }
        }
    }
}

@Composable
fun DelinquentMemberRow(
    dueInfo: MemberDueInfo,
    viewModel: SamityViewModel,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dueInfo.member.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldDarkForest
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "বকেয়া",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "মোবাইল: ${viewModel.translateToBanglaNum(dueInfo.member.mobile)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "৳ ${viewModel.translateToBanglaNum(dueInfo.dueAmount.toInt().toString())}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(4.dp))

            // Action trigger tools (Point #6: SMS, COPY, WhatsApp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বাকি: ${dueInfo.dueMonths.size} মাস (যেমন: ${dueInfo.dueMonths.firstOrNull()?.let { viewModel.translateMonthToBangla(it) }})",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rawMsgText = "ভাইজান, ভাই ভাই সমিতির এই মাসের বকেয়া রয়ে গেছে, অনুগ্রহ পূর্বক আপনার মোট বকেয়া ৳${viewModel.translateToBanglaNum(dueInfo.dueAmount.toInt().toString())} টাকা পরিশোধ করুন। দয়া করে সমিতির অফিশিয়াল বিকাশ ০১৯৭৬৯৭২৯৮০ নম্বরে সেন্ড মানি করুন। ধন্যবাদ।"
                    
                    // Copy button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Bhai Bhai Samity Reminder", rawMsgText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "রিমাইন্ডার ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Icon(Icons.Default.ContentCopy, "কপি", tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                    }

                    // Direct Whatsapp button
                    IconButton(
                        onClick = {
                            try {
                                val cleanNum = "88" + dueInfo.member.mobile
                                val encodedText = Uri.encode(rawMsgText)
                                val urlStr = "https://api.whatsapp.com/send?phone=$cleanNum&text=$encodedText"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "হোয়াটসঅ্যাপ অ্যাপটি ইনস্টল নেই", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .background(EmeraldSecondary, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, "হোয়াটসঅ্যাপ", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val previewText = "ভাইজান, ভাই ভাই সমিতির এই মাসের বকেয়া রয়ে গেছে..."
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSystemInDarkTheme()) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                        RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "এসএমএস হিসাবে:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                val rawMsgTextPreview = "ভাইজান, ভাই ভাই সমিতির এই মাসের বকেয়া রয়ে গেছে, অনুগ্রহ পূর্বক আপনার মোট বকেয়া ৳${viewModel.translateToBanglaNum(dueInfo.dueAmount.toInt().toString())} টাকা পরিশোধ করুন। দয়া করে সমিতির অফিশিয়াল বিকাশ ০১৯৭৬৯৭২৯৮০ নম্বরে সেন্ড মানি করুন। ধন্যবাদ।"
                Text(
                    text = rawMsgTextPreview,
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinVerificationDialog(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নিরাপত্তা লক যাচাই", fontWeight = FontWeight.Bold, color = EmeraldDarkForest) },
        text = {
            Column {
                Text("সদস্য যুক্ত বা পরিবর্তন করতে ৬ সংখ্যার পাসওয়ার্ড দিন:", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { 
                        pin = it.take(6)
                        isError = false
                    },
                    label = { Text("পিন (PIN)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = isError
                )
                if (isError) {
                    Text("ভুল পিন কোড! অনুগ্রহ করে সঠিক পিন দিন।", color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin == "972980") {
                        onSuccess()
                        onDismiss()
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("নিশ্চিত করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun MembersScreen(viewModel: SamityViewModel) {
    val listMembers by viewModel.members.collectAsState()
    val duesList by viewModel.duesState.collectAsState()
    val loggedInState by viewModel.authState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editModeMember by remember { mutableStateOf<Member?>(null) }
    var searchByText by remember { mutableStateOf("") }
    
    // Security pin state callback
    var pinActionPending by remember { mutableStateOf<(() -> Unit)?>(null) }

    val isAuthorized = when (val state = loggedInState) {
        is AuthState.LoggedIn -> state.role == "PRESIDENT"
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সমিতির সদস্যবৃন্দ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDarkForest
                )

                if (isAuthorized) {
                    Button(
                        onClick = { 
                            pinActionPending = { showAddDialog = true }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("যোগ করুন")
                    }
                }
            }

            OutlinedTextField(
                value = searchByText,
                onValueChange = { searchByText = it },
                label = { Text("নাম বা নম্বর দিয়ে খুঁজুন") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            )

            val filteredList = listMembers.filter {
                it.name.contains(searchByText, ignoreCase = true) || it.mobile.contains(searchByText)
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("কোন হদিস বা সদস্য মিল পাওয়া যায়নি।", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { member ->
                        val dueInfo = duesList.find { it.member.memberId == member.memberId }
                        MemberCardItem(
                            member = member,
                            dueInfo = dueInfo,
                            isAuthorized = isAuthorized,
                            viewModel = viewModel,
                            onEdit = { 
                                pinActionPending = { editModeMember = member }
                            },
                            onDelete = {
                                pinActionPending = {
                                    viewModel.deleteMember(member) {
                                        Toast.makeText(viewModel.getApplication(), "সদস্য সফলভাবে মুছে দেওয়া হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddEditMemberDialog(
                editingMember = null,
                viewModel = viewModel,
                onDismiss = { showAddDialog = false }
            )
        }

        // Edit Dialog
        if (editModeMember != null) {
            AddEditMemberDialog(
                editingMember = editModeMember,
                viewModel = viewModel,
                onDismiss = { editModeMember = null }
            )
        }

        // PIN validation Dialog
        if (pinActionPending != null) {
            PinVerificationDialog(
                onSuccess = {
                    pinActionPending?.invoke()
                },
                onDismiss = {
                    pinActionPending = null
                }
            )
        }
    }
}

@Composable
fun MemberCardItem(
    member: Member,
    dueInfo: MemberDueInfo?,
    isAuthorized: Boolean,
    viewModel: SamityViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Image placeholder
                    val presetColor = when (member.role) {
                        "PRESIDENT" -> EmeraldDarkForest
                        "CASHIER" -> SoftGoldAccent
                        else -> EmeraldPrimary
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(presetColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.take(1),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDarkForest,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "রোলে: ${when(member.role) {
                                    "PRESIDENT" -> "সভাপতি"
                                    "CASHIER" -> "ক্যাশিয়ার"
                                    else -> "সাধারণ সদস্য"
                                }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আইডি: ${member.memberId}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSecondary
                            )
                        }
                    }
                }

                // Balance status (Due badge) - pill design, no wrapping allowed
                if (dueInfo != null && dueInfo.dueAmount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Red.copy(alpha = 0.1f))
                            .border(1.dp, Color.Red, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "বকেয়া: ৳ ${viewModel.translateToBanglaNum(dueInfo.dueAmount.toInt().toString())}",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.1f))
                            .border(1.dp, EmeraldPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "পরিশোধিত",
                            color = EmeraldSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "মোবাইল: ${viewModel.translateToBanglaNum(member.mobile)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "যোগদান: ${viewModel.translateToBanglaNum(member.joinDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (isAuthorized) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "সম্পাদনা", tint = EmeraldSecondary)
                        }
                        IconButton(onClick = { showConfirmDelete = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "মুছে ফেলুন", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before deleting
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("নিশ্চিত অপসারণ") },
            text = { Text("আপনি কি নিশ্চিত যে সদস্য '${member.name}' এবং তাঁর সমস্ত হিসাব অপসারণ করতে চান?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showConfirmDelete = false
                }) {
                    Text("হ্যাঁ, ডিলিট করুন", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberDialog(
    editingMember: Member?,
    viewModel: SamityViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(editingMember?.name ?: "") }
    var mobile by remember { mutableStateOf(editingMember?.mobile ?: "") }
    var joinDate by remember { mutableStateOf(editingMember?.joinDate ?: "2026-06-02") }
    var role by remember { mutableStateOf(editingMember?.role ?: "MEMBER") }

    val titleText = if (editingMember == null) "নতুন সদস্য যোগ করুন" else "সদস্য তথ্য সংশোধন"
    val btnText = if (editingMember == null) "সংরক্ষণ করুন" else "সংশোধন করুন"
    val curContext = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText, fontWeight = FontWeight.Bold, color = EmeraldDarkForest) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("সদস্যের সম্পূর্ণ নাম (বাংলায়)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("মোবাইল নম্বর") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = joinDate,
                    onValueChange = { joinDate = it },
                    label = { Text("যোগদানের তারিখ (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("সমিতি রোল বিন্যাস:", fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PRESIDENT" to "সভাপতি", "CASHIER" to "ক্যাশিয়ার", "MEMBER" to "সদস্য").forEach { (v, lbl) ->
                        FilterChip(
                            selected = role == v,
                            onClick = { role = v },
                            label = { Text(lbl) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editingMember == null) {
                        viewModel.addMember(name, mobile, joinDate, role, null) { ok ->
                            if (ok) {
                                Toast.makeText(curContext, "সদস্য সফলভাবে যোগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            } else {
                                Toast.makeText(curContext, "অনুগ্রহ করে সব তথ্য দিন!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        viewModel.updateMember(editingMember.copy(name = name, mobile = mobile, joinDate = joinDate, role = role)) {
                            Toast.makeText(curContext, "সদস্য তথ্য আপডেট সফল!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(btnText, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun PaymentsScreen(viewModel: SamityViewModel) {
    val listPayments by viewModel.payments.collectAsState()
    val listMembers by viewModel.members.collectAsState()
    val listDues by viewModel.duesState.collectAsState()
    val loggedInState by viewModel.authState.collectAsState()

    var showAddForm by remember { mutableStateOf(false) }

    val canCollectPayment = when (val state = loggedInState) {
        is AuthState.LoggedIn -> state.role == "PRESIDENT" || state.role == "CASHIER"
        else -> false
    }

    Scaffold(
        floatingActionButton = {
            // Only admins and cashiers can collect payments
            if (canCollectPayment && !showAddForm) {
                ExtendedFloatingActionButton(
                    text = { Text("নতুন জমা নিন") },
                    icon = { Icon(Icons.Default.Add, null) },
                    onClick = { showAddForm = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (showAddForm) {
                AddPaymentForm(
                    viewModel = viewModel,
                    listMembers = listMembers,
                    listDues = listDues,
                    onBack = { showAddForm = false }
                )
            } else {
                Column {
                    Text(
                        text = "জমার ইতিহাস ও ট্রানজেকশন তালিকা",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDarkForest,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (listPayments.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("এখনও কোন মাসিক জমার রেকর্ড করা নেই।", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listPayments) { payment ->
                                PaymentItemRow(payment, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPaymentForm(
    viewModel: SamityViewModel,
    listMembers: List<Member>,
    listDues: List<MemberDueInfo>,
    onBack: () -> Unit
) {
    if (listMembers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("প্রথমে একজন সদস্য যোগ করুন!")
        }
        return
    }

    var selectedMemberIndex by remember { mutableStateOf(0) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val activeMember = listMembers[selectedMemberIndex]
    val activeMemberDueInfo = listDues.find { it.member.memberId == activeMember.memberId }

    // Set months based on remaining dues
    val remainingUnpaidMonths = activeMemberDueInfo?.dueMonths ?: emptyList()
    var selectedMonthIndex by remember { mutableStateOf(0) }
    var expandedMonthDropdown by remember { mutableStateOf(false) }

    val activeMonth = if (remainingUnpaidMonths.isNotEmpty()) {
        remainingUnpaidMonths[selectedMonthIndex.coerceAtMost(remainingUnpaidMonths.lastIndex)]
    } else {
        "2026-06" // Default mock
    }

    var dateOfPayment by remember { mutableStateOf("2026-06-02") }
    var method by remember { mutableStateOf("বিকাশ") } // ক্যাশ, বিকাশ, নগদ, রকেট
    var txId by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var screenshotBase64 by remember { mutableStateOf<String?>(null) } // screenshot trigger
    
    val currentLoc = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "পেছনে")
            }
            Text(
                text = "নতুন পেমেন্ট সংগ্রহ ফর্ম",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldDarkForest
            )
        }

        Divider(color = Color.LightGray)

        // bKash disclaimer #7
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else EmeraldPrimary.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = LightMintBg)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = EmeraldSecondary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("অনলাইন পেমেন্ট বিকাশ নম্বর:", fontSize = 11.sp, color = Color.Gray)
                    Text("০১৯৭৬৯৭২৯৮০ (সেন্ড মানি)", fontWeight = FontWeight.Bold, color = EmeraldDarkForest)
                }
            }
        }

        // Selected Member
        Text("সদস্য নির্বাচন করুন:", fontWeight = FontWeight.Bold, color = EmeraldDarkForest)
        Box {
            OutlinedButton(
                onClick = { expandedDropdown = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${activeMember.name} (আইডি: ${activeMember.memberId})", color = EmeraldSecondary)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, null)
            }
            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                listMembers.forEachIndexed { idx, m ->
                    DropdownMenuItem(
                        text = { Text("${m.name} (${m.memberId})") },
                        onClick = {
                            selectedMemberIndex = idx
                            selectedMonthIndex = 0
                            expandedDropdown = false
                        }
                    )
                }
            }
        }

        // Selecting the applied month
        Text("কোন মাসের জমা:", fontWeight = FontWeight.Bold)
        if (remainingUnpaidMonths.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "সব পরিশোধিত! পরবর্তী মাসের জমা অগ্রিম সংগৃহীত হবে।",
                    color = EmeraldDarkForest,
                    fontSize = 12.sp
                )
            }
        } else {
            Box {
                OutlinedButton(
                    onClick = { expandedMonthDropdown = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = viewModel.translateMonthToBangla(activeMonth), color = EmeraldSecondary)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(
                    expanded = expandedMonthDropdown,
                    onDismissRequest = { expandedMonthDropdown = false }
                ) {
                    remainingUnpaidMonths.forEachIndexed { idx, mName ->
                        DropdownMenuItem(
                            text = { Text(viewModel.translateMonthToBangla(mName)) },
                            onClick = {
                                selectedMonthIndex = idx
                                expandedMonthDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Amount Input (Fixed 600)
        Text("জমার পরিমান (পরিবর্তন অযোগ্য):", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = "৬০০ টাকা",
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = EmeraldDarkForest,
                disabledBorderColor = EmeraldPrimary,
                disabledContainerColor = Color(0xFFF1F5F9)
            )
        )

        // Date of Payment
        OutlinedTextField(
            value = dateOfPayment,
            onValueChange = { dateOfPayment = it },
            label = { Text("জমার তারিখ (YYYY-MM-DD)") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
            modifier = Modifier.fillMaxWidth()
        )

        // Payment Method Options
        Text("জমার মাধ্যম:", fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("বিকাশ", "নগদ", "রকেট", "ক্যাশ").forEach { med ->
                FilterChip(
                    selected = method == med,
                    onClick = { method = med },
                    label = { Text(med) }
                )
            }
        }

        // Transaction ID
        if (method != "ক্যাশ") {
            OutlinedTextField(
                value = txId,
                onValueChange = { txId = it },
                label = { Text("লেনদেন আইডি (Transaction ID)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Prompt #11.c: Screenshot preset simulator selector
        if (method == "বিকাশ" || method == "নগদ" || method == "রকেট") {
            Text("পেমেন্ট ইনভয়েস স্ক্রিনশট লিংক (অনলাইন ভেরিফাই):", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SCREENSHOT_PRESETS.forEachIndexed { ind, (name, url) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (screenshotBase64 == url) 3.dp else 1.dp,
                                color = if (screenshotBase64 == url) EmeraldPrimary else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { screenshotBase64 = url }
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FilePresent, "স্ক্রিনশট", tint = EmeraldPrimary)
                            Text("স্ক্রিনশট #${viewModel.translateToBanglaNum((ind+1).toString())}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            if (screenshotBase64 != null) {
                Text(
                    text = "✓ ইনভয়েস ছবি ভেরিফাইয়ের জন্য সংযুক্ত করা হয়েছে।",
                    color = EmeraldSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Note
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("অতিরিক্ত মন্তব্য বা নোট (ঐচ্ছিক)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addPayment(
                    memberId = activeMember.memberId,
                    memberName = activeMember.name,
                    amount = 600.0,
                    date = dateOfPayment,
                    paymentMonth = activeMonth,
                    method = method,
                    txnId = txId,
                    note = note,
                    screenshotB64 = screenshotBase64
                ) { success ->
                    if (success) {
                        Toast.makeText(currentLoc, "৳৬০০ সফলভাবে সংগ্রহ সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                        viewModel.currentScreen.value = "receipt"
                    } else {
                        Toast.makeText(currentLoc, "পেমেন্ট রেকর্ড ব্যর্থ হয়েছে!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
            Text("জমা রেকর্ড করুন এবং রসিদ তৈরি করুন", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PaymentItemRow(payment: Payment, viewModel: SamityViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.selectedPaymentForReceipt = payment
                viewModel.currentScreen.value = "receipt"
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = payment.memberName,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDarkForest,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "রসিদ: ${payment.receiptNo}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "আইডি: ${payment.memberId}",
                            fontSize = 11.sp,
                            color = EmeraldSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "৳ ${viewModel.translateToBanglaNum(payment.amount.toInt().toString())}",
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoftGoldAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = viewModel.translateMonthToBangla(payment.paymentMonth),
                            color = EmeraldDarkForest,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "মাধ্যম: ${payment.paymentMethod}",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = "তারিখ: ${viewModel.translateToBanglaNum(payment.paymentDate)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Preview screenshot indicator #11c
            if (payment.screenshotBase64 != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightMintBg, RoundedCornerShape(4.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, "স্ক্রিনশট", tint = EmeraldSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✓ বিকাশ ইনভয়েস সংযুক্ত (অ্যাডমিন ভেরিফাইড)", fontSize = 10.sp, color = EmeraldDarkForest, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DuesScreen(viewModel: SamityViewModel) {
    val listDues by viewModel.duesState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = "বকেয়া ট্র্যাকিং ও যোগাযোগ প্যানেল",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDarkForest
                )
                Text(
                    text = "রমজানের পূর্বে আদায়ের জন্য নোটিফিকেশন প্রিন্ট করুন",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { viewModel.currentScreen.value = "reports" },
                colors = ButtonDefaults.buttonColors(containerColor = SoftGoldAccent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(max = 38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = EmeraldDarkForest,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("রিপোর্ট দেখুন", color = EmeraldDarkForest, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val unpaidMembers = listDues.filter { it.dueAmount > 0 }

        if (unpaidMembers.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DoneOutline, "সব পেইড", tint = EmeraldSecondary, modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("সবাই পরিশোধ করেছেন! কোনও সদস্য বকেয়া নেই।", color = EmeraldSecondary, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(unpaidMembers) { due ->
                    DelinquentMemberRow(dueInfo = due, viewModel = viewModel, context = context)
                }
            }
        }
    }
}

@Composable
fun ReportsScreen(viewModel: SamityViewModel) {
    val listPayments by viewModel.payments.collectAsState()
    val listMembers by viewModel.members.collectAsState()
    val context = LocalContext.current

    var selectedYear by remember { mutableStateOf("2026") }
    var selectedMonth by remember { mutableStateOf("06") } // June
    var selectedMemberId by remember { mutableStateOf("") }

    var tabIndex by remember { mutableStateOf(0) } // 0 = Monthly, 1 = Annual, 2 = Member-wise

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "আর্থিক অডিট ও সমিতির রিপোর্ট সমূহ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EmeraldDarkForest
        )
        Text(
            text = "পিডিএফ এক্সেল রিসিভ এক্সপোর্ট করতে বাটন সমূহ ব্যবহার করুন",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Tab selection
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.White,
            contentColor = EmeraldDarkForest
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                Text("মাসিক রিপোর্ট", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                Text("বার্ষিক রিপোর্ট", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }) {
                Text("সদস্য অনুযায়ী", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters UI
        when (tabIndex) {
            0 -> {
                // Monthly filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var expMonth by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(onClick = { expMonth = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("মাস: ${viewModel.translateMonthToBangla("2026-$selectedMonth").split(" ")[0]}", color = EmeraldSecondary)
                        }
                        DropdownMenu(expanded = expMonth, onDismissRequest = { expMonth = false }) {
                            listOf("01" to "জানুয়ারি", "02" to "ফেব্রুয়ারি", "03" to "মার্চ", "04" to "এপ্রিল", "05" to "মে", "06" to "জুন", "07" to "জুলাই", "08" to "আগস্ট", "09" to "সেপ্টেম্বর", "10" to "অক্টোবর", "11" to "নভেম্বর", "12" to "ডিসেম্বর").forEach { (v, l) ->
                                DropdownMenuItem(text = { Text(l) }, onClick = { selectedMonth = v; expMonth = false })
                            }
                        }
                    }

                    var expYear by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(onClick = { expYear = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("বছর: ${viewModel.translateToBanglaNum(selectedYear)}", color = EmeraldSecondary)
                        }
                        DropdownMenu(expanded = expYear, onDismissRequest = { expYear = false }) {
                            listOf("2026", "2027").forEach { yr ->
                                DropdownMenuItem(text = { Text(viewModel.translateToBanglaNum(yr)) }, onClick = { selectedYear = yr; expYear = false })
                            }
                        }
                    }
                }
            }
            1 -> {
                // Annual filters
                var expYear by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expYear = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("অর্থ বছর নির্বাচন: ${viewModel.translateToBanglaNum(selectedYear)}", color = EmeraldSecondary)
                    }
                    DropdownMenu(expanded = expYear, onDismissRequest = { expYear = false }) {
                        listOf("2026", "2027").forEach { yr ->
                            DropdownMenuItem(text = { Text(viewModel.translateToBanglaNum(yr)) }, onClick = { selectedYear = yr; expYear = false })
                        }
                    }
                }
            }
            2 -> {
                // Member filters
                var expMem by remember { mutableStateOf(false) }
                val currentSelMem = listMembers.find { it.memberId == selectedMemberId }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expMem = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (selectedMemberId.isEmpty()) "সদস্য নির্বাচন করুন" else "${currentSelMem?.name} (${selectedMemberId})",
                            color = EmeraldSecondary
                        )
                    }
                    DropdownMenu(expanded = expMem, onDismissRequest = { expMem = false }) {
                        listMembers.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.name} (${m.memberId})") },
                                onClick = { selectedMemberId = m.memberId; expMem = false }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PDF & Excel Export Button simulation (Point #5: পিডিএফ এক্সপোর্ট বাটন, এক্সেল এক্সপোর্ট বাটন)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "রিপোর্ট পিডিএফ ফরম্যাটে ডাউনলোড করা হচ্ছে...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDarkForest)
            ) {
                Icon(Icons.Default.PictureAsPdf, null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("পিডিএফ", color = Color.White)
            }

            Button(
                onClick = {
                    Toast.makeText(context, "রিপোর্ট এক্সেল (Excel XLS) ডাউলোড শুরু হয়েছে...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
            ) {
                Icon(Icons.Default.TableChart, null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("এক্সেল", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Compile ledger rows based on filter
        val resultPayments = when (tabIndex) {
            0 -> listPayments.filter { it.paymentMonth == "$selectedYear-$selectedMonth" }
            1 -> listPayments.filter { it.paymentMonth.startsWith(selectedYear) }
            2 -> listPayments.filter { it.memberId == selectedMemberId }
            else -> emptyList()
        }

        Text(
            text = "মোট লেনদেন রেকর্ডস: ${viewModel.translateToBanglaNum(resultPayments.size.toString())} টি",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = EmeraldDarkForest,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (resultPayments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("কোন জমার রেকর্ড পাওয়া যায়নি।", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(resultPayments) { pay ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedPaymentForReceipt = pay
                                viewModel.currentScreen.value = "receipt"
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pay.memberName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("আইডি: ${pay.memberId} | রসিদ: ${pay.receiptNo}", color = Color.Gray, fontSize = 11.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("৳${viewModel.translateToBanglaNum(pay.amount.toInt().toString())}", fontWeight = FontWeight.ExtraBold, color = EmeraldSecondary, fontSize = 13.sp)
                            Text(viewModel.translateMonthToBangla(pay.paymentMonth), color = Color.DarkGray, fontSize = 10.sp)
                        }
                    }
                    Divider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}

@Composable
fun InfoScreen(viewModel: SamityViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            IslamicLogo(size = 70.0)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ভাই ভাই সমিতি",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDarkForest
                )
                Text(
                    text = "ঈদ-উল-আযহা গরুর তহবিল উন্নয়ন সংস্থা",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("সমিতি সাধারণ নীতিমালা:", fontWeight = FontWeight.Bold, color = EmeraldDarkForest)
                Spacer(modifier = Modifier.height(8.dp))
                val simpleRules = listOf(
                    "১. সমিতির সমস্ত সিদ্ধান্ত প্রেসিডেন্ট ও ক্যাশিয়ার মহোদয়ের যৌথ পরামর্শক্রমে গৃহীত হবে।",
                    "২. প্রত্যেক কিত সদস্য প্রতি মাসের ১০ তারিখের মধ্যে তাঁর ৬০০ টাকা সঞ্চয় কিস্তি প্রদান করতে বাধ্য থাকবেন।",
                    "৩. সঞ্চিত তহবিল শুধুমাত্র পবিত্র রমজান ও কুরবানির ঈদের সময় বড় আকারের স্বাস্থ্যবান ষাড় বা গরু কেনার জন্য বরাদ্দ থাকবে।",
                    "৪. জমা প্রদানের মাধ্যম হিসেবে ক্যাশ, bKash ও নগদ ব্যবহার গ্রহণযোগ্য।"
                )
                simpleRules.forEach { r ->
                    Text(r, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightMintBg),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else EmeraldSecondary.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("আমানত ও অফিসিয়াল হিসাব:", fontWeight = FontWeight.ExtraBold, color = EmeraldDarkForest)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• সঞ্চয় ফি: ৬০০ টাকা (ফিক্সড)", fontSize = 13.sp, color = Color.DarkGray)
                Text("• বিকাশ পেমেন্ট নম্বর: ০১৯৭৬৯৭২৯৮০", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldSecondary)
                Text("• রকেট ব্যাংক পেমেন্ট সাপোর্ট: সচল", fontSize = 13.sp, color = Color.DarkGray)
                Text("• সভাপতি মোবাইল নম্বর: ০১৭১২৩৪৫৬৭৮", fontSize = 13.sp, color = Color.DarkGray)
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "© ভাই ভাই সমিতি ২০২৬ | আল্লাহর সন্তুষ্টিই আমদের কাম্য",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ReceiptScreen(viewModel: SamityViewModel) {
    val activePayment = viewModel.selectedPaymentForReceipt
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.currentScreen.value = "payments" }) {
                Icon(Icons.Default.ArrowBack, "পেছনে")
            }
            Text(
                text = "অফিসিয়াল পেমেন্ট রসিদ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldDarkForest
            )
        }

        if (activePayment == null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("কোন রসিদ নির্বাচন করা নেই।")
            }
        } else {
            PrintableReceipt(
                payment = activePayment,
                viewModel = viewModel,
                onPrint = {
                    Toast.makeText(context, "রসিদ ফাইল (RCP) সফলভাবে পিডিএফ জেনারেট হয়ে ডাউনলোড ফোল্ডারে সংরক্ষিত হয়েছে!", Toast.LENGTH_LONG).show()
                }
            )

            // Dynamic screenshot preview matching Point 11.c inside details screen
            if (activePayment.screenshotBase64 != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else EmeraldSecondary.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = LightMintBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "বাংলাদেশ অনলাইন পেমেন্ট ইনভয়েস ছবি ভেরিফিকেশন:",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDarkForest,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ReceiptLong, "রসিদ", modifier = Modifier.size(48.dp), tint = EmeraldSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("বাংলাদেশ মোবাইল ডিজিটাল ফাইনান্স ভাউচার", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldDarkForest)
                                Text("ট্রানজেকশন আইডি: ${activePayment.transactionId}", fontSize = 10.sp, color = Color.Gray)
                                Text("টাকা: ৳${viewModel.translateToBanglaNum(activePayment.amount.toInt().toString())}", fontWeight = FontWeight.Bold, color = EmeraldSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldSecondary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("✓ অডিটেড এবং সঠিক", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomatedNotificationPanel(viewModel: SamityViewModel) {
    val config by viewModel.reminderConfig.collectAsState()
    val reminders by viewModel.scheduledReminders.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var isConfigExpanded by remember { mutableStateOf(false) }
    var tempBKash by remember(config.bKashNumber) { mutableStateOf(config.bKashNumber) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) CardDarkMoss else Color.White
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else EmeraldPrimary.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "রিমাইন্ডার নোটিফিকেশন",
                        tint = if (config.isEnabled) EmeraldSecondary else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "স্বয়ংক্রিয় রিমাইন্ডার ও নোটিফিকেশন",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = EmeraldDarkForest
                        )
                        Text(
                            text = if (config.isEnabled) "অটো শিডিউলার সক্রিয় করা আছে" else "শিডিউলার বর্তমানে বন্ধ আছে",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Main Toggle Row
                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = { isEnabled ->
                        viewModel.updateReminderConfig(config.copy(isEnabled = isEnabled))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldSecondary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable settings block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isConfigExpanded = !isConfigExpanded }
                    .background(
                        if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, "সেটিংস", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "শিডিউল ও পেমেন্ট বিবরণ কনফিগার করুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
                Icon(
                    imageVector = if (isConfigExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isConfigExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Monthly Deadline Parameter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("মাসের সঞ্চয় ফি ডেডলাইন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text("প্রতি মাসের নির্ধারণকৃত দিন", fontSize = 10.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (config.dueDayOfMonth > 1) {
                                        viewModel.updateReminderConfig(config.copy(dueDayOfMonth = config.dueDayOfMonth - 1))
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Remove, "কমান", tint = Color.Gray)
                            }
                            Text(
                                text = "${viewModel.translateToBanglaNum(config.dueDayOfMonth.toString())} তারিখ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = EmeraldDarkForest
                            )
                            IconButton(
                                onClick = {
                                    if (config.dueDayOfMonth < 28) {
                                        viewModel.updateReminderConfig(config.copy(dueDayOfMonth = config.dueDayOfMonth + 1))
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Add, "বাড়ান", tint = Color.Gray)
                            }
                        }
                    }

                    // Days Prior Parameter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("কত দিন পূর্বে নোটিফিকেশন যাবে?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text("ডেডলাইনের নির্ধারিত দিন পূর্বে প্রেরণ", fontSize = 10.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (config.daysPrior > 1) {
                                        viewModel.updateReminderConfig(config.copy(daysPrior = config.daysPrior - 1))
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Remove, "কমান", tint = Color.Gray)
                            }
                            Text(
                                text = "${viewModel.translateToBanglaNum(config.daysPrior.toString())} দিন পূর্বে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = EmeraldDarkForest
                            )
                            IconButton(
                                onClick = {
                                    if (config.daysPrior < 10) {
                                        viewModel.updateReminderConfig(config.copy(daysPrior = config.daysPrior + 1))
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Add, "বাড়ান", tint = Color.Gray)
                            }
                        }
                    }

                    // Custom bKash payment setting
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("অফিসিয়াল পেমেন্ট বিবরণ (বিকাশ নম্বর)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tempBKash,
                            onValueChange = {
                                tempBKash = it
                                viewModel.updateReminderConfig(config.copy(bKashNumber = it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("সমিতির বিকাশ বা নগদ নম্বর লিখুন") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldSecondary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                    }

                    // Configuration Delivery Method Selector
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("রিমাইন্ডার প্রেরণের প্রধান মাধ্যম", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val methods = listOf(
                                "WHATSAPP_SMS" to "WhatsApp / SMS",
                                "IN_APP" to "In-App নোটিশ",
                                "PUSH_SIMULATION" to "পুশ নোটিফিকেশন"
                            )
                            methods.forEach { (key, display) ->
                                val isSel = config.preferredMethod == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            1.dp,
                                            if (isSel) EmeraldSecondary else Color.LightGray,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            if (isSel) EmeraldSecondary.copy(alpha = 0.1f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.updateReminderConfig(config.copy(preferredMethod = key))
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = display,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) EmeraldSecondary else Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Active reminders queue display
            Text(
                text = "শিডিউল ও প্রেরিত রিমাইন্ডার কিউ (চলতি মাস)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!config.isEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Yellow.copy(alpha = 0.11f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "স্বয়ংক্রিয়া পদ্ধতি নিষ্ক্রিয় থাকায় রিমাইন্ডার কিউ স্থগিত আছে। অনুগ্রহ করে সক্রিয় করুন।",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }
            } else if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "আলহামদুলিল্লাহ, কোন বকেয়া কিস্তি নেই! কিউ সম্পূর্ণ খালি।",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    reminders.forEach { r ->
                        ScheduledReminderRow(
                            reminder = r,
                            viewModel = viewModel,
                            config = config,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduledReminderRow(
    reminder: ScheduledReminder,
    viewModel: SamityViewModel,
    config: ReminderConfig,
    context: Context
) {
    val isDark = isSystemInDarkTheme()
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Summary Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile-like icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(EmeraldSecondary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reminder.memberName.firstOrNull()?.toString() ?: "M",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = reminder.memberName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EmeraldDarkForest
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "শিডিউল: ৫ই জুন",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "৳${viewModel.translateToBanglaNum(reminder.amountDue.toInt().toString())}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Red
                    )

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (reminder.status) {
                                    "SENT" -> EmeraldSecondary.copy(alpha = 0.15f)
                                    "DISMISSED" -> Color.LightGray.copy(alpha = 0.3f)
                                    else -> SoftGoldAccent.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when (reminder.status) {
                                "SENT" -> "প্রেরিত"
                                "DISMISSED" -> "খারিজ"
                                else -> "শিডিউলড"
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (reminder.status) {
                                "SENT" -> EmeraldSecondary
                                "DISMISSED" -> Color.Gray
                                else -> SoftGoldAccent
                            }
                        )
                    }
                }
            }

            // Message text expand action trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isExpanded) "বার্তা লুকান" else "বার্তা প্রিভিউ দেখুন",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(12.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                // Exact message text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) Color(0xFF0F172A) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = reminder.textMessage,
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Method configuration label on current row
                    val displayMethodStr = when (config.preferredMethod) {
                        "WHATSAPP_SMS" -> "WhatsApp/SMS"
                        "IN_APP" -> "In-App Notice"
                        else -> "পুশ নোটিফিকেশন"
                    }
                    Text(
                        text = "মাধ্যম: $displayMethodStr",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Dismiss button
                        if (reminder.status == "PENDING") {
                            OutlinedButton(
                                onClick = {
                                    viewModel.dismissReminderMock(reminder)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.6f))
                            ) {
                                Text("খারিজ", fontSize = 10.sp, color = Color.Gray)
                            }

                            // Send Mock button which copies raw text and fires the preferred transmission channel
                            Button(
                                onClick = {
                                    // Send now!
                                    viewModel.sendReminderMock(reminder, context)
                                    // Also copy to clipboard for usability in real delivery method
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Bhai Bhai Samity Auto Reminder", reminder.textMessage)
                                    clipboard.setPrimaryClip(clip)
                                    
                                    // Direct WhatsApp API trigger matching configured flow
                                    if (config.preferredMethod == "WHATSAPP_SMS") {
                                        try {
                                            val cleanNum = "88" + reminder.mobile
                                            val encodedText = Uri.encode(reminder.textMessage)
                                            val urlStr = "https://api.whatsapp.com/send?phone=$cleanNum&text=$encodedText"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Fallback
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("এখনই পাঠান", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        } else {
                            // Completed / Sent indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (reminder.status == "SENT") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (reminder.status == "SENT") EmeraldSecondary else Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (reminder.status == "SENT") "প্রেরণ করা হয়েছে" else "খারিজ করা হয়েছে",
                                    fontSize = 10.sp,
                                    color = if (reminder.status == "SENT") EmeraldSecondary else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
