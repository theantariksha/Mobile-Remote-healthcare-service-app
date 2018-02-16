package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;



public class Register_fragment extends Fragment implements CheckConnection.testConnection {

    private static final String TAG = "Register_Fragment";
    private ChildDetails childDetails;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_fragment, container, false);
        Spinner spinner;
        ArrayAdapter<String> adapter_date = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dates));
        adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.date);
        spinner.setAdapter(adapter_date);

        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Months));
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.month);
        spinner.setAdapter(adapter_month);

        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Years));
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.year);
        spinner.setAdapter(adapter_year);

        ArrayAdapter<String> adapter_sex = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Sexs));
        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.selectSex);
        spinner.setAdapter(adapter_sex);

        ArrayAdapter<String> adapter_bloodGroup = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Blood_groups));
        adapter_bloodGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.bloodGroup);
        spinner.setAdapter(adapter_bloodGroup);

        ArrayAdapter<String> adapter_question = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Questions));
        adapter_question.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.selectQuestion);
        spinner.setAdapter(adapter_question);

        Button button = (Button) view.findViewById(R.id.buttonRegister);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: Register button clicked");
                int confirm = getDetails();
                if (confirm != 0) {
                    saveDetails();
                }
            }
        });

        return view;
    }

    /*Call back, if connection is there store*/
    @Override
    public void connectResult(boolean result) {

        if (result)
            storeNewDetails();
        else
            displayNoInternet();
    }

    /*Check connection using checkConnection class, same format, this function and above function required for checking connection*/
    void saveDetails() {


        Button bt = (Button) getView().findViewById(R.id.buttonRegister);
        bt.setEnabled(false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "saveDetails: checking connection");
        CheckConnection checkConnection = new CheckConnection(getContext(), this);
        checkConnection.execute(" ");
    }

    private int getDetails() {

        String patientName = "";
        String mothersName = "";
        String bloodGroup = "";
        String sex = "";
        String dateOfBirth = "";
        String address = "";
        String phone = "";
        String emailId = "";
        String questionAnswer = "";
        EditText editText;
        Spinner spinner;
        try {
            editText = (EditText) getView().findViewById(R.id.patientName);
            patientName = editText.getText().toString();
            if (patientName.length() == 0) {

                Toast.makeText(getContext(), "Enter name of patient", Toast.LENGTH_LONG).show();
                return 0;
            }
            editText = (EditText) getView().findViewById(R.id.mothersName);
            mothersName = editText.getText().toString();
            if (mothersName.length() == 0) {

                Toast.makeText(getContext(), "Enter mothers name", Toast.LENGTH_LONG).show();
                return 0;
            }
            spinner = (Spinner) getView().findViewById(R.id.selectSex);
            sex = spinner.getSelectedItem().toString();
            spinner = (Spinner) getView().findViewById(R.id.date);
            dateOfBirth += spinner.getSelectedItem().toString();
            spinner = (Spinner) getView().findViewById(R.id.month);
            dateOfBirth = spinner.getSelectedItem().toString() + dateOfBirth;
            spinner = (Spinner) getView().findViewById(R.id.year);
            dateOfBirth = spinner.getSelectedItem().toString() + dateOfBirth;
            if (!isValidDate(dateOfBirth)) {

                Toast.makeText(getContext(), "Invalid date", Toast.LENGTH_LONG).show();
                return 0;
            }
            spinner = (Spinner) getView().findViewById(R.id.bloodGroup);
            bloodGroup = spinner.getSelectedItem().toString();
            editText = (EditText) getView().findViewById(R.id.place);
            address = editText.getText().toString();
            if (address.length() == 0) {
                Toast.makeText(getContext(), "Enter valid address", Toast.LENGTH_LONG).show();
                return 0;
            }
            editText = (EditText) getView().findViewById(R.id.phone);
            phone = editText.getText().toString();
            if (phone.length() < 10 || phone.length() > 10) {
                Toast.makeText(getContext(), "Enter valid phone no", Toast.LENGTH_LONG).show();
                return 0;
            }
            editText = (EditText) getView().findViewById(R.id.mail);
            emailId = editText.getText().toString();
            spinner = (Spinner) getView().findViewById(R.id.selectQuestion);
            questionAnswer = spinner.getSelectedItem().toString();
            editText = (EditText) getView().findViewById(R.id.answer);
            if (editText.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "Enter your answer to security question", Toast.LENGTH_LONG).show();
                return 0;
            }
            questionAnswer += " " + editText.getText().toString();
        } catch (Exception e) {

            Log.d(TAG, "getDetails: error in input details " + e);
        }
        childDetails = new ChildDetails(patientName, mothersName, bloodGroup, sex, dateOfBirth, address, phone, emailId, questionAnswer);
        return 1;
    }

    private void checkAlreadyRegistered(final Long patientId){

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users/" + childDetails.getPhone() + "/" + childDetails.getPatientName());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                if (dataSnapshot.getValue() != null) {

                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Record Already Exists");
                    builder.setMessage("The current patient is already registered");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            Log.d(TAG, "onClick: duplicate registration");
                        }
                    });
                    builder.create();
                    builder.show();
                }
                else {
                    saveInDatabase(patientId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_LONG).show();
            }
        });
    }
    void saveInDatabase(Long patient_id) {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Current_registered_users");
        mDatabaseReference.setValue(patient_id + 1);
        childDetails.setPatient_id(patient_id);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Log.d(TAG, "saveInDatabase: "+mDatabaseReference);
        mDatabaseReference.child(childDetails.getPhone()).child(childDetails.getPatientName()).setValue(patient_id.toString());
        mDatabaseReference.child(childDetails.getPhone()).child(patient_id.toString()).child("Registration Details").setValue(childDetails);
        Button bt = (Button) getView().findViewById(R.id.buttonRegister);
        resetRegisterFields();
        String filename = "Health Assistant registration " + patient_id.toString() + ".txt";
        String details = "";
        details = "\nPatient Id - " + childDetails.getPatient_id() +
                "\nName - " + childDetails.getPatientName() +
                "\nMother's/Gurdain's name - " + childDetails.getMothersName() +
                "\nSex - " + childDetails.getSex() +
                "\nDate of Birth - " + childDetails.getDateOfBirth() +
                "\nBlood Group - " + childDetails.getBloodGroup() +
                "\nAddress - " + childDetails.getAddress() +
                "\nPhone - " + childDetails.getPhone() +
                "\nEmail id - " + childDetails.getEmailId();
        progressDialog.dismiss();
        displayPid(patient_id);
        bt.setEnabled(true);
    }

    void storeNewDetails() {

        try {

            mDatabaseReference = FirebaseDatabase.getInstance().getReference();  //get reference of top node
            mDatabaseReference.child("Current_registered_users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long patient_id = (Long) dataSnapshot.getValue();
                    checkAlreadyRegistered(patient_id);
                    Log.d(TAG, "onDataChange: got value " + patient_id);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.d(TAG, "onCancelled: Execption thrown");
                }
            });
        } catch (Exception e) {

            Log.d(TAG, "storeNewDetails: error in getting id" + e);
            Toast.makeText(getContext(), "Some error occured, can't save data", Toast.LENGTH_LONG).show();
        }
    }
   public  void displayNoInternet() {

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
        Button bt = (Button) getView().findViewById(R.id.buttonRegister);
        bt.setEnabled(true);
    }

    void resetRegisterFields() {

        EditText editText;
        Spinner spinner;
        editText = (EditText) getView().findViewById(R.id.patientName);
        editText.setText("");
        editText = (EditText) getView().findViewById(R.id.mothersName);
        editText.setText("");
        spinner = (Spinner) getView().findViewById(R.id.bloodGroup);
        spinner.setSelection(0);
        spinner = (Spinner) getView().findViewById(R.id.selectSex);
        spinner.setSelection(0);
        spinner = (Spinner) getView().findViewById(R.id.date);
        spinner.setSelection(0);
        spinner = (Spinner) getView().findViewById(R.id.month);
        spinner.setSelection(0);
        spinner = (Spinner) getView().findViewById(R.id.year);
        spinner.setSelection(0);
        editText = (EditText) getView().findViewById(R.id.place);
        editText.setText("");
        editText = (EditText) getView().findViewById(R.id.phone);
        editText.setText("");
        editText = (EditText) getView().findViewById(R.id.mail);
        editText.setText("");
        editText = (EditText) getView().findViewById(R.id.answer);
        editText.setText("");
    }

    void displayPid(Long patient_id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View displayView = inflater.inflate(R.layout.display_pid, null);
        builder.setView(displayView);
        TextView textview = (TextView) displayView.findViewById(R.id.display_pid_window);
        textview.setText(patient_id.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: pid displayed");
            }
        });
        builder.create();
        builder.show();
    }

    private boolean isValidDate(String dateString) {
        if (dateString == null || dateString.length() != "yyyyMMdd".length()) {
            return false;
        }

        int date;
        try {
            date = Integer.parseInt(dateString);
        } catch (NumberFormatException e) {
            return false;
        }

        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        // leap years calculation not valid before 1581
        boolean yearOk = (year >= 1581) && (year <= 2500);
        boolean monthOk = (month >= 1) && (month <= 12);
        boolean dayOk = (day >= 1) && (day <= daysInMonth(year, month));

        return (yearOk && monthOk && dayOk);
    }

    private  int daysInMonth(int year, int month) {
        int daysInMonth;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysInMonth = 31;
                break;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    daysInMonth = 29;
                } else {
                    daysInMonth = 28;
                }
                break;
            default:
                // returns 30 even for nonexistant months
                daysInMonth = 30;
        }
        return daysInMonth;
    }
}


