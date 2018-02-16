package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*To get the id after answering the details, phone, name and answer*/
/*Same boring, get user input and stuff, so no comment*/

public class GetId extends BaseClass implements CheckConnection.testConnection{

    private static final String TAG = "GetId";
    private String phone, patient_name, answer;
    private String question, dateOfBirth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_id);
        actionToolbar(true, "Recover Id");

        final EditText edittext1, edittext2, editText;
        final Spinner[] spinner = new Spinner[1];
        editText = (EditText) findViewById(R.id.getid_phone);
        edittext1=(EditText)findViewById(R.id.getid_patientName);
        edittext2=(EditText)findViewById(R.id.getid_answer);

        ArrayAdapter<String> adapter_date = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dates));
        adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner[0] = (Spinner) findViewById(R.id.getid_date);
        spinner[0].setAdapter(adapter_date);

        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Months));
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner[0] = (Spinner)findViewById(R.id.getid_month);
        spinner[0].setAdapter(adapter_month);

        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Years));
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner[0] = (Spinner)findViewById(R.id.getid_year);
        spinner[0].setAdapter(adapter_year);

        ArrayAdapter<String> adapter_question = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Questions));
        adapter_question.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner[0] = (Spinner)findViewById(R.id.getid_choice_question);
        spinner[0].setAdapter(adapter_question);

        final Button bt=(Button)findViewById(R.id.getid_buttonSubmit);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = editText.getText().toString();
                patient_name=edittext1.getText().toString();
                answer=edittext2.getText().toString();
                spinner[0] = (Spinner)findViewById(R.id.getid_date);
                dateOfBirth += spinner[0].getSelectedItem().toString();
                spinner[0] = (Spinner)findViewById(R.id.getid_month);
                dateOfBirth = spinner[0].getSelectedItem().toString() + dateOfBirth;
                spinner[0] = (Spinner)findViewById(R.id.getid_year);
                dateOfBirth = spinner[0].getSelectedItem().toString() + dateOfBirth;
                spinner[0] =(Spinner)findViewById(R.id.getid_choice_question);
                question= spinner[0].getSelectedItem().toString();

                if(phone.length()==0 || patient_name.length()==0 || answer.length()==0){
                    Toast.makeText(getBaseContext(),"Fill all mandatory fields correctly",Toast.LENGTH_SHORT).show();
                }
                else
                    retrieveId();
            }
        });
    }
    void resetFields(){

        EditText edittext;
        Spinner spinner;
        edittext = (EditText) findViewById(R.id.getid_phone);
        edittext.setText("");
        edittext = (EditText)findViewById(R.id.getid_patientName);
        edittext.setText("");
        edittext=(EditText)findViewById(R.id.getid_answer);
        edittext.setText("");
        spinner= (Spinner) findViewById(R.id.getid_date);
        spinner.setSelection(0);
        spinner= (Spinner) findViewById(R.id.getid_year);
        spinner.setSelection(0);
        spinner= (Spinner) findViewById(R.id.getid_month);
        spinner.setSelection(0);
    }
    void retrieveId(){

        Button bt = (Button) findViewById(R.id.getid_buttonSubmit);
        bt.setEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "retrieveId: checking connection");
        CheckConnection checkConnection = new CheckConnection(getBaseContext(), this);
        checkConnection.execute(" ");
    }
    @Override
    public void connectResult(boolean result) {

        if(!result){
            displayNoInternet();
        }
        else
            checkValidity();
    }

    void displayNoInternet() {

        progressDialog.dismiss();
        Log.d(TAG, "displayNoInternet: here");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.no_connection, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: No intenet connection");
                    }
                });
        builder.create();
        builder.show();
        Button bt=(Button)findViewById(R.id.getid_buttonSubmit);
        bt.setEnabled(true);
    }

    void matchQuestionAnswer(String pid){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DatabaseReference mDatabaseReference;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();  //get reference of top node
        mDatabaseReference.child("Users").child(phone).child(pid).child("Registration Details").addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {


                Log.d(TAG, "onDataChange: "+dataSnapshot);
                if(dataSnapshot.getValue() == null){

                    builder.setTitle("Invalid");
                    builder.setMessage("No such patient is registered");
                }
                else{

                    ChildDetails childDetails = dataSnapshot.getValue(ChildDetails.class);
                    if((question+" "+answer).equals(childDetails.getQuestionAnswer())){
                        builder.setTitle("Patient Id");
                        builder.setMessage("Id: "+ childDetails.getPatient_id().toString()+ "\n");
                    }
                    else{
                        builder.setTitle("Invalid");
                        builder.setMessage("Information incorrect");
                    }
                }
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        resetFields();
                    }
                });
                builder.create();
                progressDialog.dismiss();
                builder.show();
                Button bt=(Button)findViewById(R.id.getid_buttonSubmit);
                bt.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"Some error occured",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onCancelled: Execption thrown");
            }
        });
    }
    void checkValidity(){

        try {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DatabaseReference mDatabaseReference;
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();  //get reference of top node
            mDatabaseReference.child("Users").child(phone).child(patient_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Log.d(TAG, "onDataChange: "+dataSnapshot);
                    if(dataSnapshot.getValue() == null){

                        progressDialog.dismiss();
                        builder.setTitle("Invalid");
                        builder.setMessage("No such patient is registered");
                        builder.create();
                        progressDialog.dismiss();
                        builder.show();
                        Button bt=(Button)findViewById(R.id.getid_buttonSubmit);
                        bt.setEnabled(true);
                    }
                    else{

                        matchQuestionAnswer((String) dataSnapshot.getValue());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(),"Some error occured",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onCancelled: Execption thrown");
                }
            });
        } catch (Exception e) {

            Log.d(TAG, "storeNewDetails: error in getting id" + e);
            Toast.makeText(this, "Some error occured, can't save data", Toast.LENGTH_LONG).show();
        }
    }

}
