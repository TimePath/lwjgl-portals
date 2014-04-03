package com.timepath.lwjgl.util.drawable;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 *
 * @author TimePath
 */
public class Portal {

    private static int AUTO_ID = 0;
    private final int id;

    public float size = 32;

    public Vector3f pos, pos_dest;

    public float rotY;
    public Portal dest;

    public Vector4f color = new Vector4f(.5f, .5f, .5f, 0.75f);

    public Portal(Vector3f pos, float rotY) {
        this.pos = pos;
        this.rotY = rotY;
        this.id = AUTO_ID++;
    }

    static double radToDeg = 57.295779513082320876798154814114;
    static double degToRad = 0.0174532925;

    public static void link(Portal p1, Portal p2) {
        p1.pos_dest = p2.pos;
        p1.dest = p2;
        p1.color = new Vector4f(1, .5f, 0, 0.75f);
//        p1.color = new Vector4f(1, 0, 0, 0.2f);

        p2.pos_dest = p1.pos;
        p2.dest = p1;
        p2.color = new Vector4f(0, .5f, 1, 0.75f);
//        p2.color = new Vector4f(0, 0, 1, 0.2f);
    }

    /**
     * Start drawing from this portal
     */
    private void moveTo() {
        glTranslatef(pos.x, pos.y, pos.z);
        glRotatef(rotY, 0, 1, 0); // TODO: 3D rotation
    }

    public void calcInternal() {
//        glTranslatef(portal_view_to[i].x, 0, portal_view_to[i].z);

        switch (id) {
            case 0:
                glTranslatef(0, 0, -80);
                break;
            case 1:
                glTranslatef(-80, 0, 0);
                break;
            case 2:
                glTranslatef(0, 0, 80);
                break;
            case 3:
                glTranslatef(80, 0, 0);
                break;
        }
        // go to portal cam location
//        glTranslatef(-portal_view_from[other(i)].x, 0, -portal_view_from[other(i)].z);glRotatef(-portal_view_to_ang[i], 0, 1, 0);
        glRotatef(this.dest.rotY - this.rotY, 0, 1, 0);
    }

    private static final Disk DISK_RENDERER = new Disk();
    
    public void draw() {
        glPushMatrix();
        {
            moveTo();

            glColor4f(color.x, color.y, color.z, color.w);

            float radius = (float) (size * 0.5 + 1);

            DISK_RENDERER.draw(0, radius, 50, 2);
        }
        glPopMatrix();
    }

    /**
     * Draw portal outline
     *
     * @param flag is internal (if so, offset slightly)
     */
    public void outline(boolean flag) {
        glPushMatrix();
        {
            moveTo();
            if (flag) {
                glTranslatef(0, 0, 0.05f);
            }

            glColor4f(color.x, color.y, color.z, color.w);

            float radius = size / 2 + 1;
            glBegin(GL_LINE_LOOP);
            {
                for (int i = 0; i <= 360; i++) {
                    double degInRad = i * degToRad;
                    glVertex2d(cos(degInRad) * radius, sin(degInRad) * radius);
                }
            }
            glEnd();
        }
        glPopMatrix();
    }

}
