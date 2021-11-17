package com.highresults.questions

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG = "Main Activity"
private const val KEY_INDEX = "curIndex"
private const val KEY_ANSWER_COUNTER = "answerCounter"
private const val KEY_CORRECT = "correctAnswerCounter"
private const val KEY_ANSWERED_QUESTIONS = "AnsweredQuestions"
private const val KEY_CHEAT_COUNTER = "CheatedAnswers"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionView: TextView
    private val questionViewModel: QuestionViewModel by lazy {
        ViewModelProvider(this).get(QuestionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Got a ViewModel: $questionViewModel")



        trueButton = findViewById(R.id.true_but)
        falseButton = findViewById(R.id.false_but)
        cheatButton = findViewById(R.id.cheat_button)
        nextButton = findViewById(R.id.next_but)
        prevButton = findViewById(R.id.prev_but)
        questionView = findViewById(R.id.question_field)
        checkButtonsEnable()
        updateQuestion()


        trueButton.setOnClickListener {
            checkAnswer(true)
            updateViewAfterAnswer()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            updateViewAfterAnswer()
        }

        nextButton.setOnClickListener {
            questionViewModel.moveToNext()
            updateQuestion()
            checkButtonsEnable()
            if (questionViewModel.cheatAttempExchaused)
                cheatButton.isEnabled = false
        }

        prevButton.setOnClickListener {
            questionViewModel.moveToPrev()
            updateQuestion()
            checkButtonsEnable()
            if (questionViewModel.cheatAttempExchaused)
                cheatButton.isEnabled = false
        }

        questionView.setOnClickListener {
            questionViewModel.moveToNext()
            updateQuestion()
            checkButtonsEnable()
            if (questionViewModel.cheatAttempExchaused)
                cheatButton.isEnabled = false
        }

        cheatButton.setOnClickListener { view ->
            val answerISTrue = questionViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerISTrue, questionViewModel.cheatedQuestions[questionViewModel.currentQuestIndex], questionViewModel.cheatedAnswerCounter)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        checkAllQuestionsAnswered()
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val currentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
        questionViewModel.currentQuestIndex = currentIndex
        val answerCounter = savedInstanceState.getInt(KEY_ANSWER_COUNTER, 0)
        questionViewModel.answerCounter = answerCounter
        val cheatAnswerCounter = savedInstanceState.getInt(KEY_CHEAT_COUNTER, 0)
        questionViewModel.cheatedAnswerCounter = cheatAnswerCounter
        val correctAnswersCounter = savedInstanceState.getInt(KEY_CORRECT, 0)
        questionViewModel.correctAnswerCounter = correctAnswersCounter
        val answeredQuestions = savedInstanceState.getBooleanArray(KEY_ANSWERED_QUESTIONS)
                ?: BooleanArray(questionViewModel.continuedQuestions.size)
        questionViewModel.continuedQuestions = answeredQuestions
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, questionViewModel.currentQuestIndex)
        outState.putInt(KEY_ANSWER_COUNTER, questionViewModel.answerCounter)
        outState.putInt(KEY_CHEAT_COUNTER, questionViewModel.cheatedAnswerCounter)
        outState.putInt(KEY_CORRECT, questionViewModel.correctAnswerCounter)
        outState.putBooleanArray(KEY_ANSWERED_QUESTIONS, questionViewModel.continuedQuestions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            val isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (isCheater && !questionViewModel.cheatedQuestions[questionViewModel.currentQuestIndex]) {
                questionViewModel.cheatedAnswerCounter++
                if (questionViewModel.cheatedAnswerCounter >= cheatsPermissed)
                    questionViewModel.cheatAttempExchaused = true
                questionViewModel.cheatedQuestions[questionViewModel.currentQuestIndex] = isCheater
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    private fun updateQuestion() {
        questionView.setText(questionViewModel.currentQuestionText)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionViewModel.currentQuestionAnswer
        val messageId = when {
            questionViewModel.cheatedQuestions[questionViewModel.currentQuestIndex] -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> {
                questionViewModel.correctAnswerCounter--
                R.string.incorrect_toast
            }
        }
        questionViewModel.correctAnswerCounter++
        val toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    private fun updateViewAfterAnswer() {
        questionViewModel.continuedQuestions[questionViewModel.currentQuestIndex] = true
        falseButton.isEnabled = false
        trueButton.isEnabled = false
        cheatButton.isEnabled = false
        questionViewModel.answerCounter++
        checkAllQuestionsAnswered()
    }

    private fun checkAllQuestionsAnswered() {
        if (questionViewModel.answerCounter == questionViewModel.continuedQuestions.size) {
            nextButton.isEnabled = false
            prevButton.isEnabled = false
            questionView.setOnClickListener(null)
            questionView.text = resources.getString(
                    R.string.result,
                    questionViewModel.correctAnswerCounter,
                    questionViewModel.continuedQuestions.size, questionViewModel.cheatedAnswerCounter)
        }
    }

    private fun checkButtonsEnable() {
        if (questionViewModel.continuedQuestions[questionViewModel.currentQuestIndex]) {
            falseButton.isEnabled = false
            trueButton.isEnabled = false
            cheatButton.isEnabled = false
        } else {
            falseButton.isEnabled = true
            trueButton.isEnabled = true
            cheatButton.isEnabled = true
        }
    }
}