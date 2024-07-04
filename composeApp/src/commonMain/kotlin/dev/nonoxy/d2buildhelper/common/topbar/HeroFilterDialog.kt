package dev.nonoxy.d2buildhelper.common.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import dev.nonoxy.d2buildhelper.base.LocalImageLoader
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import dota_2_build_helper.composeapp.generated.resources.Res
import dota_2_build_helper.composeapp.generated.resources.hero_filter
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HeroFilterDialog(
    filteredHeroImageUrls: Map<HeroUI, String>,
    heroSearchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onHeroSelect: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .border(width = 1.dp, color = D2BuildHelperTheme.colors.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(D2BuildHelperTheme.colors.primaryContainer)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier,
                    value = heroSearchValue,
                    onValueChange = onSearchValueChanged,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.hero_filter),
                            style = D2BuildHelperTheme.typography.bodyMedium,
                        )
                    },
                    singleLine = true,
                    textStyle = D2BuildHelperTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = D2BuildHelperTheme.colors.primaryText,
                        unfocusedTextColor = D2BuildHelperTheme.colors.primaryText
                    ),
                    trailingIcon = {
                        if (heroSearchValue.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null,
                                modifier = Modifier.clickable { onSearchValueChanged("") }
                            )
                        }
                    }
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(
                        items = filteredHeroImageUrls.toList(),
                        key = { (hero, _) -> hero.heroId }) { (hero, url) ->
                        HeroFilterItem(
                            hero = hero,
                            imageUrl = url,
                            onItemClick = { TODO() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroFilterItem(
    hero: HeroUI,
    imageUrl: String,
    onItemClick: (Short) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(hero.heroId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            imageLoader = LocalImageLoader.current,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = hero.displayName,
            style = D2BuildHelperTheme.typography.bodySmall,
            color = D2BuildHelperTheme.colors.primaryText
        )
    }
}