package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.common.collect.ImmutableList;
import com.pubnub.api.PubNub;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.flightpaths.FlightPathsTabContentFragment;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationpublish.LocationPublishTabContentFragment;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationsubscribe.LocationSubscribeTabContentFragment;

public class MainActivityTabManager extends FragmentStatePagerAdapter {
    private final LocationSubscribeTabContentFragment locationSubscribe;
    private final LocationPublishTabContentFragment locationPublish;
    private final FlightPathsTabContentFragment flightPathsTabContentFragment;

    private ImmutableList<Fragment> items;

    public MainActivityTabManager(FragmentManager fm, int NumOfTabs, PubNub pubNub) {
        super(fm);

        this.locationSubscribe = new LocationSubscribeTabContentFragment();
        this.locationSubscribe.setPubNub(pubNub);

        this.locationPublish = new LocationPublishTabContentFragment();
        this.locationPublish.setPubNub(pubNub);

        this.flightPathsTabContentFragment = new FlightPathsTabContentFragment();
        this.flightPathsTabContentFragment.setPubNub(pubNub);

        this.items = ImmutableList.of((Fragment) locationSubscribe, (Fragment) locationPublish, (Fragment) flightPathsTabContentFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }
}
