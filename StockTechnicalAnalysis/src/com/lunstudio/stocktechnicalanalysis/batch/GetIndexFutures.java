package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.FuturesSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetIndexFutures {

	private static final Logger logger = LogManager.getLogger();

	private static final String SEPARATOR = ",";
	
	private static final String EXPIRED = "EXPIRED";
	
	private static final String DASH = "-";
	
	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMdd");

	private final static Integer RETRIEVE_SIZE = 5;
	
	@Autowired
	private FuturesSrv futuresSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetIndexFutures instance = context.getBean(GetIndexFutures.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	/**
	 * Initial data from the date specified, not normal use
	 * @throws Exception
	 */
	private void init() throws Exception {
		Calendar currentDate = Calendar.getInstance();
		java.util.Date date = dateFormatter.parse("180101");
		currentDate.setTime(date);

		List<IndexFuturesEntity> futuresList = new ArrayList<IndexFuturesEntity>();	
		Calendar now = Calendar.getInstance();
		
		IndexFuturesEntity indexFutures = null;
		while(currentDate.compareTo(now) <= 0 ) {
			//logger.info(String.format("Get Data on trade date: %s", currentDate.getTime()));
			List<String> lines = this.getIndexFuturesFile(currentDate);
			if( lines != null ) {
				Date tradeDate = this.getBusinessDay(lines.get(10));
				logger.info(String.format("Processing trade date: %s", tradeDate));
				indexFutures = this.getFuturesData("HSI", lines.get(16), tradeDate, 0);
				if( indexFutures != null ) {
					futuresList.add(indexFutures);
				}
				futuresList.add(this.getFuturesData("HSI", lines.get(17), tradeDate, 1));
				futuresList.add(this.getFuturesData("HSI", lines.get(18), tradeDate, 2));
			}
			currentDate.add(Calendar.DAY_OF_MONTH, 1);
			Thread.sleep(1000);
		}
		//Save Futures List
		logger.info(String.format("Number of futures record: %s", futuresList.size()));
		if( futuresList.size() > 0 ) {
			this.futuresSrv.saveIndexFutures(futuresList);
		}
		return;
	}
	
	private void start() throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList("INDEXHANGSENG:HSI", RETRIEVE_SIZE);
		List<IndexFuturesEntity> futuresList = new ArrayList<IndexFuturesEntity>();
		Calendar currentDate = Calendar.getInstance();
		IndexFuturesEntity indexFutures = null;
		for(StockPriceEntity stockPrice : stockPriceList) {
			Date date = stockPrice.getTradeDate();
			currentDate.setTime(date);
			logger.info(String.format("Get Data on trade date: %s", date));
			List<String> lines = this.getIndexFuturesFile(currentDate);
			if( lines != null ) {
				Date tradeDate = this.getBusinessDay(lines.get(10));
				logger.info(String.format("Processing trade date: %s", tradeDate));
				indexFutures = this.getFuturesData("HSI", lines.get(16), tradeDate, 0);
				if( indexFutures != null ) {
					futuresList.add(indexFutures);
				}
				futuresList.add(this.getFuturesData("HSI", lines.get(17), tradeDate, 1));
				futuresList.add(this.getFuturesData("HSI", lines.get(18), tradeDate, 2));
			}
			Thread.sleep(1000);
		}
		//Save Futures List
		logger.info(String.format("Number of futures record: %s", futuresList.size()));
		if( futuresList.size() > 0 ) {
			this.futuresSrv.saveIndexFutures(futuresList);
		}
		return;
	}
	
	private List<String> getIndexFuturesFile(Calendar date) throws Exception {
		//if(true) return null;
		//170102
		//https://www.hkex.com.hk/eng/stat/dmstat/dayrpt/hsif180228.zip
		String dateStr = dateFormatter.format(date.getTime());
		URL url = new URL(String.format(SystemUtils.getHangSengIndexFuturesUrl(), dateStr));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = url.openStream ();
			byte[] byteChunk = new byte[4096];
			int n;
			while ( (n = is.read(byteChunk)) > 0 ) {
				baos.write(byteChunk, 0, n);
			}
		} catch (IOException e) {
			logger.error(String.format("Invalid date %s", date.getTime()));
			return null;
		}
		finally {
			if (is != null) { is.close(); }
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    ZipInputStream zipStream = new ZipInputStream(bais);
	    zipStream.getNextEntry();
	    Scanner sc = new Scanner(zipStream);
	    List<String> lines = new ArrayList<String>();
	    while (sc.hasNextLine()) {
	    	lines.add(sc.nextLine());
	    }
		return lines;
	}
	
	private Date getBusinessDay(String line) throws Exception {
		String[] data = line.split(SEPARATOR);
		DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		java.util.Date date = format.parse(data[7].substring(1));
		return new java.sql.Date(date.getTime());
	}
	
	private IndexFuturesEntity getFuturesData(String indexCode, String line, Date tradeDate, int month) throws Exception {
		IndexFuturesEntity futures = new IndexFuturesEntity();
		String[] data = line.split(SEPARATOR);
		if( EXPIRED.equals(data[1]) ) {
			return null;
		}
		futures.setTradeDate(tradeDate);
		futures.setIndexCode(indexCode);
		futures.setMonth(month);
		if( !DASH.equals(data[1]) ) {
			futures.setNightOpen(Integer.parseInt(data[1]));
		}
		if( !DASH.equals(data[2]) ) {
			futures.setNightHigh(Integer.parseInt(data[2]));
		}
		if( !DASH.equals(data[3]) ) {
			futures.setNightLow(Integer.parseInt(data[3]));
		}
		if( !DASH.equals(data[4]) ) {
			futures.setNightClose(Integer.parseInt(data[4]));
		}
		if( !DASH.equals(data[5]) ) {
			futures.setNightVolume(Integer.parseInt(data[5]));
		}
		if( !DASH.equals(data[6]) ) {
			futures.setDayOpen(Integer.parseInt(data[6]));
		}
		if( !DASH.equals(data[7]) ) {
			futures.setDayHigh(Integer.parseInt(data[7]));
		}
		if( !DASH.equals(data[8]) ) {
			futures.setDayLow(Integer.parseInt(data[8]));
		}
		if( !DASH.equals(data[9]) ) {
			futures.setDayVolume(Integer.parseInt(data[9]));
		}
		if( !DASH.equals(data[10]) ) {
			futures.setDayClose(Integer.parseInt(data[10]));
		}
		if( !DASH.equals(data[11]) ) {
			futures.setDayChange(Integer.parseInt(data[11]));
		}
		if( !DASH.equals(data[12]) ) {
			futures.setContractHigh(Integer.parseInt(data[12]));
		}
		if( !DASH.equals(data[13]) ) {
			futures.setContractLow(Integer.parseInt(data[13]));
		}
		if( !DASH.equals(data[14]) ) {
			futures.setVolume(Integer.parseInt(data[14]));
		}
		if( !DASH.equals(data[15]) ) {
			futures.setOpenInterest(Integer.parseInt(data[15]));
		}
		if( !DASH.equals(data[16]) ) {
			futures.setOpenInterestChange(Integer.parseInt(data[16]));
		}
		return futures;
	}
	
	
}
