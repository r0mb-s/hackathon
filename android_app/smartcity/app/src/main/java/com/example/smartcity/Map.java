package com.example.smartcity;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import android.location.Address;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Random;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private SearchView mapSearchView;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapSearchView = findViewById(R.id.map_search);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> adressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(Map.this);
                    try {
                        adressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = adressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(Map.this);
                    }
                }
            }) ;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        LatLng current_location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(current_location).title("Current Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14.0f));
        pinpointLocation(45.74741894478222, 21.231679568224354, 0.1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        } else {
            Toast.makeText(this, "Please allow permission for location ", Toast.LENGTH_SHORT).show();
        }

    }

    public void pinpointLocations() {
        Random rand = new Random();

        for (int i = 1; i <= 10; i++) {
            // Generate random latitudes and longitudes within some bounds
            double lat = -90 + (90 - (-90)) * rand.nextDouble(); // Latitude between -90 and 90
            double lng = -180 + (180 - (-180)) * rand.nextDouble(); // Longitude between -180 and 180

            LatLng currentLocation = new LatLng(lat, lng);
            myMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker " + i).icon(setIcon(Map.this, R.drawable.baseline_flag_circle_24, "#ffffff")));
        }
    }

    public static String getHexColor(double percentage) {

        if (percentage >= 0 && percentage <= 1)
            percentage = 1 - percentage;
        else
            percentage = 0;

        int r = (int) (255 * (1 - percentage));
        int g = (int) (255 * percentage);
        int b = 0;

        String hex = String.format("#%02X%02X%02X", r, g, b);

        return hex;
    }
    public void pinpointLocation(double latitude, double longitude, double percentage) {
        String hex = getHexColor(percentage);

        LatLng currentLocation = new LatLng(latitude, longitude);
        myMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker " + String.valueOf(latitude) + " " + String.valueOf(longitude)).icon(setIcon(Map.this, R.drawable.baseline_flag_circle_24, hex)));
    }

    public BitmapDescriptor setIcon(Activity context, int drawableID, String color) {
        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}