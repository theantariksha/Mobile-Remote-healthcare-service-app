package com.example.ju_group.health_assist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;



public class MainActivity extends BaseClass{

    Toolbar toolbar;
    TabLayout tablayout;
    ViewPager viewPager;
    viewPagerAdapter viewpageadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionToolbar(false, "Health Assistant");
        tablayout=(TabLayout)findViewById(R.id.tabs);
        viewPager=(ViewPager)findViewById(R.id.container);
        viewpageadapter=new viewPagerAdapter(getSupportFragmentManager());
        viewpageadapter.addFragments(new Register_fragment(),"Register");
        viewpageadapter.addFragments(new Login_fragment(),"Login");
        viewPager.setAdapter(viewpageadapter);
        tablayout.setupWithViewPager(viewPager);

    }
}



