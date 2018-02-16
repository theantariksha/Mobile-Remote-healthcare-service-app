package com.example.ju_group.health_assist;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;



public class viewPagerAdapter extends FragmentPagerAdapter {


    ArrayList<Fragment> fragments=new ArrayList<Fragment>();
    ArrayList<String> tabTitles=new ArrayList<String>();

    public void addFragments(Fragment fragments,String string){

        this.fragments.add(fragments);
        this.tabTitles.add(string);
    }


    public viewPagerAdapter(android.support.v4.app.FragmentManager fm){

         super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){

        if(position==0)
             return "Register";
        else if(position==1)
             return "Log In";
        else
            return "";
    }
}
