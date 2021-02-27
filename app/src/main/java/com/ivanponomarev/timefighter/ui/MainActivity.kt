package com.ivanponomarev.timefighter.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.ivanponomarev.timefighter.R
import com.ivanponomarev.timefighter.base.longToast
import com.ivanponomarev.timefighter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GameViewModel by viewModels()
    private val bounceAnimation: Animation by lazy { loadAnimation(this, R.anim.bounce) }
    private val blinkAnimation: Animation by lazy { loadAnimation(this, R.anim.blink) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        observeScore()
        observeGameStatus()
    }

    private fun observeScore() {
        viewModel.score.observe(this, {
            binding.startButton.startAnimation(bounceAnimation)
            binding.scoreTextView.startAnimation(blinkAnimation)
        })
    }

    private fun observeGameStatus() {
        viewModel.gameRunning.observe(this, {
            if (it != false) return@observe
            longToast(R.string.game_finished, listOf(viewModel.score.value))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            displayAbout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayAbout() {
        val aboutDialog = supportFragmentManager.findFragmentByTag(AboutDialog.TAG) ?: AboutDialog()
        (aboutDialog as DialogFragment).show(supportFragmentManager, AboutDialog.TAG)
    }

}