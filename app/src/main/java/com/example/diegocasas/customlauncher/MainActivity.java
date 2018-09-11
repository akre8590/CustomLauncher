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
    ImageView chromeIcon, explorerIcon, capaIcon, admCensalIcon, settingsAll, settingsWifi, settingsBluetooth, settings3G, settingsLocation,call, sms, lock, unlock, camera, mccIcon, transIcon, btnLogout;
    TextView chromeName, explorerName, capaName, admCensalName, locationText, mccName, transName, infoLog;
    View view_bar, view_bar_bottom;
    boolean isAdmin = false, logueadoEnt = false, logueadoSup = false;
    LocationManager locationManager;
    int brightness = 204;

    /******Base de datos*****/
    Button btnLogin;
    EditText edtUsername;
    EditText edtPassword;
    DatabaseHelper databaseHelper;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoLog = (TextView) findViewById(R.id.info_log);

        /********Base de datos**********/
        view_bar = (View) findViewById(R.id.viewBar);
        view_bar_bottom = (View)findViewById(R.id.viewBarBottom) ;
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtUsername = (EditText) findViewById(R.id.et_username);
        edtPassword = (EditText) findViewById(R.id.et_password);
        btnLogout = (ImageView) findViewById(R.id.logout);
        btnLogout.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_settings_new_black_48dp));


        databaseHelper = new DatabaseHelper(MainActivity.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSup = databaseHelper.checkUserSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                boolean isEnt = databaseHelper.checkUserEnt(edtUsername.getText().toString(), edtPassword.getText().toString());
                //boolean isExist = databaseHelper.checkUserExist(edtUsername.getText().toString(), edtPassword.getText().toString());

                if(isSup){
                    databaseHelper.updateAccessSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                    cueCorrect("Bienvenido supervisor: " + edtUsername.getText().toString());
                    infoLog.setVisibility(View.VISIBLE);
                    infoLog.setText("Bienvenido supervisor: " + edtUsername.getText().toString());
                    hideLogin();
                    showElementsLayout();
                    showBarElements();
                    if (!isAdmin){
                        unlock.setVisibility(View.INVISIBLE);
                        logueadoSup = false;
                        logueadoEnt = true;
                        hideKeyboard(MainActivity.this);
                    }
                } else if (isEnt){
                    databaseHelper.updateAccessEnt(edtUsername.getText().toString(), edtPassword.getText().toString());
                    cueCorrect("Bienvenido entrevistador: " + edtUsername.getText().toString());
                    infoLog.setVisibility(View.VISIBLE);
                    infoLog.setText("Bienvenido entrevistador: " + edtUsername.getText().toString());
                    hideLogin();
                    showElementsLayoutEnt();
                    showBarElements();
                    if (!isAdmin){
                        unlock.setVisibility(View.INVISIBLE);
                        logueadoEnt = false;
                        logueadoSup = true;
                        hideKeyboard(MainActivity.this);
                    }
                } else {
                    edtPassword.setText(null);
                    cueError("Usuario o contraseña invalido");
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
   /* @Override
    protected void onResume() {
        super.onResume();
        if (logueadoSup == true){
            showElementsLayout();
            showBarElements();
        } else if (logueadoEnt == true){
            showElementsLayoutEnt();
            showBarElements();
        }
    }*/
   public static void hideKeyboard(Activity activity) {
       InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
       //Find the currently focused view, so we can grab the correct window token from it.
       View view = activity.getCurrentFocus();
       //If no view currently has focus, create a new one, just so we can grab a window token from it
       if (view == null) {
           view = new View(activity);
       }
       imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
   }
    /******Elements layout entrevistador*********/
    public void showElementsLayoutEnt(){

        btnLogout.setVisibility(View.VISIBLE);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked();
            }
        });

        PackageManager pm = this.getPackageManager();
        if (isPackageInstalled("com.android.filemanager", pm)) {

            explorerIcon = (ImageView) findViewById(R.id.chromeButton);
            explorerIcon.setImageDrawable(getActivityIcon(this, "com.android.filemanager", "com.android.filemanager.MainActivity"));
            explorerIcon.setX(100);
            explorerIcon.setY(100);
            explorerName = (TextView) findViewById(R.id.chromeName);
            explorerName.setText(getAppName("com.android.filemanager"));
            addClickListenerExplorerEnt();
        } else if (isPackageInstalled("com.android.settings", pm)){
            explorerIcon = (ImageView) findViewById(R.id.chromeButton);
            explorerIcon.setImageDrawable(getActivityIcon(this, "com.android.settings", "com.android.settings.Settings$StorageSettingsActivity"));
            explorerName = (TextView) findViewById(R.id.chromeName);
            explorerName.setText("Gestor de archivos");
          addClickListenerExplorerEnt();
        } else {
            cueWarning("Ningún gestor de archivos instalado");
        }
        if (isPackageInstalled("io.cordova.CAPACITACIONECB", pm)){
            capaIcon = (ImageView) findViewById(R.id.mcc);
            capaIcon.setImageDrawable(getActivityIcon(this, "io.cordova.CAPACITACIONECB", "io.cordova.CAPACITACIONECB.MainActivity"));
            capaName = (TextView) findViewById(R.id.mccName);
            capaName.setText(getAppName("io.cordova.CAPACITACIONECB"));
            addClickListenerCapa();
        } else {
            cueWarning("No se encuentra la aplicación CAAP");
        }
        if (isPackageInstalled("com.embarcadero.AdmCensal", pm)){
            admCensalIcon = (ImageView) findViewById(R.id.admCensal);
            admCensalIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
            admCensalName = (TextView) findViewById(R.id.admCensaltxt);
            //admCensalName.setText(getAppName("com.embarcadero.AdmCensal"));
            admCensalName.setText("Administrador Censal");
            addClickListenerAdmCensal();
        } else {
            cueWarning("No se encuentra la aplicación AdmCensal");
        }
    }
    /******Elements layout supervisor*********/
    public void showElementsLayout(){

        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked();
            }
        });

        PackageManager pm = this.getPackageManager();
        if (isPackageInstalled("com.android.filemanager", pm)) {
            explorerIcon = (ImageView) findViewById(R.id.explorer);
            explorerIcon.setImageDrawable(getActivityIcon(this, "com.android.filemanager", "com.android.filemanager.MainActivity"));
            explorerName = (TextView) findViewById(R.id.explorerName);
            explorerName.setText(getAppName("com.android.filemanager"));
            addClickListenerExplorer();
        } else if (isPackageInstalled("com.android.settings", pm)){
            explorerIcon = (ImageView) findViewById(R.id.explorer);
            explorerIcon.setImageDrawable(getActivityIcon(this, "com.android.settings", "com.android.settings.Settings$StorageSettingsActivity"));
            explorerName = (TextView) findViewById(R.id.explorerName);
            explorerName.setText("Gestor de archivos");
            addClickListenerExplorer();
        } else {
            cueWarning("Ningún gestor de archivos instalado");
        }
        if (isPackageInstalled("com.embarcadero.OperaWeb", pm)) {
            chromeName = (TextView) findViewById(R.id.chromeName);
            //chromeName.setText(getAppName("com.embarcadero.OperaWeb"));
            chromeName.setText("OPERA Web");
            chromeIcon = (ImageView) findViewById(R.id.chromeButton);
            chromeIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.OperaWeb", "com.embarcadero.firemonkey.FMXNativeActivity"));
            addClickListenerChrome();
        }else{
            cueWarning("No se encuentra la aplicación OperaWeb");
        }
        if (isPackageInstalled("io.cordova.CAPACITACIONECB", pm)){
            capaIcon = (ImageView) findViewById(R.id.capaButton);
            capaIcon.setImageDrawable(getActivityIcon(this, "io.cordova.CAPACITACIONECB", "io.cordova.CAPACITACIONECB.MainActivity"));
            capaName = (TextView) findViewById(R.id.capaName);
            capaName.setText(getAppName("io.cordova.CAPACITACIONECB"));
            addClickListenerCapa();
        } else {
            cueWarning("No se encuentra la aplicación CAAP");
        }
        if (isPackageInstalled("com.embarcadero.AdmCensal", pm)){
            admCensalIcon = (ImageView) findViewById(R.id.admCensal);
            admCensalIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
            admCensalName = (TextView) findViewById(R.id.admCensaltxt);
            admCensalName.setText("Administrador Censal");
            addClickListenerAdmCensal();
        } else {
            cueWarning("No se encuentra la aplicación AdmCensal");
        }
        if (isPackageInstalled("com.embarcadero.mcc", pm)){
            mccIcon = (ImageView) findViewById(R.id.mcc);
            mccIcon.setImageDrawable(getActivityIcon(this, "com.embarcadero.mcc","com.embarcadero.firemonkey.FMXNativeActivity"));
            mccName = (TextView) findViewById(R.id.mccName);
            //mccName.setText(getAppName("com.embarcadero.mcc"));
            mccName.setText("MCC");
            addClickListenerMCC();
        } else {
            cueWarning("No se encuentra la aplicación MCC");
        }
        if (isPackageInstalled("com.example.diegocasas.descarto",pm)){
                transIcon = (ImageView) findViewById(R.id.transfer);
                transIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_open_with_black_48dp));
                transName = (TextView) findViewById(R.id.transferName);
                transName.setText("Instalación de Cartografía");
                addClickListenerTrans();
        } else {
        }
    }
    /*********Elementos de la barra de opciones***********/
    public void showBarElements(){
        view_bar_bottom.setVisibility(View.VISIBLE);
        view_bar.setVisibility(View.VISIBLE);

        settingsAll = (ImageView) findViewById(R.id.settingsAll);
        settingsAll.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        settingsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    startActivity(launchIntent);
                } else {
                    cueError("No cuenta con los permisos");
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
                cueWarning("Función deshabilitada");
            }
        });
        sms = (ImageView) findViewById(R.id.sms);
        sms.setImageDrawable(getResources().getDrawable(R.drawable.ic_textsms_black_48dp));
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cueWarning("Función deshabilitada");
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
    }
    /******ClickListener de los botones*******/
    public void addClickListenerChrome(){
        chromeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.embarcadero.OperaWeb");
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
    public void addClickListenerExplorerEnt(){
        final PackageManager pm = this.getPackageManager();
        explorerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    if (isPackageInstalled("com.android.filemanager", pm)){
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.filemanager");
                        startActivity(launchIntent);
                    } else if (isPackageInstalled("com.android.settings", pm)){
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$StorageSettingsActivity"));
                        startActivity(intent);
                    } else {
                        cueError("Error");
                    }
                } else {
                    cueError("No cuenta con los permisos");
                }
            }
        });
    }
    public void addClickListenerExplorer(){
        final PackageManager pm = this.getPackageManager();
        explorerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPackageInstalled("com.android.filemanager", pm)){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.filemanager");
                     startActivity(launchIntent);
                } else if (isPackageInstalled("com.android.settings", pm)){
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$StorageSettingsActivity"));
                    startActivity(intent);
                } else {
                    cueError("Error");
                }
            }
        });
    }
    public void addClickListenerCapa(){
            capaIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("io.cordova.CAPACITACIONECB");
                    startActivity(launchIntent);
                }
            });
    }
    public void addClickListenerMCC(){
        mccIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.embarcadero.mcc");
                startActivity(launchIntent);
            }
        });
    }
    public void addClickListenerTrans(){
        transIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.diegocasas.descarto");
                    startActivity(launchIntent);
                } else {
                   cueError("No cuenta con los permisos");
                }
            }
        });
    }
    private void addClickListenerLogout() {
        isAdmin = false;
        lock.setVisibility(View.VISIBLE);
        unlock.setVisibility(View.INVISIBLE);
        cueError("Sin permisos de administrador");
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
        cueWarning("Opción deshabilitada");
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
                        if (pass.equals("INEGIKIOSCO2018")){
                            isAdmin = true;
                            lock.setVisibility(View.INVISIBLE);
                            unlock.setVisibility(View.VISIBLE);
                           cueCorrect("Contraseña correcta");
                        } else {
                            cueError("Contraseña incorrecta");
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
    public void cueError(String msg){
        Cue.init()
                .with(MainActivity.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#FA5858"),
                        Color.parseColor("#ffffff"),
                        Color.parseColor("#e84393"))
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
    public void cueCorrect(String msg){
        Cue.init()
                .with(MainActivity.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#088A85"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#01DFD7")) //contorno
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
    public void cueWarning(String msg){
        Cue.init()
                .with(MainActivity.this)
                .setMessage(msg)
                .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .setType(Type.CUSTOM)
                .setDuration(Duration.SHORT)
                .setBorderWidth(5)
                .setCornerRadius(10)
                .setCustomFontColor(Color.parseColor("#DF7401"), //fondo
                        Color.parseColor("#ffffff"), //letra
                        Color.parseColor("#DBA901")) //contorno
                .setPadding(30)
                .setTextSize(15)
                .show();
    }
    /**********************************Base de datos de Login**********************************/
    public void hideLogin(){
        btnLogin.setVisibility(View.INVISIBLE);
        edtPassword.setVisibility(View.INVISIBLE);
        edtUsername.setVisibility(View.INVISIBLE);
    }
    public void showAlertDialogButtonClicked() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Finalizar sesión");
        builder.setMessage("¿Está seguro?");
        // add the buttons
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.clearColumn();
                cueError("Finalizó la sesión");
                finish();
                startActivity(getIntent());
            }
        });
        builder.setNegativeButton("Cancelar", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}