package net.dodian.cache.map;

import net.dodian.cache.object.CacheObject;

/**
 * A landscape listener is notified when an object is parsed by a
 * <code>LandscapeParser</code>.
 *
 * @author Graham Edgecombe
 */
public interface LandscapeListener {

    /**
     * Handles actions when an object is parsed.
     *
     * @param obj The object that was parsed.
     */
    public void objectParsed(CacheObject obj);
}