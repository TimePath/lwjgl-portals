package com.timepath.lwjgl.util.drawable;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author TimePath
 */
public class Grid implements Drawable {

    float width, depth, spacing;
    
    public Grid() {
        this(40, 40, 10);
    }

    public Grid(float w, float x, float y) {
        this.width = w;
        this.depth = x;
        this.spacing = y;
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(float dt) {
        glColor3f(0.3f, 0.3f, 0.3f);
        glBegin(GL_LINES);
        {
            for (float x = -width; x <= width; x += spacing) {
                glVertex3f(x, 0, -depth);
                glVertex3f(x, 0, depth);
            }
            for (float z = -depth; z <= depth; z += spacing) {
                glVertex3f(-width, 0, z);
                glVertex3f(width, 0, z);
            }
        }
        glEnd();
    }

}
