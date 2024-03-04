package com.example.oggyandthecockroaches;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton main_BTN_records;
    private MaterialButton main_BTN_play;
    private EditText main_TXT_player;
    private SwitchCompat main_SWTCH_sensors;
    private SwitchCompat main_SWTCH_level;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

            findViews();
            permissionGPS();
            playSound();

        main_BTN_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(HomeActivity.this, RecordsActivity.class);
              startActivity(intent);
            };
        });


        // Start GameActivity when the user clicks the "Start Game" button
        main_BTN_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPlayerName()) {
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    String playerName = main_TXT_player.getText().toString();
                    intent.putExtra("PLAYER_NAME", playerName);

                    startActivity(intent);
                }
            }
        });

        // Handle the switch for movement speed
        main_SWTCH_level.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("GamePrefs", MODE_PRIVATE).edit();
                editor.putBoolean("FastMode", isChecked);
                editor.apply();
            }
        });

        // Handle the switch for control mode
        main_SWTCH_sensors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("GamePrefs", MODE_PRIVATE).edit();
                editor.putBoolean("SensorMode", isChecked);
                editor.apply();
            }
        });

    }

    private void findViews() {
        main_BTN_records=findViewById(R.id.main_BTN_records);
        main_BTN_play=findViewById(R.id.main_BTN_play);
        main_TXT_player=findViewById(R.id.main_TXT_player);
        main_SWTCH_sensors=findViewById(R.id.main_SWTCH_sensors);
        main_SWTCH_level=findViewById(R.id.main_SWTCH_level);

    }

    //checking if the player entered name in order to start the game
    private boolean checkPlayerName() {
        if (TextUtils.isEmpty(main_TXT_player.getText()))
        {
            main_TXT_player.setError("Please enter name.");
            return false;
        }
        return true;
    }


    private void playSound() {
       MediaPlayer.create(HomeActivity.this,R.raw.oggymusic).start();
    }

    private void permissionGPS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }





}