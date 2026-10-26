package com.urjc.practica1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Muestra la puntuación final y permite empezar una nueva partida.
 */
public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "com.urjc.practica1.SCORE";
    public static final String EXTRA_CORRECT_COUNT = "com.urjc.practica1.CORRECT_COUNT";
    public static final String EXTRA_TOTAL = "com.urjc.practica1.TOTAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int score = intent.getIntExtra(EXTRA_SCORE, 0);
        int correctCount = intent.getIntExtra(EXTRA_CORRECT_COUNT, 0);
        int total = intent.getIntExtra(EXTRA_TOTAL, 0);

        TextView tvFinalScore = findViewById(R.id.tvFinalScore);
        TextView tvCorrectCount = findViewById(R.id.tvCorrectCount);
        TextView tvResultMessage = findViewById(R.id.tvResultMessage);

        tvFinalScore.setText(String.valueOf(score));
        tvCorrectCount.setText(getString(R.string.correct_count_format, correctCount, total));
        tvResultMessage.setText(resultMessage(correctCount, total));

        findViewById(R.id.btnPlayAgain).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private int resultMessage(int correctCount, int total) {
        if (correctCount == total) {
            return R.string.result_excellent;
        } else if (correctCount * 2 >= total) {
            return R.string.result_good;
        } else {
            return R.string.result_bad;
        }
    }
}
