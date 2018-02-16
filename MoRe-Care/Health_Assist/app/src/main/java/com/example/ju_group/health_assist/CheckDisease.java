package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/*To display the primary symptoms and downloads the secondary symptoms name with their code*/

public class CheckDisease extends BaseClass implements CheckConnection.testConnection, SymptomsRecyclerClickListener.onRecyclerClickListener
        , DownloadData.Download {


    private static final String TAG = "CheckDisease";
    private SymptomsRecyclerViewAdapter mSymptomsRecyclerViewAdapter;
    private List<Symptoms> mSymptoms, secondarySymptoms, diseases;
    /*allSymptoms keeps track of which symptoms are selected and are forwarded to next activities*/
    private HashMap<String, String> allSymptoms;
    /*To keep track which symptoms were selected*/
    private int symtompsRecorded[] = new int[200];
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_disease);
        actionToolbar(true, "Primary Symptoms");


        /*Setup recycler view*/
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_symptoms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSymptomsRecyclerViewAdapter = new SymptomsRecyclerViewAdapter(new ArrayList<Symptoms>());
        recyclerView.setAdapter(mSymptomsRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new SymptomsRecyclerClickListener(this, recyclerView, this));

        Button next = (Button) findViewById(R.id.button_nextsym);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNext();
            }
        });

        allSymptoms = new HashMap<>();
        next.setEnabled(false);
        CheckConnection checkConnection = new CheckConnection(this, this);
        checkConnection.execute(" ");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;  //we have inflated the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Help");
                builder.setMessage("Check those symptoms which\nyou are having, uncheck others");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: help displayed");
                    }
                });
                builder.create();
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /*Downloads the number of primary symptoms available in database*/
    private void loadSymptoms() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading symptoms data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("No_of_primary_secondary_symptoms");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot == null) {

                    /*can't proceed further, exit here*/
                    Toast.makeText(getBaseContext(), "Database error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onDataChange: No returned value for database symptoms");
                    finish();
                } else {
                    //Log.d(TAG, "onDataChange: "+dataSnapshot);
                    String no = (String) dataSnapshot.getValue();
                    String split[] = no.split(",");
                    loadUrl(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled: " + databaseError.toString());
                Toast.makeText(getBaseContext(), "Database error", Toast.LENGTH_SHORT);
            }
        });
    }

    /*refer to symptoms class to check url format to download symptoms data*/
    /*P0, P1,... and S0,S1...
    size of P* and S* from "No_of_primary_secondary_symptoms"
     */
    private void loadUrl(int no_primary, int no_secondary, int no_disease) {

        StringBuilder url = new StringBuilder();
        for (int i = 0; i < no_primary; i++) {
            url.append("P");
            url.append(Integer.toString(i)).append(" ");
        }
        for (int i = 0; i < no_secondary; i++) {
            url.append("S");
            url.append(Integer.toString(i)).append(" ");
        }
        for (int i = 0; i < no_disease; i++) {
            url.append("D");
            url.append(Integer.toString(i)).append(" ");
        }
        DownloadData downloadData = new DownloadData(this, url.toString(), this);
        downloadData.download();
    }

    @Override
    public void connectResult(boolean result) {

        if (result)
            loadSymptoms();
        else
            displayNoInternet();

    }

    /*mark the symptoms which were selected*/
    @Override
    public void onItemClick(View view, int position) {


        if (symtompsRecorded[position] == 0) {
            symtompsRecorded[position] = 1;
            /*keep selected/unselected when scrolled*/
            mSymptomsRecyclerViewAdapter.preserveChecked(position, 1);
        } else {
            symtompsRecorded[position] = 0;
            mSymptomsRecyclerViewAdapter.preserveChecked(position, 0);
        }

    }

    @Override
    public void onDownloadComplete(List<Symptoms> primaryList, List<Symptoms> secondaryList, List<Symptoms> diseaseList, List<Symptoms> treatmentList) {

        /*only primary symptoms data required, others are ignored(if any)*/
        Log.d(TAG, "onDownloadComplete: primary " + primaryList);
        Log.d(TAG, "onDownloadComplete: secondary " + secondaryList);
        mSymptomsRecyclerViewAdapter.loadNewData(primaryList);
        mSymptoms = primaryList;
        secondarySymptoms = secondaryList;
        diseases = diseaseList;
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

    /*Processing for Next button*/
    private void processNext() {

        int recordlength = mSymptoms.size(), i;
        String temp = "";
        allSymptoms.clear();
        /*get secondary symptoms data*/
        for (i = 0; i < recordlength; i++) {

            /*if selected take has data, else no data*/
            if (symtompsRecorded[i] == 1) {

                temp += mSymptoms.get(i).getHas() + " ";
                allSymptoms.put(mSymptoms.get(i).getSymptomName(), mSymptoms.get(i).getDangerous());
            } else
                temp += mSymptoms.get(i).getNo() + " ";

        }

        parseUrl(temp);
    }

    /*Remove duplicate symptoms and pass url to next activity*/
    private void parseUrl(String dataUrl) {

        String split[] = dataUrl.split("\\s+");

        ArrayList<String> newUrl = new ArrayList<>();
        Log.d(TAG, "parseUrl: data= " + dataUrl);
        Log.d(TAG, "parseUrl split= : " + split);
        for (String token : split) {

            Log.d(TAG, "parseUrl: " + token);
            if (token == null || token.length() == 0) ; //skip error
            else
                newUrl.add(token);
        }
        /*remove duplicates using HashMap*/
        StringBuilder url = new StringBuilder();
        Set<String> u_hs = new HashSet<>();
        u_hs.addAll(newUrl);
        newUrl.clear();
        newUrl.addAll(u_hs);
        for (int i = 0; i < newUrl.size(); i++) {


            url.append(newUrl.get(i)).append(" ");

        }
        HashMap<String, String> primary_map = new HashMap<>();
        HashMap<String, String> secondarySym_map = new HashMap<>();
        HashMap<String, Integer> secondarySymDan_map = new HashMap<>();
        HashMap<String, String> disease_map = new HashMap<>();
        for (int i = 0; i < secondarySymptoms.size(); i++) {

            if (secondarySymptoms.get(i) != null) {
                secondarySym_map.put(secondarySymptoms.get(i).getHas(), secondarySymptoms.get(i).getSymptomName());
                if (secondarySymptoms.get(i).getDangerous() != null && secondarySymptoms.get(i).getDangerous().compareToIgnoreCase("True") == 0)
                    secondarySymDan_map.put(secondarySymptoms.get(i).getHas(), 1);
                else
                    secondarySymDan_map.put(secondarySymptoms.get(i).getHas(), 0);
            }
        }

        for (int i = 0; i < diseases.size(); i++) {

            if (diseases.get(i) != null)
                disease_map.put(diseases.get(i).getBacktrace(), diseases.get(i).getSymptomName());
        }
        for (int i = 0; i < mSymptoms.size(); i++)
            if (mSymptoms.get(i) != null)
                primary_map.put(mSymptoms.get(i).getBacktrace(), mSymptoms.get(i).getSymptomName());
        Log.d(TAG, "parseUrl: after first symptoms " + url);
        Intent intent = new Intent(this, SymptomsAnalyze.class);
        intent.putExtra("Url", url.toString());
        intent.putExtra("PrimarySym", primary_map);
        intent.putExtra("SecondarySymptoms", secondarySym_map);
        intent.putExtra("Secondary_sym_dan", secondarySymDan_map);
        intent.putExtra("Disease", disease_map);
        intent.putExtra("SelectedSymptoms", allSymptoms);
        startActivity(intent);
    }
}
