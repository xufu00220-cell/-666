package com.deepseek.balance.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * 单条余额信息（DeepSeek 支持多币种，一般只有 CNY）
 */
data class BalanceInfo(
    val currency: String,
    val totalBalance: Double,
    val toppedUpBalance: Double,
    val grantedBalance: Double
)

/**
 * DeepSeek /user/balance 接口返回体
 */
data class BalanceResponse(
    val isAvailable: Boolean,
    val balanceInfos: List<BalanceInfo>
) {
    companion object {
        fun fromJson(json: String): BalanceResponse {
            val obj = JSONObject(json)
            val isAvailable = obj.optBoolean("is_available", false)
            val arr: JSONArray = obj.optJSONArray("balance_infos") ?: JSONArray()
            val list = mutableListOf<BalanceInfo>()
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                list.add(
                    BalanceInfo(
                        currency = item.optString("currency", "CNY"),
                        totalBalance = item.optDouble("total_balance", 0.0),
                        toppedUpBalance = item.optDouble("topped_up_balance", 0.0),
                        grantedBalance = item.optDouble("granted_balance", 0.0)
                    )
                )
            }
            return BalanceResponse(isAvailable, list)
        }
    }
}

/**
 * 前端 UI 状态
 */
data class BalanceUiState(
    val isLoading: Boolean = false,
    val balance: BalanceResponse? = null,
    val error: String? = null,
    val lastUpdated: Long = 0L,
    val apiKey: String = "",
    val hasApiKey: Boolean = false,
    val isBalanceHidden: Boolean = false
)
