package com.nonoxy.d2buildhelper.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nonoxy.d2buildhelper.R
import com.nonoxy.d2buildhelper.domain.model.GuideInfo
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.ui.theme.fontFamily

@Composable
fun GuidesScreen(
    state: HeroGuidesViewModel.BuildsState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.guides) { guide ->
                    GuideItem(
                        guide = guide,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun GuideItem(
    guide: HeroGuideInfo,
    //build: HeroGuideBuild,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = guide.heroId.toString()
        )
        Text(
            text = guide.guidesInfo?.get(0)?.matchId.toString(),
            )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuideItemPreview(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onSecondary)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pos_1),
                        contentDescription = null,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.luna_minimap_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Luna",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "47:27",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.radiant_square),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "16",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "/",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "15",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "/",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "20",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .widthIn(min = 15.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))

                    Text(
                        text = "+39",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    LinearProgressIndicator(
                        progress = { 39 / 50f },
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.5f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(2.dp)),
                    )
                }

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.power_treads),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "06:33",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mask_of_madness),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "09:12",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.butterfly),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "15:46",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mask_of_madness),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "17:14",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mask_of_madness),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "21:35",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mask_of_madness),
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Text(
                            text = "25:41",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.specialists_array),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape))
                }
            }
        }
    }
}