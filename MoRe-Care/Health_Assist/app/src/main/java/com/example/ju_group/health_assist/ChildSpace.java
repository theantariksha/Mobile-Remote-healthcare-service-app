package com.example.ju_group.health_assist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.ListViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChildSpace extends Fragment implements CheckConnection.testConnection{

    ChildDetails childDetails2;
    private ProgressDialog progressDialog;

    public ChildSpace(){}
    public ChildSpace(ChildDetails childDetails){
        childDetails2=childDetails;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_child_space, container, false);
        setview(view);

        Button button=(Button) view.findViewById(R.id.proceed_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });


        return view;
    }


    public void setview(View view){

        TextView tv1=(TextView) view.findViewById(R.id.disp_patientid);
        tv1.setText(childDetails2.getPatient_id().toString());
        tv1=(TextView) view.findViewById(R.id.disp_patientName);
        tv1.setText(childDetails2.getPatientName());
        tv1=(TextView) view.findViewById(R.id.disp_mothersName);
        tv1.setText(childDetails2.getMothersName());
        tv1=(TextView) view.findViewById(R.id.disp_mail);
        if(childDetails2.getEmailId().length()>0)
            tv1.setText(childDetails2.getEmailId());
        else
            tv1.setText("<None>");
        tv1=(TextView) view.findViewById(R.id.disp_bloodgroup);
        tv1.setText(childDetails2.getBloodGroup());
        tv1=(TextView) view.findViewById(R.id.disp_sex);
        tv1.setText(childDetails2.getSex());
        tv1=(TextView) view.findViewById(R.id.disp_phone);
        tv1.setText(childDetails2.getPhone());
        tv1=(TextView) view.findViewById(R.id.disp_place);
        tv1.setText(childDetails2.getAddress());
        tv1=(TextView) view.findViewById(R.id.disp_dob);
        String dobirth=childDetails2.getDateOfBirth();
        Integer date=Integer.parseInt(dobirth);

        int year = date / 10000;
        int month = (date % 10000) / 100;
        int day = date % 100;

        String dob=(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
        tv1.setText(dob);
    }

    void proceed(){

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Moving to Next");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CheckConnection checkConnection = new CheckConnection(getContext(),this);
        checkConnection.execute(" ");
    }
    @Override
    public void connectResult(boolean result) {
        if(result)
            proceed_to_disease();
        else
            displayNoInternet();
    }

    void displayNoInternet() {

        progressDialog.dismiss();
        // Log.d(TAG, "displayNoInternet: here");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.no_connection, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Log.d(TAG, "onClick: No intenet connection");
                    }
                });
        builder.create();
        builder.show();
        Button bt=(Button) getView().findViewById(R.id.proceed_button);
        bt.setEnabled(true);
    }

    void proceed_to_disease(){

        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), CheckDisease.class);
        getActivity().finish();
        startActivity(intent);

    }


}
