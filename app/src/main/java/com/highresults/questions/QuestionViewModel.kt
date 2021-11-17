package com.highresults.questions

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuestionViewModel"
class QuestionViewModel: ViewModel() {
    private val questionsList = listOf(
        Question(R.string.quest_austr, false),
        Question(R.string.quest_rus, false),
        Question(R.string.quest_Jap, true),
        Question(R.string.quest_braz, true),
        Question(R.string.quest_ind, false)
    )


    var continuedQuestions:BooleanArray = BooleanArray(questionsList.size)
    var answerCounter = 0
    var cheatedAnswerCounter = 0
    var correctAnswerCounter = 0
    var currentQuestIndex = 0
    var cheatedQuestions:BooleanArray = BooleanArray(questionsList.size)
    var cheatAttempExchaused  = false

    val currentQuestionAnswer:Boolean
        get() = questionsList[currentQuestIndex].answer
    val currentQuestionText:Int
        get() = questionsList[currentQuestIndex].questionId


    init {
        Log.d(TAG,"ViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"ViewModel destroy")
    }

    fun moveToNext(){
        currentQuestIndex = ++currentQuestIndex % questionsList.size
    }

    fun moveToPrev(){
        currentQuestIndex--
        if(currentQuestIndex < 0)
            currentQuestIndex = questionsList.size - 1
    }



}