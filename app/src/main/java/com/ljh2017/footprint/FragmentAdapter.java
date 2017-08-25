package com.ljh2017.footprint;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alfo06-15 on 2017-06-07.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    Fragment[] frags = new Fragment[4];


    public FragmentAdapter(FragmentManager fm) {
        super(fm);

        frags[0] = new Page1Fragment();
        frags[1] = new Page2Fragment();
        frags[2] = new Page3Fragment();
        frags[3] = new Page4Fragment();
    }

    @Override
    public Fragment getItem(int position) {
        return frags[position];
    }

    @Override
    public int getCount() {
        return frags.length;
    }
}
