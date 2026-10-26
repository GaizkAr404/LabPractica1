package com.urjc.practica1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QuestionTest {

    private static final String[] OPTIONS = {"A", "B", "C", "D"};

    @Test
    public void isCorrect_onlyForCorrectIndex() {
        Question question = Question.textQuestion("?", OPTIONS, 2, Question.AnswerType.RADIO);

        assertTrue(question.isCorrect(2));
        assertFalse(question.isCorrect(0));
        assertFalse(question.isCorrect(-1));
        assertEquals("C", question.getCorrectOption());
    }

    @Test
    public void textQuestion_hasNoImage() {
        Question question = Question.textQuestion("?", OPTIONS, 0, Question.AnswerType.LIST);

        assertFalse(question.hasImage());
    }

    @Test
    public void imageOptionsQuestion_usesImagesAnswerType() {
        Question question = Question.imageOptionsQuestion("?", OPTIONS, new int[]{1, 2, 3, 4}, 1);

        assertEquals(Question.AnswerType.IMAGES, question.getAnswerType());
        assertEquals(4, question.getOptionImageResIds().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void correctIndexOutOfRange_throws() {
        Question.textQuestion("?", OPTIONS, 4, Question.AnswerType.RADIO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void imageOptionsQuestion_withMismatchedLabels_throws() {
        Question.imageOptionsQuestion("?", OPTIONS, new int[]{1, 2}, 0);
    }
}
