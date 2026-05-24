package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.dialog.D2Dialog
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.domain.Hero

@Composable
internal fun HeroFilterDialog(
    filteredHeroImageUrls: Map<Hero, String>,
    heroSearchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onHeroSelect: (Short) -> Unit,
) {
    D2Dialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.height(300.dp),
        shape = D2BuildHelperTheme.shapes.cornerRadius4,
        contentPadding = PaddingValues(8.dp),
    ) {
        var input by remember(heroSearchValue) { mutableStateOf(heroSearchValue) }

        OutlinedTextField(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            value = input,
            onValueChange = { newValue ->
                input = newValue
                onSearchValueChanged(newValue)
            },
            placeholder = {
                Text(
                    text = stringResource(MR.strings.hero_filter),
                    style = D2BuildHelperTheme.typography.textMD,
                )
            },
            singleLine = true,
            textStyle = D2BuildHelperTheme.typography.textMD.copy(fontWeight = FontWeight.Normal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = D2BuildHelperTheme.colors.textSecondary,
                unfocusedTextColor = D2BuildHelperTheme.colors.textSecondary,
            ),
            trailingIcon = {
                if (input.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            input = ""
                            onSearchValueChanged("")
                        },
                    )
                }
            },
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            items(
                items = filteredHeroImageUrls.toList(),
                key = { (hero, _) -> hero.heroId },
            ) { (hero, url) ->
                HeroFilterItem(
                    hero = hero,
                    imageUrl = url,
                    onItemClick = {
                        onHeroSelect(it)
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun HeroFilterItem(
    hero: Hero,
    imageUrl: String,
    onItemClick: (Short) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(hero.heroId) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Space8()

        Text(
            text = hero.displayName,
            style = D2BuildHelperTheme.typography.captionMD,
            color = D2BuildHelperTheme.colors.textSecondary,
        )
    }
}
