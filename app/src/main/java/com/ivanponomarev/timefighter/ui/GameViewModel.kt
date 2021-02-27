package com.ivanponomarev.timefighter.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanponomarev.timefighter.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {

    private val _secondsRemaining = MutableLiveData(GAME_DURATION_SECS)
    val secondsRemaining: LiveData<Int> = _secondsRemaining

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _highScores = MutableLiveData(listOf(0, 0, 0))
    val highScores: LiveData<List<Int>> = _highScores

    private val _gameRunning = SingleLiveEvent<Boolean>()
    val gameRunning: LiveData<Boolean> = _gameRunning

    private var isReady = true

    fun incrementScore() {
        if (!isReady) return
        if (gameRunning.value != true) {
            _score.value = 0
            startTimer()
        }
        _score.value = _score.value!! + 1
    }

    fun setReady() {
        isReady = true
        _score.value = 0
        _secondsRemaining.value = GAME_DURATION_SECS
    }

    private fun startTimer() {
        _secondsRemaining.value = GAME_DURATION_SECS
        _gameRunning.value = true
        // todo inject dispatchers instead of hardcoding them
        viewModelScope.launch(Dispatchers.IO) {
            var remaining = GAME_DURATION_SECS
            while (remaining-- > 0) {
                delay(1000)
                _secondsRemaining.postValue(remaining)
            }
            setHighScores()
        }
    }

    // todo inject dispatchers instead of hardcoding them
    private suspend fun setHighScores() = withContext(Dispatchers.Main) {
        val scores = _highScores.value?.toMutableList() ?: arrayListOf()
        scores.add(score.value ?: 0)
        val newScores = scores.apply {
            sortDescending()
            removeLast()
        }
        _highScores.value = newScores
        _gameRunning.value = false
        isReady = false
    }

    private companion object {
        const val GAME_DURATION_SECS = 10
    }

}