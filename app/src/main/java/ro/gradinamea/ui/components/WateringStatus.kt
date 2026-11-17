package ro.gradinamea.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun wateringLabel(daysUntil: Long): String = when {
    daysUntil < 0 -> "Intarziata cu ${daysText(-daysUntil)}"
    daysUntil == 0L -> "De udat azi"
    daysUntil == 1L -> "De udat maine"
    else -> "Peste ${daysText(daysUntil)}"
}

/** Eticheta colorata: rosu = intarziata, albastru = azi/maine, verde = mai e timp. */
@Composable
fun WateringStatusChip(daysUntil: Long, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container: Color, content: Color) = when {
        daysUntil < 0 -> colors.errorContainer to colors.onErrorContainer
        daysUntil <= 1 -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.secondaryContainer to colors.onSecondaryContainer
    }
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(50), modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(wateringLabel(daysUntil), style = MaterialTheme.typography.labelMedium)
        }
    }
}
