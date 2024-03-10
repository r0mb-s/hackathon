package com.example.smartcity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;
import android.location.Address;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private SearchView mapSearchView;
    private final int FINE_PERMISSION_CODE = 1;
    private List<Pinpoint> pinpoints;

    private volatile boolean isActivityRunning = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    /*private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {


            try {

                new FetchDataTask().execute("http://192.168.222.153:5000/location_and_percentage");
                this.wait(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };*/


    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pinpoints = new ArrayList<Pinpoint>() ;
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
    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
        startLoopingThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    private void startLoopingThread() {
        Thread loopingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isActivityRunning) {
                    new FetchDataTask().execute("http://192.168.222.153:5000/location_and_percentage");

                    try {
                        Thread.sleep(1000); // Adjust the sleep time as needed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                }
            }
        });
        loopingThread.start();
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
        getDirections(45.76011756878361, 21.218371237283456,
        45.76308144719734, 21.210420656098492);
        //new FetchDataTask().execute("http://192.168.222.153:5000/location_and_percentage");
        //create_link();
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
    public Marker pinpointLocation(double latitude, double longitude, double percentage) {
        String hex = getHexColor(percentage);

        LatLng currentLocation = new LatLng(latitude, longitude);
        return myMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker " + String.valueOf(latitude) + " " + String.valueOf(longitude)).icon(setIcon(Map.this, R.drawable.circle_1, hex)));
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

    private class FetchDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String response = performGetRequest(params[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TRYING TO CONNECT", "CONNECTING");
            if (result != null) {
                try {
                    Log.e("This is the result: ", result);

                    JSONObject jsonObject = new JSONObject(result);

                    // Iterate over the keys of the JSONObject
                    Iterator<String> keys = jsonObject.keys();
                    boolean ok = true;
                    if (pinpoints.size() != 0)
                        ok = false;

                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONArray coordinates = jsonObject.getJSONArray(key); // Get the JSONArray associated with the key

                        // Extract latitude, longitude, and percentage from the JSONArray
                        double latitude = coordinates.getDouble(0);
                        double longitude = coordinates.getDouble(1);
                        double percentage = coordinates.getDouble(2);

                        if (ok == true) {
                            Pinpoint p = new Pinpoint(latitude, longitude, percentage, Integer.parseInt(key));
                            p.marker = pinpointLocation(latitude, longitude, percentage);
                            pinpoints.add(p);
                        }
                        else {
                            for (Pinpoint i : pinpoints) {
                                if (i.id == Integer.parseInt(key))
                                {
                                    i.percentage = percentage;
                                    String hex = getHexColor(percentage);
                                    i.marker.setIcon(setIcon(Map.this, R.drawable.circle_1, hex));
                                }
                            }
                        }

                    }


                    // Update UI elements here based on extracted data
                    // Note: You must switch to the main thread to update UI elements

                } catch (JSONException e) {
                    Log.e("FetchDataTask", "Json parsing error: " + e.getMessage());
                }
            }
        }
    }

    private String performGetRequest(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String responseJsonStr = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            responseJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return responseJsonStr;
    }

    private void getDirections(double from_latitude, double from_longitude, double to_latitude, double to_longitude) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from_latitude + "," + from_longitude +
                "&destination=" + to_latitude + "," + to_longitude + "&key=" + "@string/maps_api";

// Initialize a new RequestQueue instance
        RequestQueue queue = Volley.newRequestQueue(Map.this);

// Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse JSON response
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray routes = jsonResponse.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedPolyline = overviewPolyline.getString("points");

                            // Decode polyline points
                            List<LatLng> decodedPolyline = PolyUtil.decode(encodedPolyline);

                            // Draw polyline on the map
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.addAll(decodedPolyline);
                            polylineOptions.color(Color.RED); // Set color as per your choice
                            myMap.addPolyline(polylineOptions);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("Volley Error", error.toString());
            }
        });

// Add the request to the RequestQueue
        queue.add(stringRequest);
    }

        private void getDirections1(double from_latitude, double from_longitude, double to_latitude, double to_longitude) {
        try {
            Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin=" + from_latitude + "," + from_longitude +
                    "&destination=" + to_latitude + "," + to_longitude + "@string/maps_api");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        /*List<LatLng> points = PolyUtil.decode(polyline);


        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions.color(Color.RED); // Set color as per your choice
        myMap.addPolyline(polylineOptions);*/
        }
        catch (ActivityNotFoundException e ){
            Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin=" + from_latitude + "," + from_longitude +
                    "&destination=" + to_latitude + "," + to_longitude + "@string/maps_api");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        }
    }


}