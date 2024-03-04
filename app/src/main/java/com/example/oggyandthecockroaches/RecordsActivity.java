package com.example.oggyandthecockroaches;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RecordsActivity extends AppCompatActivity implements OnMapReadyCallback {

    boolean isPermissionGranted;
    private TableLayout records_TBL_records;
    private MapView mapView;
    private MaterialButton records_BTN_home;

    private double PlayerLatitude;
    private double PlayerLongitude;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        findViews();

        // Initialize the MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Check Google Play services
        if (!checkGooglePlayServices()) {
            Toast.makeText(this, "Google PlayServices Not Available", Toast.LENGTH_SHORT).show();
        }

        List<Player> topPlayers = RecordsManager.getTopPlayers(getApplicationContext(), 10);
        if (topPlayers != null) {
            displayTopPlayers(topPlayers);
        } else {
            Log.e("RecordsActivity", "Top players list is null.");
        }

        records_BTN_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void displayTopPlayers(List<Player> players) {
        if (players != null && !players.isEmpty()) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                addTableRow(i + 1, player.getName(), String.valueOf(player.getScore()),
                        player.getLatitude(), player.getLongitude());
            }
        } else {
            Log.e("RecordsActivity", "Top players list is null or empty.");
        }
    }
    private void addTableRow(int rank, String playerName, String score, double latitude, double longitude) {
        TableRow row = new TableRow(this);

        TextView rankTextView = createTextView(String.valueOf(rank));
        TextView playerNameTextView = createTextView(playerName);
        TextView scoreTextView = createTextView(score);

        playerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayerLocationOnMap(playerName, latitude, longitude);
            }
        });

        row.addView(rankTextView);
        row.addView(playerNameTextView);
        row.addView(scoreTextView);

        records_TBL_records.addView(row);
    }


    private void showPlayerLocationOnMap(String playerName, double latitude, double longitude) {
        // Display the map and show the player's location
        mapView.setVisibility(View.VISIBLE);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // Use googleMap to show the player's location
                LatLng playerLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(playerLocation).title(playerName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(playerLocation));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(20);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(3, 3, 3, 3);
        return textView;
    }


    private void findViews() {
        records_TBL_records=findViewById(R.id.records_TBL_records);
        mapView=findViewById(R.id.mapView);
        records_BTN_home=findViewById(R.id.records_BTN_home);
    }




    private boolean checkGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            // Google Play services is available
            return true;
        } else if (apiAvailability.isUserResolvableError(resultCode)) {
            // An error occurred, but it's resolvable. Display the dialog to resolve the issue.
            apiAvailability.getErrorDialog(this, resultCode, 9000).show();
        } else {
            Log.e("PlayServicesChecker", "Google Play services are not available on this device.");
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}