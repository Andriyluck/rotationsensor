package am.rotationsensor;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import com.matterport.rotationsensor.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // view
    private CustomDrawableView mView;
    private CustomDrawableView mView3;

    // sensor
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private Sensor mGravitySensor;

    int orientation;
    int rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mView = (CustomDrawableView) findViewById(R.id.sensorView);
        mView3 = (CustomDrawableView) findViewById(R.id.sensorView3);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();

        updateLabels();
    }

    private void updateLabels() {

        String rotationText = "";
        switch (rotation) {
            case Surface.ROTATION_90:
                rotationText = " (rotation 90, AXIS_Y -> AXIS_MINUS_X)";
                break;
            case Surface.ROTATION_270:
                rotationText = " (rotation 270, AXIS_Y -> AXIS_X)";
                break;
            default:
                rotationText = " (rotation 0)";
                break;
        }

        TextView text1 = (TextView) findViewById(R.id.textView);
        if (mRotationSensor != null) {
            text1.setText(mRotationSensor.getName() + rotationText);
        } else {
            text1.setText("Rotation sensor not found");
        }

        TextView text3 = (TextView) findViewById(R.id.textView3);
        if (mGravitySensor != null) {
            text3.setText(mGravitySensor.getName());
        } else {
            text3.setText("Gravity sensor not found");
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        orientation = newConfig.orientation;

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();

        updateLabels();

        super.onConfigurationChanged(newConfig);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] toDegrees(float rotation_vector[]) {
        float orientationVals[] = new float[3];
        float mRotationMatrix[] = new float[16];
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, rotation_vector);

        switch (rotation) {
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotationMatrix);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_X, mRotationMatrix);
                break;
            default:
                break;
        }

        SensorManager.getOrientation(mRotationMatrix, orientationVals);

        return orientationVals;
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float output[] = toDegrees(event.values);
            float x = (float) Math.toDegrees((double) output[0]);
            float y = (float) Math.toDegrees((double) output[1]);
            float z = (float) Math.toDegrees((double) output[2]);
            mView.update(x, output[0], y, output[1], z, output[2], "");

        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {

            float gravity = 9.8f;
            float x = (float) ((Math.asin(event.values[0] / gravity) * 180.0f) / Math.PI);
            float y = (float) ((Math.asin(event.values[1] / gravity) * 180.0f) / Math.PI);
            float z = (float) ((Math.asin(event.values[2] / gravity) * 180.0f) / Math.PI);

            float tilt = y;
            if (rotation == Surface.ROTATION_0) {
                tilt = x > 0 ? tilt - 90.0f : 90.0f - tilt;
            }
            String text = "Pitch " + Math.round(z) + (char) 0x00B0 + ", Tilt " + Math.round(tilt) + (char) 0x00B0;

            mView3.update(x, event.values[0], y, event.values[1], z, event.values[2], text);
        }
    }
}
