package com.example.diegocasas.customlauncher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import data.model.Post;
import data.remote.APIService;
import data.remote.ApiUtils;
import ir.mahdi.mzip.zip.ZipArchive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ExampleServices;
import services.LocationService;
import services.TimeService;


public class MainActivity extends AppCompatActivity implements LocationListener {
    public final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;
    String c_oper;
    LocationManager locationManager;
    int brightness = 204;
    /**********Download file***************/
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    // File url to download
    //private static String file_url = "https://retrofit2androidmysqlphp.000webhostapp.com/mcc_071A.zip";
    private static String file_url = "https://firebasestorage.googleapis.com/v0/b/festival-13bd8.appspot.com/o/version1.apk?alt=media&token=43420512-f1d9-4ee6-a02a-ef43d293fecf";
    /******Base de datos*****/
    Button btnLogin, btnDownload;
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
            /**if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(myIntent, 101);
            }**/
                /********Base de datos**********/
                btnLogin = (Button) findViewById(R.id.btn_login);
                btnDownload = (Button) findViewById(R.id.download);
                edtUsername = (EditText) findViewById(R.id.et_username);
                edtPassword = (EditText) findViewById(R.id.et_password);

                databaseHelper = new DatabaseHelper(MainActivity.this);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSup = databaseHelper.checkUserSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                        boolean isEnt = databaseHelper.checkUserEnt(edtUsername.getText().toString(), edtPassword.getText().toString());
                        c_oper = edtUsername.getText().toString();
                        if (isSup) {
                            databaseHelper.updateAccessSup(edtUsername.getText().toString(), edtPassword.getText().toString());
                            cueMsg.cueCorrect("Bienvenido Supervisor: " + edtUsername.getText().toString());
                            Intent i = new Intent(MainActivity.this, Second.class);
                            i.putExtra("profile", true);
                            i.putExtra("ClaveOperativa", c_oper);
                            startActivity(i);
                        } else if (isEnt) {
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
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    new DownloadFileFromURL().execute(file_url);
                }else{
                   cueMsg.cueError("No hay conexión a Internet");
                }
            }
        });
        }


    /************Storage Permission*****************/
    public void storagePermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
            }
        }else {
            //no hace nada
        }
    }
    /***************Brightness Permission*****************/
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void brightnessPermission(){
        boolean settingsCanWrite = Settings.System.canWrite(MainActivity.this);
        if (!settingsCanWrite){
            Log.d("Brightness", "No se tiene permisos de escritura de opciones");
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        } else {
            Log.d("Brightness", "Si se tuvo permisos");
            ContentResolver cResolver = MainActivity.this.getApplicationContext().getContentResolver();
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
    /*******************Download files from external server***************/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Descargando archivo. Por favor espere...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                //OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/mcc_071A.zip");
                OutputStream output = new FileOutputStream("storage/emulated/0/Documents/version1.apk");
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            Toast.makeText(MainActivity.this, "Descarga completa", Toast.LENGTH_SHORT).show();
            //ZipArchive zipArchive1 =  new ZipArchive();
            //zipArchive1.unzip("storage/emulated/0/Documents/mcc_071A.zip","storage/emulated/0/Documents/", "");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File("storage/emulated/0/Documents/version1.apk")),"application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    /************************Chek if internet connection exits***********/
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}