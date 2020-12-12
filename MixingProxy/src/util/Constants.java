package util;

public class Constants {

    public static String MIXING_PROXY_SERVER_NAME = "localhost";
    public static int MIXING_PROXY_PORT_NUMBER = 1100;

    public static String MATCHING_SERVER_NAME = "localhost";
    public static int MATCHING_PORT_NUMBER = 1200;

    // milliseconds
    public static long TIMER_PERIOD_FLUSH_UNINFORMED = 1000L * 10;

    // milliseconds
    public static long TIMER_DELAY = 1000L;

    // seconds
    public static long CAPSULE_FLUSH_DELAY = 0L;

    // seconds
    public static long CAPSULE_VISITOR_FLUSH_DELAY = 60L;

    // seconds
    // 10 min of safety margin when a visitor pushes the leave button
    public static long SAFETY_MARGIN = 0;

    public static String PUBLIC_KEY_DIR_NAME = "public_keys";
    public static String MIXING_PROXY_DIR_NAME = "dir_mixing_proxy";
}
