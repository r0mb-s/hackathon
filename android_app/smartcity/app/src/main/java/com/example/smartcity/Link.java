package com.example.smartcity;
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
    
    public void updateColor(GoogleMap map)
    {
        polylineOptions.setColor(getHexColor((pair1.percentage + pair2.percentage)/2));
        polylineOptions.invalidate();
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
