package com.nonoxy.d2buildhelper.presentation.filterview

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun HeroFilterDialog(
    heroFilterState: HeroFilterViewModel.HeroFilterState,
    onDismiss: () -> Unit,
    onItemClick: (Short) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(308.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var searchText by remember { mutableStateOf("") }
                OutlinedTextField(
                    modifier = Modifier,
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                    },
                    placeholder = {
                        Text(
                            text = "Фильтр героев",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val filteredHeroes = heroFilterState.eachHeroDetails.filter { (_, value) ->
                        value["displayName"]?.contains(searchText, ignoreCase = true) ?: false
                    }
                    items(filteredHeroes.toList()
                        .sortedBy { (heroId, value) -> value["displayName"] }
                        .toMap()
                        .map { it.key }) { heroId ->
                        HeroFilterItem(
                            heroId = heroId,
                            displayName = heroFilterState.eachHeroDetails[heroId]
                                ?.get("displayName") ?: "",
                            imageUrl = heroFilterState.eachHeroImageUrls[heroId] ?: "null",
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HeroFilterItem(
    heroId: Short,
    displayName: String,
    imageUrl: String,
    onItemClick: (Short) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(heroId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.bodySmall,
            color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC)
        )
    }
}