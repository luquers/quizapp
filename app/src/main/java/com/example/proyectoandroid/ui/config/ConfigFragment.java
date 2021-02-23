package com.example.proyectoandroid.ui.config;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.proyectoandroid.MainActivity;
import com.example.proyectoandroid.R;


public class ConfigFragment extends Fragment {

    private Button btnSave;
    private Spinner spinnerLanguage;
    private String selectedLanguage;
    private int position;
    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_config, container, false);
        activity = (MainActivity) getActivity();
        btnSave = root.findViewById(R.id.btnSave);
        spinnerLanguage = root.findViewById(R.id.spinnerLanguage);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0:
                        activity.savePreferences("es");
                        break;
                    case 1:
                        activity.savePreferences("en");
                        break;
                }
                activity.reload();
            }
        });




        String spanish = getResources().getString(R.string.spanish);
        String english = getResources().getString(R.string.english);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                root.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{spanish,english}
        );
        spinnerLanguage.setAdapter(adapter);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //selectedLanguage = adapterView.getItemAtPosition(i).toString();
                position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        return root;
    }


}