package com.example.diegocasas.customlauncher;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import data.model.Post;
import data.remote.APIService;
import data.remote.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ExampleServices;
import services.LocationService;
import services.TimeService;


public class MainActivity extends AppCompatActivity implements LocationListener {
    public final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    private static final String TAG = "MainActivity";
    String c_oper;
    LocationManager locationManager;
    int brightness = 204;

    /******Base de datos*****/
    Button btnLogin;
    EditText edtUsername;
    EditText edtPassword;
    DatabaseHelper databaseHelper;
    CueMsg cueMsg = new CueMsg(MainActivity.this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********Base de datos**********/
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtUsername = (EditText) findViewById(R.id.et_username);
        edtPassword = (EditText) findViewById(R.id.et_password);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSup = databaseHelper.checkUserSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                boolean isEnt = databaseHelper.checkUserEnt(edtUsername.getText().toString(), edtPassword.getText().toString());
                c_oper = edtUsername.getText().toString();
                if(isSup){
                    databaseHelper.updateAccessSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                    cueMsg.cueCorrect("Bienvenido Supervisor: " + edtUsername.getText().toString());
                    Intent i = new Intent(MainActivity.this, Second.class);
                    i.putExtra("profile", true);
                    i.putExtra("ClaveOperativa", c_oper);
                    startActivity(i);
                } else if (isEnt){
                    databaseHelper.updateAccessEnt(edtUsername.getText().toString(), edtPassword.getText().toString());
                    cueMsg.cueCorrect("Bienvenido Entrevistador: " + edtUsername.getText().toString());
                    Intent i = new Intent(MainActivity.this, Second.class);
                    i.putExtra("profile", false);
                    i.putExtra("ClaveOperativa", c_oper);
                    startActivity(i);
                } else {
                    edtPassword.setText(null);
                    cueMsg.cueError("Usuario o contraseña invalido");
                }
            }
        });
        preventStatusBarExpansion(this);

        boolean settingsCanWrite = Settings.System.canWrite(MainActivity.this);
        if (!settingsCanWrite){
            Log.d("Brightness", "No se tiene permisos de escritura de opciones");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        } else {
            Log.d("Brightness", "Si se tuvo permisos");
            ContentResolver cResolver = this.getApplicationContext().getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        }
    }
    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {
        cueMsg.cueWarning("Opción deshabilitada");
    }
    /*****Deshabilitar controles de volumen******/
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())){
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    /*****Deshabilitar status bar (recuerda configuración en el cel)*****/
    public void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(context);

        manager.addView(view, localLayoutParams);
    }

    /***Programación de trabajo (Services)*****///
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startJob(double lat, double lon){
        getLocation();
        ComponentName componentName = new ComponentName(this, ExampleServices.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(TimeUnit.SECONDS.toMillis(30))
                .setExtras(bundle)
                .build();
        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job Schedule");
        } else {
            Log.d(TAG, "Job Scheduling failed");
        }
    }
    public void getLocation() {
        /**try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }**/
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLocationChanged(Location location) {
                /**startJob(location.getLatitude(),location.getLongitude());
                locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));

        }catch(Exception e){ }**/
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Favor de habilitar su GPS", Toast.LENGTH_SHORT).show();
    }

}