package com.hfad.stappenplan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.stappenplan.util.AsyncTaskCompleteListener;
import com.hfad.stappenplan.util.AsyncTaskListener;
import com.hfad.stappenplan.util.AsyncTaskUrlToJson;
import com.hfad.stappenplan.util.HttpGetRequest;
import com.hfad.stappenplan.util.HttpGetRequestResponse;
import com.hfad.stappenplan.util.ImageStoreAsyncTask;
import com.hfad.stappenplan.util.JsonUtil;
import com.hfad.stappenplan.util.UrlBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener,HttpGetRequestResponse,AsyncTaskListener {

    int number = 0;
    int eind= 1;
    String check;
    String username;
    String name;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 685;
    private ArrayList<String> beschrijving;
    private ArrayList<String> activiteit;
    private ArrayList<String> inhoud;
    private ArrayList<URL> stap;
    public ArrayList<String> picto;
    private ArrayList<String> stringArrayList;
    private ArrayList<String> pathArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent Intent = getIntent();
        username = Intent.getStringExtra("key");
        TextView textView_userText = (TextView) findViewById(R.id.textView_user);
        textView_userText.setText("");
        textView_userText.setText(username);
        SharedPreferences prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);

        name = prefs.getString("LOGIN_ID", "");



        activiteit = new ArrayList<>();
        beschrijving = new ArrayList<>();
        inhoud = new ArrayList<>();
        stap = new ArrayList<>();
        picto = new ArrayList<>();


        if(isNetworkAvailable()){

            HttpGetRequest httpGetRequest = new HttpGetRequest(this);
            httpGetRequest.execute(UrlBuilder.buildUrl());
        }
        else {
            SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
            pathArrayList = new ArrayList<>(sharedPreferences.getStringSet("paths",null));
            sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            String tekst = sharedPreferences.getString("tekst", null);
            jsonToArray(tekst);
            reloadUI();
            updateUI();
        }

        if(savedInstanceState == null && isNetworkAvailable()){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            }

            maakUI();


        }}

    private void maakUI() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("key");

        AsyncTaskUrlToJson asyncTaskUrlToJson = new AsyncTaskUrlToJson(this, this);

        asyncTaskUrlToJson.execute("https://prikkelapp.mediade.eu/stappenplan.php/?name="+ username);

    }

    public void next(View view) {
        if (number != eind-1) {
            number++;

        }else {
            number=eind-1;

        }
        updateUI();
        reloadUI();
    }
    public void terug(View view) {
        number--;
        if (number < 0) {
            number = 0;
        }
        updateUI();
        reloadUI();
    }
    public void sluiten(View view) {
        this.finishAffinity();
    }

    public void user(View view) {
        String User = "nieuw";
        Intent Intent = new Intent(MainActivity.this, LoginActivity.class);
        Intent.putExtra("gebruiker", User);
        MainActivity.this.startActivity(Intent);
    }

    private void reloadUI() {

        TextView textView_activiteit = (TextView) findViewById(R.id.activiteit);
        textView_activiteit.setText("");
        textView_activiteit.setText(activiteit.get(number));

        TextView textView_beschrijving = (TextView) findViewById(R.id.beschrijving);
        textView_beschrijving.setText("");
        textView_beschrijving.setText(beschrijving.get(number));

        TextView textView_inhoud = (TextView) findViewById(R.id.inhoud);
        textView_inhoud.setText("");
        textView_inhoud.setText(inhoud.get(number));



    }



    private void jsonToArray(String tekst) {
        try {
            JSONObject jsonObject = new JSONObject(tekst);

            activiteit.clear();
            beschrijving.clear();
            stap.clear();
            inhoud.clear();
            picto.clear();


            JSONArray jsonArrayBeschrijving = jsonObject.getJSONArray("beschrijving");
            eind = jsonArrayBeschrijving.length();

            for (int i = 0; i < eind; i++) {
                JSONObject jsonObj = jsonArrayBeschrijving.getJSONObject(i);
                beschrijving.add(new String(jsonObj.getString("text")));
            }
            JSONArray jsonArrayActiviteit = jsonObject.getJSONArray("activiteit");
            for (int i = 0; i < jsonArrayActiviteit.length(); i++) {
                JSONObject jsonObj = jsonArrayActiviteit.getJSONObject(i);
                activiteit.add(new String(jsonObj.getString("text")));
            }
            JSONArray jsonArrayInhoud = jsonObject.getJSONArray("inhoud");
            for (int i = 0; i < jsonArrayInhoud.length(); i++) {
                JSONObject jsonObj = jsonArrayInhoud.getJSONObject(i);
                inhoud.add(new String(jsonObj.getString("text")));
            }
            JSONArray jsonArrayStap = jsonObject.getJSONArray("stap");
            for (int i = 0; i < jsonArrayStap.length(); i++) {
                JSONObject jsonObj = jsonArrayStap.getJSONObject(i);
                picto.add(new String(jsonObj.getString("text")));
            }



        } catch (JSONException ex) {
            Log.e(LOG_TAG, "jsonToArray: ", ex);
            // TODO: 14-8-2017
        }

    }


    @Override
    public void onTaskComplete(Object result) {
        String tekst = (String) result;
        if (tekst.isEmpty()) {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            tekst = sharedPreferences.getString("tekst", null);

        } else {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tekst", tekst);
            editor.commit();
        }
        jsonToArray(tekst);




        reloadUI();}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    @Override
    public void processFinish(String output) {
        stringArrayList = JsonUtil.jsonToStringArraylist(output);
        new ImageStoreAsyncTask(this).execute(stringArrayList);
    }

    @Override
    public void imgPath(ArrayList<String> output) {
        pathArrayList = output;
        updateUI();
    }

    public void updateUI(){
        String surl=picto.get(number);
        ImageView imageView = findViewById(R.id.stap1);
        String name = surl.substring(surl.lastIndexOf("/") + 1).toLowerCase();
        String root = Environment.getExternalStorageDirectory().toString();
        imageView.setImageURI(Uri.fromFile(new File(root + "/saved_images/" + name)));
        Log.d(LOG_TAG,surl);
        Log.d(LOG_TAG,pathArrayList.get(number));




    }

    @Override
    protected void onPause() {
        super.onPause();

        if(pathArrayList != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("paths", new HashSet<String>(pathArrayList));
            editor.commit();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

   }
