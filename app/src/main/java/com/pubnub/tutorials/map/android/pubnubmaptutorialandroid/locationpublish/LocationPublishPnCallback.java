package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationpublish;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationsubscribe.LocationSubscribeMapAdapter;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationPublishPnCallback extends SubscribeCallback {
    private static final String TAG = LocationPublishPnCallback.class.getName();
    private LocationPublishMapAdapter locationMapAdapter;
    private String watchChannel;

    public LocationPublishPnCallback(LocationPublishMapAdapter locationMapAdapter, String watchChannel) {
        this.locationMapAdapter = locationMapAdapter;
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
            locationMapAdapter.locationUpdated(newLocation);
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
