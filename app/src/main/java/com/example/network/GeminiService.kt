package com.example.network

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .build()

    /**
     * Generates text response using Gemini 3.5-flash.
     * Returns empty string if the API key is unconfigured or a placeholder.
     */
    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.w("GeminiService", "Gemini API key is unconfigured or is the default placeholder.")
            return@withContext ""
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        try {
            val requestJson = JSONObject()
            
            // Contents
            val contentsArray = JSONArray()
            val contentsObject = JSONObject()
            val partsArray = JSONArray()
            val textPart = JSONObject()
            textPart.put("text", prompt)
            partsArray.put(textPart)
            contentsObject.put("parts", partsArray)
            contentsArray.put(contentsObject)
            requestJson.put("contents", contentsArray)

            // System Instruction
            if (!systemInstruction.isNullOrEmpty()) {
                val sysInstructionObject = JSONObject()
                val sysPartsArray = JSONArray()
                val sysTextPart = JSONObject()
                sysTextPart.put("text", systemInstruction)
                sysPartsArray.put(sysTextPart)
                sysInstructionObject.put("parts", sysPartsArray)
                requestJson.put("systemInstruction", sysInstructionObject)
            }

            // Small config for structured text
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            requestJson.put("generationConfig", generationConfig)

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("GeminiService", "Gemini API call failed: Code ${response.code} / ${response.message}")
                    return@withContext ""
                }
                
                val responseBodyStr = response.body?.string() ?: return@withContext ""
                val responseJson = JSONObject(responseBodyStr)
                val candidatesArray = responseJson.optJSONArray("candidates")
                val firstCandidate = candidatesArray?.optJSONObject(0)
                val contentObj = firstCandidate?.optJSONObject("content")
                val partsArr = contentObj?.optJSONArray("parts")
                val firstPartObj = partsArr?.optJSONObject(0)
                return@withContext firstPartObj?.optString("text") ?: ""
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception in Gemini API communication", e)
            return@withContext ""
        }
    }
}
