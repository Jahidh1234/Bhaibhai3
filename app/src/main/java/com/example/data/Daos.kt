package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY memberId ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE memberId = :memberId LIMIT 1")
    suspend fun getMemberById(memberId: String): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("DELETE FROM members")
    suspend fun clearAllMembers()

    @Query("SELECT MAX(memberId) FROM members")
    suspend fun getMaxMemberId(): String?
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC, id DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE memberId = :memberId ORDER BY paymentMonth DESC")
    fun getPaymentsByMember(memberId: String): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("DELETE FROM payments")
    suspend fun clearAllPayments()

    @Query("SELECT MAX(receiptNo) FROM payments")
    suspend fun getMaxReceiptNo(): String?
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder_configs WHERE id = 1 LIMIT 1")
    fun getReminderConfigFlow(): Flow<ReminderConfig?>

    @Query("SELECT * FROM reminder_configs WHERE id = 1 LIMIT 1")
    suspend fun getReminderConfig(): ReminderConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderConfig(config: ReminderConfig)

    @Query("SELECT * FROM scheduled_reminders ORDER BY lastUpdated DESC")
    fun getAllScheduledRemindersFlow(): Flow<List<ScheduledReminder>>

    @Query("SELECT * FROM scheduled_reminders WHERE memberId = :memberId AND dueMonth = :dueMonth LIMIT 1")
    suspend fun getScheduledReminderForMemberAndMonth(memberId: String, dueMonth: String): ScheduledReminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledReminder(reminder: ScheduledReminder): Long

    @Update
    suspend fun updateScheduledReminder(reminder: ScheduledReminder)

    @Delete
    suspend fun deleteScheduledReminder(reminder: ScheduledReminder)

    @Query("DELETE FROM scheduled_reminders")
    suspend fun clearAllScheduledReminders()
}

