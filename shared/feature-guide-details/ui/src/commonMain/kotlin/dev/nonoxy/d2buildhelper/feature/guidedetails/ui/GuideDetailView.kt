package dev.nonoxy.d2buildhelper.feature.guidedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.icon.D2Icon
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.iconbutton.D2IconButton
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.common.ui.compose.utils.navigationBarHeight
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchLane
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerPosition
import dev.nonoxy.d2buildhelper.common.ui.match.UiMatchPlayerRole
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiAbilitySummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiBuildHeader
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailState
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildEntry
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildPhase
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiItemBuildSection
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineup
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineupMember
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthCurve
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiNetworthMarker
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrix
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixRow
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillSummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalent
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.BuildHeaderCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.ItemBuildCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.LineupCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.NetworthCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.SkillBuildCard
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.ui.tooling.preview.Preview

private val CARD_HORIZONTAL_PADDING = 12.dp
private val CARD_VERTICAL_SPACING = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GuideDetailView(
    state: UiGuideDetailState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = D2BuildHelperTheme.colors.background,
        topBar = {
            D2TopBar(
                title = stringResource(MR.strings.guide_detail_title),
                leading = {
                    D2IconButton(onClick = onBack) {
                        D2Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(MR.strings.guide_detail_back),
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = CARD_HORIZONTAL_PADDING,
                end = CARD_HORIZONTAL_PADDING,
                top = CARD_VERTICAL_SPACING,
                bottom = CARD_VERTICAL_SPACING + navigationBarHeight,
            ),
            verticalArrangement = Arrangement.spacedBy(CARD_VERTICAL_SPACING),
        ) {
            state.header?.let { header ->
                item(key = "header") {
                    BuildHeaderCard(header = header, modifier = Modifier.fillMaxWidth())
                }
            }
            state.skillBuild?.let { skillBuild ->
                item(key = "skillBuild") {
                    SkillBuildCard(skillBuild = skillBuild, modifier = Modifier.fillMaxWidth())
                }
            }
            state.itemBuild?.let { itemBuild ->
                item(key = "itemBuild") {
                    ItemBuildCard(itemBuild = itemBuild, modifier = Modifier.fillMaxWidth())
                }
            }
            state.networth?.let { networth ->
                item(key = "networth") {
                    NetworthCard(networth = networth, modifier = Modifier.fillMaxWidth())
                }
            }
            state.lineup?.let { lineup ->
                item(key = "lineup") {
                    LineupCard(lineup = lineup, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

private fun previewHeader() = UiBuildHeader(
    heroIconUrl = null,
    heroName = "Lion",
    isRadiant = true,
    position = UiMatchPlayerPosition.POSITION_5,
    role = UiMatchPlayerRole.HARD_SUPPORT,
    lane = UiMatchLane.SAFE_LANE,
    durationText = "38:42",
    level = 25,
    isVictory = true,
    kills = 4,
    deaths = 3,
    assists = 21,
)

private fun previewSkillBuild() = UiSkillBuild(
    summary = UiSkillSummary(
        talentTierTaken = persistentListOf(true, true, true, false),
        abilities = persistentListOf(
            UiAbilitySummary(
                iconUrl = null,
                name = "Earth Spike",
                pointCount = 7,
                earlyPointCount = 3,
                isUltimate = false,
            ),
            UiAbilitySummary(
                iconUrl = null,
                name = "Hex",
                pointCount = 7,
                earlyPointCount = 2,
                isUltimate = false,
            ),
            UiAbilitySummary(
                iconUrl = null,
                name = "Mana Drain",
                pointCount = 4,
                earlyPointCount = 1,
                isUltimate = false,
            ),
            UiAbilitySummary(
                iconUrl = null,
                name = "Finger of Death",
                pointCount = 5,
                earlyPointCount = 0,
                isUltimate = true,
            ),
        ),
        scepterPurchased = true,
    ),
    matrix = UiSkillMatrix(
        rows = persistentListOf(
            UiSkillMatrixRow(
                iconUrl = null,
                name = "Earth Spike",
                isStat = false,
                isUltimate = false,
                marks = (1..30).map { it in listOf(1, 3, 5, 8, 10, 12, 14) }.toPersistentList(),
            ),
            UiSkillMatrixRow(
                iconUrl = null,
                name = "Hex",
                isStat = false,
                isUltimate = false,
                marks = (1..30).map { it in listOf(2, 4, 7, 9, 11, 13, 16) }.toPersistentList(),
            ),
            UiSkillMatrixRow(
                iconUrl = null,
                name = "Mana Drain",
                isStat = false,
                isUltimate = false,
                marks = (1..30).map { it in listOf(6, 15, 17, 19) }.toPersistentList(),
            ),
            UiSkillMatrixRow(
                iconUrl = null,
                name = "Finger of Death",
                isStat = false,
                isUltimate = true,
                marks = (1..30).map { it in listOf(18, 20, 24) }.toPersistentList(),
            ),
            UiSkillMatrixRow(
                iconUrl = null,
                name = null,
                isStat = true,
                isUltimate = false,
                marks = (1..30).map { it in listOf(21, 22, 23, 25) }.toPersistentList(),
            ),
        ),
    ),
    talents = persistentListOf(
        UiTalent(level = 10, text = "+125 Cast Range"),
        UiTalent(level = 15, text = "+100 Hex Duration"),
        UiTalent(level = 20, text = "-4s Earth Spike CD"),
        UiTalent(level = 25, text = "+250 Finger Damage"),
    ),
)

private fun previewItemBuild() = UiItemBuild(
    neutralItem = UiItemBuildEntry(iconUrl = null, name = "Ogre Seal Totem", timeText = "28:14"),
    sections = persistentListOf(
        UiItemBuildSection(
            phase = UiItemBuildPhase.LANING,
            entries = persistentListOf(
                UiItemBuildEntry(iconUrl = null, name = "Arcane Boots", timeText = "7:30"),
                UiItemBuildEntry(iconUrl = null, name = "Magic Wand", timeText = "9:05"),
                UiItemBuildEntry(iconUrl = null, name = "Wind Lace", timeText = "4:20"),
            ),
        ),
        UiItemBuildSection(
            phase = UiItemBuildPhase.MID_GAME,
            entries = persistentListOf(
                UiItemBuildEntry(iconUrl = null, name = "Blink Dagger", timeText = "17:45"),
                UiItemBuildEntry(iconUrl = null, name = "Aghanim's Scepter", timeText = "22:10"),
                UiItemBuildEntry(iconUrl = null, name = "Force Staff", timeText = "26:30"),
            ),
        ),
        UiItemBuildSection(
            phase = UiItemBuildPhase.LATE_GAME,
            entries = persistentListOf(
                UiItemBuildEntry(iconUrl = null, name = "Scythe of Vyse", timeText = "33:00"),
                UiItemBuildEntry(iconUrl = null, name = "Aeon Disk", timeText = "36:55"),
            ),
        ),
    ),
)

private fun previewNetworth() = UiNetworthCurve(
    points = persistentListOf(
        500, 800, 1200, 1700, 2100, 2600, 3000, 3500, 4100, 4700,
        5200, 5800, 6400, 7100, 7800, 8400, 9100, 9700, 10300, 11000,
        11700, 12500, 13300, 14100, 14900, 15800, 16600, 17400, 18200, 19100,
    ),
    markers = persistentListOf(
        UiNetworthMarker(minute = 5, iconUrl = null, name = "First Blood"),
        UiNetworthMarker(minute = 12, iconUrl = null, name = "Tower"),
        UiNetworthMarker(minute = 22, iconUrl = null, name = "Roshan"),
        UiNetworthMarker(minute = 30, iconUrl = null, name = "Barracks"),
    ),
    gpm = 498,
    networth = 19100,
    lastHitsAt10 = 14,
)

private fun previewLineup() = UiLineup(
    allies = persistentListOf(
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = true, role = UiMatchPlayerRole.HARD_SUPPORT),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.LIGHT_SUPPORT),
    ),
    enemies = persistentListOf(
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.CORE),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.LIGHT_SUPPORT),
        UiLineupMember(heroIconUrl = null, heroName = null, isMe = false, role = UiMatchPlayerRole.HARD_SUPPORT),
    ),
)

@Preview
@Composable
private fun PreviewGuideDetailView() {
    D2BuildHelperTheme {
        GuideDetailView(
            state = UiGuideDetailState(
                header = previewHeader(),
                skillBuild = previewSkillBuild(),
                itemBuild = previewItemBuild(),
                networth = previewNetworth(),
                lineup = previewLineup(),
                isLoading = false,
                isError = false,
            ),
            onBack = {},
        )
    }
}
