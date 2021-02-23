package com.example.proyectoandroid.ui.hacerTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.AnswerQuizActivity;
import com.example.proyectoandroid.Quiz;
import com.example.proyectoandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class HacerTestFragment extends Fragment implements HacerTestAdapter.OnQuizListener {

    ArrayList<Quiz> cuestionarios;
    RecyclerView recyclerCuestionarios;
    FirebaseFirestore mFirestore;
    HacerTestAdapter adaptadorCuestionarios;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_hacer_test, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        recyclerCuestionarios = (RecyclerView) root.findViewById(R.id.recyclerListaCuestionarios);


        GridLayoutManager mLayoutManager = new GridLayoutManager(root.getContext(),2);
        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)){
            recyclerCuestionarios.setLayoutManager(mLayoutManager);
        }else {
            recyclerCuestionarios.setLayoutManager(new LinearLayoutManager(root.getContext()));
        }


        cuestionarios = new ArrayList<>();

        mFirestore.collection("quizzes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                cuestionarios.add(buildQuiz(document));
                            }
                            startQuizRecyclerView();
                        } else {
                            Log.d(TAG, "NO FUNCIONA" );
                        }
                    }
                });


        return root;
    }






    private Quiz buildQuiz(QueryDocumentSnapshot document){
        String name = (String) document.get("name");
        String id = document.getId();


        return new Quiz(id, name);

    }

    @Override
    public void onQuizClick(int position) {
        Intent intent = new Intent(getContext(), AnswerQuizActivity.class);
        Quiz quiz = cuestionarios.get(position);
        intent.putExtra("name", quiz.getName());
        intent.putExtra("id", quiz.getId());
        startActivity(intent);

    }

    @Override
    public void onQuizRmvBtnClick(int position) {
        String quizName = cuestionarios.get(position).getName();
        final int quizToBeDeleted = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setCancelable(true);
        builder.setTitle(quizName);
        builder.setMessage(R.string.confirm_delete);
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteQuiz(quizToBeDeleted);



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

    private void deleteQuiz(int position){
        Quiz quiz = cuestionarios.get(position);
        final int quizToBeDeleted = position;

        mFirestore.collection("quizzes").document(quiz.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        showConfirmationMessage();
                        updateList(quizToBeDeleted);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);

                    }
                });
    }

    private void updateList(int position){
        cuestionarios.remove(position);
        adaptadorCuestionarios.notifyItemRemoved(position);
    }

    private void showConfirmationMessage(){
        Toast.makeText(this.getContext(), R.string.quiz_deleted, Toast.LENGTH_SHORT).show();
    }

    private void startQuizRecyclerView(){
        adaptadorCuestionarios = new HacerTestAdapter(cuestionarios, this);
        recyclerCuestionarios.setAdapter(adaptadorCuestionarios);
    }
}