package com.lunstudio.stocktechnicalanalysis.firebase;

import java.io.FileInputStream;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger.Level;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

public class FirebaseDao {
	
	private static final String StockPriceData = "StockPriceData";
	private static final String WarrantPriceData = "WarrantPriceData";
	private static final String CbbcPriceData = "CbbcPriceData";
	private static final String StockData = "StockData";
	private static final String StockTradeDate = "StockTradeDate";
	private static final String StockPriceSummary = "StockPriceSummary";
	private static final String WarrantPriceSummary = "WarrantPriceSummary";
	private static final String CbbcPriceSummary = "CbbcPriceSummary";
	private static final String CandleStickData = "CandlestickData";
	/*
	private static final String TOBUYSTOCKTRADELIST = "ToBuyStockTradeList";
	private static final String TOSELLSTOCKTRADELIST = "ToSellStockTradeList";
	private static final String TODAYSTOCKTRADELIST = "TodayStockTradeList";
	private static final String INPROGRESSSTOCKTRADELIST = "InProgressStockTradeList";
	private static final String TRADESTATLIST = "StockTradeStatList";
	private static final String STOCKINFOLIST = "StockInfoList";
	private static final String STOCKPRICELIST = "StockPriceList";
	private static final String STOCKTRADELIST = "StockTradeList";
	private static final String CHARTDATALIST = "ChartDataList";
	private static final String APPSTATUS = "AppStatus";
	private static final String APPCONFIG = "AppConfig";
	private static final String ABOUT = "About";
	*/
	private static FirebaseDao instance = new FirebaseDao();
	
	private DatabaseReference ref;

	public static FirebaseDao getInstance() {
		if( FirebaseDao.instance == null ) {
			FirebaseDao.instance = new FirebaseDao();
			return FirebaseDao.instance;
		} else {
			return FirebaseDao.instance;
		}
	}
	
	public FirebaseDao() {
		try{	
			FileInputStream serviceAccount = new FileInputStream(SystemUtils.getFirebaseAccountKey());
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					  .setDatabaseUrl(SystemUtils.getFirebaseDatabaseUrl())
					  .build();
			FirebaseApp.initializeApp(options);
			FirebaseDatabase.getInstance().setLogLevel(Level.INFO);
			this.ref = FirebaseDatabase.getInstance().getReference();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public DatabaseReference getRootRef() {
		return this.ref;
	}
	
	public DatabaseReference getCandlestickDataRef() {
		return this.ref.child(CandleStickData);
	}
	public DatabaseReference getStockPriceDataRef() {
		return this.ref.child(StockPriceData);
	}
	
	public DatabaseReference getWarrantPriceDataRef() {
		return this.ref.child(WarrantPriceData);
	}
	
	public DatabaseReference getCbbcPriceDataRef() {
		return this.ref.child(CbbcPriceData);
	}
	
	public DatabaseReference getStockDataRef() {
		return this.ref.child(StockData);
	}
	
	public DatabaseReference getStockTradeDateRef() {
		return this.ref.child(StockTradeDate);
	}
	
	public DatabaseReference getStockPriceSummaryRef() {
		return this.ref.child(StockPriceSummary);
	}
	
	public DatabaseReference getWarrantPriceSummaryRef() {
		return this.ref.child(WarrantPriceSummary);
	}
	
	public DatabaseReference getCbbcPriceSummaryRef() {
		return this.ref.child(CbbcPriceSummary);
	}
}
