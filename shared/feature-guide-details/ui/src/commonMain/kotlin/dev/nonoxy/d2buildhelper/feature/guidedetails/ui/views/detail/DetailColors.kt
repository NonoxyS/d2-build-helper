package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.ui.graphics.Color

/**
 * GuideDetail accent colors mirrored from the v16 prototype. These are
 * semantic match colors (Radiant/Dire glow, win/loss, talent gold, networth
 * curve) that have no slot in the dark-only design-system palette, so they
 * live local to the feature rather than polluting `D2BuildHelperColorScheme`.
 */
internal val WIN_COLOR = Color(0xFF3FB950)
internal val LOSS_COLOR = Color(0xFFF85149)

internal val ENEMY_GLOW_COLOR = Color(0xFFF85149)
internal val ALLY_GLOW_COLOR = Color(0xFF3FB950)

internal val TALENT_GOLD = Color(0xFFE3A008)
internal val TALENT_EMPTY = Color(0xFF2B3340)

internal val NETWORTH_LINE_COLOR = Color(0xFF3FB950)
internal val PURCHASE_MARKER_COLOR = Color(0xFF5B8FC0)

// Skill-matrix / ability tints (prototype `.sab.*` / `.cell.*`).
internal val ABILITY_Q_COLOR = Color(0xFF2F6FB0)
internal val ABILITY_W_COLOR = Color(0xFFB07D2F)
internal val ABILITY_E_COLOR = Color(0xFF2FB074)
internal val ABILITY_R_COLOR = Color(0xFFA02FA0)
internal val ABILITY_STAT_COLOR = Color(0xFF5B8FC0)
