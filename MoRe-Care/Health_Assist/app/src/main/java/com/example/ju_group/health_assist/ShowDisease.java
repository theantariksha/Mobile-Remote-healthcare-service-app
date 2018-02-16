package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*class to display disease and process from there(incomplete)*/
public class ShowDisease extends BaseClass implements CheckConnection.testConnection
        , DownloadData.Download {


    private static final String TAG = "ShowDisease";
    private String url, download_url;
    private DiseaseRecyclerViewAdapter mDiseaseRecyclerViewAdapter;
    private SelectedSymptomsRViewAdapter mSelectedSymptomsRViewAdapter;
    private HashMap<String, String> allSymptoms; //all symptoms selected till now
    private List<Symptoms> disease, treatment;
    private ProgressDialog progressDialog;
    private int dispflag; //explained later, in context
    private int diseaseDetected;
    private StringBuilder show_treatment;
    private  int maxDanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_disease);
        actionToolbar(true, "Disease");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_disease);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDiseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(new ArrayList<Symptoms>());
        recyclerView.setAdapter(mDiseaseRecyclerViewAdapter);

        RecyclerView symptomList = (RecyclerView) findViewById(R.id.list_sym);
        symptomList.setLayoutManager(new LinearLayoutManager(this));

        mSelectedSymptomsRViewAdapter = new SelectedSymptomsRViewAdapter(new HashMap<String, String>());
        symptomList.setAdapter(mSelectedSymptomsRViewAdapter);
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: getting from intent");
            url = getIntent().getStringExtra("Url");
            allSymptoms = (HashMap<String, String>) getIntent().getSerializableExtra("SelectedSymptoms");
            diseaseDetected= getIntent().getIntExtra("DiseaseDetected", 0);
            maxDanger= getIntent().getIntExtra("max_danger", 0);

        } else {
            Log.d(TAG, "onCreate: getting old state");
            url = savedInstanceState.getString("URL");
            allSymptoms = (HashMap<String, String>) savedInstanceState.getSerializable("SelectedSymptoms");
            diseaseDetected = savedInstanceState.getInt("DiseaseDetected");
            maxDanger = savedInstanceState.getInt("max_danger");
        }

        /*output all symptoms selected*/
        Button treat = (Button) findViewById(R.id.treatment);
        treat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Treatment.class);
                intent.putExtra("Treatment", show_treatment.toString());
                startActivity(intent);
            }
        });
        HashMap<String, String> temp = new HashMap<>();
        temp.putAll(allSymptoms);
        Log.d(TAG, "onCreate: disease detected " + diseaseDetected);
        mSelectedSymptomsRViewAdapter.loadNewData(temp);
        download_url = url;

        dispflag = 0;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Download Data");
        progressDialog.setMessage("Fetching disease information..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CheckConnection checkConnection = new CheckConnection(this, this);
        checkConnection.execute(" ");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("URL", url);
        outState.putSerializable("SelectedSymptoms", allSymptoms);
        outState.putInt("DiseaseDetected", diseaseDetected);
        outState.putInt("max_danger", maxDanger);
        Log.d(TAG, "onSaveInstanceState: " + allSymptoms);

    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: restoring");
    }
    private void loadSymptoms() {


        Log.d(TAG, "loadSymptoms: " + download_url);
        DownloadData downloadData = new DownloadData(this, download_url, this);
        downloadData.download();
    }

    @Override
    public void connectResult(boolean result) {

        if (result)
            loadSymptoms();
        else
            displayNoInternet();

    }

    @Override
    public void onDownloadComplete(List<Symptoms> primaryList, List<Symptoms> secondaryList, List<Symptoms> diseaseList, List<Symptoms> treatmentList) {

        List<Symptoms> temp = new ArrayList<>();
        int i;
        if (dispflag == 0) {
            for (i = 0; i < diseaseList.size(); i++)
                if (diseaseList.get(i) != null)
                    temp.add(diseaseList.get(i));
            disease = temp;
            mDiseaseRecyclerViewAdapter.loadNewData(disease);
            loadTreatment();
        } else {
            for (i = 0; i < treatmentList.size(); i++)
                if (treatmentList.get(i) != null)
                    temp.add(treatmentList.get(i));
            treatment = temp;
            dispTreatment();
        }
    }

    public void displayNoInternet() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.no_connection, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: No intenet connection");
                        finish();
                    }
                });
        builder.create();
        builder.show();

    }

    private void loadTreatment() {

        int recordlength = disease.size(), i;
        String temp = "";
        for (i = 0; i < recordlength; i++) {

            temp += disease.get(i).getHas() + " ";

        }
        ArrayList<String> newUrl = new ArrayList<>();
        String split[] = temp.split("\\s+");
        Log.d(TAG, "parseUrl split= : " + split);
        for (String token : split) {

            Log.d(TAG, "parseUrl: " + token);
            if (token == null || token.length() == 0) ; //skip error
            else
                newUrl.add(token);
        }

        /*remove duplicates using hashmap*/
        StringBuilder treatment_url = new StringBuilder();
        Set<String> u_hs = new HashSet<>();
        u_hs.addAll(newUrl);
        newUrl.clear();
        newUrl.addAll(u_hs);
        for (i = 0; i < newUrl.size(); i++) {

            treatment_url.append(newUrl.get(i)).append(" ");

        }
        dispflag = 1;
        download_url = treatment_url.toString();
        CheckConnection checkConnection = new CheckConnection(this, this);
        checkConnection.execute(" ");

    }

    private void dispTreatment() {

        show_treatment = new StringBuilder();
        /*save serial no.wise*/
        for (int i = 0; i < treatment.size(); i++) {


            show_treatment.append(i + 1).append(". ").append(treatment.get(i).getSymptomName()).append("\n");
        }

        Iterator it = allSymptoms.entrySet().iterator();
        int danger = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            try {
                if (pair.getValue().toString().equalsIgnoreCase("True"))
                    danger += 1;
            } catch (NullPointerException e) {

                Log.e(TAG, "loadNewData: No value for dangerous field, default to false");
            }
        }
        if (maxDanger>=2) {

            show_treatment = new StringBuilder("Two or more Dangerous symptoms present for a disease\n");
            show_treatment.append("REFER TO A DOCTOR AS SOON AS POSSIBLE");

        }
        if (show_treatment.length() == 0)
            show_treatment.append("No treatment found in database\nREFER TO DOCTOR");
        progressDialog.dismiss();
    }

}
