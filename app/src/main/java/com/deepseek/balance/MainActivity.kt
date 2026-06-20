package com.deepseek.balance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deepseek.balance.ui.screen.BalanceScreen
import com.deepseek.balance.ui.screen.SettingsDialog
import com.deepseek.balance.ui.theme.DeepSeekBalanceTheme
import com.deepseek.balance.ui.theme.DarkNavy
import com.deepseek.balance.viewmodel.BalanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeepSeekBalanceTheme {
                DeepSeekBalanceApp()
            }
        }
    }
}

@Composable
private fun DeepSeekBalanceApp() {
    val viewModel: BalanceViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy),
        contentAlignment = Alignment.Center
    ) {
        // 主界面（始终显示）
        BalanceScreen(
            uiState = uiState,
            onRefresh = viewModel::refreshBalance,
            onOpenSettings = { showSettings = true },
            onToggleVisibility = viewModel::toggleBalanceVisibility,
            onClearError = viewModel::clearError
        )

        // 设置弹窗
        AnimatedVisibility(
            visible = showSettings,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut() + slideOutVertically { it / 4 }
        ) {
            // 半透明遮罩
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .then(
                        Modifier.background(Color.Transparent)
                    ),
                contentAlignment = Alignment.Center
            ) {
                SettingsDialog(
                    currentApiKey = uiState.apiKey,
                    onSave = { key ->
                        viewModel.saveApiKey(key)
                        showSettings = false
                    },
                    onDismiss = { showSettings = false }
                )
            }
        }
    }
}
