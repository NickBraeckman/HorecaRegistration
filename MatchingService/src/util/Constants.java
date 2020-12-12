package util;

public class Constants {

    public static String REGISTRAR_SERVER_NAME = "localhost";
    public static int REGISTRAR_PORT_NUMBER = 1000;

    public static String MATCHING_SERVER_NAME = "localhost";
    public static int MATCHING_PORT_NUMBER = 1200;

    // milliseconds
    public static long TIMER_PERIOD = 1000L * 30L;//1000L * 60L * 60L * 24L;

    // milliseconds
    public static long TIMER_DELAY = 1000L;

    // seconds
    public static long CAPSULE_FLUSH_DELAY = 60L * 60L * 48L;

    // seconds
    // time that the user has to ACK notification of infection
    public static long UNINFORMED_TOKEN_FORWARD_DELAY = 60L;

    public static String PUBLIC_KEY_DIR_NAME = "public_keys";
    public static String MATCHING_SERVICE_DIR_NAME = "dir_matching_service";
}
