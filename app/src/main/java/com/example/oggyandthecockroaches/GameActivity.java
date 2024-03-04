
package com.example.oggyandthecockroaches;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class GameActivity extends AppCompatActivity {

    private static final int COCKROACHES_MATRIX_ROW = 7;
    private static final int COCKROACHES_MATRIX_COL = 5;
    private static final int MAX_LIVES = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    private boolean isSensorMode = false;
    private boolean isFastMode = false;
    private Intent intent;

    private TiltDetector tiltD;

    private ShapeableImageView main_IMG_background;
    private MaterialButton main_BTN_left;
    private MaterialButton main_BTN_right;
    private TextView main_TXT_score;


    //life
    private ShapeableImageView[] main_IMG_hearts; // Array of hearts (life)

    //Player- Oggy
    private ShapeableImageView[] main_IMG_oggy_position;
    private Oggy oggy = new Oggy();


    //Obstacles- cockroaches
    private ShapeableImageView[][] main_IMG_cockroaches_position;
    private ArrayList<Obstacle> cockroaches = new ArrayList<>();

    //timer
    private int chosenDelay;
    Handler handler = new Handler(Looper.myLooper());
    private static final int DELAY_SLOW = 1000;
    private static final int DELAY_FAST = 500;

    private boolean timerOn = false;
    private long startTime;

    //records
    Player currentPlayer;
    private String playerName;
    private double playerLatitude;
    private double playerLongitude;
    private int playerScore;



    private void findViews() {

        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_TXT_score = findViewById(R.id.main_TXT_score);

        //life layout
        main_IMG_hearts = new ShapeableImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };


        //Oggy's layout
        main_IMG_oggy_position = new ShapeableImageView[]{
                findViewById(R.id.main_IMG_Oggy0),
                findViewById(R.id.main_IMG_Oggy1),
                findViewById(R.id.main_IMG_Oggy2),
                findViewById(R.id.main_IMG_Oggy3),
                findViewById(R.id.main_IMG_Oggy4)
        };

        //Cockroaches' layout
        main_IMG_cockroaches_position = new ShapeableImageView[][]{
                {
                        findViewById(R.id.main_IMG_cockroach_00),
                        findViewById(R.id.main_IMG_cockroach_01),
                        findViewById(R.id.main_IMG_cockroach_02),
                        findViewById(R.id.main_IMG_cockroach_03),
                        findViewById(R.id.main_IMG_cockroach_04)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_10),
                        findViewById(R.id.main_IMG_cockroach_11),
                        findViewById(R.id.main_IMG_cockroach_12),
                        findViewById(R.id.main_IMG_cockroach_13),
                        findViewById(R.id.main_IMG_cockroach_14)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_20),
                        findViewById(R.id.main_IMG_cockroach_21),
                        findViewById(R.id.main_IMG_cockroach_22),
                        findViewById(R.id.main_IMG_cockroach_23),
                        findViewById(R.id.main_IMG_cockroach_24)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_30),
                        findViewById(R.id.main_IMG_cockroach_31),
                        findViewById(R.id.main_IMG_cockroach_32),
                        findViewById(R.id.main_IMG_cockroach_33),
                        findViewById(R.id.main_IMG_cockroach_34)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_40),
                        findViewById(R.id.main_IMG_cockroach_41),
                        findViewById(R.id.main_IMG_cockroach_42),
                        findViewById(R.id.main_IMG_cockroach_43),
                        findViewById(R.id.main_IMG_cockroach_44)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_50),
                        findViewById(R.id.main_IMG_cockroach_51),
                        findViewById(R.id.main_IMG_cockroach_52),
                        findViewById(R.id.main_IMG_cockroach_53),
                        findViewById(R.id.main_IMG_cockroach_54)
                },
                {
                        findViewById(R.id.main_IMG_cockroach_60),
                        findViewById(R.id.main_IMG_cockroach_61),
                        findViewById(R.id.main_IMG_cockroach_62),
                        findViewById(R.id.main_IMG_cockroach_63),
                        findViewById(R.id.main_IMG_cockroach_64)
                }
        };
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, chosenDelay);

            if (oggy.getLife() == 0)
                return;

            makeNewObstacle();
            updateUI();
            dropCockroaches();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();
        initViews();
        resetPreferences();
        retrievePreferences();

        // Retrieve the player name and location from the intent
        Intent intent = getIntent();
        if (intent != null) {
            playerName = intent.getStringExtra("PLAYER_NAME");
            playerLatitude = intent.getDoubleExtra("PLAYER_LATITUDE", 0.0);
            playerLongitude = intent.getDoubleExtra("PLAYER_LONGITUDE", 0.0);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        cockroaches.clear();
        lifeDefaultVisibility();
        oggyDefaultVisibility();
        cockroachDefaultVisibility();
        //resetPreferences();
        retrievePreferences();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        oggy.setLife(3);
        oggy.setScore(0);
        cockroaches.clear();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorMode && tiltD != null) {
            tiltD.stop();
        }
        resetPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetPreferences();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopTimer();
        oggy.setLife(3);
        oggy.setScore(0);
        cockroaches.clear();
        onStart();
    }


    private void startTimer() {
        if (!timerOn) {
            timerOn = true;
            startTime = System.currentTimeMillis();
            handler.postDelayed(runnable, 0);
        }
    }


    private void stopTimer() {
        timerOn = false;
        handler.removeCallbacks(runnable);
    }




    private void retrievePreferences() {
        // Retrieve preferences
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        isFastMode = prefs.getBoolean("FastMode", false);
        isSensorMode = prefs.getBoolean("SensorMode", false);

        // Adjust game behavior based on preferences
        if (isFastMode) {
            chosenDelay = DELAY_FAST;
        } else {
            chosenDelay = DELAY_SLOW;
        }

        if (isSensorMode) {
            hideGameButtons();
            startSensorMode();
        } else {
            showGameButtons();
            stopSensorMode();
        }
    }

    private void resetPreferences() {
        chosenDelay = DELAY_SLOW;
        activateButtons();
        showGameButtons();
        stopSensorMode();
    }

    private void startSensorMode() {
        tiltD = new TiltDetector(this, new TiltDetector.CallBack_moves() {
            @Override
            public void moveR() {
                moveRight();
            }

            @Override
            public void moveL() {
                moveLeft();
            }
        });
        tiltD.start();
    }

    private void stopSensorMode() {
        if (tiltD != null) {
            tiltD.stop();
        }
    }

    private void initViews() {
        //background
        Glide.with(this)
                .load(R.drawable.gamebackground)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(main_IMG_background);

        //Oggy
        for (int pos = 0; pos < main_IMG_oggy_position.length; pos++) {
            Glide.with(this).load(R.drawable.oggy).centerCrop().into(main_IMG_oggy_position[pos]);
        }

        //life
        for (int heart = 0; heart < main_IMG_hearts.length; heart++) {
            Glide.with(this)
                    .load(R.drawable.heart)
                    .centerCrop()
                    .into(main_IMG_hearts[heart]);

        }

    }

    public void moveRight() {
        moveOggy("Right");
        showOggyNow();
    }

    public void moveLeft() {
        moveOggy("Left");
        showOggyNow();
    }

    //buttons mode
    private void activateButtons(){
        main_BTN_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRight();
            }
        });
        main_BTN_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLeft();
            }
        });
    }



    //Life visibility
    private void lifeDefaultVisibility() {
        for (int heart = 0; heart < main_IMG_hearts.length; heart++)
            main_IMG_hearts[heart].setVisibility(View.VISIBLE);
    }

    //Cockroaches' visibility
    private void cockroachDefaultVisibility() {
        for (int r = 0; r < COCKROACHES_MATRIX_ROW; r++)
            for (int c = 0; c < COCKROACHES_MATRIX_COL; c++)
                main_IMG_cockroaches_position[r][c].setVisibility(View.INVISIBLE);
    }


    //Oggy's visibility
    private void oggyDefaultVisibility() {
        main_IMG_oggy_position[0].setVisibility(View.INVISIBLE);
        main_IMG_oggy_position[1].setVisibility(View.INVISIBLE);
        main_IMG_oggy_position[2].setVisibility(View.VISIBLE);
        main_IMG_oggy_position[3].setVisibility(View.INVISIBLE);
        main_IMG_oggy_position[4].setVisibility(View.INVISIBLE);
    }


    private void showOggyNow() {
        for (int pos = 0; pos < main_IMG_oggy_position.length; pos++) {
            if (oggy.getPosition() == pos)
                main_IMG_oggy_position[pos].setVisibility(View.VISIBLE);
            else
                main_IMG_oggy_position[pos].setVisibility(View.INVISIBLE);
        }
    }


    public void moveOggy(String direction) {
        int rightLimit = 4;
        int leftLimit = 0;

        if (Objects.equals(direction, "Right") && oggy.getPosition() < rightLimit){
            oggy.setPosition(oggy.getPosition() + 1);
        }

        if (Objects.equals(direction, "Left") && oggy.getPosition() > leftLimit){
            oggy.setPosition(oggy.getPosition() - 1);
        }

    }


    private void showCockroach() {
        Random random = new Random();
        int randomColumn = random.nextInt(COCKROACHES_MATRIX_COL);
        Cockroach cockroach = new Cockroach(R.drawable.cockroach, randomColumn);
        cockroaches.add(0, cockroach);
    }

    private void buildOCockroachesMatrix() {

        for (Obstacle cockroach : cockroaches) {
            cockroach.setTheNextRow();
        }
        showCockroach();
    }

    private void updateLife() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int remainingLives = oggy.getLife();

                if (remainingLives > 0 && remainingLives <= MAX_LIVES) {
                    // Update the main_IMG_hearts array based on remaining lives
                    for (int i = 0; i < MAX_LIVES; i++) {
                        if (i < remainingLives) {
                            main_IMG_hearts[i].setImageResource(R.drawable.heart);
                        } else {
                            main_IMG_hearts[i].setImageResource(R.drawable.noimage);
                        }
                    }
                }

                if (remainingLives == 0) {
                    // All lives are lost, trigger game over
                    gameOver();
                }
            }
        });
    }


    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void playSoundCockroach() {
        MediaPlayer.create(GameActivity.this,R.raw.cockroach).start();
    }
    private void playSoundButterfly() {
        MediaPlayer.create(GameActivity.this,R.raw.butterfly).start();
    }

    private void hitCase(Obstacle o) {
        if (o.getCOL() == oggy.getPosition()) {
            if (o instanceof Butterfly) {
                butterflyHit((Butterfly) o);
            } else if (o instanceof Cockroach) {
                cockroachHit((Cockroach) o);
            }
        }
    }


    private void cockroachHit(Obstacle o) {
        oggy.hit();
        playSoundCockroach();
        vibrate();
        toast("HHHIIITT!!");
        updateLife();
        updateHeartsUI();

        //if (oggy.getLife() == 0) {
            // All lives are lost -> trigger game over
          //  gameOver();
        //}
    }

    private void butterflyHit(Obstacle o) {
        playSoundButterfly();
        toast("Good Job!");
        int newScore = oggy.getScore() + ((Butterfly) o).getScore();
        oggy.setScore(newScore);
        updateScoreUI();
        cockroaches.remove(o);
    }

    private void updateHeartsUI() {
        main_IMG_hearts[oggy.getLife() - 1].setVisibility(View.VISIBLE);
    }

    private void updateScoreUI() {
        main_TXT_score.setText(String.valueOf(oggy.getScore()));
    }

    private void createCockroach() {
        cockroaches.add(0,new Cockroach (R.drawable.cockroach));
    }

    private void createButterfly() {
        cockroaches.add(0,new Butterfly (R.drawable.bonus));
    }

    private void makeNewObstacle(){
        buildOCockroachesMatrix();
        whatToDrop();
    }

    private void whatToDrop(){
        Random r = new Random();
        //0- butterfly
        //1- cockroach
        if(r.nextInt(2)==0)
            createButterfly();
        else
            createCockroach();

    }

    private void dropCockroaches() {
        ArrayList<Obstacle> obstaclesToRemove = new ArrayList<>();

        for (int i = 0; i < cockroaches.size(); i++) {
            Obstacle obstacle = cockroaches.get(i);

            // Update the obstacle's row to make it fall
            obstacle.setTheNextRow();

            // Check if the obstacle is in the same column as Oggy
            if (obstacle.getRow() == COCKROACHES_MATRIX_ROW && obstacle.getCOL() == oggy.getPosition()) {
                // Check for collisions and handle them
                hitCase(obstacle);

                // Check if the obstacle should be removed
                if (obstacle.getRow() > COCKROACHES_MATRIX_ROW) {
                    obstaclesToRemove.add(obstacle);
                    // Add log statements for debugging
                    Log.d("DropCockroaches", "Removing obstacle at index " + i +
                            ", Row: " + obstacle.getRow() +
                            ", Column: " + obstacle.getCOL());
                }
            }
        }

        // Remove obstacles outside the matrix
        cockroaches.removeAll(obstaclesToRemove);

        updateUI();
    }

    private void updateUI() {
        for (int heartIndex = 0; heartIndex < main_IMG_hearts.length; heartIndex++) {
            if (heartIndex < oggy.getLife()) {
                // Show visible hearts based on remaining lives
                main_IMG_hearts[heartIndex].setVisibility(View.VISIBLE);
            } else {
                // Hide hearts for lost lives
                main_IMG_hearts[heartIndex].setVisibility(View.INVISIBLE);
            }
        }

        for (int r = 0; r < COCKROACHES_MATRIX_ROW; r++) {
            for (int c = 0; c < COCKROACHES_MATRIX_COL; c++) {
                ShapeableImageView coc = main_IMG_cockroaches_position[r][c];

                if (cockroaches.size() > r && cockroaches.get(r).getCOL() == c) {
                    // If there is a cockroach at this position, set its image and make it visible
                    coc.setImageResource(cockroaches.get(r).getImage());
                    coc.setVisibility(View.VISIBLE);
                } else {
                    // If there is no cockroach at this position, make it invisible
                    coc.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    private void hideGameButtons(){
            main_BTN_right.setVisibility(View.INVISIBLE);
            main_BTN_left.setVisibility(View.INVISIBLE);
            //tiltD.start();
    }

    private void showGameButtons(){
        main_BTN_right.setVisibility(View.VISIBLE);
        main_BTN_left.setVisibility(View.VISIBLE);
    }

    private void gameOver() {
        // Stop the timer or any other ongoing processes
        stopTimer();
        playerScore = oggy.getScore();
        currentPlayer = new Player(playerName, playerScore, playerLatitude, playerLongitude);
        RecordsManager.addPlayerRecord(getApplicationContext(), currentPlayer);
        oggy.setLife(3);
        oggy.setScore(0);
        cockroaches.clear();
        updateUI();
        toast("The game is over!");
        Intent homeIntent = new Intent(GameActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}



