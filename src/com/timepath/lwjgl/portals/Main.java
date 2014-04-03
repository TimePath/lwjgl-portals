package com.timepath.lwjgl.portals;

import com.timepath.lwjgl.util.drawable.Portal;
import com.timepath.lwjgl.util.app.PortalAwareGame;
import com.timepath.lwjgl.util.app.Game;
import java.util.Arrays;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.vector.Vector3f;

/**
 * TODO:
 *  RTT after depth n
 * @author TimePath
 */
public class Main extends PortalAwareGame {

    public static void main(String[] args) throws LWJGLException {
        Game g = new Main();
        g.start();
    }

    //<editor-fold defaultstate="collapsed" desc="Init">
    @Override
    protected void init() {
        super.init();
        
        cam.add(0, 0, 80);

        Portal[] portals = {
            new Portal(new Vector3f(0, 10, -40), 0),
            new Portal(new Vector3f(-40, 10, 0), 90),
//            new Portal(new Vector3f(0, 10, 40), 180),
//            new Portal(new Vector3f(40, 10, 0), 270)
        };
        Portal.link(portals[0], portals[1]);
//        Portal.link(portals[2], portals[3]);

        portalList.addAll(Arrays.asList(portals));
        
//        Portal.link(portals[0], portals[2]);
//        portalList.remove(1);
    }
    //</editor-fold>

}
