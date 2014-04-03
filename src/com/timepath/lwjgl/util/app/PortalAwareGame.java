package com.timepath.lwjgl.util.app;

import com.timepath.lwjgl.util.drawable.Portal;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.PixelFormat;

/**
 * TODO: RTT (use in deeper levels?)
 * <br>
 * TODO: Back face (double stencil bits, use mask?)
 * <br>
 * TODO: auto-calculate rotation and position
 * <br>
 * To get the position and angle of the virtual camera:
 * <br>
 * >Take the distance and angle (the transformation matrix):
 * <br>
 * >>from the camera to the source portal
 * <br>
 * >>apply the inverse of that to the destination portal
 * <br>
 * TODO: oblique view frustum near-plane clipping
 * <br>
 * (https://bitbucket.org/ThomasRinsma/opengl-game-test/src/77/src/sceneobject/portal/clippedProjMat.cc)
 *
 * @author TimePath
 */
public abstract class PortalAwareGame extends SimpleGame {

    protected ArrayList<Portal> portalList = new ArrayList<>();

    @Override
    protected void display() throws LWJGLException {
        //<editor-fold defaultstate="collapsed" desc="Create display">
        int w = 800, h = 600;
        Display.setLocation((Display.getDisplayMode().getWidth() - w) / 2, (Display.getDisplayMode().getHeight() - h) / 2);
        Display.setDisplayMode(new DisplayMode(w, h));
        Display.setTitle("Portals");
        PixelFormat pf = new PixelFormat().withStencilBits(8);//.withSamples(8);
        Display.create(pf);
        //</editor-fold>
    }

    protected void rtt() {
//Create an FBO and set it as the current render target
//Generate the virtual camera’s view matrix using the view frustum clipping method.
//Render the scene from the viewpoint of the virtual camera, this will fill the FBO with the rendered frame.
//Render the scene normally from the regular camera but apply the newly generated texture from the FBO to the portal’s frame.
    }

    float time;

    @Override
    protected void pressed(int eventKey, boolean state) {
        super.pressed(eventKey, state);
        if (state) {
            switch (eventKey) {
                case Keyboard.KEY_F:
                    dumping = true;
                    break;
            }
        }
    }

    @Override
    protected void update(float dt) {
        super.update(dt);
        time = (time + dt) % (portalList.size() * (max_depth + 1));
    }

    //<editor-fold defaultstate="collapsed" desc="Dumping">
    static boolean dumping;
    static int dump;

    static void dump(int format, String s) {
        int bpp;
        switch (format) {
            case GL_STENCIL_INDEX:
                bpp = 1;
                break;
            default:
                bpp = 4;
        }
        dump(format, 800, 600, bpp, s);
    }

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    static void dump(int format, final int width, final int height, final int bpp, final String s) {
        if (!dumping) {
            return;
        }

        final ByteBuffer buf = read(format, width, height, bpp);
        final int n = dump++;
        threadPool.submit(
                new Runnable() {

                    @Override
                    public void run() {
                        write(save(buf, width, height, bpp), n + " " + s);
                    }

                });

    }

    static ByteBuffer read(int format, int width, int height, int bpp) {
        // glReadBuffer(GL_FRONT); // finished only
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        glReadPixels(0, 0, width, height, format, GL_UNSIGNED_BYTE, buffer);
        return buffer;
    }

    static BufferedImage save(ByteBuffer buffer, int width, int height, int bpp) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (x + (width * y)) * bpp;
                int r = (buffer.get(i) & 0xFF);
                int g, b;
                if (bpp == 1) {
                    r *= 64;
                    g = r;
                    b = r;
                } else {
                    g = buffer.get(i + 1) & 0xFF;
                    b = buffer.get(i + 2) & 0xFF;
                }
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return image;
    }

    static void write(BufferedImage image, String s) {
        try {
            ImageIO.write(image, "PNG", new File("dump/" + s + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    static void drawShape(Portal p) {
        glPushMatrix();
        {
            p.draw(); // Draw portal disk to stencil buffer
        }
        glPopMatrix();
    }

    void drawWorld() {
        glPushMatrix();
        {
            renderNonPortals(0);
        }
        glPopMatrix();
    }

    /**
     * Requires depth writing
     */
    void fill_screen() {
        glDisable(GL_DEPTH_TEST); // Disable depth testing.
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        {
            glLoadIdentity();
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
//        Draw a quad that covers the entire screen and whose Z is near your far plane.
            int z = -1;
            glColor3f(1, 0, 0);
            glBegin(GL_QUADS);
            {
                glVertex3i(-1, -1, z);
                glVertex3i(1, -1, z);
                glVertex3i(1, 1, z);
                glVertex3i(-1, 1, z);
            }
            glEnd();
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);
        }
        glPopMatrix();
        glEnable(GL_DEPTH_TEST);
    }

    void clampStencil(int max) {
        glStencilMask(0xFF); // enable writing
        glStencilFunc(GL_GEQUAL, max, 0xFF); // passes if max is >= s
        glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP); // replace with ref on fail
        glColorMask(false, false, false, false); // disable color mask

        fill_screen();

        dump(GL_STENCIL_INDEX, "clamp:" + max);
    }

    static void buffers(boolean stencil, boolean depth, boolean color) {
        if (stencil) {
            glStencilMask(0xFF); // enable writing
            glStencilFunc(GL_NEVER, 1, 0xFF); // always fail
            glStencilOp(GL_INCR, GL_INCR, GL_INCR); // increment failed pixels
        } else {
            glStencilMask(0x00); // Disable writing
            glStencilFunc(GL_ALWAYS, 0, 0xFF); // never fail
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP); // ignore failed pixels
        }
        glDepthMask(depth);
        glColorMask(color, color, color, color);
    }

    /**
     * 0 = don't draw inside portals 1 = draw 1 level inside portals
     */
    protected int max_depth = 2;

    private void recurse(int depth, Portal ignore) {
        if (depth < max_depth) {
            int pNum = 0;
            for (Portal p : portalList) {
                if (p == ignore) {
                    continue; // perf
                }
                // stencil per portal
                buffers(true, false, false);
                glStencilFunc(GL_NOTEQUAL, depth, 0xFF); // passes if outside
                glStencilOp(GL_INCR, GL_KEEP, GL_KEEP); // increment on fail
                drawShape(p);
                dump(GL_STENCIL_INDEX, "p" + pNum++ + ",d" + depth);

                // draw inside
                buffers(false, true, true);
                glStencilFunc(GL_EQUAL, depth + 1, 0xFF); // passes if in portal
                glPushMatrix();
                {
                    p.calcInternal();
                    recurse(depth + 1, p.dest);
                }
                glPopMatrix();

                // clear temp stencil
                clampStencil(depth);
            }
        }

        //<editor-fold defaultstate="collapsed" desc="stencil all">
        buffers(true, false, false);

        // draw within current portal
        glStencilFunc(GL_NOTEQUAL, depth, 0xFF); // passes if outside
        glStencilOp(GL_INCR, GL_KEEP, GL_KEEP); // increment on fail

        for (Portal p : portalList) {
            drawShape(p);
        }
        dump(GL_STENCIL_INDEX, "all,d" + depth);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="fix depth">
        buffers(false, true, depth == max_depth);
        // Passes if ref & mask < stencil & mask
        glStencilFunc(GL_LESS, depth, 0xFF);

        glClearDepth(1);
        glClear(GL_DEPTH_BUFFER_BIT); // clear portal depth
        for (Portal p : portalList) {
            drawShape(p);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="outline">
        glColorMask(true, true, true, true);
        glStencilFunc(GL_LEQUAL, depth, 0xFF); // allow fx outside of internal
        for (Portal p : portalList) {
            p.outline(p == ignore);
        }
        //</editor-fold>

        buffers(false, true, true);
        glStencilFunc(GL_LEQUAL, depth, 0xFF); // pass if depth <= stencil

        drawWorld();
        dump(GL_RGBA, "world,d" + depth);
    }

    /**
     * Portal aware render implementation.
     *
     * @param dt
     */
    @Override
    protected void render(float dt) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        {
            this.draw3D();
            cam.lookThrough();

            //<editor-fold defaultstate="collapsed" desc="Clear between frames">
            glClearStencil(0); // set clear value
            glStencilMask(0xFF); // enable writing
            glStencilFunc(GL_ALWAYS, 0, 0xFF); // always pass
            glDisable(GL_STENCIL_TEST); // disable checks
            glClear(GL_STENCIL_BUFFER_BIT); // stencil buffer is clear
            //</editor-fold>

            glEnable(GL_STENCIL_TEST);
            recurse(0, null);
            glDisable(GL_STENCIL_TEST);

            dumping = false;
            dump = 0;

        }
        glPopMatrix();

        glPushMatrix();
        {
            this.draw2D();
            renderGUI(dt);
        }
        glPopMatrix();
    }

    private void renderNonPortals(float dt) {
        glPushMatrix();
        {
            this.draw3D();
            renderWorld(dt);
        }
        glPopMatrix();

    }

}
