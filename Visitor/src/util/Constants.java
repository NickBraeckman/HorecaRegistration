package util;

public class Constants {
    public static String REGISTRAR_SERVER_NAME = "localhost";
    public static int REGISTRAR_PORT_NUMBER = 1000;

    public static String MIXING_PROXY_SERVER_NAME = "localhost";
    public static int MIXING_PROXY_PORT_NAME = 1100;

    public static String MATCHING_SERVER_NAME = "localhost";
    public static int MATCHING_PORT_NUMBER = 1200;

    // milliseconds
    public static long TOKEN_TIMER_PERIOD = 1000L;

    // milliseconds
    public static long TOKEN_TIMER_DELAY = 1000L;

    // milliseconds
    public static long CRITICAL_TUPLE_TIMER_PERIOD = 1000L ; //1000L * 60L * 60L * 24L;

    // milliseconds
    public static long CRITICAL_TUPLE_TIMER_DELAY = 1000L;

    // sec
    public static final long CAPSULE_VISITOR_FLUSH_DELAY = 60L;

    // 10 min of safety margin when a visitor pushes the leave button
    public static int SAFETY_MARGIN = 0;

    public static String PUBLIC_KEY_DIR_NAME = "public_keys";
}
