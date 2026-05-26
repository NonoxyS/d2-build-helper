package dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.icon.D2Icon
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.iconbutton.D2IconButton
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.textfield.D2OutlinedTextField
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiHero

@Composable
internal fun HeroPicker(
    picker: UiFilterPicker.Hero,
    onSearchChange: (String) -> Unit,
    onHeroClick: (HeroId) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var input by remember(picker.search) { mutableStateOf(picker.search) }

    LaunchedEffect(picker) { focusRequester.requestFocus() }

    Column(modifier = Modifier.padding(horizontal = 8.dp).heightIn(max = 560.dp)) {
        D2OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                onSearchChange(it)
            },
            placeholder = {
                Text(
                    text = stringResource(MR.strings.search_hero_placeholder),
                    style = D2BuildHelperTheme.typography.textMD,
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            leadingIcon = { D2Icon(imageVector = Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (input.isNotBlank()) {
                    D2IconButton(onClick = {
                        input = ""
                        onSearchChange("")
                    }) {
                        D2Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(MR.strings.content_desc_clear),
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        )

        Text(
            text = stringResource(MR.plurals.search_hero_count_plural, picker.heroes.size, picker.heroes.size),
            style = D2BuildHelperTheme.typography.captionMD,
            color = D2BuildHelperTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 72.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = picker.heroes, key = { it.id.raw }) { hero ->
                HeroCell(
                    hero = hero,
                    isSelected = picker.selectedHeroId == hero.id,
                    onClick = { onHeroClick(hero.id) },
                )
            }
        }
    }
}

@Composable
private fun HeroCell(hero: UiHero, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClickLabel = hero.displayName,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(4.dp),
    ) {
        AsyncImage(
            model = hero.iconUrl.raw,
            contentDescription = hero.displayName,
            modifier = Modifier.size(56.dp),
        )
        Space4()

        Text(
            text = hero.displayName,
            style = D2BuildHelperTheme.typography.captionMD,
            color = if (isSelected) D2BuildHelperTheme.colors.tintColor else D2BuildHelperTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
