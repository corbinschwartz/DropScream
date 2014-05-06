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
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
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
	    
	private void checkDrop(SensorEvent event) {
		float[] values = event.values;
		long currTime = System.currentTimeMillis();
		if (values[1] < .15) 
		{
			if (currTime - tTriggered < 2000) {
				return;
			}
			tTriggered = currTime;
			scream();
			Toast.makeText(this, "Ahh!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void scream() {
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
