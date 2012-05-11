package com.feildmaster.controlorble.listeners;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.Assert;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.Test;

/**
 * After a recent blunder, I've decided to write this test to assert that all
 * event listener functions are proper handlers.
 *
 * @author Feildmaster
 */
public class TestListeners {
    private Class[] listeners = new Class[]{OrbListener.class};
    @Test
    public void testListeners() {
        for (Class clazz : listeners) {
            // Assert the class is even a listener
            Assert.assertTrue("Class: " + clazz.getSimpleName() +" does not implement Listener!", Listener.class.isAssignableFrom(clazz));

            for (Method method : clazz.getDeclaredMethods()) {
                // We only care about public functions.
                if (!Modifier.isPublic(method.getModifiers())) continue;
                // Don't mess with non-void
                if (!Void.TYPE.equals(method.getReturnType())) continue;
                // Only look for functions with 1 parameter
                if (method.getParameterTypes().length != 1) continue;

                // This is an event function...
                if (Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    // Make sure @EventHandler is present!
                    Assert.assertTrue(method.getName()+" is missing @EventHandler!", method.isAnnotationPresent(EventHandler.class));
                }
            }
        }
    }
}
