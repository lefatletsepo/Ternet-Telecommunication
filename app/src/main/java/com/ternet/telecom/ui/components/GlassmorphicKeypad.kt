package com.ternet.telecom.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ternet.telecom.ui.theme.GlassBgWhiteLow
import com.ternet.telecom.ui.theme.TextWhite

/**
 * A beautiful, tactile glassmorphic keypad layout overlay.
 * Ideal for secure PIN entry (M-Pesa Lesotho replica setup) and financial input screens.
 */
@Composable
fun GlassmorphicKeypad(
    modifier: Modifier = Modifier,
    onKeyPress: (Char) -> Unit,
    onDeletePress: () -> Unit,
    onClearPress: () -> Unit
) {
    val keys = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf('C', '0', 'D') // Clear, Zero, Delete
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        keys.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowKeys.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .glassmorphic(
                                shape = CircleShape,
                                containerColor = if (key == 'C' || key == 'D') Color(0x11FFFFFF) else GlassBgWhiteLow
                            )
                            .clickable {
                                when (key) {
                                    'C' -> onClearPress()
                                    'D' -> onDeletePress()
                                    else -> onKeyPress(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (key) {
                            'C' -> {
                                Text(
                                    text = "CLR",
                                    color = Color(0xFFFF9F43),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            'D' -> {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Delete",
                                    tint = TextWhite
                                )
                            }
                            else -> {
                                Text(
                                    text = key.toString(),
                                    color = TextWhite,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
