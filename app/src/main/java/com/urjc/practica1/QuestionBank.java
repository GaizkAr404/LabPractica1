package com.urjc.practica1;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Preguntas del quiz sobre geografía. Los textos están en {@code res/values/strings.xml}.
 */
public final class QuestionBank {

    private QuestionBank() {
    }

    public static List<Question> create(Context context) {
        Resources res = context.getResources();
        List<Question> questions = new ArrayList<>();

        questions.add(Question.textQuestion(
                res.getString(R.string.q_capital_australia),
                res.getStringArray(R.array.q_capital_australia_options),
                1,
                Question.AnswerType.RADIO));

        questions.add(Question.imageQuestion(
                res.getString(R.string.q_flag_country),
                R.drawable.flag_japan,
                res.getStringArray(R.array.q_flag_japan_options),
                2,
                Question.AnswerType.SPINNER));

        questions.add(Question.imageOptionsQuestion(
                res.getString(R.string.q_flag_italy),
                res.getStringArray(R.array.q_flag_italy_options),
                new int[]{
                        R.drawable.flag_france,
                        R.drawable.flag_ireland,
                        R.drawable.flag_belgium,
                        R.drawable.flag_italy
                },
                3));

        questions.add(Question.textQuestion(
                res.getString(R.string.q_longest_river),
                res.getStringArray(R.array.q_longest_river_options),
                0,
                Question.AnswerType.LIST));

        questions.add(Question.imageQuestion(
                res.getString(R.string.q_flag_country),
                R.drawable.flag_sweden,
                res.getStringArray(R.array.q_flag_sweden_options),
                1,
                Question.AnswerType.RADIO));

        return questions;
    }
}
