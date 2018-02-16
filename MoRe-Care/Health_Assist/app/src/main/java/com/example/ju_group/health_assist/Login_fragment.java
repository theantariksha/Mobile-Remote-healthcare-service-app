package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*Login, get id and phone*/
public class Login_fragment extends Fragment implements CheckConnection.testConnection {


    private static final String TAG = "Login_fragment";
    private Long ptid = Long.parseLong("-1");
    private String phone = "";
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_login_fragment, container, false);

        TextView textview = (TextView) view.findViewById(R.id.login_forgetid);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GetId.class);
                startActivity(intent);
            }
        });


        Button bt = (Button) view.findViewById(R.id.login_button);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*bypass login*/
//                Intent intent = new Intent(getActivity(), CheckDisease.class);
//                getActivity().finish();
//                startActivity(intent);
                Log.d(TAG, "onClick: Login button clicked");
                int errors;
                final EditText editText1, editText2;
                errors = 0;
                editText1 = (EditText) view.findViewById(R.id.login_patientid);
                try {
                    ptid = Long.parseLong(editText1.getText().toString());

                } catch (Exception e) {

                    Toast.makeText(getContext(), "Invalid patient id", Toast.LENGTH_SHORT).show();
                    errors++;
                }

                editText2 = (EditText) view.findViewById(R.id.login_phoneNo);
                phone = editText2.getText().toString();
                if (phone.length() < 10 || phone.length() > 10) {
                    Toast.makeText(getContext(), "Invalid Phone number", Toast.LENGTH_SHORT).show();
                    errors++;
                }
                if (errors == 0) {

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Checking");
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final Button bt = (Button) getView().findViewById(R.id.login_button);
                    bt.setEnabled(false);
                    tryLogging();
                }
            }
        });
        return view;
    }

    private void informWrongLoginDetails() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Invalid login credentials");
        alertDialogBuilder.setMessage("Check the information entered and try again");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Log.d(TAG, "onClick: Invalid input");
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bt = (Button) getView().findViewById(R.id.login_button);
        bt.setEnabled(true);
        progressDialog.dismiss();
    }

    void tryLogging() {

        CheckConnection checkConnection = new CheckConnection(getContext(), this);
        checkConnection.execute(" ");
    }

    @Override
    public void connectResult(boolean result) {

        if (result)
            connected();
        else
            displayNoInternet();
    }

    void connected() {

        DatabaseReference mDatabaseReference;
        final EditText editText1, editText2;
        final Button bt = (Button) getView().findViewById(R.id.login_button);
        editText1 = (EditText) getView().findViewById(R.id.login_patientid);
        editText2 = (EditText) getView().findViewById(R.id.login_phoneNo);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(phone).child(ptid.toString()).child("Registration Details");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChildDetails childDetails = dataSnapshot.getValue(ChildDetails.class);
                    Log.d(TAG, "onDataChange: got data " + childDetails);
                    Intent intent = new Intent(getActivity(), Main2Activity.class);
                    intent.putExtra("ChildDetails", childDetails);
                    editText1.setText("");
                    editText2.setText("");
                    bt.setEnabled(true);
                    progressDialog.dismiss();
                    startActivity(intent);
                } else {
                    informWrongLoginDetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Connection interrupted", Toast.LENGTH_LONG).show();
            }
        });
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
        Button bt = (Button) getView().findViewById(R.id.login_button);
        bt.setEnabled(true);
    }

}
