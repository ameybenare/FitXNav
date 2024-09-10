package com.example.gpsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepsFragment extends Fragment implements SensorEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SensorManager mSensorManager = null;
    private Sensor stepSensor;
    private int previousTotalSteps = 0, totalSteps = 0, currentSteps, totalSinceBoot = 0;
    private int goalSteps = 0;
    private ProgressBar progressBar;
    private TextView steps,goalText;
    private Button startBtn, stopBtn;
    private boolean isStopped=true,isOnce=false;



    public StepsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepsFragment newInstance(String param1, String param2) {
        StepsFragment fragment = new StepsFragment();
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
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        previousTotalSteps = totalSteps;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        steps = view.findViewById(R.id.steps);
        startBtn = view.findViewById(R.id.start);
        stopBtn = view.findViewById(R.id.stop);
        goalText = view.findViewById(R.id.goalText);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        goalSteps = Integer.parseInt(sharedPreferences.getString("goal", "200"));
        goalText.setText("Goal: "+goalSteps);
        progressBar.setMax(goalSteps);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFn();
                Toast.makeText(getActivity().getApplicationContext(),"Steps count started",Toast.LENGTH_SHORT).show();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFn();
                Toast.makeText(getActivity().getApplicationContext(),"Steps count stopped",Toast.LENGTH_SHORT).show();
            }
        });
        resetSteps();
        stopFn();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(stepSensor == null){
            Toast.makeText(this.getActivity(), "This device has no sensor", Toast.LENGTH_SHORT).show();
        }else{
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && !isStopped){
            totalSteps = (int) event.values[0];
            totalSinceBoot = (int) event.values[0];
            if(!isStopped && isOnce){
                isOnce = false;
                previousTotalSteps = totalSteps;
            }
            currentSteps = totalSteps-previousTotalSteps;
            steps.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps,true);
            if(currentSteps == goalSteps){
                Toast.makeText(getActivity(),"Hooray! You have completed walked "+goalSteps/1000+" kms today",Toast.LENGTH_SHORT).show();
            }
            if(currentSteps%50==0){
                totalSinceBoot();
            }
        }
    }

    public void totalSinceBoot(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalSinceBoot",String.valueOf(totalSinceBoot));
        editor.apply();
    }

    private void resetSteps(){
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "long press to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                steps.setText(previousTotalSteps+"");
                progressBar.setProgress(previousTotalSteps);
                saveData();
                return true;
            }
        });
    }

    private void saveData(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key1",String.valueOf(previousTotalSteps));
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        int savedNumber = Integer.parseInt(sharedPreferences.getString("key1", "0"));
        previousTotalSteps = savedNumber;
    }

    private void startFn(){
        isStopped = false;
        isOnce = true;
        stopBtn.setVisibility(View.VISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        steps.setText("0");
        progressBar.setProgress(0);
        previousTotalSteps = totalSteps;
    }

    private void stopFn(){
        isStopped = true;
        stopBtn.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.VISIBLE);
        previousTotalSteps = totalSteps;
        steps.setText(currentSteps+"");
        progressBar.setProgress(currentSteps);
        saveData();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}