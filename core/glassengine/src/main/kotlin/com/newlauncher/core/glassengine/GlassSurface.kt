package com.newlauncher.core.glassengine

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newlauncher.core.designsystem.tokens.GlassMaterialParameters
import com.newlauncher.core.graphics.buildBlurRenderEffect

/**
 * A photorealistic glass surface rendered by the Physical Glass Rendering Engine.
 *
 * It resolves a [GlassRenderPlan] from the active [GlassEngineController] (or a
 * per-instance [material] override), then renders the surface using the best
 * available strategy: an AGSL shader fill on Android 12+, or a pure-Compose
 * brush fallback otherwise. Optional backdrop blur is applied through a
 * [androidx.compose.ui.graphics.RenderEffect].
 *
 * All content is drawn on top of the glass fill, so callers can place any
 * Compose content inside and it will composite correctly over the glass.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    material: GlassMaterialParameters? = null,
    borderWidth: Dp = 0.5.dp,
    borderColor: Color = Color.White.copy(alpha = 0.18f),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    val engine = glassEngine()
    val activeMaterial = material ?: engine.material
    val plan = remember(engine.profile, activeMaterial) { engine.planFor(activeMaterial) }

    val shaderBrush = remember(plan.fill) {
        if (plan.fill == GlassFillStrategy.AGSL_SHADER) GlassShaderBrush.create() else null
    }

    // Animated sheen is driven by an infinite transition, but only when the
    // profile permits it — this keeps the PERFORMANCE profile cheap.
    val timeSeconds: Float = if (plan.animatedSheen && shaderBrush != null) {
        val transition: InfiniteTransition = rememberInfiniteTransition(label = "glassSheen")
        val animated = transition.animateFloat(
            initialValue = 0f,
            targetValue = 10000f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 9000), RepeatMode.Restart),
            label = "sheenTime",
        )
        animated.value / 1000f
    } else {
        0f
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    val blurRadiusPx = with(density) { activeMaterial.blurRadius.toPx() }
    val blurEffect = remember(plan.applyBackdropBlur, blurRadiusPx) {
        if (plan.applyBackdropBlur) buildBlurRenderEffect(blurRadiusPx) else null
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape)
                else Modifier
            )
            .graphicsLayer {
                if (blurEffect != null) renderEffect = blurEffect.asComposeRenderEffect()
            }
            .drawWithContent {
                drawGlassFill(
                    plan = plan,
                    material = activeMaterial,
                    shaderBrush = shaderBrush,
                    timeSeconds = timeSeconds,
                )
                drawContent()
            }
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Draws the glass fill (shader or fallback) for the current [DrawScope].
 */
private fun DrawScope.drawGlassFill(
    plan: GlassRenderPlan,
    material: GlassMaterialParameters,
    shaderBrush: GlassShaderBrush?,
    timeSeconds: Float,
) {
    if (plan.fill == GlassFillStrategy.AGSL_SHADER && shaderBrush != null) {
        val uniforms = GlassShaderUniforms.from(material, size.width, size.height, timeSeconds)
        shaderBrush.apply(uniforms)
        drawRect(brush = shaderBrush.brush, alpha = 1f)
    } else {
        drawFallbackFill(material)
    }
}

/**
 * CPU/brush fallback used when AGSL shaders are unavailable. Produces a
 * translucent tint, a soft specular highlight and a fresnel edge glow using
 * only Compose brushes — no GPU shader required.
 */
private fun DrawScope.drawFallbackFill(material: GlassMaterialParameters) {
    val baseAlpha = (1f - material.transparency).coerceIn(0f, 1f)
    val base = if (material.tint == Color.Unspecified) Color.White else material.tint

    // Base translucent fill with a subtle top-to-bottom sheen.
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                base.copy(alpha = baseAlpha * 0.5f),
                base.copy(alpha = baseAlpha),
            ),
        ),
        alpha = 1f,
    )

    // Specular highlight (upper-right light source).
    if (material.specularStrength > 0f) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = material.specularStrength * 0.45f),
                    Color.Transparent,
                ),
                center = Offset(size.width * 0.72f, size.height * 0.28f),
                radius = size.width * 0.5f,
            ),
            alpha = 1f,
        )
    }

    // Fresnel edge glow.
    if (material.fresnelIntensity > 0f) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = material.fresnelIntensity * 0.35f),
                    Color.Transparent,
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
            ),
            alpha = 1f,
        )
    }
}

/**
 * A glass card: a [GlassSurface] with default card padding and a soft shadow.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    material: GlassMaterialParameters? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        shape = shape,
        material = material,
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * A glass panel used for docks, control-center tiles and island surfaces.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    material: GlassMaterialParameters? = null,
    content: @Composable () -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        shape = shape,
        material = material,
        contentPadding = PaddingValues(0.dp),
        content = content,
    )
}

/** Convenience: resolve a glass material preset by type at a call site. */
@Composable
fun glassMaterialOf(type: com.newlauncher.core.designsystem.tokens.GlassMaterialType): GlassMaterialParameters =
    glassEngine().materialFor(type)
