package com.ecdav.map;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends AppCompatActivity {
    private PointerStatus pointerStatus=new PointerStatus(100,100,0);
    private SensorManager sensorManager=null;
    private MapView mapView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mapView=(MapView)findViewById(R.id.map_view);
        mapView.drawThread.setPointerStatus(pointerStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerEventListener(Sensor.TYPE_STEP_DETECTOR);
        registerEventListener(Sensor.TYPE_STEP_COUNTER);
        registerEventListener(Sensor.TYPE_ORIENTATION);
    }
    @Override
    protected void onStop()
    {
        // 程序退出时取消注册传感器监听器
        sensorManager.unregisterListener(sensorEventListener);
        super.onStop();
    }
    @Override
    protected void onPause()
    {
        // 程序暂停时取消注册传感器监听器
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values=event.values;
            int sensorType=event.sensor.getType();
            switch (sensorType){
                case Sensor.TYPE_ORIENTATION:
                    pointerStatus.direction=values[0];
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    break;
                default:
                    ;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private void registerEventListener(int sensorType){
        switch (sensorType){
            case Sensor.TYPE_ORIENTATION:
                sensorManager.registerListener(sensorEventListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                        SensorManager.SENSOR_DELAY_GAME);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                sensorManager.registerListener(sensorEventListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                        SensorManager.SENSOR_DELAY_GAME);
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                sensorManager.registerListener(sensorEventListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                        SensorManager.SENSOR_DELAY_GAME);
                break;
            default:
                ;
        }
    }
}
