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
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailState
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.BuildHeaderCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.ItemBuildCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.LineupCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.NetworthCard
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail.SkillBuildCard

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
                horizontal = CARD_HORIZONTAL_PADDING,
                vertical = CARD_VERTICAL_SPACING,
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
