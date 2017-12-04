package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationpublish;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.common.collect.ImmutableMap;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.Constants;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.R;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationsubscribe.LocationSubscribeMapAdapter;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationsubscribe.LocationSubscribePnCallback;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

import java.util.Arrays;
import java.util.Map;

public class LocationPublishTabContentFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final String TAG = LocationPublishTabContentFragment.class.getName();

    private PubNub pubNub;
    private GoogleMap map;
    private LocationHelper locationHelper;
    private String userName;

    private static ImmutableMap<String, String> getNewLocationMessage(String userName, Location location) {
        return ImmutableMap.<String, String>of("who", userName, "lat", Double.toString(location.getLatitude()), "lng", Double.toString(location.getLongitude()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locationpublish, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_locationpublish);
        mapFragment.getMapAsync(this);

        return view;
    }

    public void setPubNub(PubNub pubNub) {
        this.pubNub = pubNub;
        this.userName = this.pubNub.getConfiguration().getUuid();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        this.pubNub.addListener(new LocationPublishPnCallback(new LocationPublishMapAdapter((Activity) this.getContext(), map), Constants.PUBLISH_CHANNEL_NAME));
        this.pubNub.subscribe().channels(Arrays.asList(Constants.PUBLISH_CHANNEL_NAME)).execute();

        this.locationHelper = new LocationHelper((Activity) this.getContext(), this);
    }

    @Override
    public void onLocationChanged(Location location) {
        final Map<String, String> message = getNewLocationMessage(this.userName, location);

        this.pubNub.publish().channel(Constants.PUBLISH_CHANNEL_NAME).message(message).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                            } else {
                                Log.v(TAG, "publishErr(" + status.toString() + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}