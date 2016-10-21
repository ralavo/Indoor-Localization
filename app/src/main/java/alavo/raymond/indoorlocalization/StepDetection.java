package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * This class will allow to detect the step of the user.
 *
 */
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetection extends Activity
        implements
        SensorEventListener {

    SensorManager sm;
    Sensor sensor;

    private StepDetectionListener mStepDetectionListener;

    int step = 0;

    //constructor of the class
    public StepDetection(SensorManager sm) {
        super();
        this.sm = sm;
        sensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
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

    @Override
    public void onSensorChanged(SensorEvent e) {
        float y;

        if (e.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            y = e.values[1];

            //detect if there is a step
            if (y > 1 && mStepDetectionListener != null) {
                onNewStepDetected();
            }
        }
    }

    public void onNewStepDetected() {
        float distanceStep = 0.8f;
        step++;
        mStepDetectionListener.newStep(distanceStep);
    }

    /*
    * define a listener for the step
     */
    public void setStepListener(StepDetectionListener listener) {
        mStepDetectionListener = listener;
    }

    public interface StepDetectionListener {
        public void newStep(float stepSize);

    }

}
