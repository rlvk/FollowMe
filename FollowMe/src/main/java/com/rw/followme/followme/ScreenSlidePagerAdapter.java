package com.rw.followme.followme;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.rw.followme.followme.datamodel.PlaceNearby;

import java.util.List;

/**
 * Created by rafalwesolowski on 20/04/2014.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

    private PlaceNearby placeNearby;
    private final Context context;

    public ScreenSlidePagerAdapter(FragmentManager fm, PlaceNearby placeNearby, Context context) {
        super(fm);
        this.placeNearby = placeNearby;
        this.context = context;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return MapViewFragment.newInstance(placeNearby, ((ResultActivity)context).getPagerPosition(), position);
    }

    @Override
    public int getCount() {
        return placeNearby.getResults().size();
    }
}
