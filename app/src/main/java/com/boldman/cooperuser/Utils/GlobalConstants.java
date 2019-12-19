package com.boldman.cooperuser.Utils;

public class GlobalConstants{

    //public static final String SERVER_HTTP_URL = "https://login.trycooper.com";
    public static final String SERVER_HTTP_URL = "http://192.168.1.8:8003";
    //public static final String SERVER_SOCKET_URL = "http://34.204.43.32:9090";
    public static final String SERVER_SOCKET_URL = "http://192.168.1.8:9090";
    //public static final int SERVER_PORT = 8003;
    //public static final int SERVER_SOCKET_PORT = 9090;
    public static final String SERVER_API = "api";

    /*  URL of Socket communication*/
    public static final String URL_CALLBACK = "callback";
    public static final String URL_INIT_CONNECT = "initialize";
    public static final String URL_USER_LOCATION_UPDATED = "user_location_updated";
    public static final String URL_NEAR_DRIVERS = "near_drivers";
    public static final String URL_REQUEST_RIDE = "request_ride";
    public static final String URL_RESPONSE_RIDE = "response_ride";
    public static final String URL_ACCEPTED_BY_DRIVER = "accepted_by_driver";
    public static final String URL_RIDE_DRIVER = "ride_driver";
    public static final String URL_AGREE_TO_DRIVER = "agree_to_driver";
    public static final String URL_CANCEL_RIDE = "cancel_ride";
    public static final String URL_CANCEL_USER_SELECT = "cancel_user_select";
    public static final String URL_CANCEL_USER_RIDE = "cancel_user_ride";
    public static final String URL_REQUEST_USER_ARRIVED = "request_user_arrived";
    public static final String URL_START_RIDE = "start_ride";
    public static final String URL_REQUEST_USER_PAY = "request_user_pay";
    public static final String URL_CONFIRM_USER_PAY = "confirm_user_pay";
    public static final String URL_REQUEST_USER_RATE = "request_user_rate";
    public static final String URL_CONFIRM_USER_RATE = "confirm_user_rate";
    public static final String URL_FINISH_RIDE = "finish_ride";

    public static final String stripePublishableKey="pk_test_9IoCaC1bKd9DUQ6x3E0YOTXa";

    public static String g_api_token;
    public static String g_email;
    public static String g_password;
    public static String g_sos_phone = "911";
    public static String g_sos_email_address = "gilcassar@gmail.com";

    public static int RIDE_STATUS_REQUESTED = 0;
    public static int RIDE_STATUS_CANCELED = 1;
    public static int RIDE_STATUS_ACCEPTED = 2;
    public static int RIDE_STATUS_ARRIVED = 3;
    public static int RIDE_STATUS_STARTED = 4;
    public static int RIDE_STATUS_USERENDED = 5;
    public static int RIDE_STATUS_DRIVERENDED = 6;
    public static int RIDE_STATUS_PAY = 7;
    public static int RIDE_STATUS_PAID = 8;
    public static int RIDE_STATUS_USERRATED = 9;
    public static int RIDE_STATUS_FINISHED = 10;

    public static String USER_QB_DEFAULT_PASSWORD = "quickblox_cooper";

    public static double VAT_FEE = 10;
}
