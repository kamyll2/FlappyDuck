package com.wygralak.flappyduck;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wygralak.flappyduck.Engine.FlappyDuckSurfaceView2;
import com.wygralak.flappyduck.Engine.IGameStateHolder;

public class FlappyActivity extends ActionBarActivity implements IMessageViewer, IGameStateHolder {

    RelativeLayout overlayView;
    TextView messageTextView;
    TextView countdownTextView;

    FlappyDuckSurfaceView2 surfaceView;
    private FlappyDuckSurfaceView2.FlappyDuckThread gameThread;
    private boolean countdownRunning;

    private int wallsBeaten = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flappy);

        overlayView = (RelativeLayout) findViewById(R.id.overlay);
        messageTextView = (TextView) findViewById(R.id.text);
        countdownTextView = (TextView) findViewById(R.id.countdownHolder);
        surfaceView = (FlappyDuckSurfaceView2) findViewById(R.id.pitch);
        surfaceView.setRefree(this);
        gameThread = surfaceView.getThread();

        overlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    private void updateTitleBar() {
        getSupportActionBar().setTitle("FlappyDuck\t\t\t Walls beaten: " + wallsBeaten);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameThread.setState(FlappyDuckSurfaceView2.FlappyDuckThread.STATE_READY);
                showMessage("GAME OVER!\nYour result: " + wallsBeaten);
                wallsBeaten = 0;
                updateTitleBar();
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
}
