package com.example.data

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Sealed class for Login State
sealed class AuthState {
    object LoggedOut : AuthState()
    data class LoggedIn(val role: String, val memberId: String?, val name: String) : AuthState()
}

// Struct to represent a member's Due Info computed in memory
data class MemberDueInfo(
    val member: Member,
    val totalPaid: Double,
    val expectedMonths: List<String>, // List of YYYY-MM
    val paidMonths: List<String>,     // List of YYYY-MM
    val dueMonths: List<String>,      // YYYY-MM
    val dueAmount: Double
)

class SamityViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = SamityRepository(database.memberDao(), database.paymentDao(), database.reminderDao())

    // UI state flows
    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminderConfig: StateFlow<ReminderConfig> = repository.reminderConfig
        .map { it ?: ReminderConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderConfig())

    val scheduledReminders: StateFlow<List<ScheduledReminder>> = repository.allScheduledReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic computations combinator - calculates live due lists reactivity!
    val duesState: StateFlow<List<MemberDueInfo>> = combine(members, payments) { memberList, paymentList ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentSdf = SimpleDateFormat("yyyy-MM", Locale.US)
        
        // Let's lock current month to "2026-06" as local system is year 2026
        val currentMonthStr = "2026-06"
        val currYr = 2026
        val currMo = 6 // June

        memberList.map { member ->
            // Parse join month
            var joinYear = 2026
            var joinMo = 1
            try {
                val parts = member.joinDate.split("-")
                if (parts.size >= 2) {
                    joinYear = parts[0].toInt()
                    joinMo = parts[1].toInt()
                }
            } catch (e: Exception) {
                // fallback
            }

            // Compile expected list of months "YYYY-MM" from join month to current month
            val expected = mutableListOf<String>()
            var tempYr = joinYear
            var tempMo = joinMo

            while (tempYr < currYr || (tempYr == currYr && tempMo <= currMo)) {
                val moStr = String.format(Locale.US, "%04d-%02d", tempYr, tempMo)
                expected.add(moStr)
                tempMo++
                if (tempMo > 12) {
                    tempMo = 1
                    tempYr++
                }
            }

            // Get paid months
            val paidListForMember = paymentList.filter { it.memberId == member.memberId }
            val paidMonths = paidListForMember.map { it.paymentMonth }
            val totalPaid = paidListForMember.sumOf { it.amount }

            // Due months: expected - paid
            val dueMonths = expected.filter { it !in paidMonths }
            val dueAmount = dueMonths.size * 600.0

            MemberDueInfo(
                member = member,
                totalPaid = totalPaid,
                expectedMonths = expected,
                paidMonths = paidMonths,
                dueMonths = dueMonths,
                dueAmount = dueAmount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active auth status
    var authState = MutableStateFlow<AuthState>(AuthState.LoggedIn("PRESIDENT", null, "মো: আতিকুর রহমান (সভাপতি)"))
        private set

    // Active screen routing
    var currentScreen = mutableStateOf("dashboard") // login, dashboard, members, payments, dues, reports, info, receipt

    // Selected payment for Receipt details
    var selectedPaymentForReceipt by mutableStateOf<Payment?>(null)

    // Current countdown values in Bengali
    var countdownText by mutableStateOf("রমজান কাউন্টডাউন লোড হচ্ছে...")

    private var countdownJob: Job? = null

    // Realtime Cloud Sync status
    var syncStatus by mutableStateOf("অনলাইন সিঙ্ক করুন")
        private set

    fun syncDatabase() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            FirebaseSyncManager.performSync(repository) { status ->
                viewModelScope.launch {
                    syncStatus = status
                }
            }
        }
    }

    init {
        // Clear all data exactly once to provide a 100% clean slate, and disable default data seeding
        val prefs = application.getSharedPreferences("samity_prefs", android.content.Context.MODE_PRIVATE)
        val isFirstLaunchV4Clean = prefs.getBoolean("is_first_launch_v4_clean_complete", true)
        
        viewModelScope.launch {
            if (isFirstLaunchV4Clean) {
                repository.clearAllMembers()
                repository.clearAllPayments()
                repository.clearAllScheduledReminders()
                
                // Clear cloud records sync nodes as well to start with a pristine global backend
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        FirebaseSyncManager.deleteMember("")
                        FirebaseSyncManager.deletePayment("")
                    } catch (e: Exception) {
                        android.util.Log.e("SamityViewModel", "Failed to clear cloud during first launch wipe", e)
                    }
                }
                
                prefs.edit().putBoolean("is_first_launch_v4_clean_complete", false).apply()
            }
        }

        // Run automated scheduler trigger reactively when dues state changes
        viewModelScope.launch {
            duesState.collect {
                triggerAutomatedScheduler()
            }
        }
        startRamadanCountdown()
        // Run network database sync on launch
        syncDatabase()
    }


    // Attempt login
    fun login(email: String, role: String, specMemberId: String? = null): Boolean {
        return if (email.isNotBlank()) {
            val resolvedName = when (role) {
                "PRESIDENT" -> "মো: আতিকুর রহমান (সভাপতি)"
                "CASHIER" -> "আলহাজ্ব মো: সোলায়মান (ক্যাশিয়ার)"
                else -> {
                    "মেম্বার $specMemberId"
                }
            }
            authState.value = AuthState.LoggedIn(role, specMemberId, resolvedName)
            currentScreen.value = "dashboard"
            true
        } else {
            false
        }
    }

    fun logout() {
        authState.value = AuthState.LoggedOut
        currentScreen.value = "login"
    }

    // Seed robust sample database matching prompt #13
    private suspend fun seedDefaultData() {
        // Members
        val m1 = Member(memberId = "M001", name = "মো: আতিকুর রহমান", mobile = "01712345678", joinDate = "2026-01-10", role = "PRESIDENT")
        val m2 = Member(memberId = "M002", name = "আলহাজ্ব মো: সোলায়মান", mobile = "01887654321", joinDate = "2026-01-15", role = "CASHIER")
        val m3 = Member(memberId = "M003", name = "মো: রফিকুল ইসলাম", mobile = "01912348700", joinDate = "2026-02-05", role = "MEMBER")
        val m4 = Member(memberId = "M004", name = "ড. মো: জাফর ইকবাল", mobile = "01543218765", joinDate = "2026-03-01", role = "MEMBER")
        val m5 = Member(memberId = "M005", name = "মো: আরিফুর রহমান", mobile = "01311223344", joinDate = "2026-03-20", role = "MEMBER")
        val m6 = Member(memberId = "M006", name = "মো: আসাদুল হাসান", mobile = "01655667788", joinDate = "2026-04-01", role = "MEMBER")
        val m7 = Member(memberId = "M007", name = "মো: কামাল উদ্দিন", mobile = "01499887766", joinDate = "2026-05-01", role = "MEMBER")

        repository.insertMember(m1)
        repository.insertMember(m2)
        repository.insertMember(m3)
        repository.insertMember(m4)
        repository.insertMember(m5)
        repository.insertMember(m6)
        repository.insertMember(m7)

        // Historical sample payments - Amount: 600
        // President (M001) paid Jan, Feb, Mar, Apr, May (No due or only June due)
        repository.insertPayment(Payment(receiptNo = "RCP-0001", memberId = "M001", memberName = "মো: আতিকুর রহমান", amount = 600.0, paymentDate = "2026-01-12", paymentMonth = "2026-01", paymentMethod = "ক্যাশ", transactionId = "CASH01", note = "জানুয়ারি মাসের ফি"))
        repository.insertPayment(Payment(receiptNo = "RCP-0002", memberId = "M001", memberName = "মো: আতিকুর রহমান", amount = 600.0, paymentDate = "2026-02-14", paymentMonth = "2026-02", paymentMethod = "বিকাশ", transactionId = "BK1102A", note = ""))
        repository.insertPayment(Payment(receiptNo = "RCP-0003", memberId = "M001", memberName = "মো: আতিকুর রহমান", amount = 600.0, paymentDate = "2026-03-10", paymentMonth = "2026-03", paymentMethod = "নগদ", transactionId = "NG1103X", note = "অনলাইন পেমেন্ট"))
        repository.insertPayment(Payment(receiptNo = "RCP-0004", memberId = "M001", memberName = "মো: আতিকুর রহমান", amount = 600.0, paymentDate = "2026-04-05", paymentMonth = "2026-04", paymentMethod = "বিকাশ", transactionId = "BK1104Z", note = ""))
        repository.insertPayment(Payment(receiptNo = "RCP-0005", memberId = "M001", memberName = "মো: আতিকুর রহমান", amount = 600.0, paymentDate = "2026-05-10", paymentMonth = "2026-05", paymentMethod = "ক্যাশ", transactionId = "CASH05", note = ""))

        // Cashier (M002) paid Jan, Feb, Mar (Due: Apr, May, June)
        repository.insertPayment(Payment(receiptNo = "RCP-0006", memberId = "M002", memberName = "আলহাজ্ব মো: সোলায়মান", amount = 600.0, paymentDate = "2026-01-18", paymentMonth = "2026-01", paymentMethod = "ক্যাশ", transactionId = "CASH02", note = "রমজান ফান্ড"))
        repository.insertPayment(Payment(receiptNo = "RCP-0007", memberId = "M002", memberName = "আলহাজ্ব মো: সোলায়মান", amount = 600.0, paymentDate = "2026-02-15", paymentMonth = "2026-02", paymentMethod = "রকেট", transactionId = "RK2202", note = ""))
        repository.insertPayment(Payment(receiptNo = "RCP-0008", memberId = "M002", memberName = "আলহাজ্ব মো: সোলায়মান", amount = 600.0, paymentDate = "2026-03-12", paymentMonth = "2026-03", paymentMethod = "বিকাশ", transactionId = "BK2203Y", note = ""))

        // Member 3 (M003) paid Feb, Mar (Due: Apr, May, June)
        repository.insertPayment(Payment(receiptNo = "RCP-0009", memberId = "M003", memberName = "মো: রফিকুল ইসলাম", amount = 600.0, paymentDate = "2026-02-10", paymentMonth = "2026-02", paymentMethod = "বিকাশ", transactionId = "BK3302", note = ""))
        repository.insertPayment(Payment(receiptNo = "RCP-0010", memberId = "M003", memberName = "মো: রফিকুল ইসলাম", amount = 600.0, paymentDate = "2026-03-15", paymentMonth = "2026-03", paymentMethod = "নগদ", transactionId = "NG3303", note = ""))

        // Member 4 (M004) paid Mar (Due: Apr, May, June)
        repository.insertPayment(Payment(receiptNo = "RCP-0011", memberId = "M004", memberName = "ড. মো: জাফর ইকবাল", amount = 600.0, paymentDate = "2026-03-20", paymentMonth = "2026-03", paymentMethod = "বিকাশ", transactionId = "BK4403M", note = " গরুর ফান্ড"))

        // Member 5 (M005) paid Mar, Apr, May (Due: June)
        repository.insertPayment(Payment(receiptNo = "RCP-0012", memberId = "M005", memberName = "মো: আরিফুর রহমান", amount = 600.0, paymentDate = "2026-04-01", paymentMonth = "2026-03", paymentMethod = "ক্যাশ", transactionId = "CASH03", note = "মার্চ ফি"))
        repository.insertPayment(Payment(receiptNo = "RCP-0013", memberId = "M005", memberName = "মো: আরিফুর রহমান", amount = 600.0, paymentDate = "2026-04-12", paymentMonth = "2026-04", paymentMethod = "বিকাশ", transactionId = "BK5504", note = ""))
        repository.insertPayment(Payment(receiptNo = "RCP-0014", memberId = "M005", memberName = "মো: আরিফুর রহমান", amount = 600.0, paymentDate = "2026-05-15", paymentMonth = "2026-05", paymentMethod = "নগদ", transactionId = "NG5505", note = ""))
    }

    // Start Live Ramadan Countdown
    private fun startRamadanCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            // Next estimated Ramadan - say, February 18, 2027 18:00:00 UTC (roughly)
            val ramadanCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.FEBRUARY) // 1 means Feb in 0-indexed? No! 0=Jan, 1=Feb.
                set(Calendar.DAY_OF_MONTH, 18)
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val targetMs = ramadanCal.timeInMillis

            while (true) {
                val currentMs = System.currentTimeMillis()
                val diff = targetMs - currentMs

                if (diff <= 0) {
                    countdownText = "পবিত্র রমজান মাস শুরু হয়েছে! মোবারকবাদ!"
                    break
                } else {
                    val seconds = (diff / 1000) % 60
                    val minutes = (diff / (1000 * 60)) % 60
                    val hours = (diff / (1000 * 60 * 60)) % 24
                    val days = diff / (1000 * 60 * 60 * 24)

                    // Convert numbers to Bengali
                    countdownText = "${translateToBanglaNum(days)} দিন " +
                            "${translateToBanglaNum(hours)} ঘণ্টা " +
                            "${translateToBanglaNum(minutes)} মিনিট " +
                            "${translateToBanglaNum(seconds)} সেকেন্ড"
                }
                delay(1000)
            }
        }
    }

    // Helper translation
    fun translateToBanglaNum(num: Long): String {
        val mappedNumbers = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        return num.toString().map { mappedNumbers[it] ?: it }.joinToString("")
    }

    fun translateToBanglaNum(str: String): String {
        val mappedNumbers = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        return str.map { mappedNumbers[it] ?: it }.joinToString("")
    }

    fun translateMonthToBangla(monthYearStr: String): String {
        // "2026-03" -> "মার্চ ২০২৬"
        val parts = monthYearStr.split("-")
        if (parts.size < 2) return monthYearStr
        val yearBng = translateToBanglaNum(parts[0])
        val monthBng = when (parts[1]) {
            "01" -> "জানুয়ারি"
            "02" -> "ফেব্রুয়ারি"
            "03" -> "মার্চ"
            "04" -> "এপ্রিল"
            "05" -> "মে"
            "06" -> "জুন"
            "07" -> "জুলাই"
            "08" -> "আগস্ট"
            "09" -> "সেপ্টেম্বর"
            "10" -> "অক্টোবর"
            "11" -> "নভেম্বর"
            "12" -> "ডিসেম্বর"
            else -> parts[1]
        }
        return "$monthBng $yearBng"
    }

    // Member Operations
    fun addMember(name: String, mobile: String, joinDate: String, role: String, photoB64: String?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || mobile.isBlank()) {
                onComplete(false)
                return@launch
            }
            val mId = repository.generateNextMemberId()
            val m = Member(
                memberId = mId,
                name = name,
                mobile = mobile,
                joinDate = joinDate,
                role = role,
                photoBase64 = photoB64
            )
            repository.insertMember(m)
            // Async upload new member to firebase cloud
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                FirebaseSyncManager.uploadMember(m)
            }
            onComplete(true)
        }
    }

    fun updateMember(member: Member, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateMember(member)
            // Async upload updated member to firebase cloud
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                FirebaseSyncManager.uploadMember(member)
            }
            onComplete()
        }
    }

    fun deleteMember(member: Member, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteMember(member)
            // Async remove member from cloud
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                FirebaseSyncManager.deleteMember(member.memberId)
            }
            onComplete()
        }
    }

    // Payment Operations
    fun addPayment(
        memberId: String,
        memberName: String,
        amount: Double,
        date: String,
        paymentMonth: String,
        method: String,
        txnId: String,
        note: String,
        screenshotB64: String?,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            if (memberId.isBlank() || paymentMonth.isBlank()) {
                onComplete(false)
                return@launch
            }
            val rcNo = repository.generateNextReceiptNo()
            val pay = Payment(
                receiptNo = rcNo,
                memberId = memberId,
                memberName = memberName,
                amount = amount,
                paymentDate = date,
                paymentMonth = paymentMonth,
                paymentMethod = method,
                transactionId = txnId,
                note = note,
                screenshotBase64 = screenshotB64
            )
            val insertedId = repository.insertPayment(pay)
            val updatedPay = pay.copy(id = insertedId.toInt())
            selectedPaymentForReceipt = updatedPay
            // Async upload new payment receipt to firebase cloud
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                FirebaseSyncManager.uploadPayment(updatedPay)
            }
            onComplete(true)
        }
    }

    fun deletePayment(payment: Payment, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deletePayment(payment)
            // Async delete payment receipt from firebase cloud
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                FirebaseSyncManager.deletePayment(payment.receiptNo)
            }
            onComplete()
        }
    }

    fun updateReminderConfig(config: ReminderConfig) {
        viewModelScope.launch {
            repository.updateReminderConfig(config)
            triggerAutomatedScheduler()
        }
    }

    fun sendReminderMock(reminder: ScheduledReminder, context: android.content.Context) {
        viewModelScope.launch {
            repository.insertScheduledReminder(reminder.copy(status = "SENT", lastUpdated = System.currentTimeMillis()))
            Toast.makeText(context, "রিমাইন্ডার বিজ্ঞপ্তিসমূহ সফলভাবে প্রেরণ করা হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    fun dismissReminderMock(reminder: ScheduledReminder) {
        viewModelScope.launch {
            repository.insertScheduledReminder(reminder.copy(status = "DISMISSED", lastUpdated = System.currentTimeMillis()))
        }
    }

    fun triggerAutomatedScheduler() {
        viewModelScope.launch {
            val config = repository.getReminderConfig()
            if (!config.isEnabled) return@launch

            // Current target is June 2026 ("2026-06")
            val targetMonth = "2026-06"
            val dueDay = config.dueDayOfMonth
            val daysPrior = config.daysPrior
            
            // Due date: 2026-06-10 (assuming month is June)
            val dueCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2026)
                set(Calendar.MONTH, Calendar.JUNE)
                set(Calendar.DAY_OF_MONTH, dueDay)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            
            val reminderCalendar = Calendar.getInstance().apply {
                time = dueCalendar.time
                add(Calendar.DAY_OF_MONTH, -daysPrior)
            }
            
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val scheduledDateStr = sdf.format(reminderCalendar.time) // e.g. "2026-06-05"

            // Get computed dues list
            duesState.value.forEach { dueInfo ->
                val member = dueInfo.member
                
                // check if they have a due for this month ("2026-06") or overall dues
                val hasDues = dueInfo.dueMonths.contains(targetMonth) || dueInfo.dueAmount > 0
                
                if (hasDues) {
                    val existing = repository.getScheduledReminderForMemberAndMonth(member.memberId, targetMonth)
                    val formattedDueAmount = dueInfo.dueAmount.toInt()
                    
                    val textMessage = "আসসালামু আলাইকুম ${member.name} ভাই, ভাই ভাই সমিতির আগামী ${translateToBanglaNum(dueDay.toString())} জুনের কিস্তির মোট ${translateToBanglaNum(formattedDueAmount.toString())} টাকা বকেয়া আছে। অনুগ্রহ পূর্বক সমিতির বিকাশ ${config.bKashNumber} নম্বরে সেন্ড মানি করুন। ধন্যবাদ।"
                    
                    if (existing == null) {
                        val newReminder = ScheduledReminder(
                            memberId = member.memberId,
                            memberName = member.name,
                            mobile = member.mobile,
                            amountDue = dueInfo.dueAmount,
                            dueMonth = targetMonth,
                            scheduledDate = scheduledDateStr,
                            textMessage = textMessage,
                            status = "PENDING"
                        )
                        repository.insertScheduledReminder(newReminder)
                    } else if (existing.status == "PENDING" && (existing.amountDue != dueInfo.dueAmount || existing.textMessage != textMessage)) {
                        repository.insertScheduledReminder(
                            existing.copy(
                                amountDue = dueInfo.dueAmount,
                                textMessage = textMessage,
                                lastUpdated = System.currentTimeMillis()
                            )
                        )
                    }
                } else {
                    // Paid up! Mark any scheduled reminder as DISMISSED
                    val existing = repository.getScheduledReminderForMemberAndMonth(member.memberId, targetMonth)
                    if (existing != null && existing.status == "PENDING") {
                        repository.insertScheduledReminder(existing.copy(status = "DISMISSED"))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
