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
	
	public DatabaseReference getStockTradeListRef() {
		return this.ref.child(STOCKTRADELIST);
	}
	
	public DatabaseReference getStockInfoListRef() {
		return this.ref.child(STOCKINFOLIST);
	}

	public DatabaseReference getStockPriceListRef() {
		return this.ref.child(STOCKPRICELIST);
	}
	
	public DatabaseReference getAppStatusRef() {
		return this.ref.child(APPSTATUS);
	}
	
	public DatabaseReference getAppConfigRef() {
		return this.ref.child(APPCONFIG);
	}
	
	public DatabaseReference getToBuyStockTradeListRef() {
		return this.ref.child(TOBUYSTOCKTRADELIST);
	}
	
	public DatabaseReference getToSellStockTradeListRef() {
		return this.ref.child(TOSELLSTOCKTRADELIST);
	}
	
	public DatabaseReference getTodayStockTradeListRef() {
		return this.ref.child(TODAYSTOCKTRADELIST);
	}
	
	public DatabaseReference getInProgressStockTradeListRef() {
		return this.ref.child(INPROGRESSSTOCKTRADELIST);
	}

	public DatabaseReference getStockTradeStatListRef() {
		return this.ref.child(TRADESTATLIST);
	}
	
	public DatabaseReference getAboutRef() {
		return this.ref.child(ABOUT);
	}
	
	public DatabaseReference getChartDataListRef() {
		return this.ref.child(CHARTDATALIST);
	}
}
