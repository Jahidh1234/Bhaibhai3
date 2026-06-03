package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Locale

class SamityRepository(
    private val memberDao: MemberDao,
    private val paymentDao: PaymentDao,
    private val reminderDao: ReminderDao
) {
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()
    val allPayments: Flow<List<Payment>> = paymentDao.getAllPayments()
    val reminderConfig: Flow<ReminderConfig?> = reminderDao.getReminderConfigFlow()
    val allScheduledReminders: Flow<List<ScheduledReminder>> = reminderDao.getAllScheduledRemindersFlow()

    suspend fun getReminderConfig(): ReminderConfig {
        return reminderDao.getReminderConfig() ?: ReminderConfig().also {
            reminderDao.insertReminderConfig(it)
        }
    }

    suspend fun updateReminderConfig(config: ReminderConfig) {
        reminderDao.insertReminderConfig(config)
    }

    suspend fun getScheduledReminderForMemberAndMonth(memberId: String, dueMonth: String): ScheduledReminder? {
        return reminderDao.getScheduledReminderForMemberAndMonth(memberId, dueMonth)
    }

    suspend fun insertScheduledReminder(reminder: ScheduledReminder): Long {
        return reminderDao.insertScheduledReminder(reminder)
    }

    suspend fun updateScheduledReminder(reminder: ScheduledReminder) {
        reminderDao.updateScheduledReminder(reminder)
    }

    suspend fun deleteScheduledReminder(reminder: ScheduledReminder) {
        reminderDao.deleteScheduledReminder(reminder)
    }

    suspend fun clearAllScheduledReminders() {
        reminderDao.clearAllScheduledReminders()
    }

    fun getPaymentsByMember(memberId: String): Flow<List<Payment>> {
        return paymentDao.getPaymentsByMember(memberId)
    }


    suspend fun getMemberById(memberId: String): Member? {
        return memberDao.getMemberById(memberId)
    }

    suspend fun insertMember(member: Member): Long {
        return memberDao.insertMember(member)
    }

    suspend fun updateMember(member: Member) {
        memberDao.updateMember(member)
    }

    suspend fun deleteMember(member: Member) {
        memberDao.deleteMember(member)
    }

    suspend fun clearAllMembers() {
        memberDao.clearAllMembers()
    }

    suspend fun insertPayment(payment: Payment): Long {
        return paymentDao.insertPayment(payment)
    }

    suspend fun updatePayment(payment: Payment) {
        paymentDao.updatePayment(payment)
    }

    suspend fun deletePayment(payment: Payment) {
        paymentDao.deletePayment(payment)
    }

    suspend fun clearAllPayments() {
        paymentDao.clearAllPayments()
    }

    suspend fun generateNextMemberId(): String {
        val maxId = memberDao.getMaxMemberId() ?: return "M001"
        // parse M001 -> "001" -> 1
        return try {
            val num = maxId.drop(1).toInt()
            String.format(Locale.US, "M%03d", num + 1)
        } catch (e: Exception) {
            "M001"
        }
    }

    suspend fun generateNextReceiptNo(): String {
        val maxReceipt = paymentDao.getMaxReceiptNo() ?: return "RCP-0001"
        // parse RCP-0001 -> "0001" -> 1
        return try {
            val num = maxReceipt.drop(4).toInt()
            String.format(Locale.US, "RCP-%04d", num + 1)
        } catch (e: Exception) {
            "RCP-0001"
        }
    }
}
