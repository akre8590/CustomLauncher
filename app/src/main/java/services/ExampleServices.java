package services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.diegocasas.customlauncher.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import data.model.Post;
import data.remote.APIService;
import data.remote.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class ExampleServices extends JobService  {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    private APIService mAPIService;
    private String stringOS, stringModel, stringManufacturer, stringType, stringUser, stringAppsInstalled;
    double latitude, longitude;

    @Override
    public void onCreate() {
        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        latitude = params.getExtras().getDouble("lat");
        longitude = params.getExtras().getDouble("lon");
        Log.d("LATITUDE_LONGITUDE", "LATITUDE & LONGITUDE: " + latitude + " " + longitude);

        stringOS = Build.VERSION.RELEASE;
        stringModel = Build.MODEL;
        stringManufacturer = Build.MANUFACTURER;
        stringType = Build.TYPE;
        stringUser = Build.USER;
        List<String> paquete = GetAllInstalledApkInfo();
        stringAppsInstalled = paquete.toString();
        Log.d(TAG, "PAQUETE: " + stringAppsInstalled);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                //startService(new Intent(ExampleServices.this, LocationService.class));
                sendPost(stringOS, stringModel, stringManufacturer, stringType, stringUser, stringAppsInstalled, latitude, longitude);
                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
    public List<String> GetAllInstalledApkInfo() {

        List<String> ApkPackageName = new ArrayList<>();

        PackageManager packageManager = this.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<PackageInfo> applications = this.getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : applications) {

            if (!isSystemPackage(info) && isInegiPackage(info)) {
                //  ApkPackageName.add(info.packageName + " versión: " + info.versionName);

                ApkPackageName.add((String)packageManager.getApplicationLabel(info.applicationInfo) + " Versión: " + info.versionName);
            }
        }
        return ApkPackageName;
    }
    public boolean isSystemPackage(PackageInfo packageInfo) {
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
    public boolean isInegiPackage(PackageInfo packageInfo){

        return (packageInfo.packageName.equals("com.example.diegocasas.customlauncher") || packageInfo.packageName.equals("com.example.diegocasas.myapplication") || packageInfo.packageName.equals("com.example.diegocasas.uploadfiletoserver"));
    }

    public void sendPost(String os, String model, String manufacturer, String type, String user, String nameApps, double latitude, double longitude) {
        Log.d(TAG, "NAME_APPS: " + nameApps);
        mAPIService.savePost(os, model, manufacturer, type, user, nameApps, 123, latitude, longitude).enqueue(new Callback<Post>() {

            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()) {
                    Log.i("1236", "Post submitted to API: " + response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("1236", "Unable to submit post to API: " + t);
            }
        });
    }
}