package com.timepath.lwjgl.util.app;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

/**
 *
 * @author TimePath
 */
public abstract class Game {

    /**
     * Executes the test
     */
    public void start() {
        try {
            display();
            init();
        } catch (LWJGLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        mainLoop();

        cleanup();
    }
    
    protected abstract void display() throws LWJGLException;

    protected abstract void init();
    
    protected long getTime() {
        return (Sys.getTime() * 1000L) / Sys.getTimerResolution();
    }

    protected void mainLoop() {
        float dt;
        long time, lastTime = 0;

        while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) {
            time = getTime();
            dt = (time - lastTime) / 1000f;
            lastTime = time;

            Display.update();

            if (Display.isVisible()) {
                update(dt);
                render(dt);
            } else {
                // no need to render/paint if nothing has changed (ie. window dragged over)
                if (Display.isDirty()) {
                    render(dt);
                }

                // don't waste cpu time, sleep more
                try {
                    Thread.sleep(100);
                } catch (InterruptedException inte) {
                }
            }
        }
    }

    protected abstract void cleanup();

    protected abstract void update(float dt);

    protected abstract void render(float dt);

}
