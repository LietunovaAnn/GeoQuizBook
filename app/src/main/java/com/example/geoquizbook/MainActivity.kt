package com.example.geoquizbook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CHEATER = "CHEATER"
private const val KEY_ENABLED = "enabled"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatNumTextView: TextView


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val currentEnabledButton = savedInstanceState?.getBoolean(KEY_ENABLED) ?: true
        quizViewModel.isButtonEnabled = currentEnabledButton

//        val isCheater = savedInstanceState?.getBoolean(KEY_CHEATER, false) ?: false
//        quizViewModel.isCheater = isCheater

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatNumTextView = findViewById(R.id.cheat_num)

//        val enabled = savedInstanceState?.getBoolean(KEY_ENABLED, true) ?: true

        trueButton.setOnClickListener {
            checkAnswer(true)
            quizViewModel.isButtonEnabled = false
            isEnabledButtons( quizViewModel.isButtonEnabled )
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            quizViewModel.isButtonEnabled = false
            isEnabledButtons( quizViewModel.isButtonEnabled )
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                val options = ActivityOptions
//                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
//                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
//            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
//            if (quizViewModel.cheatNum == 0) {
//                cheatButton.isEnabled = false
//                cheatNumTextView.setText(getString(R.id.cheat_num, quizViewModel.cheatNum))
//            } else {
//                quizViewModel.cheatNum--
//            }

        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            if (quizViewModel.isFinishQuiz()) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.correct_answer, quizViewModel.numCorrectAnswer)).show()
                quizViewModel.numCorrectAnswer = 0
                quizViewModel.cheatNum = 3
                cheatButton.isEnabled = true
            }

            updateQuestion()
            quizViewModel.isButtonEnabled = true
            isEnabledButtons(quizViewModel.isButtonEnabled)

            if (quizViewModel.isCheater) {
                checkAnswer(true)
            }

        }

        updateQuestion()
        isEnabledButtons( quizViewModel.isButtonEnabled )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionText)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == quizViewModel.currentQuestionAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
         if(messageResId == R.string.correct_toast) {
             quizViewModel.numCorrectAnswer ++
         }

        if ( quizViewModel.isCheater) {
            quizViewModel.numCheat++
            quizViewModel.isCheater = false
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun isEnabledButtons(enabled: Boolean) {
        trueButton.isEnabled = enabled
        falseButton.isEnabled = enabled
    }




}