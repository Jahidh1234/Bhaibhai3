package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDarkForest
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.SoftGoldAccent
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun IslamicLogo(modifier: Modifier = Modifier, size: Double = 80.0) {
    Image(
        painter = painterResource(id = R.drawable.samity_logo_1780452695033),
        contentDescription = "ভাই ভাই সমিতি লোগো",
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
    )
}

@Composable
fun IslamicBackgroundPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 1f
        val interval = 60.dp.toPx()
        val c = Color(0x0610B981) // very faint green

        for (x in 0..(size.width / interval).toInt()) {
            for (y in 0..(size.height / interval).toInt()) {
                val cx = x * interval
                val cy = y * interval
                // draw lightweight overlapping diamonds representing classic geometric grids
                drawRect(
                    color = c,
                    topLeft = Offset(cx - interval/3, cy - interval/3),
                    size = Size(interval/1.5f, interval/1.5f),
                    style = Stroke(strokeWidth)
                )
                drawLine(
                    color = c,
                    start = Offset(cx, cy - interval/2),
                    end = Offset(cx, cy + interval/2),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = c,
                    start = Offset(cx - interval/2, cy),
                    end = Offset(cx + interval/2, cy),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
fun CustomProgressRing(
    progress: Float,
    targetAmount: Double,
    currentAmount: Double,
    viewModel: com.example.data.SamityViewModel,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1200),
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "রমজান গরুর তহবিলের অগ্রগতি",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldDarkForest,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    // Background Ring
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        radius = size.minDimension / 2 - strokeWidth / 2,
                        style = Stroke(strokeWidth)
                    )
                    // Progress Arc
                    drawArc(
                        color = EmeraldPrimary,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        topLeft = Offset(strokeWidth/2, strokeWidth/2),
                        style = Stroke(strokeWidth)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${viewModel.translateToBanglaNum(String.format("%.1f", progress * 100))}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDarkForest
                    )
                    Text(
                        text = "সংগৃহীত তহবিল",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "মোট টার্গেট:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(
                        text = "৳ ${viewModel.translateToBanglaNum(String.format("%,.0f", targetAmount))}",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDarkForest,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "বর্তমান জমা:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(
                        text = "৳ ${viewModel.translateToBanglaNum(String.format("%,.0f", currentAmount))}",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = EmeraldPrimary,
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun CustomBarChart(
    data: Map<String, Double>, // e.g., "Jan 2026" -> 2400.0
    viewModel: com.example.data.SamityViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "গত ৬ মাসের জমার প্রবণতা",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldDarkForest,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (data.isEmpty()) {
                Text(
                    text = "কোন লেনদেন ডেমো পাওয়া যায়নি।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                val maxVal = data.values.maxOrNull() ?: 1.0
                val sortedKeys = data.keys.sorted() // Sorted chronology

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    sortedKeys.forEach { key ->
                        val amount = data[key] ?: 0.0
                        val rawRatio = (amount / maxVal).toFloat()
                        val animatedRatio by animateFloatAsState(
                            targetValue = rawRatio,
                            animationSpec = tween(1000),
                            label = "barRatio"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${viewModel.translateToBanglaNum(amount.toInt().toString())}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSecondary,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.75f * animatedRatio)
                                    .width(28.dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(EmeraldSecondary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val cleanLabel = viewModel.translateMonthToBangla(key).replace(" ২০২৬", "")
                            Text(
                                text = cleanLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldDarkForest,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrintableReceipt(
    payment: com.example.data.Payment,
    viewModel: com.example.data.SamityViewModel,
    onPrint: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, EmeraldDarkForest, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IslamicLogo(size = 48.0)
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "ভাই ভাই সমিতি",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDarkForest
                    )
                    Text(
                        text = "ইসলামিক উন্নয়নমূলক সঞ্চয় তহবিল",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = "জমা আদায় রসিদ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SoftGoldAccent,
                modifier = Modifier
                    .border(1.dp, SoftGoldAccent, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Details list
            val fields = listOf(
                "রসিদ নম্বর" to payment.receiptNo,
                "সদস্য আইডি" to payment.memberId,
                "সদস্যের নাম" to payment.memberName,
                "জমার মাস" to viewModel.translateMonthToBangla(payment.paymentMonth),
                "জমার তারিখ" to viewModel.translateToBanglaNum(payment.paymentDate),
                "জমার মাধ্যম" to payment.paymentMethod,
                "ট্রানজেকশন আইডি" to (if (payment.transactionId.isBlank()) "নেই" else payment.transactionId),
                "জমার পরিমাণ" to "৳ ${viewModel.translateToBanglaNum(payment.amount.toInt().toString())}"
            )

            fields.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = value,
                        color = EmeraldDarkForest,
                        fontWeight = if (label == "জমার পরিমাণ") FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (payment.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "নোট: ",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = payment.note,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dotted separator line
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                val dotRadius = 1.5.dp.toPx()
                val gap = 12.dp.toPx()
                var currentX = 0f
                while (currentX < size.width) {
                    drawCircle(
                        color = Color.LightGray,
                        radius = dotRadius,
                        center = Offset(currentX, size.height / 2)
                    )
                    currentX += gap
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signature area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Divider(color = Color.DarkGray, modifier = Modifier.width(100.dp))
                    Text(
                        text = "আদায়কারীর স্বাক্ষর",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Divider(color = Color.DarkGray, modifier = Modifier.width(100.dp))
                    Text(
                        text = "সমিতির সিল",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPrint,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
            ) {
                Text("রসিদ প্রিন্ট বা ডাউনলোড করুন", color = Color.White)
            }
        }
    }
}
