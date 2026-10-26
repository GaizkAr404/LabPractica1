package com.urjc.practica1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla de juego: muestra las preguntas una a una y lleva la puntuación.
 */
public class MainActivity extends AppCompatActivity {

    private static final int POINTS_CORRECT = 3;
    private static final int POINTS_WRONG = 2;

    private static final String STATE_ORDER = "order";
    private static final String STATE_INDEX = "index";
    private static final String STATE_SCORE = "score";
    private static final String STATE_CORRECT_COUNT = "correctCount";
    private static final String STATE_SELECTED_IMAGE = "selectedImage";
    private static final String STATE_AWAITING_DECISION = "awaitingDecision";

    private List<Question> allQuestions;
    /** Orden aleatorio de las preguntas en la partida actual (índices de allQuestions). */
    private ArrayList<Integer> order;
    private int currentIndex;
    private int score;
    private int correctCount;
    private int selectedImageIndex = -1;
    /** True mientras se muestra el diálogo de fallo y el jugador aún no ha elegido. */
    private boolean awaitingDecision;

    private TextView tvProgress;
    private TextView tvScore;
    private LinearProgressIndicator progressIndicator;
    private TextView tvQuestion;
    private ImageView ivQuestion;
    private View radioContainer;
    private RadioGroup rgOptions;
    private RadioButton[] radioButtons;
    private Spinner spOptions;
    private ListView lvOptions;
    private GridLayout glImageOptions;
    private MaterialCardView[] imageCards;
    private ImageView[] imageViews;
    private Button btnAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        allQuestions = QuestionBank.create(this);

        if (savedInstanceState != null) {
            order = savedInstanceState.getIntegerArrayList(STATE_ORDER);
            currentIndex = savedInstanceState.getInt(STATE_INDEX);
            score = savedInstanceState.getInt(STATE_SCORE);
            correctCount = savedInstanceState.getInt(STATE_CORRECT_COUNT);
            awaitingDecision = savedInstanceState.getBoolean(STATE_AWAITING_DECISION);
            showQuestion();
            selectImageOption(savedInstanceState.getInt(STATE_SELECTED_IMAGE, -1));
            if (awaitingDecision) {
                showWrongAnswerDialog();
            }
        } else {
            startNewGame();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(STATE_ORDER, order);
        outState.putInt(STATE_INDEX, currentIndex);
        outState.putInt(STATE_SCORE, score);
        outState.putInt(STATE_CORRECT_COUNT, correctCount);
        outState.putInt(STATE_SELECTED_IMAGE, selectedImageIndex);
        outState.putBoolean(STATE_AWAITING_DECISION, awaitingDecision);
    }

    private void bindViews() {
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        progressIndicator = findViewById(R.id.progressIndicator);
        tvQuestion = findViewById(R.id.tvQuestion);
        ivQuestion = findViewById(R.id.ivQuestion);
        radioContainer = findViewById(R.id.radioContainer);
        rgOptions = findViewById(R.id.rgOptions);
        radioButtons = new RadioButton[]{
                findViewById(R.id.rbOption0),
                findViewById(R.id.rbOption1),
                findViewById(R.id.rbOption2),
                findViewById(R.id.rbOption3)
        };
        spOptions = findViewById(R.id.spOptions);
        lvOptions = findViewById(R.id.lvOptions);
        glImageOptions = findViewById(R.id.glImageOptions);
        imageCards = new MaterialCardView[]{
                findViewById(R.id.cardOption0),
                findViewById(R.id.cardOption1),
                findViewById(R.id.cardOption2),
                findViewById(R.id.cardOption3)
        };
        imageViews = new ImageView[]{
                findViewById(R.id.ivOption0),
                findViewById(R.id.ivOption1),
                findViewById(R.id.ivOption2),
                findViewById(R.id.ivOption3)
        };
        btnAnswer = findViewById(R.id.btnAnswer);

        for (int i = 0; i < imageCards.length; i++) {
            final int index = i;
            imageCards[i].setOnClickListener(v -> selectImageOption(index));
        }
        btnAnswer.setOnClickListener(v -> checkAnswer());
        findViewById(R.id.btnRestart).setOnClickListener(v -> startNewGame());
    }

    private void startNewGame() {
        order = new ArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            order.add(i);
        }
        Collections.shuffle(order);
        currentIndex = 0;
        score = 0;
        correctCount = 0;
        awaitingDecision = false;
        showQuestion();
    }

    private Question currentQuestion() {
        return allQuestions.get(order.get(currentIndex));
    }

    private void showQuestion() {
        Question question = currentQuestion();
        int total = order.size();

        tvProgress.setText(getString(R.string.progress_format, currentIndex + 1, total));
        progressIndicator.setMax(total);
        progressIndicator.setProgress(currentIndex + 1);
        updateScore();

        tvQuestion.setText(question.getText());
        if (question.hasImage()) {
            ivQuestion.setImageResource(question.getImageResId());
            ivQuestion.setVisibility(View.VISIBLE);
        } else {
            ivQuestion.setVisibility(View.GONE);
        }

        Question.AnswerType type = question.getAnswerType();
        radioContainer.setVisibility(type == Question.AnswerType.RADIO ? View.VISIBLE : View.GONE);
        spOptions.setVisibility(type == Question.AnswerType.SPINNER ? View.VISIBLE : View.GONE);
        lvOptions.setVisibility(type == Question.AnswerType.LIST ? View.VISIBLE : View.GONE);
        glImageOptions.setVisibility(type == Question.AnswerType.IMAGES ? View.VISIBLE : View.GONE);

        switch (type) {
            case RADIO:
                bindRadioOptions(question.getOptions());
                break;
            case SPINNER:
                bindSpinnerOptions(question.getOptions());
                break;
            case LIST:
                bindListOptions(question.getOptions());
                break;
            case IMAGES:
                bindImageOptions(question.getOptions(), question.getOptionImageResIds());
                break;
        }
    }

    private void bindRadioOptions(String[] options) {
        rgOptions.clearCheck();
        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setText(options[i]);
        }
    }

    private void bindSpinnerOptions(String[] options) {
        // La primera entrada es una pista que no cuenta como respuesta.
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.spinner_hint));
        Collections.addAll(items, options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOptions.setAdapter(adapter);
    }

    private void bindListOptions(String[] options) {
        lvOptions.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, options));
        lvOptions.clearChoices();
    }

    private void bindImageOptions(String[] labels, int[] imageResIds) {
        for (int i = 0; i < imageCards.length; i++) {
            imageViews[i].setImageResource(imageResIds[i]);
            imageCards[i].setContentDescription(labels[i]);
        }
        selectImageOption(-1);
    }

    private void selectImageOption(int index) {
        selectedImageIndex = index;
        for (int i = 0; i < imageCards.length; i++) {
            imageCards[i].setChecked(i == index);
        }
    }

    /** Devuelve el índice de la opción elegida, o -1 si no se ha elegido ninguna. */
    private int getSelectedIndex() {
        switch (currentQuestion().getAnswerType()) {
            case RADIO:
                for (int i = 0; i < radioButtons.length; i++) {
                    if (radioButtons[i].isChecked()) {
                        return i;
                    }
                }
                return -1;
            case SPINNER:
                return spOptions.getSelectedItemPosition() - 1;
            case LIST:
                return lvOptions.getCheckedItemPosition();
            case IMAGES:
                return selectedImageIndex;
            default:
                return -1;
        }
    }

    private void checkAnswer() {
        int selected = getSelectedIndex();
        if (selected < 0) {
            Toast.makeText(this, R.string.select_answer_first, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentQuestion().isCorrect(selected)) {
            score += POINTS_CORRECT;
            correctCount++;
            Snackbar.make(findViewById(R.id.main), R.string.correct_answer_message,
                            Snackbar.LENGTH_SHORT)
                    .setAnchorView(btnAnswer)
                    .show();
            goToNextQuestion();
        } else {
            score -= POINTS_WRONG;
            updateScore();
            awaitingDecision = true;
            showWrongAnswerDialog();
        }
    }

    private void showWrongAnswerDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.wrong_answer_title)
                .setMessage(getString(R.string.wrong_answer_message,
                        currentQuestion().getCorrectOption()))
                .setCancelable(false)
                .setPositiveButton(R.string.continue_game, (dialog, which) -> {
                    awaitingDecision = false;
                    goToNextQuestion();
                })
                .setNegativeButton(R.string.start_over, (dialog, which) -> startNewGame())
                .show();
    }

    private void goToNextQuestion() {
        if (currentIndex + 1 < order.size()) {
            currentIndex++;
            showQuestion();
        } else {
            showResult();
        }
    }

    private void showResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_SCORE, score);
        intent.putExtra(ResultActivity.EXTRA_CORRECT_COUNT, correctCount);
        intent.putExtra(ResultActivity.EXTRA_TOTAL, order.size());
        startActivity(intent);
        finish();
    }

    private void updateScore() {
        tvScore.setText(getString(R.string.score_format, score));
    }
}
