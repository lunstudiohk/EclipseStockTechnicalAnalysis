package com.lunstudio.stocktechnicalanalysis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SystemUtils {

    @Value("${FIREBASE.ACCOUNT_KEY}")
    private String FIREBASE_ACCOUNT_KEY;

    @Value("${FIREBASE.DATABASE_URL}")
    private String FIREBASE_DATABASE_URL;

    @Value("${FIREBASE.ADMIN_DEVICE_ID}")
    private String ADMIN_DEVICE_ID;    

    @Value("${GOOGLE.STOCK_PRICE_URL}")
    private String DAILY_STOCK_PRICE_URL;
        
    @Value("${GOOGLE.STOCK_DATE_PRICE_URL}")
    private String DAILY_STOCK_DATE_PRICE_URL;
    
    @Value("${FIREBASE.MESSAGE_URL}")
    private String MESSAGE_URL;
    
    @Value("${FIREBASE.AUTHORIZATION_ID}")
    private String AUTHORIZATION_ID;
    
    @Value("${STIMULATION.HISTORICAL_TRADEDAY}")
    private int HISTORICAL_TRADEDAY;
    
    @Value("${STIMULATION.STRATEGY_TRADEDAY}")
    private int STRATEGY_TRADEDAY;
    
    @Value("${CLOUD.HISTORICALTRADE}")
    private int HISTORICALTRADE;
    
    @Value("${HKEX.WARRANT_FUL_LIST_URL}")
    private String WARRANT_FULLLIST_URL;
       	
    @Value("${HKEX.CBBC_FUL_LIST_URL}")
    private String CBBC_FULLLIST_URL;
        
    @Value("${HKEX.SHARE_HOLDING_URL}")
    private String SHARE_HOLDING_URL;
    
    @Value("${REPORT.STOCKLIST_HTML_TEMPLATE}")
    private String STOCKLIST_HTML_TEMPLATE;
    
    @Value("${REPORT.STOCKHOLDING_HTML_TEMPLATE}")
    private String STOCKHOLDING_HTML_TEMPLATE;
    
    @Value("${REPORT.STOCKHOLDINGSUMMARY_HTML_TEMPLATE}")
    private String STOCKHOLDINGSUMMARY_HTML_TEMPLATE;
    
    @Value("${REPORT.STOCKHOLDER_HTML_TEMPLATE}")
    private String STOCKHOLDER_HTML_TEMPLATE;
    
    @Value("${REPORT.HTML_OUTPUT}")
    private String HTML_OUTPUT;
    
    @Value("${REPORT.JSON_OUTPUT}")
    private String JSON_OUTPUT;
    
    @Value("${ALPHAVANTAGE.TIME_SERIES_DAILY_URL}")
    private String TIME_SERIES_DAILY_URL;
    
    @Value("${ALPHAVANTAGE.TIME_SERIES_WEEKLY_URL}")
    private String TIME_SERIES_WEEKLY_URL;
    
    @Value("${ALPHAVANTAGE.TIME_SERIES_MONTHLY_URL}")
    private String TIME_SERIES_MONTHLY_URL;
    
    @Value("${WARRANT.CALCULATOR_URL}")
    private String WARRANT_CALCULATOR_URL;
        
    @Value("${STOCK.AASTOCK_URL}")
    private String AASTOCK_PRICE_URL;
    
    @Value("${HKEX.HANG_SENG_INDEX_FUTURES}")
    private String HANG_SENG_INDEX_FUTURES;

    @Value("${HKEX.HANG_SENG_INDEX_OPTIONS}")
    private String HANG_SENG_INDEX_OPTIONS;
    
    @Value("${HKEX.STOCK_OPTIONS}")
    private String STOCK_OPTIONS;
    
    @Value("${WTD.LATEST_STOCK_PRICE_URL}")
    private String LATEST_STOCK_PRICE_URL;
    
    @Value("${WTD.HISTORICAL_STOCK_PRICE_URL}")
    private String HISTORICAL_STOCK_PRICE_URL;
    
    @Value("${GOOGLE.LATEST_VHSI_URL}")
    private String LATEST_VHSI_URL;
    
    private static SystemUtils instance;
    
    public static SystemUtils getInstance() {
    	if( SystemUtils.instance == null ) {
    		String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			instance = context.getBean(SystemUtils.class);
			context.close();
			return instance;
    	} else {
    		return SystemUtils.instance;
    	}
    }
    
    public static final String getFirebaseMessageUrl() {
    		return SystemUtils.getInstance().MESSAGE_URL;
    }
    
    public static final String getFirebaseAuthorizationKey() {
    		return SystemUtils.getInstance().AUTHORIZATION_ID;
    }
    
    public static final String getFirebaseDatabaseUrl() {
    		return SystemUtils.getInstance().FIREBASE_DATABASE_URL;
    }
    
    public static final String getFirebaseAccountKey() {
    		return SystemUtils.getInstance().FIREBASE_ACCOUNT_KEY;
    }
    
    public static final String getGoogleStockPriceUrl() {
    		return SystemUtils.getInstance().DAILY_STOCK_PRICE_URL;
    }
        
    public static final String getGoogleStockDatePriceUrl() {
    		return SystemUtils.getInstance().DAILY_STOCK_DATE_PRICE_URL;
    }
    
    public static final int getStimulationHistoricalTradeday() {
    		return SystemUtils.getInstance().HISTORICAL_TRADEDAY;
    }
    
    public static final int getStimulationStrategyTradeday() {
    		return SystemUtils.getInstance().STRATEGY_TRADEDAY;
    }
    
    public static final int getCloudHistoricalTrade() {
    		return SystemUtils.getInstance().HISTORICALTRADE;
    }
    
    public static final String getWarrantFullListUrl() {
    		return SystemUtils.getInstance().WARRANT_FULLLIST_URL;
    }
    
    public static final String getCbbcFullListUrl() {
		return SystemUtils.getInstance().CBBC_FULLLIST_URL;
    }

    public static final String getShareHoldingUrl() {
		return SystemUtils.getInstance().SHARE_HOLDING_URL;
    }
    
    public static final String getStockListHtmlTemplate() {
    	return SystemUtils.getInstance().STOCKLIST_HTML_TEMPLATE;
    }
    
    public static final String getStockHoldingHtmlTemplate() {
    	return SystemUtils.getInstance().STOCKHOLDING_HTML_TEMPLATE;
    }
    
    public static final String getStockHoldingSummaryHtmlTemplate() {
    	return SystemUtils.getInstance().STOCKHOLDINGSUMMARY_HTML_TEMPLATE;
    }
    
    public static final String getStockHolderHtmlTemplate() {
    	return SystemUtils.getInstance().STOCKHOLDER_HTML_TEMPLATE;
    }
    
    public static final String getHtmlOutput() {
    	return SystemUtils.getInstance().HTML_OUTPUT;
    }
    
    public static final String getJsonOutput() {
    	return SystemUtils.getInstance().JSON_OUTPUT;
    }
    
    public static final String getTimeSeriesDailyUrl() {
    	return SystemUtils.getInstance().TIME_SERIES_DAILY_URL;
    }
    
    public static final String getTimeSeriesWeeklyUrl() {
    	return SystemUtils.getInstance().TIME_SERIES_WEEKLY_URL;
    }
    
    public static final String getTimeSeriesMonthlyUrl() {
    	return SystemUtils.getInstance().TIME_SERIES_MONTHLY_URL;
    }
    
    public static final String getWarrantCalculatorUrl() {
    	return SystemUtils.getInstance().WARRANT_CALCULATOR_URL;
    }
    
    public static final String getAAStockPriceUrl() {
    	return SystemUtils.getInstance().AASTOCK_PRICE_URL;
    }
    
    public static final String getHangSengIndexFuturesUrl() {
    	return SystemUtils.getInstance().HANG_SENG_INDEX_FUTURES;
    }
    
    public static final String getHangSengIndexOptionsUrl() {
    	return SystemUtils.getInstance().HANG_SENG_INDEX_OPTIONS;
    }
    
    public static final String getStockOptionsUrl() {
    	return SystemUtils.getInstance().STOCK_OPTIONS;
    }
    
    public static final String getLatestStockPriceUrl() {
    	return SystemUtils.getInstance().LATEST_STOCK_PRICE_URL;
    }
    
    public static final String getHistoricalStockPriceUrl() {
    	return SystemUtils.getInstance().HISTORICAL_STOCK_PRICE_URL;
    }
    
    public static final String getLatestVHSIUrl() {
    	return SystemUtils.getInstance().LATEST_VHSI_URL;
    }
}
