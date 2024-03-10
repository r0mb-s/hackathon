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
public class Link {
    public PolylineOptions polylineOptions;
    public Pinpoint pair1;
    public Pinpoint pair2;

    Link(PolylineOptions polylineOptions, Pinpoint pair1, Pinpoint pair2)
    {
        this.polylineOptions = polylineOptions;
        this.pair1 = pair1;
        this.pair2 = pair2;
    }

    public void updateColor()
    {
        String color = getHexColor((pair1.percentage + pair2.percentage)/2);
        polylineOptions.color(Color.parseColor(color));

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
}
