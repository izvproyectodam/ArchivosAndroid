package org.izv.aad.aadarchivos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.LocaleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ActividadPrincipal extends AppCompatActivity {

    static final String TAG = "mitag";
    static final String ARCHIVO = "archivo.txt";

    private android.widget.EditText etValor;
    private android.widget.RadioButton rbInPr;
    private android.widget.RadioButton rbExPr;
    private android.widget.RadioButton rbExPu;
    private android.widget.RadioGroup rgTipo;
    private android.widget.Button btWrite;

    private final int VARIABLE_PERMISOS = 1;

    private File getInternaPrivada(String name) {
        return new File(getFilesDir(), name);
    }

    private File getExternaPrivada(String name) {
        return getExternaPrivada(null, name);
    }

    private File getExternaPrivada(String type, String name) {
        String tipos = Environment.DIRECTORY_ALARMS +
                Environment.DIRECTORY_DCIM +
                Environment.DIRECTORY_DOCUMENTS +
                Environment.DIRECTORY_DOWNLOADS +
                Environment.DIRECTORY_MOVIES +
                Environment.DIRECTORY_MUSIC +
                Environment.DIRECTORY_NOTIFICATIONS +
                Environment.DIRECTORY_PICTURES +
                Environment.DIRECTORY_PODCASTS +
                Environment.DIRECTORY_RINGTONES +
                "null";
        return new File(getExternalFilesDir(type), name);
    }

    private File getExternaPublica(String name) {
        return new File(Environment.getExternalStorageDirectory() , name);
    }

    private File getExternaPublica(String type, String name) {
        return new File(Environment.getExternalStoragePublicDirectory(type), name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        init();
    }

    private void init() {
        this.btWrite = (Button) findViewById(R.id.btWrite);
        this.rgTipo = (RadioGroup) findViewById(R.id.rgTipo);
        this.rbExPu = (RadioButton) findViewById(R.id.rbExPu);
        this.rbExPr = (RadioButton) findViewById(R.id.rbExPr);
        this.rbInPr = (RadioButton) findViewById(R.id.rbInPr);
        this.etValor = (EditText) findViewById(R.id.etValor);
        setEventListeners();
    }

    private void setEventListeners() {
        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int opcion = rgTipo.getCheckedRadioButtonId();
                String texto = etValor.getText().toString();
                if(texto.compareTo("") != 0) {
                    File f = null;
                    switch (opcion) {
                        case R.id.rbExPr:
                            f = getExternaPrivada(Environment.DIRECTORY_DOCUMENTS, ARCHIVO);
                            break;
                        case R.id.rbExPu:
                            comprobarPermisos();
                            break;
                        case R.id.rbInPr:
                            f = getInternaPrivada(ARCHIVO);
                            break;
                        default:
                    }
                    if(f !=  null) {
                        write(f, texto);
                    }
                }
                Log.v(TAG, opcion + "");
            }
        });
    }

    private boolean write(File f, String texto) {
        boolean written = true;
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write(texto);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            Log.v(TAG, e.toString());
            written = false;
        }
        return written;
    }

    private void pedirPermisos(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, VARIABLE_PERMISOS);
    }

    private void comprobarPermisos(){
        int permisoComprobado = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permisoComprobado != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this ,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setMessage("Mira necesito los permisos para " +
                        "escribir en la externa publica");
                build.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pedirPermisos();
                    }
                });
                build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ActividadPrincipal.this,"No tengo permisos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                build.create().show();

            }
            else {
                pedirPermisos();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == VARIABLE_PERMISOS){
            String texto = etValor.getText().toString();
            File f = getExternaPublica(ARCHIVO);
            write(f, texto);
        }

    }
}
