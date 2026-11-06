package com.brendigo.lensigo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val FilterCyan = Color(0xFF00D4FF)
private val FilterInk = Color(0xFF081724)

/**
 * The user-supplied offline reference photograph is reused for the thumbnails.
 * Filters are rendered with the same FilterEngine as image export, not arbitrary
 * gradients or static demo thumbnails. A short debounce avoids recomputation
 * on every intermediate slider frame.
 */
@Composable
internal fun LensigoFilterPreviewStrip(
    selected: LensFilter,
    intensity: Float,
    onSelected: (LensFilter) -> Unit
) {
    val context = LocalContext.current
    val previews by produceState<Map<LensFilter, ImageBitmap>>(
        initialValue = emptyMap(), key1 = intensity, key2 = context
    ) {
        delay(120)
        value = withContext(Dispatchers.Default) {
            val source = BitmapFactory.decodeResource(
                context.resources, R.drawable.lensigo_home_hero
            )
            if (source == null) emptyMap()
            else try {
                LensFilter.entries.associateWith { filter ->
                    FilterEngine.apply(
                        source, PhotoLook(filter = filter, intensity = intensity)
                    ).asImageBitmap()
                }
            } finally {
                source.recycle()
            }
        }
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        contentPadding = PaddingValues(horizontal = 11.dp)
    ) {
        items(LensFilter.entries, key = { it.name }) { item ->
            val chosen = selected == item
            val shape = RoundedCornerShape(7.dp)
            Column(
                Modifier.width(76.dp)
                    .background(FilterInk.copy(alpha = .96f), shape)
                    .border(
                        if (chosen) 2.dp else 1.dp,
                        if (chosen) FilterCyan else Color(0xFF53778A), shape
                    )
                    .clip(shape)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "Primijeni filtar ${item.title}"
                    ) { onSelected(item) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bitmap = previews[item]
                if (bitmap != null) {
                    Image(bitmap = bitmap, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(48.dp))
                } else {
                    Image(
                        painter = painterResource(R.drawable.lensigo_home_hero),
                        contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                }
                Text(
                    item.title, color = if (chosen) FilterCyan else Color.White,
                    fontSize = 10.sp, fontWeight = if (chosen) FontWeight.Bold
                        else FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 3.dp)
                )
            }
        }
    }
}
