package com.example.gpsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final static float DEFAULT_STEP_SIZE = 0.75f;

    private Button goalSet;
    private EditText goal;

    private int val,totalSteps;

    private float distance;

    private String name,email;


    TextView welcomeText,distanceCovered,stepsDetail;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        val = Integer.parseInt(sharedPreferences.getString("goal", "0"));

        sharedPreferences = this.getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
         totalSteps = Integer.parseInt(sharedPreferences.getString("totalSinceBoot", "0"));

        sharedPreferences = this.getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        name = sharedPreferences.getString("username", "dummy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        goalSet = view.findViewById(R.id.setGoal);
        goal = view.findViewById(R.id.goal);
        welcomeText = view.findViewById(R.id.welcomeText);
        distanceCovered = view.findViewById(R.id.distance);
        stepsDetail = view.findViewById(R.id.stepsDetail);
        distance = (totalSteps * DEFAULT_STEP_SIZE)/1000;

        stepsDetail.setText(""+totalSteps+" total steps");
        distanceCovered.setText(distance+" kms covered");
        welcomeText.setText("Welcome! " + (name == "dummy" ? "" : name));

        goal.setText(val+"");
        goalSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoal();
            }
        });

        return view;
    }

    public void setGoal(){
        if(goal.getText().toString().length() > 0 && goal.getText().toString() != ""){
            val = Integer.parseInt(goal.getText().toString());
        }else{
            Toast.makeText(this.getActivity().getApplicationContext(),"Please enter a valid goal",Toast.LENGTH_SHORT).show();
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("goal",String.valueOf(val));
        editor.apply();
        Toast.makeText(getActivity().getApplicationContext(),"Goal modified to "+val, Toast.LENGTH_SHORT).show();
    }


}