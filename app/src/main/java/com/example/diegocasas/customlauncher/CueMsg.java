package com.example.diegocasas.customlauncher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

public class CueMsg {

    public Context context;

    public CueMsg(Context context){
        this.context = context;
    }
    public void cueError(String msg){
        Cue.init()
                .with(context)
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
                .with(context)
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
                .with(context)
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
}