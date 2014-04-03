package com.timepath.lwjgl.util.drawable;

/**
 *
 * @author TimePath
 */
public interface Drawable {
    
    public void init();
    
    public void update(float dt);
    
    public void render(float dt);
    
}
