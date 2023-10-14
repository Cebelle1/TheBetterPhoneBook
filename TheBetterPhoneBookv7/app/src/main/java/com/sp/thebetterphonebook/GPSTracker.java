package com.sp.thebetterphonebook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;


public class GPSTracker extends Service implements LocationListener{
    private Context mContext = null;
    boolean isGPSEnabled = false;          //flags
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; //location
    double latitude; //lat
    double longitude; //lon

    //The min distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; //10 meters
    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1; // 1 minute
    //Declaring a Location Manager
    protected LocationManager locationManger;

    public GPSTracker() {
        checkGPSPermissions();
    }

    public GPSTracker(Context context) {
        this.mContext = context;
        checkGPSPermissions();
    }

    public Location getLocation() {
        this.canGetLocation = false;
        try {
            locationManger = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            //get gps status
            isGPSEnabled = locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                //no network provider is enabled
                //prompt user to enable location services
                showEnableLocationAlert();
            } else{
                this.canGetLocation = true;
                if(isNetworkEnabled) {
                    //Permission granted
                    locationManger.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if (locationManger !=null) {
                        location = locationManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location !=null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                //if GPS Enabled, get lat/long using GPS services
                if(isGPSEnabled) {
                    if(location==null){
                        locationManger.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                        if (locationManger !=null) {
                            location = locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location !=null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (SecurityException e) {e.printStackTrace();}
        return location;
    }

    //Function to get latitude
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //Function to get Longitude
    public double getLongitude() {
        if(location!=null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public void stopUsingGPS() {
        if(locationManger !=null) {
            locationManger.removeUpdates(GPSTracker.this);
        }
    }

    //Check if GPS/Wifi enables @return boolean
    public boolean isCanGetLocation() {
        checkGPSPermissions();
        return canGetLocation;
    }

    //check for location permission
    public void checkGPSPermissions(){
        int permissionState1 = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionState1 == PackageManager.PERMISSION_GRANTED &&
                permissionState2==PackageManager.PERMISSION_GRANTED) {
            //perm granted, get location
            getLocation();
        }else {
            //Prompt user to enable location permission
            showEnablePermissionAlert();
        }
    }

    public void showEnablePermissionAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        //Setting dialog title
        alertDialog.setTitle("Location Permission Settings");
        //Setting dialog message
        alertDialog.setMessage("Restaurant List Location Permission is not enabled." +
                "Do you want to go to settings menu?");
        //On pressing settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                //Go to app setting
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        //on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showEnableLocationAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        //Seting Dialog title
        alertDialogBuilder.setTitle("Location Service Settings");
        alertDialogBuilder.setMessage("Location service is disabled in your device" +
                "Would you like to enable it?").setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable Location Service",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {getLocation();}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider){}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
