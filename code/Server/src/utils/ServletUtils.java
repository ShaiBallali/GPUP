package utils;

import constants.Constants;
import engine.managers.MainManager;
import engine.managers.users.UserManager;
import engine.managers.users.WorkerManager;
import jakarta.servlet.ServletContext;

public class ServletUtils {
    public static final Object registrationLocked = new Object();
    public static final Object runningTargetLocked = new Object();

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String WORKER_MANAGER_ATTRIBUTE_NAME = "workerManager";
    private static final String MAIN_MANAGER_ATTRIBUTE_NAME = "mainManager";

    public static Object getRunningTargetLocked () { return runningTargetLocked; }
    public static Object getRegistrationLocked () { return registrationLocked; }

    private static final Object userManagerLock = new Object();
    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }


    private static final Object workerManagerLock = new Object();
    public static WorkerManager getWorkerManager(ServletContext servletContext) {

        synchronized (workerManagerLock) {
            if (servletContext.getAttribute(WORKER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(WORKER_MANAGER_ATTRIBUTE_NAME, new WorkerManager());
            }
        }
        return (WorkerManager) servletContext.getAttribute(WORKER_MANAGER_ATTRIBUTE_NAME);
    }


    private static final Object mainManagerLock = new Object();
    public static MainManager getMainManager (ServletContext servletContext) {
        synchronized (mainManagerLock) {
            if (servletContext.getAttribute(MAIN_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(MAIN_MANAGER_ATTRIBUTE_NAME, new MainManager(Constants.ROOT_DIRECTORY));
            }
        }
        return (MainManager) servletContext.getAttribute((MAIN_MANAGER_ATTRIBUTE_NAME));
    }
}
