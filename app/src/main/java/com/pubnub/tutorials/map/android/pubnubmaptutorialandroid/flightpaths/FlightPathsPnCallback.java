package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.flightpaths;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlightPathsPnCallback extends SubscribeCallback {
    private static final String TAG = FlightPathsPnCallback.class.getName();
    private FlightPathsMapAdapter flightPathsMapAdapter;
    private String watchChannel;

    public FlightPathsPnCallback(FlightPathsMapAdapter flightPathsMapAdapter, String watchChannel) {
        this.flightPathsMapAdapter = flightPathsMapAdapter;
        this.watchChannel = watchChannel;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.d(TAG, "status: " + status.toString());
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        if (!message.getChannel().equals(watchChannel)) {
            return;
        }

        try {
            Log.d(TAG, "message: " + message.toString());

            Map<String, String> newLocation = JsonUtil.fromJson(message.getMessage().toString(), LinkedHashMap.class);
            flightPathsMapAdapter.locationUpdated(newLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        if (!presence.getChannel().equals(watchChannel)) {
            return;
        }

        Log.d(TAG, "presence: " + presence.toString());
    }
}
