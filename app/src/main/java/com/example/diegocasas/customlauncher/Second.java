package com.example.diegocasas.customlauncher;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Second extends AppCompatActivity {

    ImageView chromeIcon, explorerIcon, capaIcon, admCensalIcon, settingsAll, settingsWifi, settingsBluetooth, settings3G, settingsLocation,call, sms, lock, unlock, camera, mccIcon, transIcon, btnLogout;
    TextView chromeName, explorerName, capaName, admCensalName, mccName, transName, infoLog;
    View view_bar, view_bar_bottom;
    boolean isAdmin = false;
    DatabaseHelper databaseHelper;
    CueMsg cueMsg = new CueMsg(Second.this);
    EnableFeatures enableFeatures = new EnableFeatures(Second.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        infoLog = (TextView) findViewById(R.id.info_log);
        databaseHelper = new DatabaseHelper(Second.this);
        view_bar = (View) findViewById(R.id.viewBar);
        view_bar_bottom = (View)findViewById(R.id.viewBarBottom) ;
        btnLogout = (ImageView) findViewById(R.id.logout);
        btnLogout.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_settings_new_black_48dp));

        Intent intent = getIntent();
        final boolean ent_sup = intent.getExtras().getBoolean("profile");
        final String cOper = intent.getExtras().getString("ClaveOperativa");

        if (ent_sup){
            showElementsLayout();
            showBarElements();
            infoLog.setText("Bienvenido supervisor: " + cOper);
            if (!isAdmin){
                unlock.setVisibility(View.INVISIBLE);
                enableFeatures.hideKeyboard();
            }
        }else {
            showElementsLayoutEnt();
           showBarElements();
            infoLog.setText("Bienvenido entrevistador: " + cOper);
            if (!isAdmin){
                unlock.setVisibility(View.INVISIBLE);
                enableFeatures.hideKeyboard();
            }
        }
    }
    /******Elements layout entrevistador*********/
    public void showElementsLayoutEnt(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked();
            }
        });
        PackageManager pm = this.getPackageManager();
        if (enableFeatures.isPackageInstalled("com.android.filemanager", pm)) {
            explorerIcon = (ImageView) findViewById(R.id.chromeButton);
            explorerIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.android.filemanager", "com.android.filemanager.MainActivity"));
            explorerName = (TextView) findViewById(R.id.chromeName);
            explorerName.setText(enableFeatures.getAppName("com.android.filemanager"));
            addClickListenerExplorerEnt();
        } else if (enableFeatures.isPackageInstalled("com.android.settings", pm)){
            explorerIcon = (ImageView) findViewById(R.id.chromeButton);
            explorerIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.android.settings", "com.android.settings.Settings$StorageSettingsActivity"));
            explorerName = (TextView) findViewById(R.id.chromeName);
            explorerName.setText("Gestor de archivos");
            addClickListenerExplorerEnt();
        } else {
            cueMsg.cueWarning("Ninngún gestor de archivos instalado");
        }
        if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACIONECB", pm)){
            capaIcon = (ImageView) findViewById(R.id.mcc);
            capaIcon.setImageDrawable(enableFeatures.getActivityIcon("io.cordova.CAPACITACIONECB", "io.cordova.CAPACITACIONECB.MainActivity"));
            capaName = (TextView) findViewById(R.id.mccName);
            capaName.setText(enableFeatures.getAppName("io.cordova.CAPACITACIONECB"));
            addClickListenerCapa();
        } else if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACION", pm)){
            capaIcon = (ImageView) findViewById(R.id.mcc);
            capaIcon.setImageDrawable(enableFeatures.getActivityIcon("io.cordova.CAPACITACION", "io.cordova.CAPACITACION.MainActivity"));
            capaName = (TextView) findViewById(R.id.mccName);
            capaName.setText(enableFeatures.getAppName("io.cordova.CAPACITACION"));
            addClickListenerCapa();
        } else {
            cueMsg.cueWarning("No se encuentra la aplicación CAAP");
        }
        if (enableFeatures.isPackageInstalled("com.embarcadero.AdmCensal", pm)){
            admCensalIcon = (ImageView) findViewById(R.id.admCensal);
            admCensalIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
            admCensalName = (TextView) findViewById(R.id.admCensaltxt);
            admCensalName.setText("Administrador Censal");
            addClickListenerAdmCensal();
        } else {
            cueMsg.cueWarning("No se encuentra la aplicación AdmCensal");
        }
    }
    /******Elements layout supervisor*********/
    public void showElementsLayout(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked();
            }
        });
        PackageManager pm = this.getPackageManager();
        if (enableFeatures.isPackageInstalled("com.android.filemanager", pm)) {
            explorerIcon = (ImageView) findViewById(R.id.explorer);
            explorerIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.android.filemanager", "com.android.filemanager.MainActivity"));
            explorerName = (TextView) findViewById(R.id.explorerName);
            explorerName.setText(enableFeatures.getAppName("com.android.filemanager"));
            addClickListenerExplorer();
        } else if (enableFeatures.isPackageInstalled("com.android.settings", pm)){
            explorerIcon = (ImageView) findViewById(R.id.explorer);
            explorerIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.android.settings", "com.android.settings.Settings$StorageSettingsActivity"));
            explorerName = (TextView) findViewById(R.id.explorerName);
            explorerName.setText("Gestor de archivos");
            addClickListenerExplorer();
        } else {
            cueMsg.cueWarning("Ningún gestor instalado");
        }
        if (enableFeatures.isPackageInstalled("com.embarcadero.OperaWeb", pm)) {
            chromeName = (TextView) findViewById(R.id.chromeName);
            chromeName.setText("OPERA Web");
            chromeIcon = (ImageView) findViewById(R.id.chromeButton);
            chromeIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.embarcadero.OperaWeb", "com.embarcadero.firemonkey.FMXNativeActivity"));
            addClickListenerChrome();
        }else{
            cueMsg.cueWarning("No se encuentra la aplicación OperaWeb");
        }
        if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACIONECB", pm)){
            capaIcon = (ImageView) findViewById(R.id.capaButton);
            capaIcon.setImageDrawable(enableFeatures.getActivityIcon("io.cordova.CAPACITACIONECB", "io.cordova.CAPACITACIONECB.MainActivity"));
            capaName = (TextView) findViewById(R.id.capaName);
            capaName.setText(enableFeatures.getAppName("io.cordova.CAPACITACIONECB"));
            addClickListenerCapa();
        } else if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACION", pm)){
            capaIcon = (ImageView) findViewById(R.id.capaButton);
            capaIcon.setImageDrawable(enableFeatures.getActivityIcon("io.cordova.CAPACITACION", "io.cordova.CAPACITACION.MainActivity"));
            capaName = (TextView) findViewById(R.id.capaName);
            capaName.setText(enableFeatures.getAppName("io.cordova.CAPACITACION"));
            addClickListenerCapa();
        } else {
            cueMsg.cueWarning("No se encuentra la aplicación CAAP");
        }
        if (enableFeatures.isPackageInstalled("com.embarcadero.AdmCensal", pm)){
            admCensalIcon = (ImageView) findViewById(R.id.admCensal);
            admCensalIcon.setImageDrawable(enableFeatures.getActivityIcon( "com.embarcadero.AdmCensal", "com.embarcadero.firemonkey.FMXNativeActivity"));
            admCensalName = (TextView) findViewById(R.id.admCensaltxt);
            admCensalName.setText("Administrador Censal");
            addClickListenerAdmCensal();
        } else {
            cueMsg.cueWarning("No se encuentra la aplicación AdmCensal");
        }if (enableFeatures.isPackageInstalled("com.embarcadero.mcc", pm)){
            mccIcon = (ImageView) findViewById(R.id.mcc);
            mccIcon.setImageDrawable(enableFeatures.getActivityIcon("com.embarcadero.mcc","com.embarcadero.firemonkey.FMXNativeActivity"));
            mccName = (TextView) findViewById(R.id.mccName);
            mccName.setText("MCC");
            addClickListenerMCC();
        } else {
            cueMsg.cueWarning("No se encuentra la aplicación MCC");
        }
        if (enableFeatures.isPackageInstalled("com.example.diegocasas.descarto",pm)){
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
        settingsAll = (ImageView) findViewById(R.id.settingsAll);
        settingsAll.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        settingsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    startActivity(launchIntent);
                } else {
                    cueMsg.cueError("No cuenta con permisos");
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
                cueMsg.cueWarning("Función deshabilitada");
            }
        });
        sms = (ImageView) findViewById(R.id.sms);
        sms.setImageDrawable(getResources().getDrawable(R.drawable.ic_textsms_black_48dp));
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cueMsg.cueWarning("Función deshabilitada");
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
                showAddItemDialog(Second.this);
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
                    if (enableFeatures.isPackageInstalled("com.android.filemanager", pm)){
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.filemanager");
                        startActivity(launchIntent);
                    } else if (enableFeatures.isPackageInstalled("com.android.settings", pm)){
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$StorageSettingsActivity"));
                        startActivity(intent);
                    } else {
                        cueMsg.cueError("Error");
                    }
                } else {
                    cueMsg.cueError("No cuenta con los permisos");
                }
            }
        });
    }
    public void addClickListenerExplorer(){
        final PackageManager pm = this.getPackageManager();
        explorerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableFeatures.isPackageInstalled("com.android.filemanager", pm)){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.filemanager");
                    startActivity(launchIntent);
                } else if (enableFeatures.isPackageInstalled("com.android.settings", pm)){
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$StorageSettingsActivity"));
                    startActivity(intent);
                } else {
                    cueMsg.cueError("Error");
                }
            }
        });
    }
    public void addClickListenerCapa(){
        final PackageManager pm = this.getPackageManager();
        capaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACIONECB",pm)){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("io.cordova.CAPACITACIONECB");
                    startActivity(launchIntent);
                } else if (enableFeatures.isPackageInstalled("io.cordova.CAPACITACION", pm)){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("io.cordova.CAPACITACION");
                    startActivity(launchIntent);
                } else {
                    cueMsg.cueError("Error");
                }
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
                    cueMsg.cueError("No cuenta con los permisos");
                }
            }
        });
    }
    private void addClickListenerLogout() {
        isAdmin = false;
        lock.setVisibility(View.VISIBLE);
        unlock.setVisibility(View.INVISIBLE);
        cueMsg.cueError("Sin permisos de administrador");
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
                        String con = String.valueOf(taskEditText.getText());
                        if (con.equals(getString(R.string.pp4455))){
                            isAdmin = true;
                            lock.setVisibility(View.INVISIBLE);
                            unlock.setVisibility(View.VISIBLE);
                            cueMsg.cueCorrect("Contraseña correcta");
                        } else {
                            cueMsg.cueError("Contraseña incorrecta");
                            isAdmin = false;
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }
    /*******Dialog Logout*******/
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
                cueMsg.cueError("Finalizó la sesión");
                Intent intent = new Intent(Second.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /*****Deshabilitar back******/
    @Override
    public void onBackPressed() {
        cueMsg.cueWarning("Opción deshabilitada");
    }
   /** @Override
    protected void onStop() {
        super.onStop();
        databaseHelper.clearColumn();
        isAdmin = false;
    }**/
}
