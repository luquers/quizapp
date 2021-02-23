package com.example.proyectoandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AnswerQuizActivity extends AppCompatActivity {

    private static final long COUNTDOWN = 30200;

    TextView tvQuizName, tvQuestion, tvScore, tvQuestionNumber, tvCountDown;
    RadioButton rbAnswer1, rbAnswer2, rbAnswer3;
    RadioGroup rgAnswers;
    ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeft;
    Button btnConfirm;
    ArrayList<Question> questions;
    Question currentQuestion;
    FirebaseFirestore mFirestore;
    int score, questionCounter, questionCountTotal, questionNumber;
    private long backPressedTime;
    boolean answered;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_quiz);

        questions = new ArrayList<>();

        score = 0;
        questionCounter = 0;

        Intent intent = getIntent();
        mFirestore = FirebaseFirestore.getInstance();

        tvScore = findViewById(R.id.tvScore);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCountDown = findViewById(R.id.tvTimer);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rgAnswers = findViewById(R.id.rgAnswers);
        textColorDefaultRb = rbAnswer1.getTextColors();
        textColorDefaultCd = tvCountDown.getTextColors();

        tvQuizName = findViewById(R.id.quizName);
        tvQuizName.setText(intent.getStringExtra("name"));
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListener();
            }
        });

        String id = intent.getStringExtra("id");
        getQuestions(id);


    }

    private void getQuestions(String id){
        mFirestore.collection("quizzes/" + id + "/questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "funsiona");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Success");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                questions.add(buildQuestion(document));
                            }
                            questionCountTotal = questions.size();

                            printNextQuestion();

                        } else {
                            Log.d(TAG, "No funciona" );
                        }
                    }
                });
    }
    private Question buildQuestion(QueryDocumentSnapshot document){
        String id = document.getId();
        String question = document.getString("question");
        String option1 = document.getString("answer1");
        String option2 = document.getString("answer2");
        String option3 = document.getString("answer3");
        long answerNumber = document.getLong("correctAnswer");

        return new Question(id, question, option1, option2, option3, answerNumber);

    }

    private void printNextQuestion(){


        rbAnswer1.setTextColor(textColorDefaultRb);
        rbAnswer2.setTextColor(textColorDefaultRb);
        rbAnswer3.setTextColor(textColorDefaultRb);
        rgAnswers.clearCheck();

    if (questionCounter < questionCountTotal){
        questionNumber = questionCounter + 1;
        currentQuestion = questions.get(questionCounter);
        tvQuestion.setText(questionNumber + ".- "+currentQuestion.getQuestion());

        rbAnswer1.setText(currentQuestion.getOption1());
        rbAnswer2.setText(currentQuestion.getOption2());
        rbAnswer3.setText(currentQuestion.getOption3());
        questionCounter++;

        updateQuestionNumber();//TODO show questions
        updateScore();
        answered = false;
        btnConfirm.setText(R.string.confirm);
        
        timeLeft = COUNTDOWN;
        startCountDown();
    } else {
        finishQuiz();
    }



    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvCountDown.setText(timeFormatted);

        if (timeLeft < 10000){
            tvCountDown.setTextColor(Color.RED);
        } else{
            tvCountDown.setTextColor(textColorDefaultCd);
        }


    }

    private void finishQuiz() {
        finish();
    }

    private void buttonListener(){
        if (!answered){
            if (rbAnswer1.isChecked() || rbAnswer2.isChecked() || rbAnswer3.isChecked()){
                checkAnswer();

            } else {
                Toast.makeText(AnswerQuizActivity.this, R.string.answer_not_checked, Toast.LENGTH_SHORT).show();
            }
        } else {
            printNextQuestion();
        }


    }

    private void checkAnswer(){
        answered = true;

        countDownTimer.cancel();
        RadioButton rbSelected = findViewById(rgAnswers.getCheckedRadioButtonId());
        int answerNumber = rgAnswers.indexOfChild(rbSelected)+1;
        long correctAnswer = currentQuestion.getAnswerNumber();


        if(answerNumber == (int) correctAnswer){

            score++;
            updateScore();

        }
        showSolution();
    }

    private void updateScore (){
        tvScore.setText(getText(R.string.score) + ": " + score + "/" + questionCountTotal);
    }

    private void updateQuestionNumber(){
        tvQuestionNumber.setText(getText(R.string.question) + ": " + questionNumber + "/" + questionCountTotal);
    }

    private void showSolution(){
        rbAnswer1.setTextColor(Color.RED);
        rbAnswer2.setTextColor(Color.RED);
        rbAnswer3.setTextColor(Color.RED);

        switch ((int)(currentQuestion.getAnswerNumber())){
            case 1:
                rbAnswer1.setTextColor(Color.GREEN);
                break;
            case 2:
                rbAnswer2.setTextColor(Color.GREEN);
                break;
            case 3:
                rbAnswer3.setTextColor(Color.GREEN);
                break;


        }
        if (questionCounter < questionCountTotal){
            btnConfirm.setText(R.string.next);
        } else {
            showScore();
            btnConfirm.setText(R.string.finish);
        }
    }
    private void showScore(){
        String title = "";
        String message = "";

        if (score < ((double)questionCountTotal)/2){
            title = (String) getText(R.string.failed);
        } else {
            title = (String) getText(R.string.congratulations);
        }
        message = (String) getText(R.string.finalScore) +" " + score + "/" + questionCountTotal + ".";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onBackPressed(){

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        } else{
            Toast.makeText(this, R.string.press_again_to_finish, Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}