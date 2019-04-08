package com.example.moham.my_whatsapp.User;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class viewbageradabter extends FragmentPagerAdapter {


    ArrayList<Fragment>fragments;
    ArrayList<String>titles;


    public viewbageradabter(FragmentManager fm) {
        super(fm);
        fragments=new ArrayList<>();
        titles=new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void add(Fragment fragment,String title){
        fragments.add(fragment);
        titles.add(title);
    }

}
