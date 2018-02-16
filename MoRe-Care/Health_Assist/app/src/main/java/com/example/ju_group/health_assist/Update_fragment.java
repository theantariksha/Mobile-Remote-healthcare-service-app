package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class Update_fragment extends Fragment implements View.OnClickListener,CheckConnection.testConnection{

    ChildDetails childDetails2;
    EditText editText;
    Spinner spinner;
    Button button,button1,button2;
    private DatabaseReference mDatabaseReference;
    private Long ptid = Long.parseLong("-1");
    private String TAG="Update Activity";
    private ProgressDialog progressDialog;
    private int flag=0;

    public Update_fragment(){}
    public Update_fragment(ChildDetails childDetails){
        childDetails2=childDetails;
        ptid=childDetails2.getPatient_id();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_update_fragment, container, false);

        ArrayAdapter<String> adapter_date = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dates));
        adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.update_date);
        spinner.setAdapter(adapter_date);
        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Months));
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.update_month);
        spinner.setAdapter(adapter_month);
        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Years));
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.update_year);
        spinner.setAdapter(adapter_year);
        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter_sex = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Sexs));
        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner= (Spinner) view.findViewById(R.id.update_selectSex);
        spinner.setAdapter(adapter_sex);
        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter_bloodgroup = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Blood_groups));
        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner= (Spinner) view.findViewById(R.id.update_bloodgroup);
        spinner.setAdapter(adapter_bloodgroup);
        spinner.setVisibility(View.GONE);

        ArrayAdapter<String> adapter_question = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Questions));
        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner= (Spinner) view.findViewById(R.id.update_selectQuestion);
        spinner.setAdapter(adapter_question);
        spinner.setVisibility(View.GONE);

        spinner= (Spinner) view.findViewById(R.id.verify_selectQuestion);
        spinner.setAdapter(adapter_question);



        button = (Button) view.findViewById(R.id.update_button);
        button.setOnClickListener(this);
        button.setVisibility(View.INVISIBLE);
        button1 = (Button) view.findViewById(R.id.update_edit);
        button1.setOnClickListener(this);
        button1.setVisibility(View.INVISIBLE);
        button2 = (Button) view.findViewById(R.id.update_button1);
        button2.setOnClickListener(this);

        return view;
    }


    private void informEditDetails() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Edit your changes and press Submit");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.update_button:
                get_text();
                break;
            case R.id.update_edit:
                set_text();
                break;
            case R.id.update_button1:
                set_field();
                break;
            default:
                break;
        }

    }

    private void set_field(){

        spinner=(Spinner)getView().findViewById(R.id.verify_selectQuestion);
        String quesans=spinner.getSelectedItem().toString()+" ";
        editText=(EditText)getView().findViewById(R.id.verify_answer);
        quesans+=editText.getText().toString();

        //button2.setEnabled(false);
        //button2.setText("Verifying...");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Verifying");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            flag=1;
            CheckConnection checkConnection = new CheckConnection(getContext(), this);
            checkConnection.execute(" ");
            verifywithserver(quesans);
        }catch (Exception e){
            progressDialog.dismiss();
            displayNoInternet();
            button2.setText("Submit");
            button2.setEnabled(true);
        }

    }

    private void verifywithserver(final String quesans){

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(childDetails2.getPhone()).child(childDetails2.getPatient_id().toString()).child("Registration Details");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChildDetails childDetails = dataSnapshot.getValue(ChildDetails.class);
                    Log.d(TAG, "onDataChange: got data " + childDetails);

                    progressDialog.dismiss();
                    if(childDetails.getQuestionAnswer().equals(quesans)){

                        TextView textView;
                        textView=(TextView)getView().findViewById(R.id.update_ptname);
                        textView.setVisibility(getView().VISIBLE);
                        editText=(EditText)getView().findViewById(R.id.update_patientName);
                        editText.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_dispMotherName);
                        textView.setVisibility(getView().VISIBLE);
                        editText=(EditText)getView().findViewById(R.id.update_mothersName);
                        editText.setVisibility(getView().VISIBLE);


                        textView=(TextView)getView().findViewById(R.id.update_dispPlace);
                        textView.setVisibility(getView().VISIBLE);
                        editText=(EditText)getView().findViewById(R.id.update_place);
                        editText.setVisibility(getView().VISIBLE);


                        textView=(TextView)getView().findViewById(R.id.update_dispMail);
                        textView.setVisibility(getView().VISIBLE);
                        editText=(EditText)getView().findViewById(R.id.update_mail);
                        editText.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_tv9);
                        textView.setVisibility(getView().VISIBLE);
                        spinner=(Spinner)getView().findViewById(R.id.update_date);
                        spinner.setVisibility(getView().VISIBLE);

                        spinner=(Spinner)getView().findViewById(R.id.update_month);
                        spinner.setVisibility(getView().VISIBLE);

                        spinner=(Spinner)getView().findViewById(R.id.update_year);
                        spinner.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_dispSex);
                        textView.setVisibility(getView().VISIBLE);
                        spinner=(Spinner)getView().findViewById(R.id.update_selectSex);
                        spinner.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_dispbloodgroup);
                        textView.setVisibility(getView().VISIBLE);
                        spinner=(Spinner)getView().findViewById(R.id.update_bloodgroup);
                        spinner.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_dispQuestion);
                        textView.setVisibility(getView().VISIBLE);
                        spinner=(Spinner)getView().findViewById(R.id.update_selectQuestion);
                        spinner.setVisibility(getView().VISIBLE);
                        editText=(EditText)getView().findViewById(R.id.update_answer);
                        editText.setVisibility(getView().VISIBLE);

                        textView=(TextView)getView().findViewById(R.id.update_mandatoryField);
                        textView.setVisibility(getView().VISIBLE);

                        button.setVisibility(getView().VISIBLE);
                        button1.setVisibility(getView().VISIBLE);

                        button2.setVisibility(getView().GONE);
                        textView=(TextView)getView().findViewById(R.id.verify_dispQuestion);
                        textView.setVisibility(getView().GONE);
                        editText=(EditText)getView().findViewById(R.id.verify_answer);
                        editText.setVisibility(getView().GONE);
                        spinner=(Spinner)getView().findViewById(R.id.verify_selectQuestion);
                        spinner.setVisibility(getView().GONE);


                    }
                    else{
                        Toast.makeText(getContext(), "Your security question answer does not match", Toast.LENGTH_SHORT).show();
                        button2.setText("Submit");
                        button2.setEnabled(true);
                    }

                } else {
                    //
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Connection interrupted", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void get_text(){

        button1.setEnabled(false);

        editText=(EditText)getView().findViewById(R.id.update_patientName);
        if(editText.getText().toString().length()==0){
            Toast.makeText(getContext(),"Fill patientname correctly",Toast.LENGTH_SHORT).show();
            button1.setEnabled(true);
            return ;
        }
        editText=(EditText)getView().findViewById(R.id.update_mothersName);
        if(editText.getText().toString().length()==0){
            Toast.makeText(getContext(),"Fill Gurdianname correctly",Toast.LENGTH_SHORT).show();
            button1.setEnabled(true);
            return ;
        }
        editText=(EditText)getView().findViewById(R.id.update_place);
        if(editText.getText().toString().length()==0){
            Toast.makeText(getContext(),"Fill address correctly",Toast.LENGTH_SHORT).show();
            button1.setEnabled(true);
            return ;
        }
        editText=(EditText)getView().findViewById(R.id.update_mail);
      /*  if(editText.getText().toString().length()==0){
            Toast.makeText(getContext(),"Fill email id correctly",Toast.LENGTH_SHORT).show();
            return ;
        }*/



        editText=(EditText)getView().findViewById(R.id.update_patientName);
        childDetails2.setPatientName(editText.getText().toString());
        editText=(EditText)getView().findViewById(R.id.update_mothersName);
        childDetails2.setMothersName(editText.getText().toString());
        editText=(EditText)getView().findViewById(R.id.update_mail);
        if(editText.getText().toString().length()!=0)
           childDetails2.setEmailId(editText.getText().toString());
        editText=(EditText)getView().findViewById(R.id.update_place);
        childDetails2.setAddress(editText.getText().toString());

        String dob="";
        spinner=(Spinner)getView().findViewById(R.id.update_date);
        dob=spinner.getSelectedItem().toString();
        spinner=(Spinner)getView().findViewById(R.id.update_month);
        dob=spinner.getSelectedItem().toString()+dob;
        spinner=(Spinner)getView().findViewById(R.id.update_year);
        dob=spinner.getSelectedItem().toString()+dob;
        childDetails2.setDateOfBirth(dob);

        spinner=(Spinner)getView().findViewById(R.id.update_selectSex);
        childDetails2.setSex(spinner.getSelectedItem().toString());

        spinner=(Spinner)getView().findViewById(R.id.update_bloodgroup);
        childDetails2.setBloodGroup(spinner.getSelectedItem().toString());

        spinner=(Spinner)getView().findViewById(R.id.update_selectQuestion);
        String quesans=spinner.getSelectedItem().toString()+" ";
        editText=(EditText)getView().findViewById(R.id.update_answer);
        quesans+=editText.getText().toString();

        childDetails2.setQuestionAnswer(quesans);

       /* ConnectivityManager cm=(ConnectivityManager)getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {

            button.setEnabled(false);
            button.setText("Updating..");
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(childDetails2.getPhone()).child(childDetails2.getPatientName());
            mDatabaseReference.child("Registration Details").setValue(childDetails2);
            Toast.makeText(getContext(),"Your details is updated successfully",Toast.LENGTH_LONG).show();
            reset();
            button.setEnabled(true);
            button.setText("Update");
        }
        else{
            Toast.makeText(getContext(),"You have no active Internet Connection. Turn it on ",Toast.LENGTH_LONG).show();
        }*/

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CheckConnection checkConnection = new CheckConnection(getContext(), this);
        checkConnection.execute(" ");

        button1.setEnabled(true);

    }

    private void set_text(){

        informEditDetails();
        //button2.setVisibility(getView().GONE);

        TextView textView=(TextView)getView().findViewById(R.id.update_ptid);
        textView.setText("Patient Id:    "+Long.toString(ptid));
        textView.setVisibility(getView().VISIBLE);

        textView=(TextView)getView().findViewById(R.id.update_ptname);
        textView.setVisibility(getView().VISIBLE);
        editText=(EditText)getView().findViewById(R.id.update_patientName);
        editText.setVisibility(getView().VISIBLE);
        editText.setText(childDetails2.getPatientName());

        textView=(TextView)getView().findViewById(R.id.update_dispMotherName);
        textView.setVisibility(getView().VISIBLE);
        editText=(EditText)getView().findViewById(R.id.update_mothersName);
        editText.setVisibility(getView().VISIBLE);
        editText.setText(childDetails2.getMothersName());

        textView=(TextView)getView().findViewById(R.id.update_dispPlace);
        textView.setVisibility(getView().VISIBLE);
        editText=(EditText)getView().findViewById(R.id.update_place);
        editText.setVisibility(getView().VISIBLE);
        editText.setText(childDetails2.getAddress());


        textView=(TextView)getView().findViewById(R.id.update_dispMail);
        textView.setVisibility(getView().VISIBLE);
        editText=(EditText)getView().findViewById(R.id.update_mail);
        editText.setVisibility(getView().VISIBLE);
        editText.setText(childDetails2.getEmailId());

        editText=(EditText)getView().findViewById(R.id.update_answer);
        editText.setText(((childDetails2.getQuestionAnswer().split("\\? "))[1]));


        int dob=Integer.parseInt(childDetails2.getDateOfBirth());
        int year=dob/10000;
        int month=(dob%10000)/100;
        int date=(dob%10000)%100;

        textView=(TextView)getView().findViewById(R.id.update_tv9);
        textView.setVisibility(getView().VISIBLE);
        spinner=(Spinner)getView().findViewById(R.id.update_date);
        spinner.setVisibility(getView().VISIBLE);
        spinner.setSelection(date-1);
        spinner=(Spinner)getView().findViewById(R.id.update_month);
        spinner.setVisibility(getView().VISIBLE);
        spinner.setSelection(month-1);
        spinner=(Spinner)getView().findViewById(R.id.update_year);
        spinner.setVisibility(getView().VISIBLE);
        spinner.setSelection(year-1990);

        textView=(TextView)getView().findViewById(R.id.update_dispSex);
        textView.setVisibility(getView().VISIBLE);
        spinner=(Spinner)getView().findViewById(R.id.update_selectSex);
        spinner.setVisibility(getView().VISIBLE);
        if(childDetails2.getSex().equals("Male"))
            spinner.setSelection(0);
        if(childDetails2.getSex().equals("Female"))
            spinner.setSelection(1);
        if(childDetails2.getSex().equals("Other"))
            spinner.setSelection(2);

        textView=(TextView)getView().findViewById(R.id.update_dispbloodgroup);
        textView.setVisibility(getView().VISIBLE);
        spinner=(Spinner)getView().findViewById(R.id.update_bloodgroup);
        spinner.setVisibility(getView().VISIBLE);
        if(childDetails2.getBloodGroup().equals("A+"))
            spinner.setSelection(0);
        else if(childDetails2.getBloodGroup().equals("A-"))
            spinner.setSelection(1);
        else if(childDetails2.getBloodGroup().equals("B+"))
            spinner.setSelection(2);
        else if(childDetails2.getBloodGroup().equals("B-"))
            spinner.setSelection(3);
        else if(childDetails2.getBloodGroup().equals("AB+"))
            spinner.setSelection(4);
        else if(childDetails2.getBloodGroup().equals("AB-"))
            spinner.setSelection(5);
        else if(childDetails2.getBloodGroup().equals("O+"))
            spinner.setSelection(6);
        else
            spinner.setSelection(7);

        spinner=(Spinner)getView().findViewById(R.id.update_selectQuestion);
        spinner.setVisibility(getView().VISIBLE);
        if(((childDetails2.getQuestionAnswer().split("\\?"))[0]+"?").equals("What is your Hometown ?"))
            spinner.setSelection(0);
        if(((childDetails2.getQuestionAnswer().split("\\?"))[0]+"?").equals("What is your favourite book ?"))
            spinner.setSelection(1);
        if(((childDetails2.getQuestionAnswer().split("\\?"))[0]+"?").equals("What is your favourite place ?"))
            spinner.setSelection(2);




    }

    private void reset(){

        TextView textView=(TextView)getView().findViewById(R.id.update_ptid);
        textView.setText("");
        editText=(EditText)getView().findViewById(R.id.update_patientName);
        editText.setText("");
        editText=(EditText)getView().findViewById(R.id.update_mothersName);
        editText.setText("");
        editText=(EditText)getView().findViewById(R.id.update_place);
        editText.setText("");
        editText=(EditText)getView().findViewById(R.id.update_mail);
        editText.setText("");
        editText=(EditText)getView().findViewById(R.id.update_answer);
        editText.setText("");
        spinner=(Spinner)getView().findViewById(R.id.update_date);
        spinner.setSelection(0);
        spinner=(Spinner)getView().findViewById(R.id.update_month);
        spinner.setSelection(0);
        spinner=(Spinner)getView().findViewById(R.id.update_year);
        spinner.setSelection(0);
        spinner=(Spinner)getView().findViewById(R.id.update_selectSex);
        spinner.setSelection(0);
        spinner=(Spinner)getView().findViewById(R.id.update_bloodgroup);
        spinner.setSelection(0);
        spinner=(Spinner)getView().findViewById(R.id.update_selectQuestion);
        spinner.setSelection(0);

    }

    @Override
    public void connectResult(boolean result) {

        if(result)
            update_data();
        else
            displayNoInternet();
    }

    private void update_data(){

        button.setEnabled(false);
        button.setText("Submitting..");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(childDetails2.getPhone()).child(childDetails2.getPatient_id().toString());
        mDatabaseReference.child("Registration Details").setValue(childDetails2);
        if(flag==0)
          Toast.makeText(getContext(),"Your details is updated successfully",Toast.LENGTH_LONG).show();
        else
          flag=0;

        TextView textView=(TextView)getView().findViewById(R.id.update_ptid);
        textView.setVisibility(getView().GONE);

        progressDialog.dismiss();
        reset();
        button.setEnabled(true);
        button.setText("Submit");
    }

    void displayNoInternet() {

        progressDialog.dismiss();
        Log.d(TAG, "displayNoInternet: here");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
    }

}
