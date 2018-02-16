package com.example.ju_group.health_assist;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class Main2Activity extends BaseClass {

    Toolbar toolbar;
    TabLayout tablayout;
    ViewPager viewPager;
    viewPagerAdapter2 viewpageadapter;
    ChildDetails childDetails2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        actionToolbar(true, "Child Space");
        if(savedInstanceState==null)
            childDetails2=(ChildDetails) getIntent().getSerializableExtra("ChildDetails");
        else
            childDetails2=(ChildDetails) savedInstanceState.getSerializable("ChildDetails");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tablayout=(TabLayout)findViewById(R.id.tabs2);
        viewPager=(ViewPager)findViewById(R.id.container2);
        viewpageadapter=new viewPagerAdapter2(getSupportFragmentManager());
        viewpageadapter.addFragments(new ChildSpace(childDetails2),"Details");
        viewpageadapter.addFragments(new Update_fragment(childDetails2),"Edit");
        viewPager.setAdapter(viewpageadapter);
        tablayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("ChildDetails", childDetails2);
    }
}
