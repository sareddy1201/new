package perfectride;

/**
 * Created by IBLE 1 on 29-Aug-16.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

//

/**
 * LocationHelper
 */
public class LocationHelper implements LocationListener {
    /** Used to tag logs */
    //@SuppressWarnings("unused")
    private static final String TAG = "LocationHelper";

    private static long LOCATION_VALIDITY_DURATION_MS = 2 * 60 * 1000; // 2 min

    private WeakReference<LocationListener> mListenerWeak;
    private LocationManager mLocationManager;

    private final static int LOCATION_TIMEOUT_MS = 30 * 1000; // 30 seconds
    private Handler mHandler;
    private Context context;

    /**
     * Constructor.
     */
    public LocationHelper(Context context, LocationListener listener) {
        this.context = context;
        mListenerWeak = new WeakReference<>(listener);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean isGpsLocationProviderEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return false;
    }

    private boolean isNetworkLocationProviderEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return false;
    }

    //----------------------------------------------
    // Public API

    /** Start retrieving location, caching it for a few minutes. */
    public void startRetrievingLocation() {
        Location location = getLastKnownLocation();
        // If the last known location is recent enough, just return it, else request location updates
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= LOCATION_VALIDITY_DURATION_MS) {
            LocationListener listener = mListenerWeak.get();
            if (listener != null) {
                listener.onLocationChanged(location);
            }
        } else {
            // Don't start listeners if no provider is enabled
            if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) {
                LocationListener listener = mListenerWeak.get();
                if (listener != null) {
                    listener.onLocationChanged(null);
                }
                return;
            }

            if (isGpsLocationProviderEnabled()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            if (isNetworkLocationProviderEnabled()) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }

            mHandler = new Handler();
            mHandler.postDelayed(new GetLastLocation(this), LOCATION_TIMEOUT_MS);
        }
    }

    /** Get last known location even if this location is old. */
    public Location getLastKnownLocation() {
        Location netLoc = null;
        Location gpsLoc = null;
        if (isGpsLocationProviderEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (isNetworkLocationProviderEnabled()) {
            netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // If there are both values use the latest one
        if (gpsLoc != null && netLoc != null) {
            if (gpsLoc.getTime() > netLoc.getTime()) {
                return gpsLoc;
            } else {
                return netLoc;
            }
        }

        if (gpsLoc != null) {
            return gpsLoc;
        }
        if (netLoc != null) {
            return netLoc;
        }
        return null;
    }



    @Override
    public void onLocationChanged(Location location) {
        // Only update once
        removeUpdateListener();

        LocationListener listener = mListenerWeak.get();
        if (listener != null) {
            listener.onLocationChanged(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) { //Only if both are no longer available
            removeUpdateListener();

            LocationListener listener = mListenerWeak.get();
            if (listener != null) {
                Log.e(TAG,"Unable to find location, provider disabled: ");
                listener.onLocationChanged(null);
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Nothing to do
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) { //Only if both are no longer available
                // Only update once
                removeUpdateListener();

                LocationListener listener = mListenerWeak.get();
                if (listener != null) {
                    Log.e(TAG,"Unable to find location, provider unavailable or out of service: " + provider);
                    listener.onLocationChanged(null);
                }
            }
        }
    }

    private void removeUpdateListener() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(this);
    }

    private static class GetLastLocation implements Runnable {
        WeakReference<LocationHelper> mRef;

        public GetLastLocation(LocationHelper helper) {
            mRef = new WeakReference<>(helper);
        }

        @Override
        public void run() {
            LocationHelper helper = mRef.get();

            if (helper != null) {

                if (ActivityCompat.checkSelfPermission(helper.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(helper.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                helper.mLocationManager.removeUpdates(helper);

                Location location = helper.getLastKnownLocation();
                LocationListener listener = helper.mListenerWeak.get();
                if (listener != null) {
                    if (location != null) {
                        listener.onLocationChanged(location);
                    } else {
                        Log.e(TAG,"No last known location");
                        listener.onLocationChanged(null);
                    }
                }
            }
        }
    }

}

