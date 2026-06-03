package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: String,       // format: M001, M002, M003
    val name: String,
    val mobile: String,
    val joinDate: String,       // "YYYY-MM-DD" style
    val role: String,           // "PRESIDENT", "CASHIER", "MEMBER"
    val photoBase64: String? = null // Base64 profile photo (optional)
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNo: String,      // format: RCP-0001, RCP-0002
    val memberId: String,       // format: M001
    val memberName: String,     // cache member name
    val amount: Double = 600.0,  // Always 600 taka
    val paymentDate: String,    // "YYYY-MM-DD"
    val paymentMonth: String,   // "YYYY-MM" (e.g. "2023-01", "2024-05")
    val paymentMethod: String,  // "বিকাশ", "নগদ", "রকেট", "ক্যাশ"
    val transactionId: String,
    val note: String,
    val screenshotBase64: String? = null // Optional bKash payment screenshot (Base64)
)

@Entity(tableName = "reminder_configs")
data class ReminderConfig(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean = true,
    val dueDayOfMonth: Int = 10,
    val daysPrior: Int = 5,
    val bKashNumber: String = "০১৯৭৬৯৭২৯৮০",
    val preferredMethod: String = "WHATSAPP_SMS" // WHATSAPP_SMS, IN_APP, PUSH_SIMULATION
)

@Entity(tableName = "scheduled_reminders")
data class ScheduledReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: String,
    val memberName: String,
    val mobile: String,
    val amountDue: Double,
    val dueMonth: String, // format: YYYY-MM
    val scheduledDate: String, // format: YYYY-MM-DD
    val textMessage: String,
    val status: String, // "PENDING", "SENT", "DISMISSED"
    val lastUpdated: Long = System.currentTimeMillis()
)

