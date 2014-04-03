package com.timepath.lwjgl.util.drawable;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author TimePath
 */
public class Axes implements Drawable {

    public void render(float dt) {
        glLineWidth(5);
        glTranslatef(0, 0.01f, 0);
        glBegin(GL_LINES);
        {
            glColor3f(1, 0, 0);
            glVertex3f(1, 0, 0);
            glVertex3f(100, 0, 0);

            glColor3f(0, 1, 0);
            glVertex3f(0, 1, 0);
            glVertex3f(0, 100, 0);

            glColor3f(0, 0, 1);
            glVertex3f(0, 0, 1);
            glVertex3f(0, 0, 100);
        }
        glEnd();
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {

    }

}
