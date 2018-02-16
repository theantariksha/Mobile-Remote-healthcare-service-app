package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SymptomsAnalyze extends BaseClass implements CheckConnection.testConnection, SymptomsRecyclerClickListener.onRecyclerClickListener
        , DownloadData.Download {

    private static final String TAG = "symptomsAnalyze";
    private SymptomsRecyclerViewAdapter mSymptomsRecyclerViewAdapter;
    private List<Symptoms> mSymptoms;
    private ArrayList<String> identifiedDiseases;
    private int symtompsRecorded[] = new int[200]; //maximum 200 symptoms
    private ProgressDialog progressDialog;
    private HashMap<String, String> allSymptoms;
    private String url;
    private HashMap<String, String> primarySym;
    private HashMap<String, String> secondarySymptoms;
    private HashMap<String, String> disease;
    private HashMap<String, Integer> secondarySymDan;
    private HashMap<String, Integer> symAlreadyDisp;
    private List<Symptoms> alreadySelected;
    private List<Symptoms> alreadyNotSelected;
    private HashMap<String, Integer> danSym_per_disease;
    private int disable_popup,  total_popups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_analyze);
        actionToolbar(true, "Other signs");

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        try {
            disable_popup = sharedPref.getInt("Disable_popup", 0);

        } catch (Exception e) {

            disable_popup = 0;
        }
        identifiedDiseases = new ArrayList<>();
        danSym_per_disease = new HashMap<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_symptoms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSymptomsRecyclerViewAdapter = new SymptomsRecyclerViewAdapter(new ArrayList<Symptoms>());
        recyclerView.setAdapter(mSymptomsRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new SymptomsRecyclerClickListener(this, recyclerView, this));

        if (savedInstanceState == null) {
            url = getIntent().getStringExtra("Url");
            primarySym = (HashMap<String, String>) getIntent().getSerializableExtra("PrimarySym");
            allSymptoms = (HashMap<String, String>) getIntent().getSerializableExtra("SelectedSymptoms");
            secondarySymptoms = (HashMap<String, String>) getIntent().getSerializableExtra("SecondarySymptoms");
            disease = (HashMap<String, String>) getIntent().getSerializableExtra("Disease");
            secondarySymDan = (HashMap<String, Integer>) getIntent().getSerializableExtra("Secondary_sym_dan");

            symAlreadyDisp = new HashMap<>();
        } else {
            url = savedInstanceState.getString("URL");
            primarySym = (HashMap<String, String>) savedInstanceState.getSerializable("PrimarySym");
            allSymptoms = (HashMap<String, String>) savedInstanceState.getSerializable("SelectedSymptoms");
            secondarySymptoms = (HashMap<String, String>) savedInstanceState.getSerializable("SecondarySymptoms");
            disease = (HashMap<String, String>) savedInstanceState.getSerializable("Disease");
            secondarySymDan = (HashMap<String, Integer>) savedInstanceState.getSerializable("Secondary_sym_dan");
            symAlreadyDisp = (HashMap<String, Integer>) savedInstanceState.getSerializable("AlreadyDisplayed");

            identifiedDiseases = (ArrayList<String>) savedInstanceState.getSerializable("IdentifiedDisease");
            danSym_per_disease = (HashMap<String, Integer>) savedInstanceState.getSerializable("Danger_per_disease");


        }

        Log.d(TAG, "onCreate: dan sym info"+secondarySymDan);
        Button next = (Button) findViewById(R.id.button_nextsym);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNext();
            }
        });
        next.setEnabled(false);

        CheckConnection checkConnection = new CheckConnection(this, this);
        checkConnection.execute(" ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        try {
            if (disable_popup == 1)
                menu.findItem(R.id.dis_new).setChecked(true);
        } catch (Exception e) {

            Log.e(TAG, "onCreateOptionsMenu: Unknown error");
        }
        return true;  //we have inflated the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Help");
                builder.setMessage("Check those symptoms which\nyou are having, uncheck others\n\nEnable popup to see details at each step\n\n" +
                        "Disease will be shown multiple times if they have at least one symptom different");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: help displayed");
                    }
                });
                builder.create();
                builder.show();
                return true;
            case R.id.dis_new:


                if (!item.isChecked()) {

                    Log.d(TAG, "onOptionsItemSelected: disabled popup");
                    editor.putInt("Disable_popup", 0);
                    disable_popup = 1;
                    item.setChecked(true);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: enabled popup");
                    editor.putInt("Disable_popup", 1);
                    disable_popup = 0;
                    item.setChecked(false);
                }
                editor.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("URL", url);
        outState.putSerializable("SelectedSymptoms", allSymptoms);
        outState.putSerializable("PrimarySym", primarySym);
        outState.putSerializable("SecondarySymptoms", secondarySymptoms);
        outState.putSerializable("Disease", disease);
        outState.putSerializable("Secondary_sym_dan", secondarySymDan);
        outState.putSerializable("AlreadyDisplayed", symAlreadyDisp);

        outState.putSerializable("IdentifiedDisease", identifiedDiseases);
        outState.putSerializable("Danger_per_disease", danSym_per_disease);

    }

    private void loadSymptoms() {

        Arrays.fill(symtompsRecorded, 0);
        total_popups=0;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading symptoms data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "loadSymptoms: " + url);
        url = parseUrl(url);
        if (url.length() == 0) {
            progressDialog.dismiss();
            showDisease();
        } else {
            DownloadData downloadData = new DownloadData(this, url, this);
            downloadData.download();
        }
    }

    @Override
    public void connectResult(boolean result) {

        if (result)
            loadSymptoms();
        else
            displayNoInternet();

    }

    @Override
    public void onItemClick(View view, int position) {

        if (symtompsRecorded[position] == 0) {
            symtompsRecorded[position] = 1;
            mSymptomsRecyclerViewAdapter.preserveChecked(position, 1);
        } else {
            symtompsRecorded[position] = 0;
            mSymptomsRecyclerViewAdapter.preserveChecked(position, 0);
        }
    }

    @Override
    public void onDownloadComplete(List<Symptoms> primaryList, List<Symptoms> secondaryList, List<Symptoms> diseaseList, List<Symptoms> treatmentList) {
        List<Symptoms> temp = new ArrayList<>();
        alreadySelected = new ArrayList<>();
        alreadyNotSelected = new ArrayList<>();
//        Log.d(TAG, "onDownloadComplete: " + primaryList);
        HashMap<String, Integer> unique_secondary = new HashMap<>();
        for (int i = 0; i < primaryList.size(); i++) {
            if (primaryList.get(i) != null) {

                String secondarySymptomCode = primaryList.get(i).getSymptomName();
                /*for displaying purpose same symptoms are not shown again*/
                if (unique_secondary.get(secondarySymptomCode) == null) {

                    if (secondarySymptoms.get(secondarySymptomCode) == null) {
                        Log.e(TAG, "onDownloadComplete: data not available in database");
                        primaryList.get(i).setSymptomName("--data not found--");
                    } else
                        primaryList.get(i).setSymptomName(secondarySymptoms.get(secondarySymptomCode));
                    if (symAlreadyDisp.get(primaryList.get(i).getSymptomName()) == null) {
                        temp.add(primaryList.get(i));
                        unique_secondary.put(secondarySymptomCode, temp.size() - 1);
                    } else {
                        if (symAlreadyDisp.get(primaryList.get(i).getSymptomName()) == 1)
                            alreadySelected.add(primaryList.get(i));
                        else {
                            alreadyNotSelected.add(primaryList.get(i));
//                            Log.d(TAG, "onDownloadComplete: not selected" + primaryList.get(i));
                        }

                    }

                } else {
                    int temp_index;
                    temp_index = unique_secondary.get(secondarySymptomCode);
                    String cur_has, cur_no;
                    cur_has = temp.get(temp_index).getHas();
                    cur_no = temp.get(temp_index).getNo();
                    cur_has = cur_has + " " + primaryList.get(i).getHas();
                    cur_no = cur_no + " " + primaryList.get(i).getNo();
                    temp.get(temp_index).setHas(cur_has);
                    temp.get(temp_index).setNo(cur_no);
                }

            }
        }
        mSymptomsRecyclerViewAdapter.loadNewData(temp);
        mSymptoms = temp;
        Button next = (Button) findViewById(R.id.button_nextsym);
        next.setEnabled(true);
        progressDialog.dismiss();

    }

    public void displayNoInternet() {


        Log.d(TAG, "displayNoInternet: here");
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

    private void processNext() {

        int recordlength = mSymptoms.size(), i;
        String temp = "";
//        Log.d(TAG, "processNext: " + mSymptoms);
        for (i = 0; i < recordlength; i++) {

            if (symtompsRecorded[i] == 1) {

                symAlreadyDisp.put(mSymptoms.get(i).getSymptomName(), 1);
                allSymptoms.put(mSymptoms.get(i).getSymptomName(), mSymptoms.get(i).getDangerous());
                temp += mSymptoms.get(i).getHas() + " ";
            } else {
                symAlreadyDisp.put(mSymptoms.get(i).getSymptomName(), 0);
                temp += mSymptoms.get(i).getNo() + " ";
            }
        }
        for (i = 0; i < alreadySelected.size(); i++) {

            temp += alreadySelected.get(i).getHas() + " ";
        }
        for (i = 0; i < alreadyNotSelected.size(); i++) {

            temp += alreadyNotSelected.get(i).getNo() + " ";
        }
        url = temp;
        Button next = (Button) findViewById(R.id.button_nextsym);
        next.setEnabled(false);
        processPopUp(); //added later and in a hurry, so a lot of redundancy

    }
    void proceed() {

        total_popups--;
        if (total_popups <= 0) {
            CheckConnection checkConnection = new CheckConnection(this, this);
            checkConnection.execute(" ");
        }
    }
    /*Disease are not retrieved but saved until all questions are finished */
    private String parseUrl(String dataUrl) {


        ArrayList<String> newUrl = new ArrayList<>();
        String split[] = dataUrl.split("\\s+");
        Log.d(TAG, "parseUrl: data= " + dataUrl);
        Log.d(TAG, "parseUrl split= : " + split);
        for (String token : split) {

            Log.d(TAG, "parseUrl: " + token);
            if (token == null || token.length() == 0) ; //skip error
            else if (token.charAt(0) == 'D')
                identifiedDiseases.add(token);
            else if (token.charAt(0) == 'P')
                newUrl.add(token);
        }
        StringBuilder url = new StringBuilder();
        Set<String> u_hs = new HashSet<>();
        u_hs.addAll(newUrl);
        newUrl.clear();
        newUrl.addAll(u_hs);
        for (int i = 0; i < newUrl.size(); i++) {


            url.append(newUrl.get(i)).append(" ");

        }

        /*Store the unique disease id*/
        Set<String> hs = new HashSet<>();
        hs.addAll(identifiedDiseases);
        identifiedDiseases.clear();
        identifiedDiseases.addAll(hs);
        return url.toString();
    }

    private void showDisease() {

//        Log.d(TAG, "showDisease: " + identifiedDiseases);
        int diseaseDetected = 0;
        StringBuilder disease_url = new StringBuilder();

        for (int i = 0; i < identifiedDiseases.size(); i++) {
            if (!identifiedDiseases.get(i).equals("D0")) {
                disease_url.append(identifiedDiseases.get(i)).append(" ");
                diseaseDetected++;
            }
        }
        if (identifiedDiseases.size() == 1 && identifiedDiseases.get(0).equals("D0")) { //if no disease found add default
            disease_url.append("D0").append(" ");
            diseaseDetected = 0;
        }
        int max_danger=0;
        for (int i : danSym_per_disease.values()){

            if(i>max_danger)
                max_danger=i;
        }

        Intent intent = new Intent(this, ShowDisease.class);
        intent.putExtra("Url", disease_url.toString());
        intent.putExtra("SelectedSymptoms", allSymptoms);
        intent.putExtra("DiseaseDetected", diseaseDetected);
        intent.putExtra("max_danger", max_danger);
        startActivity(intent);
    }

    /*In a hurry correct later*/
    private void processPopUp() {

        int i, seq, dseq, danseq;
        total_popups=1;
        int recordLength = mSymptoms.size();
        StringBuilder disease_names;
        StringBuilder dangerous_sym;
        StringBuilder not_dan_sym;


        for (i = 0; i < recordLength; i++) {

            disease_names = new StringBuilder();
            dangerous_sym = new StringBuilder();
            not_dan_sym = new StringBuilder();
            String split[] = mSymptoms.get(i).getBacktrace().split("\\s+");

            seq = danseq = dseq = 1;

            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'P')
                    not_dan_sym.append(dseq++).append(". ").append(primarySym.get(token)).append("\n");
                else if (token.charAt(0) == 'S') {

                    if (secondarySymDan.get(token) == 1) {
                        dangerous_sym.append(danseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");

                    } else {
                        not_dan_sym.append(dseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");
                        Log.d(TAG, "processPopUp: not dan"+token);
                    }
                }
            }
            if (symtompsRecorded[i] == 1) {

                split = mSymptoms.get(i).getHas().split("\\s+");

            } else {
                split = mSymptoms.get(i).getNo().split("\\s+");
            }
            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'D' && token.compareToIgnoreCase("D0") != 0) {

                    if(danSym_per_disease.get(token)==null){

                        danSym_per_disease.put(token ,1);
                    }
                    else
                        danSym_per_disease.put(token, danSym_per_disease.get(token)+1);
                    disease_names.append(seq++).append(". ").append(disease.get(token)).append("\n");

                }

            }

            if (disease_names.length() != 0 && disable_popup==0) {
                Log.d(TAG, "processPopUp: called by sym");
                total_popups++;
                display_popup(disease_names.toString(), dangerous_sym.toString(), not_dan_sym.toString());
            }

        }
        for (i = 0; i < alreadySelected.size(); i++) {


            disease_names = new StringBuilder();
            dangerous_sym = new StringBuilder();
            not_dan_sym = new StringBuilder();
            String split[] = alreadySelected.get(i).getBacktrace().split("\\s+");

            seq = danseq = dseq = 1;

            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'P')
                    not_dan_sym.append(dseq++).append(". ").append(primarySym.get(token)).append("\n");
                else if (token.charAt(0) == 'S') {

                    if (secondarySymDan.get(token) == 1) {
                        dangerous_sym.append(danseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");

                    }
                    else
                        not_dan_sym.append(dseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");
                }
            }
            split = alreadySelected.get(i).getHas().split("\\s+");
            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'D' && token.compareToIgnoreCase("D0") != 0) {

                    if(danSym_per_disease.get(token)==null){

                        danSym_per_disease.put(token ,1);
                    }
                    else
                        danSym_per_disease.put(token, danSym_per_disease.get(token)+1);
                    disease_names.append(seq++).append(". ").append(disease.get(token)).append("\n");

                }

            }
            if (disease_names.length() != 0 && disable_popup==0) {
                Log.d(TAG, "processPopUp: called by already selected");
                total_popups++;
                display_popup(disease_names.toString(), dangerous_sym.toString(), not_dan_sym.toString());
            }


        }
        for (i = 0; i < alreadyNotSelected.size(); i++) {


            disease_names = new StringBuilder();
            dangerous_sym = new StringBuilder();
            not_dan_sym = new StringBuilder();
            String split[] = alreadyNotSelected.get(i).getBacktrace().split("\\s+");

            seq = danseq = dseq = 1;

            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'P')
                    not_dan_sym.append(dseq++).append(". ").append(primarySym.get(token)).append("\n");
                else if (token.charAt(0) == 'S') {

                    if (secondarySymDan.get(token) == 1) {
                        dangerous_sym.append(danseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");

                    }
                    else
                        not_dan_sym.append(dseq++).append(". ").append(secondarySymptoms.get(token)).append("\n");
                }
            }
            split = alreadyNotSelected.get(i).getNo().split("\\s+");
            for (String token : split) {


                if (token == null || token.length() == 0) ; //skip error
                else if (token.charAt(0) == 'D' && token.compareToIgnoreCase("D0") != 0) {

                    if(danSym_per_disease.get(token)==null){

                        danSym_per_disease.put(token ,1);
                    }
                    else
                        danSym_per_disease.put(token, danSym_per_disease.get(token)+1);
                    disease_names.append(seq++).append(". ").append(disease.get(token)).append("\n");

                }

            }
            if (disease_names.length() != 0 && disable_popup==0) {
                Log.d(TAG, "processPopUp: called  from already not selected");
                total_popups++;
                display_popup(disease_names.toString(), dangerous_sym.toString(), not_dan_sym.toString());
            }

        }
        proceed();
    }

    void display_popup(String disease_name, String dangerous_sym, String not_dan_sym) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View displayView = inflater.inflate(R.layout.display_disease, null);
        builder.setView(displayView);
        TextView dname = (TextView) displayView.findViewById(R.id.dname);
        TextView dan_sym = (TextView) displayView.findViewById(R.id.dang_sym);
        TextView no_dan_sym = (TextView) displayView.findViewById(R.id.non_dang_sym);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                proceed();

                Log.d(TAG, "disease displayed");

            }
        });
        dname.setText(disease_name);
        dan_sym.setText(dangerous_sym);
        no_dan_sym.setText(not_dan_sym);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }
}

