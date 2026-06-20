package com.deepseek.balance.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deepseek.balance.model.BalanceUiState
import com.deepseek.balance.network.DeepSeekApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("deepseek_balance", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    init {
        // 启动时读取本地存储的 API Key
        val savedKey = prefs.getString("api_key", "") ?: ""
        if (savedKey.isNotBlank()) {
            _uiState.update { it.copy(apiKey = savedKey, hasApiKey = true) }
            // 自动查询余额
            refreshBalance()
        }
    }

    /**
     * 查询余额
     */
    fun refreshBalance() {
        val currentState = _uiState.value
        if (currentState.apiKey.isBlank()) {
            _uiState.update { it.copy(error = "请先设置 API Key") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = DeepSeekApiService.getBalance(currentState.apiKey)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        balance = response,
                        error = null,
                        lastUpdated = System.currentTimeMillis(),
                        hasApiKey = true
                    )
                }
            } catch (e: SecurityException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "API Key 无效，请检查后重新输入"
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "网络连接失败：${e.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "未知错误：${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 保存 API Key
     */
    fun saveApiKey(key: String) {
        val trimmedKey = key.trim()
        prefs.edit().putString("api_key", trimmedKey).apply()
        _uiState.update { it.copy(apiKey = trimmedKey, hasApiKey = true, error = null) }
        refreshBalance()
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * 切换余额显示/隐藏
     */
    fun toggleBalanceVisibility() {
        _uiState.update { it.copy(isBalanceHidden = !it.isBalanceHidden) }
    }
}
