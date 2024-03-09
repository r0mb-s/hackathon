package com.example.smartcity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
        new FetchDataTask().execute("http://192.168.222.153:5000/location_and_percentage");
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
                    pinpoints = new ArrayList<Pinpoint>() ;
                    JSONObject jsonObject = new JSONObject(result);

                    // Iterate over the keys of the JSONObject
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONArray coordinates = jsonObject.getJSONArray(key); // Get the JSONArray associated with the key

                        // Extract latitude, longitude, and percentage from the JSONArray
                        double latitude = coordinates.getDouble(0);
                        double longitude = coordinates.getDouble(1);
                        double percentage = coordinates.getDouble(2);
                        Pinpoint p = new Pinpoint(latitude, longitude, percentage);
                        pinpoints.add(p);
                        pinpointLocation(latitude, longitude, percentage);

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

   /*public void create_link() {
       String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + 45.76011756878361 + "," + 21.218371237283456 +
               "&destination=" + 45.76308144719734 + "," + 21.210420656098492 + "&key=" + ;

// Make a request to the Directions API
       RequestQueue queue = Volley.newRequestQueue(context);
       StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       // Parse the JSON response
                       try {
                           JSONObject jsonResponse = new JSONObject(response);
                           JSONArray routesArray = jsonResponse.getJSONArray("routes");
                           JSONObject route = routesArray.getJSONObject(0);
                           JSONObject poly = route.getJSONObject("overview_polyline");
                           String polyline = poly.getString("points");

                           // Decode polyline points
                           List<LatLng> points = PolyUtil.decode(polyline);

                           // Draw the route on the map
                           PolylineOptions options = new PolylineOptions();
                           options.addAll(points);
                           options.color(Color.RED);
                           myMap.addPolyline(options);
                       } catch (JSONException e) {
                       Log.e("FetchDataTask", "Json parsing error: " + e.getMessage());
                        }
                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               // Handle error
           }
       });
    }
*/


}