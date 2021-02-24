package com.ivanponomarev.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat

class MainActivity : AppCompatActivity() {

    private var score = 0
    private var highScoresArray = IntArray(3)

    private var defaultInitialTime: Long = 10000
    private var initialTime: Long = 10000
    private val timerInterval: Long = 1000

    private var initialHighScoreOne: Int? = 0
    private var initialHighScoreTwo: Int? = 0
    private var initialHighScoreThree: Int? = 0

    private var timeLeftStorage: Long = 10000

    private var gameStarted: Boolean = false

    private lateinit var startButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var timeLeftTextView: TextView

    private lateinit var highScoreTextViewOne: TextView
    private lateinit var highScoreTextViewTwo: TextView
    private lateinit var highScoreTextViewThree: TextView

    private lateinit var countDownTimer: CountDownTimer

    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val HIGH_SCORES_KEY = "HIGH_SCORES_KEY"
        private const val GAME_STARTED_KEY = "GAME_STARTED_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start_button)
        scoreTextView = findViewById(R.id.score_textView)
        timeLeftTextView = findViewById(R.id.time_left_textView)

        // set initial high scores
        highScoreTextViewOne = findViewById(R.id.highscore_textview_one)
        highScoreTextViewTwo = findViewById(R.id.highscore_textview_two)
        highScoreTextViewThree = findViewById(R.id.highscore_textview_three)

        // set animation
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)

        // set text views text
        scoreTextView.text = getString(R.string.score, score)
        highScoreTextViewOne.text = getString(R.string.high_score_one, initialHighScoreOne)
        highScoreTextViewTwo.text = getString(R.string.high_score_two, initialHighScoreTwo)
        highScoreTextViewThree.text = getString(R.string.high_score_three, initialHighScoreThree)

        startButton.setOnClickListener { view ->
            view.startAnimation(bounceAnimation)
            // just increment if game already started
            incrementScore()
            scoreTextView.startAnimation(blinkAnimation)
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            initialTime = savedInstanceState.getLong(TIME_LEFT_KEY)
            gameStarted = savedInstanceState.getBoolean(GAME_STARTED_KEY)

            highScoresArray = savedInstanceState.getIntArray(HIGH_SCORES_KEY)!!

            restoreGame()

        } else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.actionAbout)
            showInfo()

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save values and stop timer
        outState.putInt(SCORE_KEY, score)
        outState.putIntArray(HIGH_SCORES_KEY, highScoresArray)
        outState.putLong(TIME_LEFT_KEY, timeLeftStorage)
        outState.putBoolean(GAME_STARTED_KEY, gameStarted)

        countDownTimer.cancel()
    }

    private fun resetGame() {
        // set score at default value and update textview
        score = 0
        scoreTextView.text = getString(R.string.score, score)

        // set time left at default and update text view
        val initialTimeLeft = defaultInitialTime / 1000
        timeLeftStorage = 10000
        timeLeftTextView.text = getString(R.string.time_left, initialTimeLeft)

        // create countdown object
        countDownTimer = object : CountDownTimer(initialTime, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {

                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)

                timeLeftStorage = millisUntilFinished
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun incrementScore() {
        if (!gameStarted)
            startGame()
        score += 1
        scoreTextView.text = getString(R.string.score, score)
    }

    private fun startGame() {
        gameStarted = true
        countDownTimer.start()
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_finished, score), Toast.LENGTH_LONG + 100).show()
        setHighScores(score)
        resetGame()
    }

    private fun setHighScores(score: Int) {
        for (x in 0..2) {
            if (highScoresArray[x] < score) {
                when (x) {
                    0 -> {
                        highScoresArray[x+2] = highScoresArray[x+1]
                        highScoresArray[x+1] = highScoresArray[x]
                        highScoresArray[x] = score
                    }
                    1 -> {
                        highScoresArray[x+1] = highScoresArray[x]
                        highScoresArray[x] = score
                    }
                    2 -> highScoresArray[x] = score
                }

                highScoreTextViewThree.text = getString(R.string.high_score_three, highScoresArray[2])
                highScoreTextViewTwo.text = getString(R.string.high_score_two, highScoresArray[1])
                highScoreTextViewOne.text = getString(R.string.high_score_one, highScoresArray[0])

                return
            }
        }
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            builder.setMessage(Html.fromHtml("<a href=\"https://www.linkedin.com/in/ivan-ponomarev-38222018a\">Created by Ivan Ponomarev</a>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        } else {

            builder.setMessage(Html.fromHtml("<a href=\"https://www.linkedin.com/in/ivan-ponomarev-38222018a\">Created by Ivan Ponomarev</a>"))
        }
        // initialize builder object to work with it later
        val alert = builder.create()
        alert.show()
        // Make the textview clickable. Must be called after show()
        alert.findViewById<TextView>(android.R.id.message)!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun restoreGame() {
        // restore score text views
        scoreTextView.text = getString(R.string.score, score)
        highScoreTextViewThree.text = getString(R.string.high_score_three, highScoresArray[2])
        highScoreTextViewTwo.text = getString(R.string.high_score_two, highScoresArray[1])
        highScoreTextViewOne.text = getString(R.string.high_score_one, highScoresArray[0])

        // restore time text view
        val restoredInitialTimeLeft = initialTime / 1000
        timeLeftTextView.text = getString(R.string.time_left, restoredInitialTimeLeft)

        // initialize countdown object
        countDownTimer = object : CountDownTimer(initialTime, timerInterval) {
            // change time on every tick
            override fun onTick(millisUntilFinished: Long) {

                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left, timeLeft)

                timeLeftStorage = millisUntilFinished
            }

            override fun onFinish() {
                endGame()
            }
        }

        when (gameStarted) {
            true -> startGame()
            false -> resetGame()
        }
    }

}