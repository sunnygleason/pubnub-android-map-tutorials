package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.flightpaths;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlightPathsTabContentFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = FlightPathsTabContentFragment.class.getName();
    private static final Double generatorOriginLat = 37.8199;
    private static final Double generatorOriginLng = -122.4783;
    private PubNub pubNub;
    private GoogleMap map;
    private String userName;
    private ScheduledExecutorService executorService;
    private Random random = new Random();
    private Long startTime;

    private static ImmutableMap<String, String> getNewLocationMessage(String userName, int randomLat, int randomLng, long elapsedTime) {
        String newLat = Double.toString(generatorOriginLat + ((randomLat + elapsedTime) * 0.000003));
        String newLng = Double.toString(generatorOriginLng + ((randomLng + elapsedTime) * 0.00001));

        return ImmutableMap.<String, String>of("who", userName, "lat", newLat, "lng", newLng);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flightpaths, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_flightpaths);
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

        pubNub.addListener(new FlightPathsPnCallback(new FlightPathsMapAdapter((Activity) this.getContext(), map), Constants.FLIGHTPATHS_CHANNEL_NAME));
        pubNub.subscribe().channels(Arrays.asList(Constants.FLIGHTPATHS_CHANNEL_NAME)).execute();

        scheduleRandomUpdates();
    }

    private void scheduleRandomUpdates() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.startTime = System.currentTimeMillis();

        this.executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ((Activity) FlightPathsTabContentFragment.this.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int randomLat = random.nextInt(10);
                        int randomLng = random.nextInt(10);
                        long elapsedTime = System.currentTimeMillis() - startTime;

                        final Map<String, String> message = getNewLocationMessage(userName, randomLat, randomLng, elapsedTime);

                        pubNub.publish().channel(Constants.FLIGHTPATHS_CHANNEL_NAME).message(message).async(
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
                });
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}