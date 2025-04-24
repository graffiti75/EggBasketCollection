package com.cericatto.eggbasketcollection.ui.basket

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BasketScreenViewModel @Inject constructor(): ViewModel() {

	private val _state = MutableStateFlow(BasketScreenState())
	val state: StateFlow<BasketScreenState> = _state.asStateFlow()

	fun onAction(action: BasketScreenAction) {
		when (action) {
			is BasketScreenAction.FlipCard -> TODO()
		}
	}

	init {
		_state.update { state ->
			state.copy(
				loading = false
			)
		}
	}
}