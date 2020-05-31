package com.lunstudio.stocktechnicalanalysis.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Component;

@Component
public class DateUtils {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyMMdd");
	private final static SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat googleDateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // 6/10/2016 15:59:59
	private final static SimpleDateFormat googleTradeDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private final static SimpleDateFormat googleHistoricalDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private final static SimpleDateFormat hkexDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private final static SimpleDateFormat csvDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final static SimpleDateFormat weekFormatter = new SimpleDateFormat("EE");

	//private static final SimpleDateFormat googleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static Calendar firstCal = Calendar.getInstance();
	private static Calendar secondCal = Calendar.getInstance();
	
	public static String getDayOfWeek(Date theDate) {
		return weekFormatter.format(theDate);
	}
	public static String calendarDateToString(Calendar date) {
		return dateFormat.format(date.getTime());
	}
	
	public static boolean isMonthBeginTradeDate(Date currentDate, Date previousDate) {
		firstCal.setTime(currentDate);
		secondCal.setTime(previousDate);
		if( firstCal.get(Calendar.MONTH) == secondCal.get(Calendar.MONTH) ) {
			return false;
		}
		return true;
	}
	
	public static boolean isWeekBeginTradeDate(Date currentDate, Date previousDate) {
		firstCal.setTime(currentDate);
		secondCal.setTime(previousDate);
		if( firstCal.get(Calendar.WEEK_OF_YEAR) == secondCal.get(Calendar.WEEK_OF_YEAR) ) {
			return false;
		}
		return true;
	}
	
	public static boolean isSameWeek(Date firstDate, Date secondDate) {
		firstCal.setTime(firstDate);
		secondCal.setTime(secondDate);
		if( firstCal.get(Calendar.WEEK_OF_YEAR) == secondCal.get(Calendar.WEEK_OF_YEAR) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isSameDate(Date date1, Date date2) {
		if( date1 == null || date2 == null ) {
			return false;
		} else {
			return dateFormat.format(date1).equals(dateFormat.format(date2));
		}
	}
	
	public static LocalDate getLocalDate(Date date) {
		return LocalDate.parse(dateFormat.format(date), DATE_FORMAT);
	}
	
	public static LocalDate getLocalDate(int year, int week) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.getActualMinimum(Calendar.DAY_OF_WEEK);
		Date date = new Date(cal.getTime().getTime());
		return LocalDate.parse(dateFormat.format(date), DATE_FORMAT);
	}
	
	public static long getDayDiff(Date endDate, Date startDate) {
		return (endDate.getTime()-startDate.getTime())/86400000;
	}
	
	public static Date getRetentionDate(int retentionMonth) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1*retentionMonth);
		return new Date(cal.getTimeInMillis());
	}
	
	public static Date getNextDateByTiestamp(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		cal.add(Calendar.DATE, 1);
		return new Date(cal.getTimeInMillis());
	}
	
	
	public static Timestamp getTimestampMinusMonth(long ts, int minusMonth){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts);
		cal.add(Calendar.MONTH, -1*minusMonth);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}
	
	public static String getShortDateString(Date date) {
		return shortDateFormat.format(date);
	}
	
	public static String getLongDateString(Date date) {
		return longDateFormat.format(date);
	}
		
	public static String getGoogleHistoricalDate(Date date) {
		return googleHistoricalDateFormat.format(date);
	}
	
	public static Date getGoogleHistoricalDate(String dateStr) {
		java.util.Date date = null;
		try {
			date = googleHistoricalDateFormat.parse(dateStr);
		}catch(Exception e) {
			date = new java.util.Date();
		}
		return Date.valueOf(dateFormat.format(date));
	}
	
	public static Date getHkexDate(String dateStr) {
		java.util.Date date = null;
		try {
			date = hkexDateFormat.parse(dateStr);
		}catch(Exception e) {
			date = new java.util.Date();
		}
		return Date.valueOf(dateFormat.format(date));
	}
	
	public static Date getCsvDate(String dateStr) {
		java.util.Date date = null;
		try {
			date = csvDateFormat.parse(dateStr);
		}catch(Exception e) {
			date = new java.util.Date();
		}
		return Date.valueOf(dateFormat.format(date));
	}
	
	public static Date getDateFromString(String dateStr) {
		return Date.valueOf(dateStr);
	}
	
	public static String getDateString(Timestamp timestamp) {
		return dateFormat.format(timestamp);
	}

	public static String getDateString(Date date) {
		return dateFormat.format(date);
	}

	public static String getFirebaseDateString(Date date) {
		return dateFormat.format(date);
	}
	
	public static Timestamp getDateString(String date) {
		Timestamp tm = null;
		try{
			tm = new Timestamp(dateFormat.parse(date).getTime());
		}catch(Exception e) {
			tm = new Timestamp(0);
		}
		return tm;
	}

	public static Date getGoogleDateString(String inputDate) {
		Date date = null;
		try{
			date = new Date(googleDateTimeFormat.parse(inputDate).getTime());
		}catch(Exception e) {
			try {
				date = new Date(googleTradeDateFormat.parse(inputDate).getTime());
			} catch(Exception ee) {
				
			}
		}
		return date;
	}
	
	
	/*
	public static Timestamp getGoogleDateString(String inputDate) {
		Timestamp tm = null;
		try{
			Date date = googleDateFormat.parse(inputDate);
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			tm = new Timestamp(date.getTime());
		}catch(Exception e) {
			tm = new Timestamp(0);
		}
		return tm;
	}
	*/
	
	public static Timestamp getDateStringAddMonth(String date, int month){
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.getDateString(date));
		cal.add(Calendar.MONTH, month);
		return new Timestamp(cal.getTimeInMillis());
	}
	
    public static Date addDays(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return new Date(cal.getTimeInMillis());
    }

    public static java.util.Date addDays(java.util.Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return new java.util.Date(cal.getTimeInMillis());
    }
    
    public static Timestamp addTradeDay(Timestamp ts, int days) {
	    	Calendar cal = new GregorianCalendar();
	    	cal.setTimeInMillis(ts.getTime());
	    	for (int i=0; i<days; i++) {
	    		do {
	    			cal.add(Calendar.DAY_OF_MONTH, 1);
	            } while ( !isWorkingDay(cal));
	    	}
	    	return new Timestamp(cal.getTime().getTime());
    }
    
    private static boolean isWorkingDay(Calendar cal) {
	    	int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
	    	if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
	    		return false;
	    	}
        return true;
    }
    
    public static void resetTimestamp(Calendar cal) {
    	cal.set( Calendar.HOUR_OF_DAY, 0 );
    	cal.set( Calendar.MINUTE, 0 );
    	cal.set( Calendar.SECOND, 0 );
    	cal.set( Calendar.MILLISECOND, 0 );
    	return;
    }
}
