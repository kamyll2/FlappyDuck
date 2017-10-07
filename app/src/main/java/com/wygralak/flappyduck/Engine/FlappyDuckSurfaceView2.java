package com.wygralak.flappyduck.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInterpreter;
import com.wygralak.flappyduck.ColissionUtils.ICollisionInvoker;
import com.wygralak.flappyduck.R;
import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-10-02.
 */
public class FlappyDuckSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback {
    private final Bitmap bitmap;

    public class FlappyDuckThread extends Thread implements ICollisionInterpreter {

        /*
         * State-tracking constants
         */
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_PLAYER_FAILED = 5;

        /*
         * Member (state) fields
         */

        /**
         * Current height of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;

        /**
         * Current width of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;

        /**
         * Used to figure out elapsed time between frames
         */
        private long mLastTime;

        /**
         * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
         */
        private int mMode;

        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean mRun = false;

        private final Object mRunLock = new Object();

        /**
         * Handle to the surface manager object we interact with
         */
        private SurfaceHolder mSurfaceHolder;

        /**
         * Engine objects
         */
        //private List<DuckWall> duckWalls;
        private DuckWall duckWall;
        private DuckEngine duckEngine;

        /**
         * FlappyDuck self objects
         */
        public FlappyDuckThread(SurfaceHolder surfaceHolder, Context context) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mContext = context;

            generateDuckWalls();
            duckEngine = new DuckEngine();

            duckEngine.addColissionable(this);
            duckEngine.addColissionable(duckWall);
            //ballEngine.addColissionables(duckWalls);todo
        }

        private void generateDuckWalls() {
            /*pitchWalls = new ArrayList<>();todo
            pitchWalls.add(new NorthLeftPitchWall());
            pitchWalls.add(new NorthRightPitchWall());
            pitchWalls.add(new SouthLeftPitchWall());
            pitchWalls.add(new SouthRightPitchWall());
            pitchWalls.add(new WestUpPitchWall());
            pitchWalls.add(new WestDownPitchWall());
            pitchWalls.add(new EastUpPitchWall());
            pitchWalls.add(new EastDownPitchWall());*/
            duckWall = new DuckWall();
        }

        public boolean isGameInStateReady() {
            return mMode == STATE_READY;
        }

        public boolean isGameInStateRunning() {
            return mMode == STATE_RUNNING;
        }

        public boolean isGameInStatePaused() {
            return mMode == STATE_PAUSE;
        }

        @Override
        public boolean checkForCollision(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
            return y + DuckEngine.DUCK_RADIUS > mCanvasHeight ||
                    y - DuckEngine.DUCK_RADIUS < 0;
        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                //TODO inicjalizacja pozycji

                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) {
                    setState(STATE_PAUSE);
                }
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);
                //TODO restore state
            }
        }

        private void gameOver() {
            setState(STATE_PLAYER_FAILED);
            setDefaultPositions();
            duckEngine.setDefaultSpeed();
            mGameState.notifyPlayerFailed();
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) updatePhysics();
                        // Critical section. Do not allow mRun to be set false until
                        // we are sure all canvas draw operations are complete.
                        //
                        // If mRun has been toggled false, inhibit canvas operations.
                        synchronized (mRunLock) {
                            if (mRun) doDraw(c);
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                //TODO save state
            }
            return map;
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            // Do not allow mRun to be modified while any canvas operations
            // are potentially in-flight. See doDraw().
            synchronized (mRunLock) {
                mRun = b;
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @see #setState(int, CharSequence)
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode    one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder) {
                mMode = mode;

                if (mMode == STATE_PAUSE) {
                    mGameState.notifyGamePaused();
                }
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                setDefaultPositions();
                updatePitchWallSizes(mCanvasWidth, mCanvasHeight);
            }
        }

        private void setDefaultPositions() {
            duckEngine.setupDefaultPosition(mCanvasWidth, mCanvasHeight);
        }

        private void updatePitchWallSizes(int pitchWidth, int pitchHeight) {
            /*for (int i = 0; i < pitchWalls.size(); i++) {todo
                pitchWalls.get(i).updateSize(pitchWidth, pitchHeight);
            }*/
            duckWall.updateSize(pitchWidth, pitchHeight);
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }

        boolean doTouchEvent(MotionEvent event) {
            boolean handled = true;
            if (mMode == STATE_RUNNING) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        doJump();
                        break;
                }
            } else {
                handled = false;
            }
            return handled;
        }

        private void doJump() {
            duckEngine.updateVector(new Vector2(0f, -1f));
        }

        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
            canvas.drawColor(Color.GREEN);
            if (duckEngine != null) {
                //canvas.drawCircle(duckEngine.getCurrentX(), duckEngine.getCurrentY(), BallEngine.BALL_RADIUS, BallEngine.BALL_PAINT);
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        new Rect((int) (duckEngine.getCurrentX() - DuckEngine.DUCK_RADIUS),
                                (int) (duckEngine.getCurrentY() - DuckEngine.DUCK_RADIUS),
                                (int) (duckEngine.getCurrentX() + DuckEngine.DUCK_RADIUS),
                                (int) (duckEngine.getCurrentY() + DuckEngine.DUCK_RADIUS)), null);
            }
            drawPitchWalls(canvas);
        }

        private void drawPitchWalls(Canvas canvas) {
            /*for (int i = 0; i < pitchWalls.size(); i++) {todo
                canvas.drawRect(pitchWalls.get(i), pitchWalls.get(i).getCurrentPaint());
            }*/
            canvas.drawRect(duckWall, duckWall.getCurrentPaint());
        }

        /**
         * Figures the lander state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void updatePhysics() {
            long now = System.currentTimeMillis();

            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) return;

            double elapsed = (now - mLastTime) / 1000.0;

            double ratio = elapsed / 0.015d;
            duckEngine.updatePosition(ratio);
            duckWall.updatePosition(ratio);
            if (duckEngine.checkForCollisions()) {
                gameOver();
            }

            mLastTime = now;
        }
    }

    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;

    /**
     * The thread that actually draws the animation
     */
    private FlappyDuckThread thread;

    private IGameStateHolder mGameState;

    public FlappyDuckSurfaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new FlappyDuckThread(holder, context);

        setFocusable(true); // make sure we get key events
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.duck);
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public FlappyDuckThread getThread() {
        return thread;
    }

    public void setRefree(IGameStateHolder refree) {
        this.mGameState = refree;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }


    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
        thread.setState(FlappyDuckThread.STATE_READY);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();

    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}