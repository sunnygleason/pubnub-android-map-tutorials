package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationsubscribe;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationSubscribePnCallback extends SubscribeCallback {
    private static final String TAG = LocationSubscribePnCallback.class.getName();
    private LocationSubscribeMapAdapter locationMapAdapter;
    private String watchChannel;

    public LocationSubscribePnCallback(LocationSubscribeMapAdapter locationMapAdapter, String watchChannel) {
        this.locationMapAdapter = locationMapAdapter;
        this.watchChannel = watchChannel;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.d(TAG + "/PN_STATUS", "status: " + status.toString());
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        if (!message.getChannel().equals(watchChannel)) {
            return;
        }

        try {
            Log.d(TAG + "/PN_MESSAGE", "message: " + message.toString());

            Map<String, String> newLocation = JsonUtil.fromJson(message.getMessage().toString(), LinkedHashMap.class);
            locationMapAdapter.locationUpdated(newLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        Log.d(TAG + "/PN_PRESENCE", "presence: " + presence.toString());
    }
}
