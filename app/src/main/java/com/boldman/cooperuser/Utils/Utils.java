package com.boldman.cooperuser.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.boldman.cooperuser.Model.AccessDetails;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /*  hide keyboard from window*/
    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //  Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //  If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*  check validation email address.*/
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*  check validation password*/
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        boolean status = matcher.matches();

        return status;
    }

    public static void displayMessage(Activity activity, String toastString) {

        try {

            Snackbar snackbar = Snackbar.make(activity.getCurrentFocus(), toastString, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setMaxLines(3);
            snackbar.show();

        } catch (Exception e) {
            try {
                Toast.makeText(activity, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    public static void showAlert(Context context, String message){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(message)
                    .setTitle(AccessDetails.siteTitle)
                    .setCancelable(true)
//                    .setIcon(R.mipmap.ic_launcher)
                    .setIcon(AccessDetails.site_icon)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isAfterToday(int year, int month, int day)
    {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        if (myDate.before(today))
        {
            return false;
        }
        return true;
    }

    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public static boolean checkTime(String time) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(currentTime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String checkZeroVal(double val){
        if (val != 0)
            return String.format("%.2f", val);
        else
            return "0";
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Drawable drawable) {
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static Bitmap drawableToBitmap(PictureDrawable pd) {
        Bitmap bm = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawPicture(pd.getPicture());
        return bm;
    }

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String parseErrorMessage(JSONObject jsonObject){

        String strMsg = "";
        String strSubMsg = "";
        String strKey = "";
        JSONArray arrValidationError = null;
        JSONArray arrVerifyError = null;

        try {
            strKey = jsonObject.getString("message");

            try {
                arrValidationError = jsonObject.getJSONArray("validation_error");
            } catch (Exception e){
                e.printStackTrace();
                arrValidationError = null;
            }

            if (arrValidationError != null){

                for (int i = 0; i < arrValidationError.length(); i ++) {

                    JSONObject object = arrValidationError.getJSONObject(i);

                    for(Iterator<String> iter = object.keys(); iter.hasNext();) {
                        String key = iter.next();
                        String value = object.getString(key);

                        strSubMsg = strSubMsg + key + " - " + value + "\n";
                    }

                }
            }

            try {
                arrVerifyError = jsonObject.getJSONArray("verify_error");
            } catch (Exception e){
                e.printStackTrace();
                arrVerifyError = null;
            }

            if (arrVerifyError != null){

                for (int i = 0; i < arrVerifyError.length(); i ++) {

                    JSONObject object = arrVerifyError.getJSONObject(i);

                    for(Iterator<String> iter = object.keys(); iter.hasNext();) {
                        String key = iter.next();
                        String value = object.getString(key);

                        strSubMsg = strSubMsg + key + " - " + value + "\n";
                    }

                }
            }

            if (!strSubMsg.equalsIgnoreCase(""))
                strMsg = getMessageForKey(strKey) + "\n" + strSubMsg;
            else
                strMsg = getMessageForKey(strKey) + strSubMsg;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return strMsg;
    }

    public static String getMessageForKey(String strKey){

        String response = strKey;

        switch (strKey){
            case "E_INVALID_EMAIL":
                response = "Email address is incorrect.";
                break;
            case "E_VALIDATION_ERROR" :
                response = "Validation Error";
                break;
            case "E_UNKNOWN_ERROR" :
                response = "Something went wrong.";
                break;
            case "E_WRONG_PASSWORD" :
                response = "Password entered is incorrect.";
                break;
            case "E_DUPLICATED_EMAIL" :
                response = "Email address is already used.";
                break;
            case "S_SUCCESS_GET":
                response = "Success Get.";
                break;
            case "S_SUCCESS_RESIGNUP":
                response = "Success Resignup.";
                break;
            case "S_ALREADY_SIGNUP":
                response = "You have already signed up.";
                break;
            case "E_INVALID_APITOKEN":
                response = "API token is invalid.";
                break;
            case "E_VERIFY_TIMEOUT":
                response = "Verify timeout.";
                break;
            case "E_INVALID_SMS_VERIFYCODE":
                response = "SMS verify code is invalid.";
                break;
            case "S_SUCCESS_SMS_VERIFY":
                response = "Success SMS verify.";
                break;
            case "E_INVALID_EMAIL_VERIFYCODE":
                response = "Email verify code is invalid.";
                break;
            case "S_SUCCESS_EMAIL_VERIFY":
                response = "Success email verify.";
                break;
            case "E_INVALID_DEVICETOKEN":
                response = "Device token is invalid.";
                break;
            case "E_NOT_SMS_VERIFY":
                response = "SMS is not verified.";
                break;
            case "E_NOT_EMAIL_VERIFY":
                response = "Email is not verified.";
                break;
            case "E_NOT_VERIFY":
                response = "Your account is not verified.";
                break;

            case "E_INVALID_RESETCODE":
                response = "Reset code is invalid.";
                break;
            case "E_PAYPAL_CONNECTION_TIMEOUT":
                response = "Paypal account connection timeout.";
                break;
            case "E_PAYPAL_UNKNOWN_ERROR":
                response = "Unknown error with Paypal account.";
                break;
            case "E_PAY_CHARGE_ERROR":
                response = "Charge balance error.";
                break;
            case "E_PAY_PAY_ERROR":
                response = "Payment error.";
                break;
            case "E_PAY_OUT_ERROR":
                response = "Payout error.";
                break;
            case "NO_SUCH_COUPONCODE":
                response = "No such coupon code.";
                break;
            case "E_TWILIO_NUMBER_VERIFY_ERROR":
                response = "Twilio SMS cannot send verify message to unverified phone number.";
                break;
            case "E_VERIFICATION_ERROR":
                response = "Verification Error.";
                break;
            case "S_SUCCESS_LOGIN":
                response = "Success login.";
                break;
            case "S_SUCCESS_LOGIN_WITHOUT_UPDATING_SERVICETYPE":
                response = "Your service type change request has not completed because you have some incomplete rides.";
                break;
            case "S_SUCCESS_LOGIN_WITH_UPDATING_SERVICETYPE":
                response = "Your service type has completed successfully.";
                break;
            case "S_SUCCESS_DELETECARIMAGE":
                response = "The car image has deleted successfully.";
                break;
            case "S_SUCCESS_ADDCARIMAGE":
                response = "The car image has added successfully.";
                break;
            case "S_SUCCESS_UPDATEPROFILE":
                response = "The profile has updated successfully.";
                break;
            case "S_PAY_CHARGE_SUCCESS":
                response = "Charged successfully.";
                break;
            default:
                response = strKey;
        }

        return response;
    }
}
