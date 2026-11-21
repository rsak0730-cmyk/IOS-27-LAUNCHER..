package com.newlauncher.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.newlauncher.core.common.AppInfo
import com.newlauncher.core.designsystem.component.AppIcon
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens
import com.newlauncher.core.glassengine.GlassPanel
import com.newlauncher.core.storage.DockSlot
import com.newlauncher.core.storage.HomeCell
import kotlin.math.abs

/**
 * The premium iPhone-inspired home screen.
 *
 * Composition:
 *  - a [HorizontalPager] of pages, each showing an app grid
 *  - a glass dock pinned to the bottom
 *  - page indicators
 *  - a top toolbar with search (opens the app drawer) and an edit-mode toggle
 *
 * Rearrangement is real: in edit mode, long-press an icon and drag to swap
 * with an adjacent cell, or tap the remove affordance. Layout changes are
 * persisted by [HomeViewModel].
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenAppDrawer: () -> Unit,
) {
    val layout by viewModel.layout.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val editMode by viewModel.editMode.collectAsState()
    val apps by viewModel.apps.collectAsState()

    LaunchedEffect(layout.pages.size) {
        viewModel.ensurePages()
    }

    val appByKey = remember(apps) { apps.associateBy { it.key } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SemanticColors.Background)
            .padding(horizontal = SpacingTokens.L),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpacingTokens.Xl, bottom = SpacingTokens.Sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.toggleEditMode() }) {
                Icon(
                    if (editMode) Icons.Filled.Apps else Icons.Filled.Edit,
                    contentDescription = "Edit home screen",
                    tint = SemanticColors.TextPrimary,
                )
            }
            IconButton(onClick = onOpenAppDrawer) {
                Icon(
                    Icons.Filled.Apps,
                    contentDescription = "Open app drawer",
                    tint = SemanticColors.TextPrimary,
                )
            }
        }

        val pagerState = rememberPagerState(pageCount = { layout.pages.size.coerceAtLeast(1) })
        LaunchedEffect(pagerState.currentPage) {
            viewModel.onPageChange(pagerState.currentPage)
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val pageData = layout.pages.getOrNull(page)
            val cells = pageData?.cells ?: emptyList()
            AppGridPage(
                cells = cells,
                appByKey = appByKey,
                columns = settings.gridColumns,
                iconSize = settings.appIconSize,
                spacing = settings.gridSpacing,
                editMode = editMode,
                onLaunch = { viewModel.launch(it) },
                onRemove = { viewModel.removeFromPage(page, it) },
                onMove = { from, to -> viewModel.moveWithinPage(page, from, to) },
                onMoveToDock = { viewModel.moveToDock(page, it) },
            )
        }

        PageIndicator(
            count = layout.pages.size.coerceAtLeast(1),
            current = pagerState.currentPage,
            modifier = Modifier.padding(vertical = SpacingTokens.Sm),
        )

        Dock(
            slots = layout.dock,
            appByKey = appByKey,
            iconSize = settings.appIconSize,
            editMode = editMode,
            onLaunch = { viewModel.launch(it) },
            onReorder = { from, to -> viewModel.reorderDock(from, to) },
        )
    }
}

@Composable
private fun AppGridPage(
    cells: List<HomeCell>,
    appByKey: Map<String, AppInfo>,
    columns: Int,
    iconSize: Float,
    spacing: Float,
    editMode: Boolean,
    onLaunch: (AppInfo) -> Unit,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onMoveToDock: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = SpacingTokens.Xl),
    ) {
        AppGrid(
            cells = cells,
            appByKey = appByKey,
            columns = columns,
            iconSize = iconSize,
            spacing = spacing,
            editMode = editMode,
            onLaunch = onLaunch,
            onRemove = onRemove,
            onMove = onMove,
            onMoveToDock = onMoveToDock,
        )
    }
}

@Composable
private fun AppGrid(
    cells: List<HomeCell>,
    appByKey: Map<String, AppInfo>,
    columns: Int,
    iconSize: Float,
    spacing: Float,
    editMode: Boolean,
    onLaunch: (AppInfo) -> Unit,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onMoveToDock: (Int) -> Unit,
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Column(verticalArrangement = Arrangement.spacedBy((8 * spacing).dp)) {
        for (row in 0 until ((cells.size + columns - 1) / columns)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((8 * spacing).dp),
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < cells.size) {
                        AppGridCell(
                            cell = cells[index],
                            app = appByKey[cells[index].key],
                            iconSize = iconSize,
                            editMode = editMode,
                            isDragged = draggedIndex == index,
                            onLaunch = { onLaunch(it) },
                            onRemove = { onRemove(index) },
                            onMoveToDock = { onMoveToDock(index) },
                            onDragStart = { draggedIndex = index; dragOffset = Offset.Zero },
                            onDrag = { dragOffset = it },
                            onDragEnd = {
                                val from = draggedIndex
                                draggedIndex = null
                                if (from != null) {
                                    val target = computeAdjacentTarget(from, columns, dragOffset)
                                    if (target != null && target != from) onMove(from, target)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun computeAdjacentTarget(index: Int, columns: Int, offset: Offset): Int? {
    val inRowEnd = index % columns == columns - 1
    val inRowStart = index % columns == 0
    return when {
        abs(offset.x) > abs(offset.y) -> {
            if (!inRowEnd && offset.x > 0) index + 1
            else if (!inRowStart && offset.x < 0) index - 1
            else null
        }
        else -> {
            if (offset.y > 0) index + columns
            else if (offset.y < 0 && index - columns >= 0) index - columns
            else null
        }
    }
}

@Composable
private fun AppGridCell(
    cell: HomeCell,
    app: AppInfo?,
    iconSize: Float,
    editMode: Boolean,
    isDragged: Boolean,
    onLaunch: (AppInfo) -> Unit,
    onRemove: () -> Unit,
    onMoveToDock: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val sizeDp = (64 * iconSize).dp
    Box(
        modifier = Modifier
            .size(sizeDp)
            .then(
                if (editMode) Modifier.pointerInput(cell.key) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = onDragStart,
                        onDrag = { change, _ -> onDrag(change.position) },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() },
                    )
                } else Modifier.clickable { app?.let(onLaunch) }
            )
            .clip(RoundedCornerShape(SpacingTokens.Md))
            .then(if (isDragged) Modifier else Modifier),
    ) {
        if (app != null) {
            AppIcon(drawable = app.icon, sizeDp = sizeDp.value.toInt())
        } else {
            Text(
                text = cell.label.firstOrNull()?.toString() ?: "?",
                color = SemanticColors.TextTertiary,
            )
        }
        if (editMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(SemanticColors.Error)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "×", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun Dock(
    slots: List<DockSlot>,
    appByKey: Map<String, AppInfo>,
    iconSize: Float,
    editMode: Boolean,
    onLaunch: (AppInfo) -> Unit,
    onReorder: (Int, Int) -> Unit,
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SpacingTokens.Xxl, top = SpacingTokens.Sm),
        shape = RoundedCornerShape(SpacingTokens.Xxl),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingTokens.Md, vertical = SpacingTokens.Md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            slots.forEachIndexed { index, slot ->
                val app = appByKey["${slot.packageName}/${slot.className}"]
                val sizeDp = (64 * iconSize).dp
                Box(
                    modifier = Modifier
                        .size(sizeDp)
                        .then(
                            if (editMode) Modifier.pointerInput(slot.packageName) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { draggedIndex = index; dragOffset = Offset.Zero },
                                    onDrag = { change, _ -> dragOffset = change.position },
                                    onDragEnd = {
                                        val from = draggedIndex
                                        draggedIndex = null
                                        if (from != null) {
                                            val target = if (abs(dragOffset.x) > abs(dragOffset.y)) {
                                                if (dragOffset.x > 0) from + 1 else from - 1
                                            } else {
                                                if (dragOffset.y > 0) from else from
                                            }
                                            if (target in slots.indices && target != from) onReorder(from, target)
                                        }
                                    },
                                    onDragCancel = { draggedIndex = null },
                                )
                            } else Modifier.clickable { app?.let(onLaunch) }
                        )
                        .clip(RoundedCornerShape(SpacingTokens.Md)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (app != null) {
                        AppIcon(drawable = app.icon, sizeDp = sizeDp.value.toInt())
                    } else {
                        Text(slot.label.firstOrNull()?.toString() ?: "?", color = SemanticColors.TextTertiary)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (i == current) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == current) SemanticColors.TextPrimary else SemanticColors.TextTertiary),
            )
        }
    }
}
