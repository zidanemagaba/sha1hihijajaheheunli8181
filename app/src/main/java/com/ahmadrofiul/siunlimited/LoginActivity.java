package com.ahmadrofiul.siunlimited;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    private Button btn_login;
    private EditText txtusername, txtpassword;
    private AlertDialog.Builder builder;
    private String nim, password, setuju, studi;
    private CheckBox ShowPass;
    private String status = "0", nama = "MAHASISWA UDINUS";
    private int cek = 0, jumlah = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView nfc = (TextView)findViewById(R.id.nfc);
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this, LoginnfcActivity.class));
                //finish();
            }
        });

        mProgressDialog = new ProgressDialog(this);

        builder = new AlertDialog.Builder(LoginActivity.this);

        txtusername = (EditText) findViewById(R.id.txt_username);
        txtpassword = (EditText)findViewById(R.id.txt_password);

        ShowPass = (CheckBox) findViewById(R.id.showPass);

        //Set onClickListener, untuk menangani kejadian saat Checkbox diklik
        ShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShowPass.isChecked()){
                    //Saat Checkbox dalam keadaan Checked, maka password akan di tampilkan
                    txtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    //Jika tidak, maka password akan di sembuyikan
                    txtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        SharedPreferences share = getSharedPreferences("mahasiswa", MODE_PRIVATE);
        nim = share.getString("nim","");
        password = share.getString("pass","");
        setuju = share.getString("setuju","");
        txtusername.setText(nim);
        txtpassword.setText(password);

        btn_login =(Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nim = txtusername.getText().toString();
                password = txtpassword.getText().toString();
                //Toast.makeText(LoginActivity.this,nim+"+"+password,Toast.LENGTH_LONG).show();

                if(setuju.equals("1")){
                    mProgressDialog.setMessage("Loading ...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    getData();

                }else {
                    builder.setTitle("Perhatian");
                    builder.setMessage("Silahkan Baca Kebijakan Privasi terlebih dahulu");
                    //builder.setCancelable(false);
                    builder.setPositiveButton("Siapp", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });

        TextView t = (TextView)findViewById(R.id.kebijakan);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, KebijakanActivity.class));
            }
        });
    }


    private void getData(){
        final StringRequest request = new StringRequest(Request.Method.GET, UtilsActivity.URL_LOGIN+nim+"&pass="+password+"&ver="+UtilsActivity.VERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Toast.makeText(LoginActivity.this, Konfigurasi.URL_GET_ADMIN+username+"&password="+password,Toast.LENGTH_LONG).show();
                            iniData(response);
                            mProgressDialog.dismiss();
                        }catch (Exception e){
                            Toast.makeText(LoginActivity.this, "Eror "+e,Toast.LENGTH_LONG).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void iniData(String response){
        try {
            cek=0;
            JSONObject jsonObject = new JSONObject(response);

            // ini utk mengambil attribute array yg ada di json (yaitu attribute data)
            status = jsonObject.getString("result");
            nama = jsonObject.getString("nama");
            studi = jsonObject.getString("studi");


            if(status.equals("passwordsalah")){
                builder.setTitle("Login Gagal");
                builder.setMessage("Nim / Password salah");
                builder.setCancelable(false);
                builder.setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else if(status.equals("maintenance")){
                builder.setTitle("Perhatian");
                builder.setMessage("Maaf Aplikasi ini sedang Maintenance :) ");
                builder.setCancelable(false);
                builder.setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else if(status.equals("update")){
                builder.setTitle("Perhatian");
                builder.setMessage("Silahkan Update Aplikasi ini untuk Kenyamanan :)");
                builder.setCancelable(false);
                builder.setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                        editor.putBoolean("login", false);
                        editor.apply();

                        SharedPreferences share = getSharedPreferences("mahasiswa", MODE_PRIVATE);
                        SharedPreferences.Editor save = share.edit();
                        save.putString("nim", "");
                        save.putString("pass", "");
                        save.apply();

                        String url = "https://play.google.com/store/apps/details?id=com.ahmadrofiul.siunlimited";
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else if(status.equals("official")){
                builder.setTitle("Perhatian");
                builder.setMessage("Maaf Aplikasi ini tidak dapat digunakan lagi, karena sudah ada official resmi\nTerimakasih atas dukungannya :) ");
                builder.setCancelable(false);
                builder.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Go To Official", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        String url = UtilsActivity.DOMAIN+"/official.php";
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                //mengubah nilai login nya jadi true
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                editor.putBoolean("login", true);
                editor.apply();

                SharedPreferences share = getSharedPreferences("mahasiswa", MODE_PRIVATE);
                SharedPreferences.Editor save = share.edit();
                save.putString("nama", nama);
                save.putString("nim", nim);
                save.putString("pass", password);
                save.putString("login", status);
                save.putString("studi", studi);
                save.apply();

                builder.setTitle("SELAMAT DATANG");
                builder.setMessage(""+nama);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
