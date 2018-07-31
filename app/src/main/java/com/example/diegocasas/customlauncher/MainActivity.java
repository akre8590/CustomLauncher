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
    ImageView bluetoothIcon, explorerIcon, settingsIcon, admCensalIcon, settingsAll, settingsWifi, settingsBluetooth, settings3G, settingsLocation,call, sms, lock, unlock, camera;
    TextView bluetoothName, explorerName, settingsName, admCensalName, locationText;

    boolean isAdmin = false;

    LocationManager locationManager;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = this.getPackageManager();
        if (isPackageInstalled("com.android.filemanager", pm)) {
            //Toast.makeText(this, "Paquetes necesarios ya instalados", Toast.LENGTH_SHORT).show();
            explorerIcon = (ImageView) findViewById(R.id.explorer);
            explorerIcon.setImageDrawable(getActivityIcon(this, "com.android.filemanager", "com.android.filemanager.MainActivity"));
            explorerName = (TextView) findViewById(R.id.explorerName);
            explorerName.setText(getAppName("com.android.filemanager"));
            addClickListenerExplorer();
        }else{
            Toast.makeText(this, "No se encuentra la aplicación: " + getAppName("com.android.filemanager"), Toast.LENGTH_SHORT).show();
        }
        if (isPackageInstalled("com.android.chrome", pm)) {

            bluetoothName = (TextView) findViewById(R.id.bluetoothName);
            bluetoothName.setText(getAppName("com.android.chrome"));
            bluetoothIcon = (ImageView) findViewById(R.id.bluetoothButton);
            bluetoothIcon.setImageDrawable(getActivityIcon(this, "com.android.chrome", "com.google.android.apps.chrome.Main"));
            addClickListenerBluetooth();
        }else{
            Toast.makeText(this, "No se encuentra la aplicación: " + getAppName("com.android.chrome"), Toast.LENGTH_SHORT).show();
        }
        if (isPackageInstalled("com.android.settings", pm)){
            settingsIcon = (ImageView) findViewById(R.id.settingsButton);
            settingsIcon.setImageDrawable(getActivityIcon(this, "com.android.settings", "com.android.settings.Settings"));
            settingsName = (TextView) findViewById(R.id.settingsName);
            //settingsName.setText(getAppName("com.android.settings"));
            settingsName.setText("Settings");
            settingsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addClickListenerSettings();
                }
            });
        } else {
            Toast.makeText(this, "No se encuentra la aplicación: " + getAppName("com.android.settings"), Toast.LENGTH_SHORT).show();
        }
        if (isPackageInstalled("com.embarcadero.AdmCensal", pm)){
            admCensalIcon = (ImageView) findViewById(R.id.admCensal);
            admCensalIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
            admCensalName = (TextView) findViewById(R.id.admCensaltxt);
            admCensalName.setText(getAppName("com.embarcadero.AdmCensal"));

            addClickListenerAdmCensal();

         } else {
                Toast.makeText(this, "No se encuentrá la aplicación: " + getAppName("com.embarcadero.AdmCensal"), Toast.LENGTH_SHORT).show();
         }

        settingsAll = (ImageView) findViewById(R.id.settingsAll);
        settingsAll.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        settingsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(MainActivity.this, "No cuenta con los permisos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        settingsWifi = (ImageView) findViewById(R.id.settingsWifi);
        settingsWifi.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_4_bar_black_48dp));
        settingsWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        settingsBluetooth = (ImageView) findViewById(R.id.settingsBluetooth);
        settingsBluetooth.setImageDrawable(getResources().getDrawable(R.drawable.ic_bluetooth_black_48dp));
        settingsBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        });

        settings3G = (ImageView)findViewById(R.id.settings3G);
        settings3G.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_cellular_4_bar_black_48dp));
        settings3G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
            }
        });

        settingsLocation = (ImageView)findViewById(R.id.settingsLocation);
        settingsLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_black_48dp));
        settingsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });


        call = (ImageView) findViewById(R.id.call);
        call.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_black_48dp));
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        sms = (ImageView) findViewById(R.id.sms);
        sms.setImageDrawable(getResources().getDrawable(R.drawable.ic_textsms_black_48dp));
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        camera = (ImageView) findViewById(R.id.cameraSettings);
        camera.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_48dp));
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.lenovo.camera");
                startActivity(launchIntent);
            }
        });

        lock = (ImageView) findViewById(R.id.lock);
        lock.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_black_48dp));
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(MainActivity.this);

            }
        });
        unlock = (ImageView) findViewById(R.id.unlock);
        unlock.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open_black_48dp));
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickListenerLogout();
            }
        });

        preventStatusBarExpansion(this);

        if (!isAdmin){
            unlock.setVisibility(View.INVISIBLE);

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

    public void addClickListenerAdmCensal(){
        admCensalIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.embarcadero.AdmCensal");
                startActivity(launchIntent);
            }
        });
    }
    public void addClickListenerExplorer(){
        explorerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.filemanager");
                startActivity(launchIntent);
            }
        });
    }
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

        lock.setVisibility(View.VISIBLE);
        unlock.setVisibility(View.INVISIBLE);


        Toast.makeText(this, "Sin permisos de admin", Toast.LENGTH_SHORT).show();
    }
    /*****Privilegios (Admin - Entrevistador)****/

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
   /** @Override
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
                            lock.setVisibility(View.INVISIBLE);
                            unlock.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Contraseña correcta", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            isAdmin = false;

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
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}