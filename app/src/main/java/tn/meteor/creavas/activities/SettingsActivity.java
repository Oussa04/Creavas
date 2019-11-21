package tn.meteor.creavas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import tn.meteor.creavas.R;

public class SettingsActivity extends AppCompatActivity {

    private final String element1 = "Log Out";
    String[] settings = {element1};
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
       // getSupportActionBar().setTitle("Settings");
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.settings_listview, settings);
        ListView listView = (ListView) findViewById(R.id.settingsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView) view).getText().toString();
                switch (item) {
                    case element1: {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        Intent backToLogin = new Intent(SettingsActivity.this, UserSignActivity.class); // Your list's Intent
                        backToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        preferences.edit().clear();
                        startActivity(backToLogin);
                        finish();
                        break;
                    }
                }

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
            }
        });
        listView.setAdapter(adapter);
    }
}
