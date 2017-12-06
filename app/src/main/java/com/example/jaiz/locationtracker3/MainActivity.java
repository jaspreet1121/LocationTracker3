package com.example.jaiz.locationtracker3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements SensorEventListener{
    //These objects are required
    private Button mFirebaseButton;

    private DatabaseReference mDatabase;


    private LocationManager locationManager;
    private LocationListener listener;
    private Button b;
    //private TextView t;
    private double lat=0,lon=0;
    private String tim;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    TimeZone tz = TimeZone.getTimeZone("IST");
    private Calendar cal;
    //private int timer = 0;
    //private TextView text;
    private Sensor mySensor;
    private SensorManager SM;
    private Button Start,Stop;
    private Boolean on;
    private double[] x = {0,0,0,0,0};
    private double[] y = {0,0,0,0,0};
    private double[] z = {0,0,0,0,0};
    private double value_x,value_y,value_z;

    //These objects are in edit section
    private TextView acc,accv,lati,latv,loni,lonv,dat,datv;
    private double rms;
    //Close Edit section

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup the strict mode policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        acc = (TextView) findViewById(R.id.acc);
        accv = (TextView) findViewById(R.id.accv);
        lati = (TextView) findViewById(R.id.lat);
        latv = (TextView) findViewById(R.id.latv);
        loni = (TextView) findViewById(R.id.lon);
        lonv = (TextView) findViewById(R.id.lonv);
        dat = (TextView) findViewById(R.id.dat);
        datv = (TextView) findViewById(R.id.datv);

        b = (Button) findViewById(R.id.get_loca);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                //t.setText("\n Longitude: " + lat + "\n Latitude:    " + lon);
                latv.setText(Double.toString(lat));
                lonv.setText(Double.toString(lon));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();



        mFirebaseButton = (Button) findViewById(R.id.send_button);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Test_01");

        mFirebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(tim).child("Latitude").setValue(lat);
                mDatabase.child(tim).child("Longitude").setValue(lon);
                mDatabase.child(tim).child("Accelerometer X").setValue(value_x);
                mDatabase.child(tim).child("Accelerometer Y").setValue(value_y);
                mDatabase.child(tim).child("Accelerometer Z").setValue(value_z);
                String msg = "Data enterred successfully!!";
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

            }
        });

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SM.registerListener( this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);

        Start = (Button) findViewById(R.id.start_button);
        Stop = (Button) findViewById(R.id.stop_button);

        Start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                on = Boolean.TRUE;
            }
        });
        Stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                on = Boolean.FALSE;
            }
        });

        //do here


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }

    //Do here

    public double[] Shift(double s[], double a){
        for (int i = 0; i<s.length-1;i++){
            s[i]=s[i+1];
        }
        s[s.length-1]=a;
        return s;
    }

    public double SGFilter(double s[]){
        if (s.length==5){
            return (-3*s[0]+12*s[1]+17*s[2]+12*s[3]-3*s[4])/35;
        }
        else return -1;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (on == Boolean.TRUE){
            //timer++;

            x = this.Shift(x,(double) sensorEvent.values[0]);
            y = this.Shift(y,(double) sensorEvent.values[1]);
            z = this.Shift(z,(double) sensorEvent.values[2]);

            value_x = this.SGFilter(x);
            value_y = this.SGFilter(y);
            value_z = this.SGFilter(z);

            rms = Math.sqrt((value_x*value_x+value_y*value_y+value_z*value_z)/3);

            cal = Calendar.getInstance(tz);
            tim = dateFormat.format(cal.getTime());

            accv.setText(Double.toString(rms));
            datv.setText(tim);

            if(tim.substring(17,19).equals("00")||tim.substring(17,19).equals("30")){

                mDatabase.child(tim).child("Latitude").setValue(lat);
                mDatabase.child(tim).child("Longitude").setValue(lon);
                mDatabase.child(tim).child("Accelerometer RMS").setValue(rms);
                mDatabase.child(tim).child("Accelerometer X").setValue(value_x);
                mDatabase.child(tim).child("Accelerometer Y").setValue(value_y);
                mDatabase.child(tim).child("Accelerometer Z").setValue(value_z);
                String msg = "Data enterred successfully!!";
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();


            }

        }
        else{
            value_x = 0;value_y  = 0;value_z = 0;
            accv.setText("NA");
            //tText.setText("Time:  NA");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void Kalman(){

    }



}
