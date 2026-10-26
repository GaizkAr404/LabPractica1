package com.urjc.practica1;

import androidx.annotation.DrawableRes;

/**
 * Una pregunta del quiz con cuatro opciones, de las cuales solo una es correcta.
 */
public class Question {

    /** Control de la interfaz con el que se muestran las opciones de respuesta. */
    public enum AnswerType {
        RADIO,
        SPINNER,
        LIST,
        IMAGES
    }

    private final String text;
    @DrawableRes
    private final int imageResId;
    private final String[] options;
    private final int[] optionImageResIds;
    private final int correctIndex;
    private final AnswerType answerType;

    private Question(String text, @DrawableRes int imageResId, String[] options,
                     int[] optionImageResIds, int correctIndex, AnswerType answerType) {
        if (correctIndex < 0 || correctIndex >= options.length) {
            throw new IllegalArgumentException("Índice de respuesta correcta fuera de rango");
        }
        this.text = text;
        this.imageResId = imageResId;
        this.options = options;
        this.optionImageResIds = optionImageResIds;
        this.correctIndex = correctIndex;
        this.answerType = answerType;
    }

    /** Pregunta de texto con opciones de texto. */
    public static Question textQuestion(String text, String[] options, int correctIndex,
                                        AnswerType answerType) {
        return new Question(text, 0, options, null, correctIndex, answerType);
    }

    /** Pregunta sobre una imagen con opciones de texto. */
    public static Question imageQuestion(String text, @DrawableRes int imageResId,
                                         String[] options, int correctIndex,
                                         AnswerType answerType) {
        return new Question(text, imageResId, options, null, correctIndex, answerType);
    }

    /**
     * Pregunta de texto cuyas opciones son imágenes. {@code labels} se usa como
     * descripción accesible de cada imagen y para indicar la respuesta correcta.
     */
    public static Question imageOptionsQuestion(String text, String[] labels,
                                                int[] optionImageResIds, int correctIndex) {
        if (labels.length != optionImageResIds.length) {
            throw new IllegalArgumentException("Cada imagen necesita su etiqueta");
        }
        return new Question(text, 0, labels, optionImageResIds, correctIndex, AnswerType.IMAGES);
    }

    public String getText() {
        return text;
    }

    public boolean hasImage() {
        return imageResId != 0;
    }

    @DrawableRes
    public int getImageResId() {
        return imageResId;
    }

    public String[] getOptions() {
        return options;
    }

    public int[] getOptionImageResIds() {
        return optionImageResIds;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getCorrectOption() {
        return options[correctIndex];
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctIndex;
    }
}
