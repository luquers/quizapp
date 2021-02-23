package com.example.proyectoandroid.ui.crearTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.AnswerQuizActivity;
import com.example.proyectoandroid.Question;
import com.example.proyectoandroid.Quiz;
import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.hacerTest.HacerTestAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CrearTestFragment extends Fragment implements CrearTestAdapter.OnQuestionListener{

    private ArrayList<Question> questions;
    private CrearTestAdapter adapter;
    private Button btnAddQuestion, btnSaveQuiz;
    private EditText etQuizName;
    private FirebaseFirestore db;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_crear_test, container, false);
        questions = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        etQuizName = root.findViewById(R.id.etQuizName);
        btnAddQuestion = root.findViewById(R.id.btnAddQuestion);
        btnSaveQuiz = root.findViewById(R.id.btnSaveQuiz);

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questions.add(new Question());
                adapter.notifyItemInserted(questions.size()-1);
            }
        });

        btnSaveQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        RecyclerView recyclerQuestions = (RecyclerView) root.findViewById(R.id.RecyclerCreateQuiz);
        adapter = new CrearTestAdapter(questions, this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(root.getContext(),2);
        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)){
            recyclerQuestions.setLayoutManager(mLayoutManager);
        }else {
            recyclerQuestions.setLayoutManager(new LinearLayoutManager(root.getContext()));
        }

        recyclerQuestions.setAdapter(adapter);

        return root;
    }

    private void saveQuiz(String quizName){
        ArrayList<Question> quizQuestions = adapter.getQuestions();

        if (quizName.isEmpty()){
            Toast.makeText(this.getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        for (Question q : quizQuestions){
            if (isNullOrEmpty(q.getQuestion())
                    || isNullOrEmpty(q.getOption1())
                    || isNullOrEmpty(q.getOption2())
                    || isNullOrEmpty(q.getOption3())
                    || q.getAnswerNumber() == 0){

                Toast.makeText(this.getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

        }

        uploadQuiz(quizName, quizQuestions);
        showConfirmationMessage();

    }

    private void uploadQuiz(String quizName, final ArrayList<Question> questionsFilled){


        Map<String, Object> quiz = new HashMap<>();
        quiz.put("name", quizName);


        db.collection("quizzes")
                .add(quiz)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        uploadQuestions(documentReference.getId(), questionsFilled);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void uploadQuestions(String quizId, ArrayList<Question> questionsFilled){
        ArrayList<Map<String, Object>> questionFields = new ArrayList<>();


        for ( Question q : questionsFilled){
            Map<String, Object> question = new HashMap<>();
            question.put("question", q.getQuestion());
            question.put("answer1", q.getOption1());
            question.put("answer2", q.getOption2());
            question.put("answer3", q.getOption3());
            question.put("correctAnswer", q.getAnswerNumber());
            questionFields.add(question);
        }

        WriteBatch batch = db.batch();
        for (Map<String, Object> qf : questionFields){
            DocumentReference docRef = db.collection("quizzes/" + quizId + "/questions").document();
            batch.set(docRef, qf);

        }
        batch.commit();
        clearUi();



    }

    @Override
    public void onQuestionClick(int position) {

        questions.remove(position);
        adapter.notifyItemRemoved(position);


    }

    public void onSaveClicked(){
        final String quizName = etQuizName.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setCancelable(true);
        builder.setTitle(quizName);
        builder.setMessage(R.string.confirm_save);
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveQuiz(quizName);

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmationMessage(){
        Toast.makeText(this.getContext(), R.string.quiz_saved, Toast.LENGTH_SHORT).show();
    }

    private void clearUi(){
        etQuizName.setText("");
        int length = questions.size();
        questions.clear();
        adapter.notifyItemRangeRemoved(0, length);
    }

    private boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }
}