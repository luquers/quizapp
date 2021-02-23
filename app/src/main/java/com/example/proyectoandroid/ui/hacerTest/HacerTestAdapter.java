package com.example.proyectoandroid.ui.hacerTest;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.Quiz;
import com.example.proyectoandroid.R;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class HacerTestAdapter extends RecyclerView.Adapter<HacerTestAdapter.ViewHolderHacerTest>{

    ArrayList<Quiz> cuestionarios;
    OnQuizListener onQuizListener;
    public HacerTestAdapter(ArrayList<Quiz> cuestionarios, OnQuizListener onQuizListener) {
        this.cuestionarios = cuestionarios;
        this.onQuizListener = onQuizListener;
    }


    @Override
    public ViewHolderHacerTest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_cuestionarios, null, false);
                return new ViewHolderHacerTest(view, onQuizListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHacerTest holder, int position) {
        holder.tvQuizName.setText(cuestionarios.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return cuestionarios.size();
    }

    public class ViewHolderHacerTest extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnQuizListener quizListener;
        TextView tvQuizName;
        ImageButton ibRemoveQuiz;


        public ViewHolderHacerTest(@NonNull View itemView, OnQuizListener onQuizListener) {
            super(itemView);
            tvQuizName = (TextView) itemView.findViewById(R.id.tvTituloCuestionario);
            ibRemoveQuiz = (ImageButton) itemView.findViewById(R.id.btnRemoveQuiz);

            quizListener = onQuizListener;
            itemView.setOnClickListener(this);
            ibRemoveQuiz.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == ibRemoveQuiz.getId()){
                quizListener.onQuizRmvBtnClick(getAdapterPosition());
            } else {
                quizListener.onQuizClick(getAdapterPosition());
            }
        }

    }
        public interface OnQuizListener{
        void onQuizClick(int position);
        void onQuizRmvBtnClick(int position);
    }
}
