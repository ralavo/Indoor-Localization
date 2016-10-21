package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * This class will allow to determine the step position
 * using the location of the user.
 */
import android.location.Location;

public class StepPosition {

    private Location mCurrentLocation;
    private static final int eRadius = 6371000; //earth radius in meter

    //accessor to get the location
    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    //mutator to define the location
    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    /*
     * Compute the next position of the user using the previous position
     */
    public Location computeNextStep(float stepSize,float bearing) {
        Location newLoc = new Location(mCurrentLocation);
        float angDistance = stepSize / eRadius;

        double oldLat = mCurrentLocation.getLatitude();
        double oldLng = mCurrentLocation.getLongitude();

        //new latitude
        double newLat = Math.asin( Math.sin(Math.toRadians(oldLat))*Math.cos(angDistance) +
                Math.cos(Math.toRadians(oldLat))*Math.sin(angDistance)*Math.cos(bearing) );
        //new longitude
        double newLon = Math.toRadians(oldLng) +
                Math.atan2(Math.sin(bearing)*Math.sin(angDistance)*Math.cos(Math.toRadians(oldLat)),
                        Math.cos(angDistance) - Math.sin(Math.toRadians(oldLat))*Math.sin(newLat));

        //convert the latitude and longitude in degrees
        newLoc.setLatitude(Math.toDegrees(newLat));
        newLoc.setLongitude(Math.toDegrees(newLon));

        //set the bearing
        newLoc.setBearing((mCurrentLocation.getBearing()+180)% 360);
        mCurrentLocation = newLoc;

        return newLoc;
    }

}
