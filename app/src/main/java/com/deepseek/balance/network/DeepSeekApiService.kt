package com.deepseek.balance.network

import com.deepseek.balance.model.BalanceResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * DeepSeek API 服务 —— 查询账户余额
 *
 * 接口文档：
 *   GET https://api.deepseek.com/user/balance
 *   Header: Authorization: Bearer {API_KEY}
 *
 * 响应示例：
 * {
 *   "is_available": true,
 *   "balance_infos": [
 *     {
 *       "currency": "CNY",
 *       "total_balance": "86.53",
 *       "topped_up_balance": "86.53",
 *       "granted_balance": "0.00"
 *     }
 *   ]
 * }
 */
object DeepSeekApiService {

    private const val BASE_URL = "https://api.deepseek.com"
    private const val BALANCE_PATH = "/user/balance"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * 查询余额
     * @param apiKey DeepSeek API Key（sk-xxx）
     * @return BalanceResponse 余额信息
     * @throws IOException 网络异常
     * @throws SecurityException 认证失败（401/403）
     */
    suspend fun getBalance(apiKey: String): BalanceResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL$BALANCE_PATH")
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "application/json")
            .get()
            .build()

        val response = client.newCall(request).execute()

        when (response.code) {
            200 -> {
                val body = response.body?.string()
                    ?: throw IOException("响应体为空")
                BalanceResponse.fromJson(body)
            }
            401, 403 -> {
                throw SecurityException("API Key 无效或权限不足")
            }
            429 -> {
                throw IOException("请求过于频繁，请稍后再试")
            }
            else -> {
                throw IOException("服务器异常 (${response.code})")
            }
        }
    }
}
