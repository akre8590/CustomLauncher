package com.example.diegocasas.customlauncher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.mahdi.mzip.zip.ZipArchive;

public class DesCarto extends AppCompatActivity {

    Button buttonEnt, buttonRA07, buttonRA08;
    TextView entTxt, raTxt;
    AlertDialog alertDialog1;
    AlertDialog alertDialog2;
    CharSequence[] entidades = {" Entidad 07 "," Entidad 08 "};
    CharSequence[] RA07 = {" RA1 "," RA2 ", " RA3 ", " RA4 "};
    CharSequence[] RA08 = {" RA1 "," RA2 ", " RA3 ", " RA4 "};
    CharSequence[] AB = {" Ampliado "," Básico "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des_carto);

        buttonEnt = (Button)findViewById(R.id.buttonEntidad);
        buttonRA07 = (Button)findViewById(R.id.btnRA07);
        buttonRA08 = (Button)findViewById(R.id.btnRA08);

        entTxt = (TextView)findViewById(R.id.entidadTxt);
        raTxt = (TextView) findViewById(R.id.RATxt);

        buttonEnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEntidad();
            }
        });
        buttonRA07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRA07();
            }
        });
        buttonRA08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRA08();
            }
        });
    }
    public void dialogEntidad(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DesCarto.this);

        builder.setTitle("Elige la entidad");

        builder.setSingleChoiceItems(entidades, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        entTxt.setText("Elegiste la entidad 07");
                        buttonRA08.setVisibility(View.INVISIBLE);
                        buttonRA07.setVisibility(View.VISIBLE);
                        Toast.makeText(DesCarto.this, "Elegiste la entidad 07", Toast.LENGTH_LONG).show();
                        //KToast.successToast(DesCarto.this, "Elegiste la entidad 07.", Gravity.CENTER, KToast.LENGTH_SHORT);
                        raTxt.setText("");
                        break;
                    case 1:
                        entTxt.setText("Elegiste la entidad 08");
                        buttonRA07.setVisibility(View.INVISIBLE);
                        buttonRA08.setVisibility(View.VISIBLE);
                        Toast.makeText(DesCarto.this, "Elegiste la entidad 08", Toast.LENGTH_LONG).show();
                        raTxt.setText("");
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }
    public void dialogRA07(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DesCarto.this);

        builder.setTitle("Elige el RA");

        builder.setSingleChoiceItems(RA07 , -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        AlertDialog.Builder builderA = new AlertDialog.Builder(DesCarto.this);
                        builderA.setTitle("Elegiste la entidad 07 - RA1");
                        builderA.setSingleChoiceItems(AB, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0:
                                        raTxt.setText("Elegiste el RA1 - Ampliado");
                                        copyAssets("mcc_071A.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive1 =  new ZipArchive();
                                        zipArchive1.unzip("storage/emulated/0/mcc_071A.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_071A.zip");
                                        File fdelete = new File("storage/emulated/0/mcc_071A.zip");
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_071A.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_071A.zip");
                                            }
                                        }
                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        raTxt.setText("Elegiste el RA1 - Básico");
                                        copyAssets("mcc_071B.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive2 =  new ZipArchive();
                                        zipArchive2.unzip("storage/emulated/0/mcc_071B.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_071b.zip");
                                        File fdelete1 = new File("storage/emulated/0/mcc_071B.zip");
                                        if (fdelete1.exists()) {
                                            if (fdelete1.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_071B.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_071B.zip");
                                            }
                                        }
                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                alertDialog2.dismiss();
                            }
                        });
                        alertDialog2 = builderA.create();
                        alertDialog2.show();
                        break;
                    case 1:
                        raTxt.setText("Elegiste el RA2 - Básico");
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(DesCarto.this);
                        builder2.setTitle("Elegiste la Entidad 07 - RA2");
                        builder2.setMessage("¿Seguro de continuar?");

                        // add the buttons
                        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                raTxt.setText("Elegiste el RA2");
                                copyAssets("mcc_072B.zip",   "storage/emulated/0/");
                                ZipArchive zipArchive1 =  new ZipArchive();
                                zipArchive1.unzip("storage/emulated/0/mcc_072B.zip","storage/emulated/0/mcc/", "");
                                Log.d("DESCOMPRESO", "storage/emulated/0/mcc_072B.zip");
                                File fdelete = new File("storage/emulated/0/mcc_072B.zip");
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Log.d("DELETE", "storage/emulated/0/mcc_072B.zip");
                                    } else {
                                        Log.d("FAILED", "storage/emulated/0/mcc_072B.zip");
                                    }
                                }
                                Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();

                        break;
                    case 2:
                        AlertDialog.Builder builderB = new AlertDialog.Builder(DesCarto.this);
                        builderB.setTitle("Elegiste la entidad 07 - RA3");
                        builderB.setSingleChoiceItems(AB, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0:
                                        raTxt.setText("Elegiste el RA3 - Ampliado");
                                        copyAssets("mcc_073A.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive1 =  new ZipArchive();
                                        zipArchive1.unzip("storage/emulated/0/mcc_073A.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_073A.zip");
                                        File fdelete = new File("storage/emulated/0/mcc_073A.zip");
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_073A.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_073A.zip");
                                            }
                                        }

                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        raTxt.setText("Elegiste el RA3 - Básico");
                                        copyAssets("mcc_073B.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive2 =  new ZipArchive();
                                        zipArchive2.unzip("storage/emulated/0/mcc_073B.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_073B.zip");
                                        File fdelete2 = new File("storage/emulated/0/mcc_073B.zip");
                                        if (fdelete2.exists()) {
                                            if (fdelete2.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_073B.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_073B.zip");
                                            }
                                        }

                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                alertDialog2.dismiss();
                            }
                        });
                        alertDialog2 = builderB.create();
                        alertDialog2.show();
                        break;
                    case 3:
                        AlertDialog.Builder builderC = new AlertDialog.Builder(DesCarto.this);
                        builderC.setTitle("Elegiste la entidad 07 - RA4");
                        builderC.setSingleChoiceItems(AB, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0:
                                        raTxt.setText("Elegiste el RA4 - Ampliado");
                                        copyAssets("mcc_074A.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive1 =  new ZipArchive();
                                        zipArchive1.unzip("storage/emulated/0/mcc_074A.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_074A.zip");
                                        File fdelete = new File("storage/emulated/0/mcc_074A.zip");
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_074A.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_074A.zip");
                                            }
                                        }

                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        raTxt.setText("Elegiste el RA4 - Básico");
                                        copyAssets("mcc_074B.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive2 =  new ZipArchive();
                                        zipArchive2.unzip("storage/emulated/0/mcc_074B.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_074B.zip");
                                        File fdelete2 = new File("storage/emulated/0/mcc_074B.zip");
                                        if (fdelete2.exists()) {
                                            if (fdelete2.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_074B.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_074B.zip");
                                            }
                                        }
                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                alertDialog2.dismiss();
                            }
                        });
                        alertDialog2 = builderC.create();
                        alertDialog2.show();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }
    public void dialogRA08(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DesCarto.this);

        builder.setTitle("Elige el RA");

        builder.setSingleChoiceItems(RA08, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        AlertDialog.Builder builderC = new AlertDialog.Builder(DesCarto.this);
                        builderC.setTitle("Elegiste la entidad 08 - RA1");
                        builderC.setSingleChoiceItems(AB, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0:
                                        raTxt.setText("Elegiste el RA1 - Ampliado");
                                        copyAssets("mcc_081A.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive1 =  new ZipArchive();
                                        zipArchive1.unzip("storage/emulated/0/mcc_081A.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_081A.zip");
                                        File fdelete = new File("storage/emulated/0/mcc_081A.zip");
                                        if (fdelete.exists()) {
                                            if (fdelete.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_081A.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_081A.zip");
                                            }
                                        }
                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        raTxt.setText("Elegiste el RA1 - Básico");
                                        copyAssets("mcc_081B.zip",   "storage/emulated/0/");
                                        ZipArchive zipArchive2 =  new ZipArchive();
                                        zipArchive2.unzip("storage/emulated/0/mcc_081B.zip","storage/emulated/0/mcc/", "");
                                        Log.d("DESCOMPRESO", "storage/emulated/0/mcc_081B.zip");
                                        File fdelete2 = new File("storage/emulated/0/mcc_081B.zip");
                                        if (fdelete2.exists()) {
                                            if (fdelete2.delete()) {
                                                Log.d("DELETE", "storage/emulated/0/mcc_081B.zip");
                                            } else {
                                                Log.d("FAILED", "storage/emulated/0/mcc_081B.zip");
                                            }
                                        }
                                        Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                alertDialog2.dismiss();
                            }
                        });
                        alertDialog2 = builderC.create();
                        alertDialog2.show();
                        break;
                    case 1:
                        raTxt.setText("Elegiste el RA2 - Básico");
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(DesCarto.this);
                        builder2.setTitle("Elegiste la Entidad 08 - RA2.");
                        builder2.setMessage("¿Seguro de continuar?");

                        // add the buttons
                        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyAssets("mcc_082B.zip",   "storage/emulated/0/");
                                ZipArchive zipArchive1 =  new ZipArchive();
                                zipArchive1.unzip("storage/emulated/0/mcc_082B.zip","storage/emulated/0/mcc", "");
                                Log.d("DESCOMPRESO", "storage/emulated/0/mcc_082B.zip");
                                File fdelete = new File("storage/emulated/0/mcc_082B.zip");
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Log.d("DELETE", "storage/emulated/0/mcc_082B.zip");
                                    } else {
                                        Log.d("FAILED", "storage/emulated/0/mcc_082B.zip");
                                    }
                                }

                                Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                        break;
                    case 2:
                        raTxt.setText("Elegiste el RA3 - Básico");
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(DesCarto.this);
                        builder3.setTitle("Elegiste la Entidad 08 - RA3.");
                        builder3.setMessage("¿Seguro de continuar?");

                        // add the buttons
                        builder3.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyAssets("mcc_083B.zip",   "storage/emulated/0/");
                                ZipArchive zipArchive1 =  new ZipArchive();
                                zipArchive1.unzip("storage/emulated/0/mcc_083B.zip","storage/emulated/0/mcc/", "");
                                Log.d("DESCOMPRESO", "storage/emulated/0/mcc_083B.zip");
                                File fdelete = new File("storage/emulated/0/mcc_083B.zip");
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Log.d("DELETE", "storage/emulated/0/mcc_083B.zip");
                                    } else {
                                        Log.d("FAILED", "storage/emulated/0/mcc_083B.zip");
                                    }
                                }
                                Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog3 = builder3.create();
                        dialog3.show();
                        break;
                    case 3:
                        raTxt.setText("Elegiste el RA4 - Básico");
                        AlertDialog.Builder builder4 = new AlertDialog.Builder(DesCarto.this);
                        builder4.setTitle("Elegiste la Entidad 08 - RA4.");
                        builder4.setMessage("¿Seguro de continuar?");

                        // add the buttons
                        builder4.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyAssets("mcc_084B.zip",   "storage/emulated/0/");
                                ZipArchive zipArchive1 =  new ZipArchive();
                                zipArchive1.unzip("storage/emulated/0/mcc_084B.zip","storage/emulated/0/mcc/", "");
                                Log.d("DESCOMPRESO", "storage/emulated/0/mcc_084B.zip");
                                File fdelete = new File("storage/emulated/0/mcc_084B.zip");
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        Log.d("DELETE", "storage/emulated/0/mcc_084B.zip");
                                    } else {
                                        Log.d("FAILED", "storage/emulated/0/mcc_084B.zip");
                                    }
                                }
                                Toast.makeText(DesCarto.this, "Finalizó el proceso", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog4 = builder4.create();
                        dialog4.show();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }
    public void copyAssets(String filename, String dirPath){
        File dir = new File(dirPath);
        if (!dir.exists()){
            dir.mkdir();
        }
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File (dirPath, filename);
            out =  new FileOutputStream(outFile);
            copyFile(in, out);
            Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error de guardado", Toast.LENGTH_SHORT).show();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void copyFile(InputStream in, OutputStream out) throws IOException{
        byte [] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1 ){
            out.write(buffer, 0, read);
        }
    }
    public void salir(View view){
        finish();

    }
}

