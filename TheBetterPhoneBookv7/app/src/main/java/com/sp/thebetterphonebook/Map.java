package com.sp.thebetterphonebook;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.List;


public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String addr;
    private double lat;
    private double lon;
    private String contactName;
    private double myLat;
    private double myLon;
    private LatLng CONTACT;
    private LatLng ME;
    private GPSTracker gpsTracker;
    private ImageButton btnPlus;
    private ImageButton btnMinus;
    BitmapDescriptor smallMarkerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btnPlus=findViewById(R.id.btnMagnifyPlus);
        btnPlus.setOnClickListener(onZoomIn);
        btnMinus=findViewById(R.id.btnMagnifyMinus);
        btnMinus.setOnClickListener(onZoomOut);

        gpsTracker = new GPSTracker(Map.this);

        addr=getIntent().getStringExtra("addr");
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(Map.this);
        try{
            addressList=geocoder.getFromLocationName(addr,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        if(gpsTracker.isCanGetLocation()) {
            lat = address.getLatitude();
            lon = address.getLongitude();
        }


        contactName = getIntent().getStringExtra("name");

        myLat = gpsTracker.getLatitude();
        myLon = gpsTracker.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.elderlymap);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CONTACT = new LatLng(lat,lon);
        //ME = new LatLng(myLat,myLon);

        //For testing without GPS
        ME = new LatLng(1.344128,103.710911); //Home
        //ME = new LatLng(1.310662,103.778429);   //School


        mMap.addMarker(new MarkerOptions().position(CONTACT).title(contactName));
        mMap.addMarker(new MarkerOptions().position(ME).title("ME")
                .snippet("My location")
                .icon(smallMarkerIcon));

        //Move the cam instantly to restaurant with a zoom of 18
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 20));
    }

    View.OnClickListener onZoomIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
    };

    View.OnClickListener onZoomOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    };
}
