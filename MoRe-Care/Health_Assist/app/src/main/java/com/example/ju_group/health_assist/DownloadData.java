package com.example.ju_group.health_assist;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/*To download symptoms from database*/
    /*Url format->  letterNumber letter1Number1...
    letter --> P- Primary, S-Secondary, D-Disease
    Number --> 1,2... symptom number

    Ex- if url is P1 S2 D1
    Then data for primary symptom1, seconday symptom2 and disease 1 will be downloaded

    Error checking is not good, avoid passing blank url or symptoms not there in database, will crash or froze in progress dialog
    if any error occurs
     */
class DownloadData {

    private static final String TAG = "DownloadData";
    /*list for primary symptoms, disease, disease this are returned */
    private List<Symptoms> p_Symptoms, s_symptoms, d_symptoms, t_symptoms;
    private int p_len, s_len, d_len, t_len, errors;
    private ArrayList<String> p_key, s_key, d_key, t_key;
    private int counter_p, counter_s, counter_d, counter_t;
    private String dataUrl;
    private Download mCallback;
    private Context mContext;

    /*callback*/
    interface Download {

        void onDownloadComplete(List<Symptoms> primaryList, List<Symptoms> secondaryList, List<Symptoms> diseaseList, List<Symptoms> treatment);
    }


    public class SymptomComparator implements Comparator<Symptoms>{

        @Override
        public int compare(Symptoms o1, Symptoms o2) {
            if(o1==null || o2==null)
                return 0;
            return (o1.getSymptomName().compareToIgnoreCase(o2.getSymptomName()));
        }
    }

    public DownloadData(Context context, String dataUrl, Download callback) {
        this.dataUrl = dataUrl;
        mCallback = callback;
        mContext = context;
        p_len = s_len = d_len = t_len = errors = 0;
    }

    void download() {

        Log.d(TAG, "download: Downloading data");
        p_key = new ArrayList<>();
        s_key = new ArrayList<>();
        d_key = new ArrayList<>();
        t_key = new ArrayList<>();
        counter_t = counter_d = counter_s = counter_p = 0;
        /*get the symptom numbers for each category*/
        parseUrl();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        int i, j;
        /*new list*/
        p_Symptoms = new ArrayList<>();
        s_symptoms = new ArrayList<>();
        d_symptoms = new ArrayList<>();
        t_symptoms = new ArrayList<>();
        if((p_len+s_len+d_len+t_len)==0)
            mCallback.onDownloadComplete(p_Symptoms, s_symptoms, d_symptoms, t_symptoms);
        for (j = 0; j < 4; j++) {

            String tempChild;
            ArrayList<String> tempKey;
            int tempLen;
            /*get url, symptom number array and its length*/
            if (j == 0) {
                tempChild = "PrimarySymptoms";
                tempLen = p_len;
                tempKey = p_key;
            } else if (j == 1) {
                tempChild = "SecondarySymptoms";
                tempLen = s_len;
                tempKey = s_key;
            } else if (j == 2) {
                tempChild = "ProbableDisease";
                tempLen = d_len;
                tempKey = d_key;
            } else {
                tempChild = "Treatment";
                tempLen = t_len;
                tempKey = t_key;
            }

            for (i = 0; i < tempLen; i++) {

                databaseReference.child(tempChild).child(tempKey.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            // Log.d(TAG, "onDataChange: " + databaseReference.child(String.valueOf(finalI)));
                            Symptoms symptoms = dataSnapshot.getValue(Symptoms.class);
                            //Log.d(TAG, "onDataChange: " + dataSnapshot);
                            /*If data received save it*/
                            confirmData(dataSnapshot.getKey().charAt(0), symptoms);
                        } else {

                            Log.e(TAG, "onDataChange: Key  not found in database, null returned");
                            confirmData('E', null);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(mContext, "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    /*Wait until downloading is finished by checking length*/
    private void confirmData(char c, Symptoms symptoms) {


        if (c == 'P') {

            p_Symptoms.add(symptoms);
            counter_p++;
        } else if (c == 'S') {
            s_symptoms.add(symptoms);
            counter_s++;
        } else if (c == 'D') {
            d_symptoms.add(symptoms);
            counter_d++;
        } else if (c == 'T') {
            t_symptoms.add(symptoms);
            counter_t++;
        } else {
            errors++;
        }
        if ((counter_s + counter_p + counter_d + counter_t + errors) >= (p_len + s_len + d_len + t_len)) {
//            Log.d(TAG, "confirmData: primary"+p_Symptoms);
            Log.d(TAG, "confirmData: Download complete");
//            Log.d(TAG, "confirmData: "+p_Symptoms + s_symptoms + d_symptoms + t_symptoms);
            Collections.sort(p_Symptoms, new SymptomComparator());
            Collections.sort(s_symptoms, new SymptomComparator());
            Collections.sort(d_symptoms, new SymptomComparator());
            Collections.sort(t_symptoms, new SymptomComparator());
            mCallback.onDownloadComplete(p_Symptoms, s_symptoms, d_symptoms, t_symptoms);
        }
    }

    /*Saves symptoms key to an array*/
    private void parseUrl() {

        String split[] = dataUrl.split("\\s+");
        for (String token : split) {

//            Log.d(TAG, "parseUrl: " + token);

            if (token == null || token.length() == 0) ;
            else if (token.charAt(0) == 'P') {
                p_key.add(token);
                p_len++;
            } else if (token.charAt(0) == 'S') {

                s_key.add(token);
                s_len++;
            } else if (token.charAt(0) == 'D') {
                d_key.add(token);
                d_len++;
            } else if (token.charAt(0) == 'T') {
                t_key.add(token);
                t_len++;
            }
        }
    }
}
