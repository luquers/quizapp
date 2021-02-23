package com.example.proyectoandroid.ui.crearTest;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.Question;
import com.example.proyectoandroid.R;

import java.util.ArrayList;

public class CrearTestAdapter extends RecyclerView.Adapter<CrearTestAdapter.ViewHolderCrearTest>{

    ArrayList<Question> questions;
    OnQuestionListener onQuestionListener;
    public CrearTestAdapter(ArrayList<Question> questions, OnQuestionListener onQuestionListener) {
        this.questions = questions;
        this.onQuestionListener = onQuestionListener;
    }


    @Override
    public ViewHolderCrearTest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_preguntas, null, false);
                return new ViewHolderCrearTest(view, onQuestionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCrearTest holder, final int position) {

        //Saves the question name
        holder.etQuestionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questions.get(position).setQuestion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Saves the correct answer number
        holder.rgEditAnswers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                questions.get(position).setAnswerNumber(group.indexOfChild(rb)+1); // El +1 es porque el índice de las preguntas son 0, 1 y 2
            }
        });

        //Saves answer 1
        holder.etAnswer1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questions.get(position).setOption1(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Saves answer 2
        holder.etAnswer2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questions.get(position).setOption2(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Saves answer 3
        holder.etAnswer3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questions.get(position).setOption3(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });


    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }

    public class ViewHolderCrearTest extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnQuestionListener quizListener;
        EditText etQuestionName, etAnswer1, etAnswer2, etAnswer3;
        RadioGroup rgEditAnswers;
        ImageButton ibRemoveQuestion;


        public ViewHolderCrearTest(@NonNull final View itemView, OnQuestionListener onQuestionListener) {
            super(itemView);
            etQuestionName = (EditText) itemView.findViewById(R.id.etQuestion);
            rgEditAnswers = (RadioGroup) itemView.findViewById(R.id.editRgAnswers);
            ibRemoveQuestion = (ImageButton) itemView.findViewById(R.id.btnRemoveQuestion);
            etAnswer1 = (EditText) itemView.findViewById(R.id.etAnswer1);
            etAnswer2 = (EditText) itemView.findViewById(R.id.etAnswer2);
            etAnswer3 = (EditText) itemView.findViewById(R.id.etAnswer3);




            quizListener = onQuestionListener;

            ibRemoveQuestion.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            quizListener.onQuestionClick(getAdapterPosition());
        }
    }
        public interface OnQuestionListener {
        void onQuestionClick(int position);
    }


}
