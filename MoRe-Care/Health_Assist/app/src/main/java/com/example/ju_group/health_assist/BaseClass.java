package com.example.ju_group.health_assist;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class BaseClass extends AppCompatActivity {

    /*Extend from this class to have the back button, and toolbar support*/

    void actionToolbar(boolean enableHome, String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null){
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            if(toolbar!=null){
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
                actionBar.setTitle(title);
            }
        }

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }
}
