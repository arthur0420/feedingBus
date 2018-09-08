package arthur.feedingControl.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import arthur.feedingControl.main.LaunchProcessor;

/**
 * Application Lifecycle Listener implementation class LaunchListener
 *
 */
@WebListener
public class LaunchListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public LaunchListener() {
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	LaunchProcessor launchProcessor = new LaunchProcessor();
    	launchProcessor.start();
    }
	
}
