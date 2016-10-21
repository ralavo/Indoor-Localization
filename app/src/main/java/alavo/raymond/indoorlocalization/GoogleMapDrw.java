package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * this class allows display the map location with a marker.
 * It allows to start to dram a new segment to represent the user
 * movement and to set a end marker when its movement is completed.
 *
 **/

import java.util.List;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GoogleMapDrw {

    GoogleMap gm;
    Polyline current = null;        //to draw the segment
    Marker currentMarker;           //to set the markers

    //constructor of the class
    public GoogleMapDrw(GoogleMap gm, LatLng lastKnown) {
        this.gm = gm;

        //adjust the map to focus on the actual position of the user
        gm.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnown, 18));

        //set the marker on the current position
        currentMarker = gm.addMarker(new MarkerOptions()
                .title("Current")
                .position(lastKnown));

        //make the marker invisible
        currentMarker.setVisible(false);
    }

    /*
    * this method allows to start drawing a new segment
     */
    public void newSegment() {
        //draw a new segment
        current = gm.addPolyline(new PolylineOptions().color(Color.BLUE).width(5));
        current.setVisible(true);
    }

    /*
     * This method allows to add a new point on the segment
     * point: represent the coordinate of the location
     */
    public void newPoint(LatLng point, double angle) {
        if (current == null) {
            //if there is no segment drawing, start a new one
            newSegment();
        }
        // if it is a new point add a marker
        if (current.getPoints().size() == 0) {
            gm.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        //make the marker visible
        currentMarker.setVisible(true);
        currentMarker.setPosition(point);
        currentMarker.setRotation((float) Math.toDegrees(angle));

        //for each new position detected add a new point to the segment
        List<LatLng> points = current.getPoints();  //add it to the list
        points.add(point);                          //add the position
        current.setPoints(points);

        //adjust the map to focus on the actual position of the user
        CameraUpdate center = CameraUpdateFactory.newLatLng(point);
        gm.moveCamera(center);

    }

    /*
    * This method allows to set the end of the current segment
    */
    public void endSegment() {

        if (current != null && current.getPoints().size() > 0) {
            List<LatLng> points = current.getPoints();

            //add a marker to show the end of the segment
            gm.addMarker(new MarkerOptions()
                    .position(points.get(points.size() - 1))
                    .title("End")
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        currentMarker.setVisible(false);
    }

    /*
    * clear the map
    */
    public void clear() {
        gm.clear();
    }

}