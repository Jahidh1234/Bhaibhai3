package com.example.data

import android.content.Context
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"
    
    // Public Firebase Realtime Database instance with open security rules for real-time collaboration out-of-the-box
    private const val BASE_URL = "https://bhaibhaisamity-2026-default-rtdb.firebaseio.com/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Bidirectional Sync function:
     * 1. Fetches cloud records (members, payments)
     * 2. Merges with local Room DB
     * 3. Uploads local-only records to cloud
     */
    suspend fun performSync(repository: SamityRepository, onStatusUpdate: (String) -> Unit = {}) {
        try {
            onStatusUpdate("ক্লাউড থেকে তথ্য ডাউনলোড করা হচ্ছে...")
            
            // ------------------ SYNC MEMBERS ------------------
            val cloudMembersMap = fetchCloudMembers()
            val localMembers = mutableListOf<Member>()
            repository.allMembers.collect { localList ->
                localMembers.addAll(localList)
                return@collect // collect once
            }

            // Sync down: Insert any cloud member not in local database
            for ((mId, cloudMember) in cloudMembersMap) {
                val existsLocally = localMembers.any { it.memberId == mId }
                if (!existsLocally) {
                    repository.insertMember(cloudMember)
                    localMembers.add(cloudMember)
                }
            }

            // Sync up: Upload local members not on cloud
            for (localMember in localMembers) {
                if (!cloudMembersMap.containsKey(localMember.memberId)) {
                    uploadMember(localMember)
                }
            }

            // ------------------ SYNC PAYMENTS ------------------
            onStatusUpdate("পেমেন্ট ও হিসাব বিবরণী মেলানো হচ্ছে...")
            val cloudPaymentsMap = fetchCloudPayments()
            val localPayments = mutableListOf<Payment>()
            repository.allPayments.collect { localList ->
                localPayments.addAll(localList)
                return@collect // collect once
            }

            // Sync down: Insert any cloud payment not in local database
            for ((rcNo, cloudPayment) in cloudPaymentsMap) {
                val existsLocally = localPayments.any { it.receiptNo == rcNo }
                if (!existsLocally) {
                    repository.insertPayment(cloudPayment)
                    localPayments.add(cloudPayment)
                }
            }

            // Sync up: Upload local payments not on cloud
            for (localPayment in localPayments) {
                if (!cloudPaymentsMap.containsKey(localPayment.receiptNo)) {
                    uploadPayment(localPayment)
                }
            }

            onStatusUpdate("অনলাইন সফলভাবে সিঙ্ক হয়েছে!")
        } catch (e: Exception) {
            Log.e(TAG, "Error performing sync: ", e)
            onStatusUpdate("সিঙ্ক ব্যর্থ হয়েছে। অনুগ্রহ করে ইন্টারনেট সংযোগ পরীক্ষা করুন।")
        }
    }

    // Helper: Upload Single Member to Cloud
    suspend fun uploadMember(member: Member) {
        try {
            val memberJson = JSONObject().apply {
                put("memberId", member.memberId)
                put("name", member.name)
                put("mobile", member.mobile)
                put("joinDate", member.joinDate)
                put("role", member.role)
                if (member.photoBase64 != null) {
                    put("photoBase64", member.photoBase64)
                }
            }

            val body = memberJson.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("${BASE_URL}members/${member.memberId}.json")
                .put(body)
                .build()

            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading member: ${member.memberId}", e)
        }
    }

    // Helper: Delete Single Member from Cloud
    suspend fun deleteMember(memberId: String) {
        try {
            val request = Request.Builder()
                .url("${BASE_URL}members/${memberId}.json")
                .delete()
                .build()

            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting member: $memberId", e)
        }
    }

    // Helper: Upload Single Payment to Cloud
    suspend fun uploadPayment(payment: Payment) {
        try {
            val paymentJson = JSONObject().apply {
                put("receiptNo", payment.receiptNo)
                put("memberId", payment.memberId)
                put("memberName", payment.memberName)
                put("amount", payment.amount)
                put("paymentDate", payment.paymentDate)
                put("paymentMonth", payment.paymentMonth)
                put("paymentMethod", payment.paymentMethod)
                put("transactionId", payment.transactionId)
                put("note", payment.note)
                if (payment.screenshotBase64 != null) {
                    put("screenshotBase64", payment.screenshotBase64)
                }
            }

            val body = paymentJson.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("${BASE_URL}payments/${payment.receiptNo}.json")
                .put(body)
                .build()

            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading payment: ${payment.receiptNo}", e)
        }
    }

    // Helper: Delete Single Payment from Cloud
    suspend fun deletePayment(receiptNo: String) {
        try {
            val request = Request.Builder()
                .url("${BASE_URL}payments/${receiptNo}.json")
                .delete()
                .build()

            client.newCall(request).execute().close()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting payment: $receiptNo", e)
        }
    }

    // Fetch lists helper: Members
    private fun fetchCloudMembers(): Map<String, Member> {
        val membersMap = mutableMapOf<String, Member>()
        try {
            val request = Request.Builder()
                .url("${BASE_URL}members.json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank() && bodyString != "null") {
                        val jsonObject = JSONObject(bodyString)
                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val mJson = jsonObject.getJSONObject(key)
                            val mId = mJson.getString("memberId")
                            val name = mJson.getString("name")
                            val mobile = mJson.getString("mobile")
                            val joinDate = mJson.getString("joinDate")
                            val role = mJson.getString("role")
                            val photoB64 = if (mJson.has("photoBase64")) mJson.optString("photoBase64") else null

                            membersMap[mId] = Member(
                                memberId = mId,
                                name = name,
                                mobile = mobile,
                                joinDate = joinDate,
                                role = role,
                                photoBase64 = if (photoB64 == "null") null else photoB64
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch cloud members", e)
        }
        return membersMap
    }

    // Fetch lists helper: Payments
    private fun fetchCloudPayments(): Map<String, Payment> {
        val paymentsMap = mutableMapOf<String, Payment>()
        try {
            val request = Request.Builder()
                .url("${BASE_URL}payments.json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank() && bodyString != "null") {
                        val jsonObject = JSONObject(bodyString)
                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val pJson = jsonObject.getJSONObject(key)
                            val rcNo = pJson.getString("receiptNo")
                            val mId = pJson.getString("memberId")
                            val mName = pJson.getString("memberName")
                            val amount = pJson.getDouble("amount")
                            val pDate = pJson.getString("paymentDate")
                            val pMonth = pJson.getString("paymentMonth")
                            val pMethod = pJson.getString("paymentMethod")
                            val txnId = pJson.getString("transactionId")
                            val note = pJson.getString("note")
                            val ssB64 = if (pJson.has("screenshotBase64")) pJson.optString("screenshotBase64") else null

                            paymentsMap[rcNo] = Payment(
                                receiptNo = rcNo,
                                memberId = mId,
                                memberName = mName,
                                amount = amount,
                                paymentDate = pDate,
                                paymentMonth = pMonth,
                                paymentMethod = pMethod,
                                transactionId = txnId,
                                note = note,
                                screenshotBase64 = if (ssB64 == "null") null else ssB64
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch cloud payments", e)
        }
        return paymentsMap
    }
}
