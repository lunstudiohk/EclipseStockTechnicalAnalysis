package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockVolatilitySrv;
import com.lunstudio.stocktechnicalanalysis.service.SystemMessageSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetStockImplVol {
	
	private static final Logger logger = LogManager.getLogger();

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMdd");
	
	private final static Integer UPDATE_DAYS = 1;

	@Autowired
	private SystemMessageSrv systemMessageSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockVolatilitySrv stockVolatilitySrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockImplVol instance = context.getBean(GetStockImplVol.class);
			if( args != null && args.length > 0 ) {
				instance.start(args[0]);
			} else {
				instance.start(null);
			}
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void start(String startDate) throws Exception {
		List<StockPriceEntity> dateList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockEntity.HSI, UPDATE_DAYS);
		List<StockVolatilityEntity> stockVolatilityList = new ArrayList<StockVolatilityEntity>();
		Calendar currentDate = Calendar.getInstance();
		for(StockPriceEntity stockPrice : dateList) {
			Date date = stockPrice.getTradeDate();
			//Date date = Date.valueOf("2019-02-28");
			if( startDate != null && date.compareTo(Date.valueOf(startDate)) < 0 ) {
				continue;
			}
			currentDate.setTime(date);
			List<String> lines = this.getStockOptionsFile(currentDate);
			if( lines != null ) {
				int startIndex = this.getStartIndex(lines);
				if( startIndex > -1) {
					Map<String, String> stockCodeMap = this.getStockMap();
					for(int i=startIndex+1; i<lines.size(); i++) {
						String[] token = lines.get(i).split(",");
						if( token == null || token.length == 0 || token[0].trim().length() == 0 ) {
							break;
						} else {
							try {
								if( stockCodeMap.containsKey(token[0]) ) {
									StockVolatilityEntity entity = new StockVolatilityEntity(); 
									entity.setStockCode(stockCodeMap.get(token[0]));
									entity.setTradeDate(date);
									entity.setImplVol(new BigDecimal(token[9]));
									stockVolatilityList.add(entity);
								}
							}catch(Exception e) {
								this.systemMessageSrv.saveSystemErrorMessage(String.format("Invalid Format: ", lines.get(i)));
							}
						}
					}
				}
			}
			StockVolatilityEntity entity = this.getVHSI(date);
			if( entity != null ) {
				stockVolatilityList.add(entity);
			}
			logger.info(String.format("Process Date: %s - %s", date, stockVolatilityList.size()));
			this.stockVolatilitySrv.saveStockVolatilityList(stockVolatilityList);
		}
		return;
	}
	
	private StockVolatilityEntity getVHSI(Date tradeDate) throws Exception {
		StockVolatilityEntity stockVolatilityEntity = null;
		try {
			String csvData = HttpUtils.sendGet(SystemUtils.getLatestVHSIUrl());
			String[] val = csvData.split(",");

			if( DateUtils.isSameDate(tradeDate, DateUtils.getGoogleDateString(val[3])) ) {
				stockVolatilityEntity = new StockVolatilityEntity();
				stockVolatilityEntity.setStockCode("INDEXHANGSENG:HSI");
				stockVolatilityEntity.setTradeDate(tradeDate);
				stockVolatilityEntity.setImplVol(new BigDecimal(val[4]));
			} else {
				this.systemMessageSrv.saveSystemErrorMessage("Invalid VHSI Data");
			}
		}catch(Exception e) {
			this.systemMessageSrv.saveSystemErrorMessage(String.format("Invalid VHSI Data: ", e.getMessage()));
		}
		return stockVolatilityEntity;
	}
	
	
	private Map<String, String> getStockMap() throws Exception {
		Map<String, String> stockCodeMap = new HashMap<String, String>();
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			stockCodeMap.put(stock.getStockAtsCode(), stock.getStockCode());
		}
		return stockCodeMap;
	}
	
	private int getStartIndex(List<String> lines) {
		for(int i=0; i<lines.size(); i++) {
			if( lines.get(i).startsWith("\"HKATS CODE\"") || lines.get(i).startsWith("HKATS CODE")) {
				return i;
			}
		}
		return -1;
	}
	
	
	private List<String> getStockOptionsFile(Calendar date) throws Exception {
		//if(true) return null;
		//170102
		//https://www.hkex.com.hk/eng/stat/dmstat/dayrpt/hsif180228.zip
		String dateStr = dateFormatter.format(date.getTime());
		URL url = new URL(String.format(SystemUtils.getStockOptionsUrl(), dateStr));
		logger.info(url.toString());
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
	
}
