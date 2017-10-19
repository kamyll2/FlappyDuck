package com.wygralak.flappyduck;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wygralak.flappyduck.Engine.FlappyDuckSurfaceView2;
import com.wygralak.flappyduck.Engine.GameHandler;
import com.wygralak.flappyduck.Engine.IGameStateHolder;

public class FlappyActivity extends ActionBarActivity
        implements IMessageViewer, IGameStateHolder, View.OnClickListener {

    RelativeLayout overlayView;
    TextView messageTextView;
    TextView countdownTextView;
    private SoundPool soundPool;
    private int deadSoundId;
    private int[] quackSoundIds;
    private AudioManager audioManager;

    FlappyDuckSurfaceView2 surfaceView;
    private FlappyDuckSurfaceView2.FlappyDuckThread gameThread;
    private boolean countdownRunning;

    private int wallsBeaten = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flappy);
        setupSoundPool();

        overlayView = (RelativeLayout) findViewById(R.id.overlay);
        messageTextView = (TextView) findViewById(R.id.text);
        countdownTextView = (TextView) findViewById(R.id.countdownHolder);
        surfaceView = (FlappyDuckSurfaceView2) findViewById(R.id.pitch);
        surfaceView.setRefree(this);
        gameThread = surfaceView.getThread();

        overlayView.setOnClickListener(this);

        gameThread.highScore = GameHandler.loadHighScore(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overlay:
                if (gameThread.isGameInStateReady() && !countdownRunning) {
                    countdownRunning = true;
                    new Thread(new CountdownRunnable(new CountdownRunnable.ICountdownNotifier() {
                        @Override
                        public void notifyCountdown(final int step) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (step != 0) {
                                        showCountdown("Game will start in", "" + step);
                                    } else {
                                        updateTitleBar();
                                        hideMessageBox();
                                        gameThread.doStart();
                                        countdownRunning = false;
                                    }
                                }
                            });
                        }
                    })).start();
                } else if (gameThread.isGameInStatePaused() && !countdownRunning) {
                    countdownRunning = true;
                    new Thread(new CountdownRunnable(new CountdownRunnable.ICountdownNotifier() {
                        @Override
                        public void notifyCountdown(final int step) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (step != 0) {
                                        showCountdown("Game will resume in", "" + step);
                                    } else {
                                        hideMessageBox();
                                        gameThread.doStart();
                                        countdownRunning = false;
                                    }
                                }
                            });
                        }
                    })).start();
                }
                break;
            default:
                break;
        }
    }

    private void setupSoundPool() {
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                //loaded = true;
            }
        });
        deadSoundId = soundPool.load(this, R.raw.dead, 1);
        quackSoundIds = new int[2];
        quackSoundIds[0] = soundPool.load(this, R.raw.quack1, 1);
        quackSoundIds[1] = soundPool.load(this, R.raw.quack2, 1);
    }

    private void updateTitleBar() {
        gameThread.beatenWallsCounter = wallsBeaten;

        if (wallsBeaten > gameThread.highScore) {
            gameThread.highScore = wallsBeaten;
        }
    }

    @Override
    protected void onPause() {
        gameThread.pause(); // pause game when Activity pauses
        super.onPause();
    }

    @Override
    public void showMessage(String message) {
        messageTextView.setText(message);
        messageTextView.setVisibility(View.VISIBLE);
        countdownTextView.setVisibility(View.GONE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCountdown(String step) {
        countdownTextView.setText(step);
        messageTextView.setVisibility(View.GONE);
        countdownTextView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCountdown(String message, String step) {
        messageTextView.setText(message);
        countdownTextView.setText(step);
        messageTextView.setVisibility(View.VISIBLE);
        countdownTextView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMessageBox() {
        overlayView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void notifyPlayerFailed() {
        GameHandler.saveGameStats(wallsBeaten, this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameThread.setState(FlappyDuckSurfaceView2.FlappyDuckThread.STATE_READY);
                showMessage("GAME OVER!\nYour result: " + wallsBeaten);
                wallsBeaten = 0;
                updateTitleBar();
                soundPool.play(deadSoundId, 0.5f, 0.5f, 1, 0, 1f);
            }
        });
    }

    @Override
    public void notifyPlayerBeatsWall() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wallsBeaten++;
                updateTitleBar();
            }
        });
    }

    @Override
    public void notifyGamePaused() {
        if (!countdownRunning) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMessage("Game paused\nTap screen to resume\nClick back to quit");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (countdownRunning) {
            return;
        }
        if (gameThread.isGameInStateRunning()) {
            gameThread.pause();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void notifyJump() {
        int rand = Math.random() >= 0.5d ? 0 : 1;
        soundPool.play(quackSoundIds[rand], 0.5f, 0.5f, 1, 0, 1f);
    }
}
