package com.timepath.lwjgl.util.drawable;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author TimePath
 */
public class Flat implements Drawable {

    float w, x, y;

    public Flat(float w, float x, float y) {
        this.w = w;
        this.x = x;
        this.y = y;
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(float dt) {
        glBegin(GL_QUADS);
        {
            glColor3d(0.0, 1.0, 0.0); // g
            glVertex3d(-w, y, -x);

            glColor3d(0.0, 0.0, 1.0); // b
            glVertex3d(-w, y, x);

            glColor3d(1.0, 1.0, 1.0); // w
            glVertex3d(w, y, x);

            glColor3d(1.0, 0.0, 0.0); // r
            glVertex3d(w, y, -x);
        }
        glEnd();
    }

}
