package Util;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class Constants {

    // global constants
    public final static int REFRESH_RATE = 1000;
    public final static int DELAY_RATE = 0;
    public final static String DARK_THEME_SKIN = "Dark Theme";
    public final static String DEFAULT_SKIN = "Default";

    //http
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient();


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    public final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/gpup";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    //public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String GRAPH_LIST = FULL_SERVER_PATH + "/graph-list";
    public final static String EXECUTION_LIST = FULL_SERVER_PATH + "/execution-list";
    public final static String FIND_PATH = FULL_SERVER_PATH + "/find-path";
    public final static String WHAT_IF = FULL_SERVER_PATH + "/what-if";
    public final static String DETECT_CIRCLE = FULL_SERVER_PATH + "/detect-circle";
    public final static String CREATE_EXECUTION = FULL_SERVER_PATH + "/create-execution";
    public final static String CREATE_DUP_EXECUTION = FULL_SERVER_PATH + "/create-dup-execution";
    public final static String SET_STATUS = FULL_SERVER_PATH + "/set-execution-status";
    public final static String RUN_TIME = FULL_SERVER_PATH +"/run-time-details";
    public final static String RUN_RESULT = FULL_SERVER_PATH +"/run-result";
    public final static String IS_VALID_NAME = FULL_SERVER_PATH +"/is-valid-name";
    public final static String CREATE_GRAPHVIZ = FULL_SERVER_PATH + "/create-graphviz";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
