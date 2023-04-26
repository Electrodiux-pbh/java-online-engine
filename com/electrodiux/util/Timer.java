package com.electrodiux.util;

public class Timer {

    private static int lastTimerId = 1;

    private float fpsValue = 0;
    private short timerFps = 0;

    private boolean running = false;
    private TimerHandler timerHandler = null;
    private Thread thread = null;

    private float deltaTime = 0;
    private float timeReference = 0;
    private float delta = 0;
    private long updateReference = 0;
    private long resetReference = 0;

    /**
     * Creates a new Timer object and synchronize the timer to 60fps calling method
     * sync
     */
    public Timer() {
        sync(60);

        lastTimerId++;
    }

    /**
     * Creates a new Timer object and synchronize the timer to the specific fps
     * calling {@link #sync(int)}
     */
    public Timer(int fps) {
        this();
        sync(fps);
    }

    /**
     * Creates a new Timer object and synchronize the timer to the specific fps
     * calling {@link #sync(int)} and
     * adds a default handler calling {@link #addHandler(TimerHandler)}
     */
    public Timer(TimerHandler handler, int fps) {
        this(fps);
        setHandler(handler);
    }

    /**
     * Creates a new Timer object with a default handler
     */
    public Timer(TimerHandler handler) {
        this();
        setHandler(handler);
    }

    /**
     * This method start the timer counts calling {@link Timer#start(boolean)} and
     * giving
     * for params true
     */
    public void start() {
        start(true);
    }

    /**
     * This method start the timer counts, if is allredy initialized the method will
     * return, and do nothing.
     * and if you give for params true, the execution will start on a new thread, in
     * case you
     * give a false, the method start on the current thread.
     * 
     * @param newThread
     */
    public void start(boolean newThread) {
        if (running)
            return;
        running = true;

        if (newThread) {
            thread = new Thread(getClass().getName() + ": " + lastTimerId) {

                public void run() {
                    runTimer();
                }

            };
            thread.start();
        } else {
            runTimer();
        }
    }

    /**
     * This method it's called when it's time to call all handlers
     */
    protected void update() {
        try {
            timerHandler.update();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    /**
     * This method calls to resetDeltas(), to reset deltas, and stop running while.
     * If a proccess not finished, it will finished
     * before the stop thread was closed
     */
    public void stop() {
        resetDeltas();
        running = false;
    }

    /**
     * This method resets all delta values, for restart timer calculations
     */
    public void resetDeltas() {
        deltaTime = 0;
        timeReference = 0;
        delta = 0;
        updateReference = System.currentTimeMillis();
        resetReference = System.currentTimeMillis() + 1000; // set current time and adds 1000 milliseconds
    }

    /**
     * This method synchronized dynamically, the fps of your timer. How much times
     * per second calls method update();
     * 
     * @param fps to sync
     */
    public void sync(float fps) {
        if (fps <= 0)
            return;
        fpsValue = 1000F / fps;
    }

    /**
     * This method sets a TimerHandler
     * 
     * @param handler to introduce
     */
    public void setHandler(TimerHandler handler) {
        timerHandler = handler;
    }

    /**
     * This method returns the value of delta time, this is how much time, pass in
     * to last fps and this fps. Can used to
     * calculate cooldowns. That example show how much blocks you need move that
     * second to move the blocksPerSecond sync.
     * <p>
     * int blocksPerSecond = 5;<br>
     * <br>
     * update() {<br>
     * move(blocksPerSecond * timer.getDeltaTime());<br>
     * }<br>
     * </p>
     * 
     * 
     * @return deltaTime value
     */
    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Return the times the timer updates in the last second
     * 
     * @return
     */
    public short getFps() {
        return timerFps;
    }

    /**
     * The default run method
     */
    private void runTimer() {

        resetDeltas();

        long startTime = 0;
        short fpsCounter = 0;

        while (running) {
            startTime = System.currentTimeMillis();

            timeReference = startTime - updateReference;
            updateReference = startTime;

            delta += timeReference / fpsValue;

            while (delta >= 1) {
                update();
                fpsCounter++;
                delta = 0;
            }

            long endTime = System.currentTimeMillis();

            if (resetReference <= endTime) {
                resetReference = endTime + 1000; // set current time and adds 1000 milliseconds
                timerFps = fpsCounter;
                deltaTime = 1F / getFps();
                fpsCounter = 0;
            }

            try {
                Thread.yield();
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

}
