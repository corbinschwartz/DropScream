package com.corbin.dropscream;

import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements SensorEventListener{
	private SensorManager manager;
	private long tTriggered;
	private int count;
	//private float[] rollingValues;
	//private int keepScreaming;
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        initialize();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SensorFragment())
                    .commit();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		checkDrop(event);
		}
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
    	super.onResume();
    	manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }
	    
    private void initialize() {
    	count = 0;
    	//rollingValues = new float[]{9.81f,9.81f,9.81f};
    	//keepScreaming = 0;
    	tTriggered = 0;
    }
    
	private void checkDrop(SensorEvent event) {
		float[] values = event.values;
		//rollingValues[count%rollingValues.length]=values[1];
		long currTime;
		float x = values[0];
	    float y = values[1];
	    float z = values[2];
		count++;
		float a = (float) java.lang.Math.sqrt(x * x + y * y + z * z);
		/*for (int i=0;i<rollingValues.length;i++) {
			if (rollingValues[i] > -0.1f) {
				return;
			}
		}*/
		if (a >=19 ) {
			currTime = System.currentTimeMillis();
			if (currTime - tTriggered < 1000) {
				/*if (keepScreaming == 0) {
					rollingValues[count%rollingValues.length] = 9.81f;
				}*/
				return;
			}
			scream();
			tTriggered = currTime;
			Toast.makeText(this, Integer.toString(count) + ":" + Float.toString(a), Toast.LENGTH_SHORT).show();
			count=0;
			/*if (keepScreaming == 0) {
				for (int i=0; i<rollingValues.length; i++) {
					rollingValues[i] = 9.81f;
				}
			}*/
		}
		
	}
	
	private void scream() {
		long currTime = System.currentTimeMillis();
		if (currTime - tTriggered > 1000) {
			MediaPlayer mp = MediaPlayer.create(this, R.raw.scream);
		    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		    try {
				mp.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    mp.start();
		}
	}

    public static class SensorFragment extends Fragment {
        public SensorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
