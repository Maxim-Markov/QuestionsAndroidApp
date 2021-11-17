package com.highresults.questions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_ANSWER_SHOWN = "com.highresults.android.questions.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.highresults.android.questions.answer_is_true"
private const val EXTRA_QUESTION_CHEATED = "com.highresults.android.questions.question_was_cheated"
private const val EXTRA_CHEATER_COUNTER = "com.highresults.android.questions.cheats_count"
private const val KEY_QUESTION_CHEATED = "question_was_cheated"
private const val KEY_CHEATS_LEFT = "cheat_left"
private const val KEY_IS_CHEATER = "is_cheating"
private const val KEY_TEXTVIEW_ANSWER_IS_TRUE = "textview_answer"

const val cheatsPermissed = 3

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private var questionCheated = false
    private var isCheater = false
    private var cheatsLeft = cheatsPermissed
    private lateinit var answerTextView: TextView
    private lateinit var cheatLeftTextView: TextView
    private lateinit var showAnswerButton: Button

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean, questionCheated: Boolean, cheaterCounter: Int): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_QUESTION_CHEATED, questionCheated)
                putExtra(EXTRA_CHEATER_COUNTER, cheaterCounter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        questionCheated = intent.getBooleanExtra(EXTRA_QUESTION_CHEATED, false)
        cheatsLeft = cheatsPermissed - intent.getIntExtra(EXTRA_CHEATER_COUNTER, cheatsPermissed)

        answerTextView = findViewById(R.id.answer_text_view)
        cheatLeftTextView = findViewById(R.id.cheats_left)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            val answerText = if (answerIsTrue)
                R.string.button_true
            else
                R.string.button_false
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
            isCheater = true
            if (!questionCheated) {
                cheatsLeft--
                questionCheated = true
            }
            cheatLeftTextView.text = resources.getString(R.string.cheats_left, cheatsLeft)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false)
        questionCheated = savedInstanceState.getBoolean(KEY_QUESTION_CHEATED, false)
        cheatsLeft = savedInstanceState.getInt(KEY_CHEATS_LEFT, 0)
        answerTextView.text = savedInstanceState.getString(KEY_TEXTVIEW_ANSWER_IS_TRUE, "Ответ неизвестен")
        setAnswerShownResult(isCheater)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_CHEATER, isCheater)
        outState.putBoolean(KEY_QUESTION_CHEATED, questionCheated)
        outState.putInt(KEY_CHEATS_LEFT, cheatsLeft)
        outState.putString(KEY_TEXTVIEW_ANSWER_IS_TRUE, answerTextView.text.toString())
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
}