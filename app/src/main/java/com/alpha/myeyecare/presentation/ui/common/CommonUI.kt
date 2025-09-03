package com.alpha.myeyecare.presentation.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.Companion
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.Companion.size(48.dp)
            )
            Spacer(modifier = Modifier.Companion.width(16.dp))
            Column(modifier = Modifier.Companion.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Bold),
                    color = contentColor
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Set Reminder",
                tint = contentColor,
                modifier = Modifier.Companion.size(24.dp)
            )
        }
    }
}
