package specificstep.com.perfectrecharge_dist.GlobalClasses;

/**
 * Created by ubuntu on 12/1/17.
 */

public class URL {

    public static String base_url = "http://www.perfectrecharge.in/webservices/";
    //private static final String base_url = "http://chagtelecom.in/webservices/"; //local
    //public static String base_url = "http://192.168.30.117:8026/webservices/";

   //public static String register = base_url + "register";
    public static String company = base_url + "company";
    public static String product = base_url + "product";
    public static String state = base_url + "state";
    public static String login = base_url + "login";
    public static String forgot_password = base_url + "forgotpassword";
    public static String forgot_password_otp = base_url + "forgototp";
    public static String balance = base_url + "balance";
    public static String setting = base_url + "setting";

    public static String child_user = base_url + "getchild";
    public static String add_balance = base_url + "addbalancenew";
    public static String cash_users = base_url + "getchilduser";
    public static String cash_summary = base_url + "cashsummarynew";

    //for purchase user
    public static String purchase_user_schemetype = base_url + "purchasetype";
    public static String purchase_user_schemename = base_url + "purchaseidscheme";
    public static String purchase_user_submit = base_url + "purchaseid";

    //for Add User
    public static String add_user_type = base_url + "usertype";
    public static String add_user_scheme = base_url + "userscheme";
    public static String add_user_register = base_url + "userregister";

    public static String changePassword = base_url + "changepass";

    // 2017_05_02 - get parent user details URL
    public static String GET_PARENT_USER_DETAILS = base_url + "getparent";

    // 2017_05_09 - reverse payment
    public static String GET_REVERSE_PAYMENT = base_url + "reverse";

    public static String accountLedger = base_url + "accounts";

}
