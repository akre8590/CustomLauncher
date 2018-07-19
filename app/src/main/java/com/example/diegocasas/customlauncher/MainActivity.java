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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.PersistableBundle;
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
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView bluetoothIcon, wifiIcon, settingsIcon, cameraIcon;
    TextView bluetoothName, wifiName, settingsName, cameraName, status, locationText;
    Button admin, logout, jobService;
    boolean isAdmin = false;

    LocationManager locationManager;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        bluetoothIcon = (ImageView) findViewById(R.id.bluetoothButton);
        bluetoothIcon.setImageDrawable(getActivityIcon(this, "com.android.chrome","com.google.android.apps.chrome.Main"));
        //wifiIcon = (ImageView) findViewById(R.id.wifiButton);
        //wifiIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
        settingsIcon = (ImageView) findViewById(R.id.settingsButton);
        settingsIcon.setImageDrawable(getActivityIcon(this, "com.android.settings", "com.android.settings.Settings"));

        cameraIcon = (ImageView) findViewById(R.id.camera);
        cameraIcon.setImageDrawable(getActivityIcon(this, "com.lenovo.camera","com.android.camera.Camera"));
        cameraName = (TextView)findViewById(R.id.cameraName);
        cameraName.setText(getAppName("com.lenovo.camera"));

        settingsName = (TextView)findViewById(R.id.settingsName);
        //settingsName.setText(getAppName("com.android.settings"));
        settingsName.setText("Settings");
        bluetoothName = (TextView)findViewById(R.id.bluetoothName);
        bluetoothName.setText(getAppName("com.android.chrome"));
        //wifiName = (TextView)findViewById(R.id.wifiName);
        //wifiName.setText(getAppName("com.embarcadero.AdmCensal"));
        admin = (Button)findViewById(R.id.buttonAdmin);
        logout = (Button) findViewById(R.id.buttonLogOut);
        jobService = (Button)findViewById(R.id.scheduleJob);
        status = (TextView)findViewById(R.id.statusLabel);
        locationText = (TextView)findViewById(R.id.location);

        addClickListenerBluetooth();
        //addClickListenerWifi();
        addClickListenerCamera();
        status();
        preventStatusBarExpansion(this);

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickListenerSettings();
            }
        });
        admin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              showAddItemDialog(MainActivity.this);
          }
      });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickListenerLogout();
            }
        });
        jobService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getLocation();
            }
        });

        if (!isAdmin){
            logout.setVisibility(View.INVISIBLE);
        }
    }
    /******ClickListener de los botones*******/
    public void addClickListenerBluetooth(){
        bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                startActivity(launchIntent);
            }
        });
    }

    public void addClickListenerCamera(){
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.lenovo.camera");
                startActivity(launchIntent);
            }
        });
    }
    /***public void addClickListenerWifi(){
        wifiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.embarcadero.AdmCensal");
                startActivity(launchIntent);
            }
        });
    }***/
    public void addClickListenerSettings(){

        if (isAdmin) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "No cuenta con permisos", Toast.LENGTH_SHORT).show();
        }
    }
    private void addClickListenerLogout() {
        isAdmin = false;
        status();
        admin.setVisibility(View.VISIBLE);
        logout.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Sin permisos de admin", Toast.LENGTH_SHORT).show();
    }
    /*****Privilegios (Admin - Entrevistador)****/
    private void status(){

        if (isAdmin){
            status.setText("PERMISOS: ADMINISTRADOR");
        } else {
            status.setText("PERMISOS: ENTREVISTADOR");
        }
    }
    /*****Obtener icono de la app******/
    public static Drawable getActivityIcon(Context context, String packageName, String activityName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityName));
        ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);

        return resolveInfo.loadIcon(pm);
    }
    /*****Obtener nombre de la app******/
    public String getAppName(String packageName){
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return  applicationName;
    }
    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Opción Deshabilitada", Toast.LENGTH_SHORT).show();
    }
    /*****Deshabilitar control de apagado (presión larga)*****/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
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
    /*******Dialog contraseña de usuario*******/
    public void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        taskEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Permisos de administrador")
                .setMessage("Ingrese la contraseña")
                .setView(taskEditText)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = String.valueOf(taskEditText.getText());
                        if (pass.equals("1234")){
                            isAdmin = true;
                            status();
                            admin.setVisibility(View.INVISIBLE);
                            logout.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Contraseña correcta", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            isAdmin = false;
                            status();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
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
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLocationChanged(Location location) {

                startJob(location.getLatitude(),location.getLongitude());
                locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));

        }catch(Exception e){ }
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