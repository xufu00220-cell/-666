  package com.deepseek.balance.ui.screen     
  import androidx.compose.animation.AnimatedVisibility
  import androidx.compose.animation.core.Animatable
  import androidx.compose.animation.core.animateFloatAsState
  import androidx.compose.animation.core.FastOutSlowInEasing      
  import androidx.compose.animation.core.RepeatMode
  import androidx.compose.animation.core.animateFloat
  import androidx.compose.animation.core.infiniteRepeatable
  import
  androidx.compose.animation.core.rememberInfiniteTransition
  import androidx.compose.animation.core.tween
  import androidx.compose.animation.fadeIn
  import androidx.compose.animation.fadeOut
  import androidx.compose.animation.slideInVertically
  import androidx.compose.foundation.background
  import androidx.compose.foundation.border
  import androidx.compose.foundation.clickable
  import androidx.compose.foundation.layout.Arrangement
  import androidx.compose.foundation.layout.Box
  import androidx.compose.foundation.layout.Column
  import androidx.compose.foundation.layout.Row
  import androidx.compose.foundation.layout.Spacer
  import androidx.compose.foundation.layout.fillMaxSize
  import androidx.compose.foundation.layout.fillMaxWidth
  import androidx.compose.foundation.layout.height
  import androidx.compose.foundation.layout.padding
  import androidx.compose.foundation.layout.size
  import androidx.compose.foundation.layout.width
  import androidx.compose.foundation.rememberScrollState
  import androidx.compose.foundation.shape.CircleShape
  import androidx.compose.foundation.shape.RoundedCornerShape     
  import androidx.compose.foundation.verticalScroll
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.Key
  import androidx.compose.material.icons.filled.Refresh
  import androidx.compose.material.icons.filled.Settings
  import androidx.compose.material.icons.filled.Visibility        
  import androidx.compose.material.icons.filled.VisibilityOff     
  import androidx.compose.material.icons.rounded.Circle
  import androidx.compose.material.icons.rounded.CurrencyYuan     
  import androidx.compose.material.icons.rounded.Redeem
  import androidx.compose.material.icons.rounded.Savings
  import androidx.compose.material3.Card
  import androidx.compose.material3.CardDefaults
  import androidx.compose.material3.CircularProgressIndicator     
  import androidx.compose.material3.Icon
  import androidx.compose.material3.IconButton
  import androidx.compose.material3.MaterialTheme
  import androidx.compose.material3.Text
  import androidx.compose.runtime.Composable
  import androidx.compose.runtime.LaunchedEffect
  import androidx.compose.runtime.getValue
  import androidx.compose.runtime.mutableStateOf
  import androidx.compose.runtime.remember
  import androidx.compose.runtime.setValue
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.blur
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.draw.shadow
  import androidx.compose.ui.geometry.Offset
  import androidx.compose.ui.graphics.Brush
  import androidx.compose.ui.graphics.Color
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextAlign
  import androidx.compose.ui.unit.dp
  import com.deepseek.balance.model.BalanceUiState
  import com.deepseek.balance.ui.theme.CardGradientEnd
  import com.deepseek.balance.ui.theme.CardGradientStart
  import com.deepseek.balance.ui.theme.CoralAccent
  import com.deepseek.balance.ui.theme.DarkNavy
  import com.deepseek.balance.ui.theme.DeepBlue
  import com.deepseek.balance.ui.theme.GlassBorder
  import com.deepseek.balance.ui.theme.GlassWhite
  import com.deepseek.balance.ui.theme.GlassWhiteStrong
  import com.deepseek.balance.ui.theme.GoldAccent
  import com.deepseek.balance.ui.theme.MidnightCard
  import com.deepseek.balance.ui.theme.TealAccent
  import com.deepseek.balance.ui.theme.TextPrimary
  import com.deepseek.balance.ui.theme.TextSecondary
  import com.deepseek.balance.ui.theme.TextTertiary
  import java.text.SimpleDateFormat
  import java.util.Date
  import java.util.Locale
  import kotlin.math.abs

  // ─────────────────────────────────────────────
  // 主屏幕
  // ─────────────────────────────────────────────

  @Composable
  fun BalanceScreen(
      uiState: BalanceUiState,
      onRefresh: () -> Unit,
      onOpenSettings: () -> Unit,
      onToggleVisibility: () -> Unit,
      onClearError: () -> Unit
  ) {
      val scrollState = rememberScrollState()

      Box(
          modifier = Modifier
              .fillMaxSize()
              .background(
                  brush = Brush.verticalGradient(
                      colors = listOf(DarkNavy, CardGradientEnd,  
  DarkNavy)
                  )
              )
      ) {
          // 背景装饰光斑
          BackgroundGlow()

          Column(
              modifier = Modifier
                  .fillMaxSize()
                  .verticalScroll(scrollState)
                  .padding(horizontal = 20.dp)
                  .padding(top = 48.dp, bottom = 32.dp),
              horizontalAlignment = Alignment.CenterHorizontally  
          ) {
              // ── 顶部栏 ──
              TopBar(
                  onSettingsClick = onOpenSettings,
                  onToggleVisibility = onToggleVisibility,        
                  isHidden = uiState.isBalanceHidden
              )

              Spacer(modifier = Modifier.height(24.dp))

              // ── 主余额卡片 ──
              MainBalanceCard(
                  isLoading = uiState.isLoading,
                  balance = uiState.balance,
                  isHidden = uiState.isBalanceHidden,
                  error = uiState.error,
                  onClearError = onClearError
              )

              Spacer(modifier = Modifier.height(20.dp))

              // ── 余额明细 ──
              AnimatedVisibility(
                  visible = uiState.balance != null &&
  !uiState.isLoading,
                  enter = fadeIn() + slideInVertically { it / 2 },
                  exit = fadeOut()
              ) {
                  Column {
                      BalanceDetailCards(
                          balance = uiState.balance,
                          isHidden = uiState.isBalanceHidden      
                      )

                      Spacer(modifier = Modifier.height(20.dp))   

                      // ── 使用进度条 ──
                      UsageProgressBar(
                          balance = uiState.balance,
                          isHidden = uiState.isBalanceHidden      
                      )
                  }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // ── 底部：上次更新时间 + 刷新按钮 ──
              FooterSection(
                  lastUpdated = uiState.lastUpdated,
                  isLoading = uiState.isLoading,
                  hasApiKey = uiState.hasApiKey,
                  onRefresh = onRefresh
              )

              Spacer(modifier = Modifier.height(16.dp))

              // ── 没有 API Key 时的提示 ──
              AnimatedVisibility(
                  visible = !uiState.hasApiKey,
                  enter = fadeIn(),
                  exit = fadeOut()
              ) {
                  SetupPrompt(onClick = onOpenSettings)
              }
          }
      }
  }

  // ─────────────────────────────────────────────
  // 背景光斑
  // ─────────────────────────────────────────────

  @Composable
  private fun BackgroundGlow() {
      val infiniteTransition = rememberInfiniteTransition(label = 
  "glow")
      val glowAlpha by infiniteTransition.animateFloat(
          initialValue = 0.3f,
          targetValue = 0.6f,
          animationSpec = infiniteRepeatable(
              animation = tween(3000, easing =
  FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
          ),
          label = "glowAlpha"
      )

      Box(
          modifier = Modifier
              .fillMaxSize()
              .background(
                  brush = Brush.radialGradient(
                      colors = listOf(
                          DeepBlue.copy(alpha = glowAlpha * 0.3f),
                          Color.Transparent
                      ),
                      center = Offset(300f, 200f),
                      radius = 600f
                  )
              )
      )

      Box(
          modifier = Modifier
              .fillMaxSize()
              .background(
                  brush = Brush.radialGradient(
                      colors = listOf(
                          TealAccent.copy(alpha = glowAlpha *     
  0.15f),
                          Color.Transparent
                      ),
                      center = Offset(100f, 800f),
                      radius = 500f
                  )
              )
      )
  }

  // ─────────────────────────────────────────────
  // 顶部栏
  // ─────────────────────────────────────────────

  @Composable
  private fun TopBar(
      onSettingsClick: () -> Unit,
      onToggleVisibility: () -> Unit,
      isHidden: Boolean
  ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,       
          verticalAlignment = Alignment.CenterVertically
      ) {
          Column {
              Text(
                  text = "DeepSeek",
                  style = MaterialTheme.typography.headlineMedium,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
              )
              Text(
                  text = "API 余额查询",
                  style = MaterialTheme.typography.bodyMedium,    
                  color = TextTertiary
              )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {   
              IconButton(onClick = onToggleVisibility) {
                  Icon(
                      imageVector = if (isHidden)
  Icons.Filled.VisibilityOff
                          else Icons.Filled.Visibility,
                      contentDescription = if (isHidden)
  "显示余额" else "隐藏余额",
                      tint = TextSecondary
                  )
              }
              IconButton(onClick = onSettingsClick) {
                  Icon(
                      imageVector = Icons.Filled.Settings,        
                      contentDescription = "设置",
                      tint = TextSecondary
                  )
              }
          }
      }
  }

  // ─────────────────────────────────────────────
  // 主余额卡片（Glassmorphism）
  // ─────────────────────────────────────────────

  @Composable
  private fun MainBalanceCard(
      isLoading: Boolean,
      balance: com.deepseek.balance.model.BalanceResponse?,       
      isHidden: Boolean,
      error: String?,
      onClearError: () -> Unit
  ) {
      Box(
          modifier = Modifier
              .fillMaxWidth()
              .shadow(30.dp, RoundedCornerShape(24.dp),
  ambientColor = DeepBlue.copy(alpha = 0.4f))
              .clip(RoundedCornerShape(24.dp))
              .border(1.dp, GlassBorder,
  RoundedCornerShape(24.dp))
              .background(GlassWhite)
              .padding(32.dp),
          contentAlignment = Alignment.Center
      ) {
          Column(horizontalAlignment =
  Alignment.CenterHorizontally) {
              // 标签
              Text(
                  text = "总 余 额",
                  style = MaterialTheme.typography.labelLarge,    
                  color = TextTertiary,
                  letterSpacing =
  androidx.compose.ui.unit.TextUnit(4f,
  androidx.compose.ui.unit.TextUnitType.Sp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // 余额数字
              if (isLoading) {
                  CircularProgressIndicator(
                      modifier = Modifier.size(40.dp),
                      color = DeepBlue,
                      strokeWidth = 3.dp
                  )
              } else if (error != null) {
                  // 显示错误
                  Text(
                      text = error,
                      style = MaterialTheme.typography.bodyMedium,
                      color = CoralAccent,
                      textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(8.dp))        
                  Text(
                      text = "点击关闭",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextTertiary,
                      modifier = Modifier.clickable {
  onClearError() }
                  )
              } else if (isHidden) {
                  Text(
                      text = "****",
                      style =
  MaterialTheme.typography.displayMedium,
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      letterSpacing =
  androidx.compose.ui.unit.TextUnit(8f,
  androidx.compose.ui.unit.TextUnitType.Sp)
                  )
              } else {
                  val totalBalance =
  balance?.balanceInfos?.firstOrNull()?.totalBalance ?: 0.0       
                  AnimatedBalanceNumber(
                      targetValue = totalBalance,
                      modifier = Modifier
                  )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // 货币单位
              Text(
                  text = if (balance != null && !isLoading) {     
                      balance.balanceInfos.firstOrNull()?.currency
  ?: "CNY"
                  } else "CNY",
                  style = MaterialTheme.typography.bodyLarge,     
                  color = TextSecondary,
                  fontWeight = FontWeight.Medium
              )

              // 状态指示器
              if (balance != null && !isLoading && error == null) 
  {
                  Spacer(modifier = Modifier.height(16.dp))       
                  Row(verticalAlignment =
  Alignment.CenterVertically) {
                      Icon(
                          imageVector = Icons.Rounded.Circle,     
                          contentDescription = null,
                          tint = TealAccent,
                          modifier = Modifier.size(8.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))     
                      Text(
                          text = "账户可用",
                          style =
  MaterialTheme.typography.labelSmall,
                          color = TealAccent
                      )
                  }
              }
          }
      }
  }

  // ─────────────────────────────────────────────
  // 动画数字
  // ─────────────────────────────────────────────

  @Composable
  private fun AnimatedBalanceNumber(
      targetValue: Double,
      modifier: Modifier = Modifier
  ) {
      val animatedValue by animateFloatAsState(
          targetValue = targetValue.toFloat(),
          animationSpec = tween(
              durationMillis = 1200,
              easing = FastOutSlowInEasing
          ),
          label = "balance"
      )

      Text(
          text = "¥ %.2f".format(abs(animatedValue)),
          style = MaterialTheme.typography.displayMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          modifier = modifier
      )
  }

  // ─────────────────────────────────────────────
  // 余额明细卡片组
  // ─────────────────────────────────────────────

  @Composable
  private fun BalanceDetailCards(
      balance: com.deepseek.balance.model.BalanceResponse?,       
      isHidden: Boolean
  ) {
      val info = balance?.balanceInfos?.firstOrNull() ?: return   

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)     
      ) {
          DetailCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Rounded.Savings,
              iconTint = DeepBlue,
              label = "充值余额",
              amount = if (isHidden) "****" else "¥
  %.2f".format(info.toppedUpBalance),
              accentColor = DeepBlue
          )
          DetailCard(
              modifier = Modifier.weight(1f),
              icon = Icons.Rounded.Redeem,
              iconTint = TealAccent,
              label = "赠送余额",
              amount = if (isHidden) "****" else "¥
  %.2f".format(info.grantedBalance),
              accentColor = TealAccent
          )
      }
  }

  @Composable
  private fun DetailCard(
      modifier: Modifier = Modifier,
      icon: androidx.compose.ui.graphics.vector.ImageVector,      
      iconTint: Color,
      label: String,
      amount: String,
      accentColor: Color
  ) {
      Card(
          modifier = modifier
              .border(1.dp, GlassBorder,
  RoundedCornerShape(20.dp)),
          colors = CardDefaults.cardColors(containerColor =       
  GlassWhiteStrong),
          shape = RoundedCornerShape(20.dp)
      ) {
          Column(
              modifier = Modifier.padding(16.dp)
          ) {
              Row(verticalAlignment = Alignment.CenterVertically) 
  {
                  Box(
                      modifier = Modifier
                          .size(36.dp)
                          .clip(CircleShape)
                          .background(accentColor.copy(alpha =    
  0.15f)),
                      contentAlignment = Alignment.Center
                  ) {
                      Icon(
                          imageVector = icon,
                          contentDescription = null,
                          tint = iconTint,
                          modifier = Modifier.size(18.dp)
                      )
                  }
              }
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,    
                  color = TextTertiary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                  text = amount,
                  style = MaterialTheme.typography.bodyLarge,     
                  color = TextPrimary,
                  fontWeight = FontWeight.SemiBold
              )
          }
      }
  }

  // ─────────────────────────────────────────────
  // 使用进度条
  // ─────────────────────────────────────────────

  @Composable
  private fun UsageProgressBar(
      balance: com.deepseek.balance.model.BalanceResponse?,       
      isHidden: Boolean
  ) {
      val info = balance?.balanceInfos?.firstOrNull() ?: return   

      // 计算使用比例（充值余额的使用情况）
      val toppedUp = info.toppedUpBalance
      val total = info.totalBalance
      if (total <= 0.0) return

      // 总余额 = 充值 + 赠送，这里展示充值余额消耗的"反向比例"   
      // 实际上我们展示一个抽象的进度
      val grantedRatio = info.grantedBalance / total
      val toppedUpRatio = info.toppedUpBalance / total

      Card(
          modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, GlassBorder,
  RoundedCornerShape(20.dp)),
          colors = CardDefaults.cardColors(containerColor =       
  GlassWhiteStrong),
          shape = RoundedCornerShape(20.dp)
      ) {
          Column(modifier = Modifier.padding(20.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
              ) {
                  Text(
                      text = "余额构成",
                      style = MaterialTheme.typography.labelLarge,
                      color = TextSecondary
                  )
                  Text(
                      text = if (isHidden) "--" else
  "%.1f%%".format(toppedUpRatio * 100),
                      style = MaterialTheme.typography.labelLarge,
                      color = DeepBlue,
                      fontWeight = FontWeight.Bold
                  )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // 进度条
              Box(
                  modifier = Modifier
                      .fillMaxWidth()
                      .height(8.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(GlassWhite)
              ) {
                  // 充值部分
                  Box(
                      modifier = Modifier

  .fillMaxWidth(toppedUpRatio.toFloat().coerceIn(0f, 1f))
                          .height(8.dp)
                          .clip(RoundedCornerShape(4.dp))
                          .background(
                              brush = Brush.horizontalGradient(   
                                  colors = listOf(DeepBlue,       
  DeepBlue.copy(alpha = 0.7f))
                              )
                          )
                  )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // 图例
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
              ) {
                  LegendDot(color = DeepBlue, label = "充值")     
                  LegendDot(color = TealAccent, label = "赠送")   
              }
          }
      }
  }

  @Composable
  private fun LegendDot(color: Color, label: String) {
      Row(verticalAlignment = Alignment.CenterVertically) {       
          Box(
              modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(color)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,        
              color = TextTertiary
          )
      }
  }

  // ─────────────────────────────────────────────
  // 底部信息
  // ─────────────────────────────────────────────

  @Composable
  private fun FooterSection(
      lastUpdated: Long,
      isLoading: Boolean,
      hasApiKey: Boolean,
      onRefresh: () -> Unit
  ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,       
          verticalAlignment = Alignment.CenterVertically
      ) {
          // 上次更新时间
          if (lastUpdated > 0 && !isLoading) {
              Row(verticalAlignment = Alignment.CenterVertically) 
  {
                  Icon(
                      imageVector = Icons.Rounded.Circle,
                      contentDescription = null,
                      tint = TealAccent,
                      modifier = Modifier.size(6.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                      text = "更新于
  ${SimpleDateFormat("HH:mm:ss",
  Locale.getDefault()).format(Date(lastUpdated))}",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextTertiary
                  )
              }
          } else {
              Spacer(modifier = Modifier.width(1.dp)) // 占位     
          }

          // 刷新按钮
          IconButton(
              onClick = onRefresh,
              enabled = hasApiKey && !isLoading,
              modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(if (hasApiKey) DeepBlue.copy(alpha =
  0.2f) else Color.Transparent)
          ) {
              if (isLoading) {
                  CircularProgressIndicator(
                      modifier = Modifier.size(20.dp),
                      color = DeepBlue,
                      strokeWidth = 2.dp
                  )
              } else {
                  Icon(
                      imageVector = Icons.Filled.Refresh,
                      contentDescription = "刷新",
                      tint = if (hasApiKey) DeepBlue else
  TextTertiary
                  )
              }
          }
      }
  }

  // ─────────────────────────────────────────────
  // 初始设置提示
  // ─────────────────────────────────────────────

  @Composable
  private fun SetupPrompt(onClick: () -> Unit) {
      Card(
          modifier = Modifier
              .fillMaxWidth()
              .clickable { onClick() }
              .border(1.dp, GoldAccent.copy(alpha = 0.3f),        
  RoundedCornerShape(20.dp)),
          colors = CardDefaults.cardColors(
              containerColor = GoldAccent.copy(alpha = 0.05f)     
          ),
          shape = RoundedCornerShape(20.dp)
      ) {
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp),
              verticalAlignment = Alignment.CenterVertically      
          ) {
              Box(
                  modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(GoldAccent.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
              ) {
                  Icon(
                      imageVector = Icons.Filled.Key,
                      contentDescription = null,
                      tint = GoldAccent,
                      modifier = Modifier.size(22.dp)
                  )
              }
              Spacer(modifier = Modifier.width(16.dp))
              Column(modifier = Modifier.weight(1f)) {
                  Text(
                      text = "设置 API Key",
                      style = MaterialTheme.typography.bodyLarge, 
                      color = TextPrimary,
                      fontWeight = FontWeight.SemiBold
                  )
                  Text(
                      text = "点击此处配置 DeepSeek API Key       
  以查询余额",
                      style = MaterialTheme.typography.labelSmall,
                      color = TextTertiary
                  )
              }
          }
      }
  }
