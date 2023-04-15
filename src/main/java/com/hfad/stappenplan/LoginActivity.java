package com.hfad.stappenplan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    String name;
    String Gebruiker;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent Intent = getIntent();
        Gebruiker = "0";
        if (Gebruiker == null){
            Gebruiker = "";
        }

        SharedPreferences prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        name = prefs.getString("LOGIN_ID", "");

        if (Gebruiker.equals("nieuw")){
            name = "";}


        if (name.length() > 0) {
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            myIntent.putExtra("key", name); //Optional parameters
            LoginActivity.this.startActivity(myIntent);
        } else {

        }

    }

    public void Login(View view) {

        EditText txtname = (EditText)findViewById(R.id.username);
        name = txtname.getText().toString();

        SharedPreferences prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOGIN_ID", name);
        editor.commit();
        Intent Intent = new Intent(LoginActivity.this, MainActivity.class);
        Intent.putExtra("key", name); //Optional parameters
        LoginActivity.this.startActivity(Intent);

    }



}