package com.timepath.lwjgl.util.app;

import com.timepath.lwjgl.util.Camera;
import com.timepath.lwjgl.util.drawable.TrueTypeFont;
import com.timepath.lwjgl.util.drawable.Grid;
import com.timepath.lwjgl.util.drawable.Axes;
import com.timepath.lwjgl.util.drawable.Gears;
import com.timepath.lwjgl.util.drawable.Flat;
import com.timepath.lwjgl.util.drawable.ShadedGears;
import java.awt.Font;
import org.lwjgl.LWJGLException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public abstract class SimpleGame extends Game {

    /**
     * The mouse cursor position
     */
    protected static int mouse_x;
    protected static int mouse_y;

    protected TrueTypeFont trueTypeFont;

    @Override
    protected void display() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(300, 300));
        Display.setTitle("SimpleGame");
        Display.create();
    }

    @Override
    protected void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1);
//        Display.setVSyncEnabled(true);

        Mouse.setGrabbed(true);
        Mouse.setClipMouseCoordinatesToWindow(false);

        Font font = new Font("Monospaced", Font.BOLD, 16);
        trueTypeFont = new TrueTypeFont(font, true);

        gears.init();

        cam.add(0, 10, 40);

        // GL params
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }

    protected Camera cam = new Camera(0, 0, 0);

    float movementSpeed = 30.0f; // move 10 units per second

    protected float frames, fps;

    long lastFPS = getTime();

    @Override
    protected void update(float dt) {
        processKeyboard(dt);
        processMouse();

        if (getTime() - lastFPS > 1000) {
            fps = frames;
//            Display.setTitle("FPS: " + fps);
            frames = 0;
            lastFPS += 1000;
        }
        frames++;
//        Display.sync(100);
        simpleUpdate(dt);
    }

    protected void simpleUpdate(float dt) {
        gears.update(dt);
    }

    /**
     * Reset GL_PROJECTION, switch to ortho, switch to GL_MODELVIEW
     */
    public void draw2D() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glOrtho(0, 800, 600, 0, -100, 50);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    /**
     * Reset GL_PROJECTION, switch to perspective, switch to GL_MODELVIEW
     */
    public void draw3D() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        gluPerspective(45.0f,
                (float) Display.getDisplayMode().getWidth()
                / (float) Display.getDisplayMode().getHeight(),
                0.1f,
                10000.0f);

        glMatrixMode(GL_MODELVIEW);
//        glLoadIdentity();
    }

    @Override
    protected void render(float dt) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glLoadIdentity();

        glPushMatrix();
        {
            this.draw3D();
            cam.lookThrough();
            renderWorld(dt);
        }
        glPopMatrix();
        glPushMatrix();
        {
            this.draw2D();
            renderGUI(dt);
        }
        glPopMatrix();
    }

    protected Axes axes = new Axes();
    protected Gears gears = new ShadedGears();
    protected Flat floor = new Flat(100, 100, 0);
    protected Grid grid = new Grid(40, 40, 10);

    protected void renderWorld(float dt) {
        // opaque

        glPushMatrix();
        {
            glTranslatef(0, 0.1f, 0);
            grid.render(dt);
        }
        glPopMatrix();

        glPushMatrix();
        {
            floor.render(dt);
        }
        glPopMatrix();

        glPushMatrix();
        {
            glTranslatef(0, 0.1f, 0);
            axes.render(dt);
        }
        glPopMatrix();

        // translucent
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        {
            glPushMatrix();
            {
                glTranslatef(-20, 10, 0);
                gears.render(dt);
            }
            glPopMatrix();
        }
        glDisable(GL_BLEND);
    }

    protected void renderGUI(float dt) {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        {
            glColor3f(1, 1, 1);
            trueTypeFont.drawString(Display.getWidth() - 4, trueTypeFont.getHeight(), "fps: " + fps, 1f, -1f, TrueTypeFont.ALIGN_RIGHT);
        }
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glPushMatrix();
        {
            glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
            glRotatef(cam.getPitch(), 1.0f, 0.0f, 0.0f);
            glRotatef(-cam.getYaw() + 180, 0.0f, 1.0f, 0.0f);
            glRotatef(180, 0.0f, 0.0f, 1.0f);
            axes.render(dt);
        }
        glPopMatrix();
    }

    /**
     * Processes keyboard input
     */
    private void processKeyboard(float dt) {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            cam.walkForward(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            cam.walkBackwards(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            cam.strafeLeft(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            cam.strafeRight(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            cam.rise(movementSpeed * dt);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
            cam.rise(-movementSpeed * dt);
        }

        while (Keyboard.next()) {
            pressed(Keyboard.getEventKey(), Keyboard.getEventKeyState());
        }
    }

    protected void pressed(int eventKey, boolean state) {
        if (state) {
            switch (eventKey) {
                case Keyboard.KEY_SPACE:
                    Mouse.setGrabbed(!Mouse.isGrabbed());
                    break;
            }
        }
    }

    private void processMouse() {
        int newx = Mouse.getX();
        int newy = Mouse.getY();
        int dx = newx - mouse_x;
        int dy = newy - mouse_y;
        mouse_x = newx;
        mouse_y = newy;

        float m_yaw = 0.022f, m_pitch = 0.022f, sensitivity = 3f; // Familiar

        cam.yaw(dx * m_yaw * sensitivity);
        cam.pitch(-dy * m_pitch * sensitivity);

        while (Mouse.next()) {
            int button = Mouse.getEventButton();
            if (button >= 0 && button < 3 && Mouse.getEventButtonState()) {
//                mouse_btn = Mouse.getEventButton();
//                switchCursor();
            }
        }
    }

    /**
     * Cleans up the test
     */
    @Override
    protected void cleanup() {
        try {
            Mouse.setNativeCursor(null);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        trueTypeFont.destroy();
        Display.destroy();
    }

}
