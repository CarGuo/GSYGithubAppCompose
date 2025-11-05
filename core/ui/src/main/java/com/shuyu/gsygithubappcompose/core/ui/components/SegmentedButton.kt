package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedButton(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (index: Int) -> Unit
) {
    val buttonShape = RoundedCornerShape(8.dp)
    val borderColor = MaterialTheme.colorScheme.primary
    val selectedSegmentColor = MaterialTheme.colorScheme.primary
    val unselectedSegmentBackgroundColor = MaterialTheme.colorScheme.primaryContainer // Overall background for the button
    val selectedTextColor = Color.White
    val unselectedTextColor = Color.Gray
    val dividerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f) // Subtle divider

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = buttonShape,
        color = unselectedSegmentBackgroundColor, // Overall background color
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                Box( // Using Box for simpler background and content alignment
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (isSelected) selectedSegmentColor else Color.Transparent)
                        .selectable(
                            selected = isSelected,
                            onClick = { onItemSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (isSelected) selectedTextColor else unselectedTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                // Add divider if not the last item
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(dividerColor)
                    )
                }
            }
        }
    }
}
