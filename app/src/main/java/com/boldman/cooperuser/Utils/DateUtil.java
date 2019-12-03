package com.boldman.cooperuser.Utils;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.GregorianCalendar;

public class DateUtil {


    public static SimpleDateFormat simpleDateFormat_yyyy_MM_dd;
    public static SimpleDateFormat simpleDateFormat_year_yyyy;
    public static SimpleDateFormat simpleDateFormat_month_MMM;
    public static SimpleDateFormat simpleDateFormat_month_M;
    public static SimpleDateFormat simpleDateFormat_date_d;
    public static SimpleDateFormat simpleDateFormat_date_dd;
    public static SimpleDateFormat simpleDateFormat_dayInWeek_E;
    public static SimpleDateFormat simpleDateFormat_hour_h;
    public static SimpleDateFormat simpleDateFormat_hour_hh;
    public static SimpleDateFormat simpleDateFormat_min_m;
    public static SimpleDateFormat simpleDateFormat_min_mm;
    public static SimpleDateFormat simpleDateFormat_sec_s;
    public static SimpleDateFormat simpleDateFormat_sec_ss;
    public static SimpleDateFormat simpleDateFormat_meridiem_a;

    public static void initialize(Context context) {
        simpleDateFormat_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat_year_yyyy = new SimpleDateFormat("yyyy");
        simpleDateFormat_month_MMM = new SimpleDateFormat("MMM");
        simpleDateFormat_month_M = new SimpleDateFormat("M");
        simpleDateFormat_date_d = new SimpleDateFormat("d");
        simpleDateFormat_date_dd = new SimpleDateFormat("dd");
        simpleDateFormat_dayInWeek_E = new SimpleDateFormat("E");
        simpleDateFormat_hour_h = new SimpleDateFormat("h");
        simpleDateFormat_hour_hh = new SimpleDateFormat("hh");
        simpleDateFormat_min_m = new SimpleDateFormat("m");
        simpleDateFormat_min_mm = new SimpleDateFormat("mm");
        simpleDateFormat_sec_s = new SimpleDateFormat("s");
        simpleDateFormat_sec_ss = new SimpleDateFormat("ss");
        simpleDateFormat_meridiem_a = new SimpleDateFormat("a");
    }

    public static String toString_yyyy_MM_dd(Date date) {
        return simpleDateFormat_yyyy_MM_dd.format(date);
    }

    public static Date parse_from_yyyy_MM_dd_0(String string) {
        try {
            return simpleDateFormat_yyyy_MM_dd.parse(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString_yyyy_M_d(Date date) {
        return simpleDateFormat_year_yyyy.format(date)
                + "-"
                + simpleDateFormat_month_M.format(date)
                + "-"
                + simpleDateFormat_date_d.format(date);
    }

    public static String toString_d_MMM_yyyy(Date date) {
        return toString_d_MMM(date)
                + " "
                + simpleDateFormat_year_yyyy.format(date);
    }

    public static String toString_d_MMM_abbryyyy(Date date) {
        Date todayDate = new Date();
        GregorianCalendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(todayDate);

        GregorianCalendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTime(date);
        if (todayCalendar.get(GregorianCalendar.YEAR) == dateCalendar.get(GregorianCalendar.YEAR))
            return toString_d_MMM(date);
        else
            return toString_d_MMM_yyyy(date);
    }

    public static String toString_dth_MMM_abbryyyy_comma_nbsp_h_ua(Date date) {
        return toString_dth_MMM_abbryyyy(date) + ", " + toString_h_ua(date);
    }

    public static String toString_dth_MMM_abbryyyy(Date date) {
        if (date == null) {
            return "Null Date";
        }

        Date todayDate = new Date();
        GregorianCalendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(todayDate);

        GregorianCalendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTime(date);
        if (todayCalendar.get(GregorianCalendar.YEAR) == dateCalendar.get(GregorianCalendar.YEAR))
            return toString_dth_MMM(date);
        else
            return toString_dth_MMM_yyyy(date);
    }

    public static String toString_d_MMM(Date date) {
        return simpleDateFormat_date_d.format(date)
                + " "
                + simpleDateFormat_month_MMM.format(date);
    }

    public static String toString_MMM_yyy(Date date) {
        return simpleDateFormat_month_MMM.format(date)
                + " "
                + simpleDateFormat_year_yyyy.format(date);
    }

    public static String toString_dth_MMM_yyyy(Date date) {
        return toString_dth_MMM(date)
                + " "
                + simpleDateFormat_year_yyyy.format(date);
    }

    public static String toString_dth_MMM(Date date) {
        return toString_dth(date)
                + " "
                + simpleDateFormat_month_MMM.format(date);
    }

    public static String toString_dth(Date date) {
        String dateStr = simpleDateFormat_date_d.format(date);
        int dateNum = Integer.valueOf(dateStr);
        if (dateNum >= 4 && dateNum <= 20)
            dateStr += "th";
        else if (dateStr.endsWith("1"))
            dateStr += "st";
        else if (dateStr.endsWith("2"))
            dateStr += "nd";
        else if (dateStr.endsWith("3"))
            dateStr += "rd";
        else
            dateStr += "th";
        return dateStr;
    }

    public static String toString_dayInWeek_d(Date date) {
        return simpleDateFormat_dayInWeek_E.format(date)
                + " "
                + simpleDateFormat_date_d.format(date);
    }

    public static String toString_dayInWeek_dth(Date date) {
        return simpleDateFormat_dayInWeek_E.format(date)
                + " "
                + toString_dth(date);
    }

    public static String toString_dayInWeek_d_MMM_yyyy_at_h_mm_la(Date date) {
        return toString_dayInWeek_d(date)
                + " "
                + toString_MMM_yyy(date)
                + " @ "
                + toString_h_mm_la(date);
    }

    public static String toString_dayInWeek_dth_MMM_yyyy_at_h_mm_la(Date date) {
        return toString_dayInWeek_dth(date)
                + " "
                + toString_MMM_yyy(date)
                + " @ "
                + toString_h_mm_la(date);
    }

    public static String toString_la(Date date) {
        String am = simpleDateFormat_meridiem_a.format(date);
        return am.toLowerCase();
    }

    public static String toString_h_mm_la(Date date) {
        return toString_h_mm(date) + toString_la(date);
    }

    public static String toString_h_mm_nbsp_la(Date date) {
        return toString_h_mm(date) + " " + toString_la(date);
    }

    public static String toString_h_mm(Date date) {
        return simpleDateFormat_hour_h.format(date)
                + ":"
                + simpleDateFormat_min_mm.format(date);
    }

    public static String toString_h_mm_nbsp_ua(Date date) {
        return toString_h_mm(date) + " " + simpleDateFormat_meridiem_a.format(date);
    }

    public static String toString_h_ua(Date date) {
        return simpleDateFormat_hour_h.format(date) + simpleDateFormat_meridiem_a.format(date);
    }

    public static String toString_today(Date date) {
        Date todayDate = new Date();
        GregorianCalendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(todayDate);

        GregorianCalendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTime(date);

        if (todayCalendar.get(GregorianCalendar.YEAR) == dateCalendar.get(GregorianCalendar.YEAR)
                && todayCalendar.get(GregorianCalendar.MONTH) == dateCalendar.get(GregorianCalendar.MONTH)
                && todayCalendar.get(GregorianCalendar.DATE) == dateCalendar.get(GregorianCalendar.DATE))
            return "Today";

        GregorianCalendar yesterdayCalendar = (GregorianCalendar) todayCalendar.clone();
        yesterdayCalendar.add(GregorianCalendar.DATE, -1);
        if (yesterdayCalendar.get(GregorianCalendar.YEAR) == dateCalendar.get(GregorianCalendar.YEAR)
                && yesterdayCalendar.get(GregorianCalendar.MONTH) == dateCalendar.get(GregorianCalendar.MONTH)
                && yesterdayCalendar.get(GregorianCalendar.DATE) == dateCalendar.get(GregorianCalendar.DATE))
            return "Yesterday";

        if (todayCalendar.get(GregorianCalendar.YEAR) == dateCalendar.get(GregorianCalendar.YEAR))
            return toString_dth_MMM(date);

        return toString_dth_MMM_yyyy(date);
    }

    public static String toString_dmy(Date date) {
        return "";
    }

    public static String toString_dmha(Date date) {
        return "";
    }

    public static String toString_edmyhma(Date date) {
        return "";
    }

    public static Date parse_from_yyyy_MM_dd_1(String dateString) throws Exception {
        int year = Integer.valueOf(dateString.substring(0, 4));
        int month = Integer.valueOf(dateString.substring(5, 7));
        int date = Integer.valueOf(dateString.substring(8, 10));
        return new Date(year - 1900, month - 1, date);
    }

    public static Date parse_from_yyyy_MM_dd_hh_mm_ss(String dateString) throws Exception {
        int year = Integer.valueOf(dateString.substring(0, 4));
        int month = Integer.valueOf(dateString.substring(5, 7));
        int date = Integer.valueOf(dateString.substring(8, 10));
        int hour = Integer.valueOf(dateString.substring(11, 13));
        int minute = Integer.valueOf(dateString.substring(14, 16));
        int second = Integer.valueOf(dateString.substring(17, 19));
        return new Date(year - 1900, month - 1, date, hour, minute, second);
    }

    public static String convertDateToString24HoursFormat(String dateS){
        SimpleDateFormat dest = new SimpleDateFormat(" dd MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat src = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = src.parse(dateS);

        } catch (ParseException e) {
            Log.d("Exception",e.getMessage());
        }
        String result =  getTime(date)+","+dest.format(date);
        //   Log.d("result", result);
        return result;
    }

    public static String getTime(Date date){
        String meridian;
        int dateHours = date.getHours();
        String hour;
        if (dateHours < 12) {
            hour = Integer.toString(dateHours);
            meridian = "AM";
        } else {
            hour = Integer.toString(dateHours - 12);
            meridian = "PM";
        }

        int dateMins = date.getMinutes();
        String minutes;
        if (dateMins < 10) {
            minutes = "0" + dateMins;
        } else {
            minutes = Integer.toString(dateMins);
        }
        if(hour.equalsIgnoreCase("0")) hour = "12";
        return hour + ":" + minutes +" "+ meridian;
    }

}
