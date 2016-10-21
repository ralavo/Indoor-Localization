package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * this class will allow to determine the device position.
 * the device position is important to know in which
 * direction the user is going (Heading)
 * This class implement the SensorListenerEvent
 */
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DevicePosition extends Activity
        implements
        SensorEventListener {

    SensorManager sm;
    Sensor sensor;
    private float[] mRotationMatrixFromVector = new float[9];
    private float[] mRotationMatrix = new float[9];
    public float[] orientationVals = new float[3];
    private final int sensorType = Sensor.TYPE_ROTATION_VECTOR;

    //constructor
    public DevicePosition(SensorManager sm) {
        super();
        this.sm = sm;
        sensor = sm.getDefaultSensor(sensorType);
    }

    //register the information from the sensors
    public void start() {
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //stop to register the information
    public void stop() {
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    //each time we get new event on the sensors
    public void onSensorChanged(SensorEvent event) {

        //get the rotation vector and convert it to a rotation matrix
        SensorManager.getRotationMatrixFromVector(mRotationMatrixFromVector, event.values);

        //rotated the the rotation matrix in a different coordinate system
        SensorManager.remapCoordinateSystem(mRotationMatrixFromVector, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRotationMatrix);

        //compute the device orientation based on the rotation matrix
        SensorManager.getOrientation(mRotationMatrix, orientationVals);

        //get the device orientation in float in the system x, y, z
        orientationVals[0] = (float) orientationVals[0];
        orientationVals[1] = (float) orientationVals[1];
        orientationVals[2] = (float) orientationVals[2];

    }

}
