package com.xposed.wetypehook.wetype.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context

@Composable
fun SwipeGestureSettingsScreen(context: Context) {
    var selectedKey by remember { mutableStateOf<String?>(null) }
    var showActionPicker by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "下滑手势设置",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "点击按键设置下滑操作",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 键盘布局：三行
        val keyboardRows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("z","x","c","v","b","n","m")
        )

        for (row in keyboardRows) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (letter in row) {
                    val keyId = "S2_key_$letter"
                    val action = WeTypeSettings.getSwipeAction(context, keyId)
                    val hasAction = action != WeTypeSettings.SwipeAction.NONE
                    
                    Button(
                        onClick = {
                            selectedKey = keyId
                            showActionPicker = true
                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .size(48.dp, 56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasAction) Color(0xFF4CAF50) 
                                            else Color(0xFFE0E0E0),
                            contentColor = if (hasAction) Color.White else Color.Black
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(letter.uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            if (hasAction) {
                                Text(
                                    action.displayName.take(3),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 重置按钮
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                WeTypeSettings.ALL_LETTER_KEYS.forEach { key ->
                    WeTypeSettings.setSwipeAction(context, key, 
                        WeTypeSettings.DEFAULT_SWIPE_CONFIG[key] ?: WeTypeSettings.SwipeAction.NONE)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("恢复默认")
        }
    }

    // 操作选择弹窗
    if (showActionPicker && selectedKey != null) {
        AlertDialog(
            onDismissRequest = { showActionPicker = false },
            title = { Text("按键 ${selectedKey!!.takeLast(1).uppercase()} 下滑操作") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(400.dp)
                ) {
                    items(WeTypeSettings.SwipeAction.entries) { action ->
                        TextButton(
                            onClick = {
                                WeTypeSettings.setSwipeAction(context, selectedKey!!, action)
                                showActionPicker = false
                            },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(action.displayName, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionPicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}