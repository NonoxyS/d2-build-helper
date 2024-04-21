package com.nonoxy.d2buildhelper.presentation.filterview

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nonoxy.d2buildhelper.domain.usecases.GetEachHeroDetailsUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroImageUrlByNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroFilterViewModel @Inject constructor(
    private val getEachHeroDetailsUseCase: GetEachHeroDetailsUseCase,
    private val getHeroImageUrlByNameUseCase: GetHeroImageUrlByNameUseCase
): ViewModel() {

    private val _heroFilterState = MutableStateFlow(HeroFilterState())
    val heroFilterState = _heroFilterState.asStateFlow()

    init {
        viewModelScope.launch {
            val eachHeroDetails = async { getEachHeroDetailsUseCase.execute() }
            val eachHeroImageUrls = async { getHeroImages(eachHeroDetails.await()) }

            _heroFilterState.update { it.copy(
                eachHeroDetails = eachHeroDetails.await(),
                eachHeroImageUrls = eachHeroImageUrls.await()
            ) }
        }
    }

    fun selectHero(heroId: Short?) {
        _heroFilterState.update { it.copy(
            heroSelected = heroId
        ) }
    }

    @Stable
    data class HeroFilterState(
        val heroSelected: Short? = null,
        val eachHeroDetails: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val eachHeroImageUrls: MutableMap<Short, String> = mutableMapOf(),
    )

    private suspend fun getHeroImages(heroDetails: MutableMap<Short, MutableMap<String, String>>):
            MutableMap<Short, String> {

        val heroImageUrls = mutableMapOf<Short, String>()
        viewModelScope.launch(Dispatchers.Default) {
            heroDetails.map { hero ->
                async(Dispatchers.IO) {
                    val heroId = hero.key
                    val shortName = hero.value["shortName"]?: "null"
                    val imageUrl = getHeroImageUrlByNameUseCase.execute(shortName)
                    heroId to imageUrl
                }
            }.awaitAll().forEach { (heroId, imageUrl) ->
                heroImageUrls[heroId] = imageUrl
            }
        }
        return heroImageUrls
    }
}