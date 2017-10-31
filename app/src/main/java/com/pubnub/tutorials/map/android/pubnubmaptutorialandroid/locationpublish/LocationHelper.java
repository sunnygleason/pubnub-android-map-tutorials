package com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.locationpublish;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pubnub.tutorials.map.android.pubnubmaptutorialandroid.util.JsonUtil;

public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationListener mLocationListener;

    public LocationHelper(Context context, LocationListener mLocationListener) {
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.mGoogleApiClient.connect();

        this.mLocationListener = mLocationListener;
    }

    public void connect() {
        this.mGoogleApiClient.connect();
    }

    public void disconnect() {
        this.mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (lastLocation != null) {
                onLocationChanged(lastLocation);
            }
        } catch (SecurityException e) {
            Log.v("locationDenied", e.getMessage());
        }

        try {
            LocationRequest locationRequest = LocationRequest.create().setInterval(5000);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            Log.v("locationDenied", e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("googlePlayDenied", connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.v("locationChanged", JsonUtil.asJson(location));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mLocationListener.onLocationChanged(location);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mLocationListener.onLocationChanged(null);
    }
}