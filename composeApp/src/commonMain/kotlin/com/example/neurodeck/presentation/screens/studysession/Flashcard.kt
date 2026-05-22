package com.example.neurodeck.presentation.screens.studysession

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Kartu flashcard yang user lihat saat Study Session.
 *
 * Tap saat showingBack=false → trigger [onTap] → ViewModel flip ke back side.
 * Animasi: slide horizontal (front slide out left, back slide in right).
 *
 * AnimatedContent works dengan "key": ketika showingBack berubah, AnimatedContent
 * detect perubahan key dan animate transition. Inside-nya kita kasih conditional
 * content berdasarkan target state.
 */
@Composable
fun Flashcard(
    front: String,
    back: String,
    showingBack: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = !showingBack, onClick = onTap),  // disable tap saat showingBack
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        AnimatedContent(
            targetState = showingBack,
            transitionSpec = {
                // Slide horizontal: konten masuk dari kanan, keluar ke kiri
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetX = { fullWidth -> fullWidth },
                ) + fadeIn(animationSpec = tween(durationMillis = 300))).togetherWith(
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = 300),
                        targetOffsetX = { fullWidth -> -fullWidth },
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))
                )
            },
            label = "FlashcardFlip",
        ) { isShowingBack ->
            if (isShowingBack) {
                FlashcardBack(front = front, back = back)
            } else {
                FlashcardFront(front = front)
            }
        }
    }
}

/**
 * Front side: pertanyaan + hint "Tap untuk lihat jawaban".
 */
@Composable
private fun FlashcardFront(front: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = front,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "👆 Tap untuk lihat jawaban",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

/**
 * Back side: pertanyaan (di atas, kecil) + jawaban (di bawah, prominent).
 * Menampilkan keduanya supaya user bisa associate Q→A saat baca jawaban.
 */
@Composable
private fun FlashcardBack(front: String, back: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Pertanyaan di atas (kecil, sebagai context)
        Text(
            text = front,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        )
        // Jawaban besar
        Text(
            text = back,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}