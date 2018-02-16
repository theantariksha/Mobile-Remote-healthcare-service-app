package com.example.ju_group.health_assist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Treatment extends BaseClass{

    String treat_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        actionToolbar(true, "Treatment");
        TextView mTextView = (TextView) findViewById(R.id.treatment_list);
        if(savedInstanceState==null) {
            treat_list = getIntent().getStringExtra("Treatment");
            mTextView.setText(treat_list);
        }
        else{

            mTextView.setText(savedInstanceState.getString("Treatment"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Treatment", treat_list);
    }
}
