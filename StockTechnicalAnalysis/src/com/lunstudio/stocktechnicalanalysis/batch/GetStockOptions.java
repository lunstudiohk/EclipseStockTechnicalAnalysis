package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.OptionsSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.FileUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetStockOptions {
	
	private static final Logger logger = LogManager.getLogger();

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMdd");
	private final static SimpleDateFormat optionDateFormatter = new SimpleDateFormat("dd MMM yyyy");
	private final static SimpleDateFormat optionMonthFormatter = new SimpleDateFormat("MMMyy");
	
	private static Integer RETRIEVE_SIZE = 5;
	private static final String EMPTY = ""; 
	private static final String COMMA = ",";
	private static final String DASH = "-";
	private static final String ZERO = "0";

	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private OptionsSrv optionsSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockOptions instance = context.getBean(GetStockOptions.class);
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
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockEntity.HSI, RETRIEVE_SIZE);
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		Calendar currentDate = Calendar.getInstance();
		for(StockPriceEntity stockPrice : stockPriceList) {
			List<StockOptionsEntity> optionsList = new ArrayList<StockOptionsEntity>();
			Date tradeDate = stockPrice.getTradeDate();
			//Date date = Date.valueOf("2018-04-28");
			if( startDate != null && tradeDate.compareTo(Date.valueOf(startDate)) < 0 ) {
				continue;
			}
			currentDate.setTime(tradeDate);
			//List<String> lines = this.getStockOptionsFile();
			List<String> lines = this.getStockOptionsFile(currentDate);
			String optionDate = GetStockOptions.optionDateFormatter.format(tradeDate).toUpperCase();
			logger.info(String.format("Processing date: %s", optionDate));
			
			List<String> optionMonths = this.getOptionMonths(tradeDate);
			String stockCode = null;
			if( this.isValidOptionDate(lines, optionDate) ) {
				if( lines != null ) {
					for(int i=0; i<lines.size(); i++) {
						String line = lines.get(i);
						if( line.startsWith("\"CLASS ") ) {
							stockCode = this.getStockCode(line, stockList);
						} else {
							if( stockCode != null ) {
								StockOptionsEntity stockOption = this.getStockOptionEntity(line, optionMonths);
								if( stockOption != null ) {
									stockOption.setStockCode(stockCode);
									stockOption.setTradeDate(tradeDate);
									optionsList.add(stockOption);
									//logger.info(stockOption);
								}
							}
						}
					}
				}
			}
			logger.info(String.format("Number of options: %s", optionsList.size()));
			if( optionsList.size() > 0 ) {
				this.optionsSrv.saveStockOptions(optionsList);
			}	
			Thread.sleep(10000);
		}
		return;
	}
	
	
	private StockOptionsEntity getStockOptionEntity(String line, List<String> optionMonths) {
		int month = this.getOptionMonthIndex(line, optionMonths);
		if( month == -1 ) {
			return null;
		}
		StockOptionsEntity stockOption = new StockOptionsEntity();
		String[] data = line.split(COMMA);
		if( !data[10].equals(ZERO) || (!data[11].equals(ZERO) && !data[11].equals(DASH)) ) {
			stockOption.setMonth(month);
			stockOption.setStrikePrice(new BigDecimal(data[1].trim()));
			stockOption.setOptionType(data[2].trim());
			stockOption.setDayOpen(new BigDecimal(data[3].trim()));
			stockOption.setDayHigh(new BigDecimal(data[4].trim()));
			stockOption.setDayLow(new BigDecimal(data[5].trim()));
			stockOption.setDayClose(new BigDecimal(data[6].trim()));
			if( !DASH.equals(data[7].trim()) ) {
				stockOption.setDayChange(new BigDecimal(data[7].trim()));
			}
			stockOption.setIv(new BigDecimal(data[8].trim()));
			stockOption.setDayVolume(Integer.parseInt(data[9].trim().replace(COMMA, EMPTY)));
			stockOption.setOpenInterest(Integer.parseInt(data[10].trim().replace(COMMA, EMPTY)));
			if( !DASH.equals(data[11].trim()) ) {
				stockOption.setOpenInterestChange(Integer.parseInt(data[11].trim().replace(COMMA, EMPTY)));
			}
			return stockOption;
		}
		return null;
	}
	
	private int getOptionMonthIndex(String line, List<String> optionMonths) {
		for(int i=0; i<optionMonths.size(); i++) {
			if( line.startsWith(optionMonths.get(i)) ) {
				return i;
			}
		}
		return -1;
	}
	
	private List<String> getOptionMonths(Date tradeDate) {
		Calendar currentDate = Calendar.getInstance();
		List<String> optionMonths = new ArrayList<String>();
		for(int i=0; i<3; i++) {
			currentDate.setTime(tradeDate);
			currentDate.add(Calendar.MONTH, i);
			optionMonths.add(optionMonthFormatter.format(currentDate.getTime()).toUpperCase());
		}
		return optionMonths;
	}
	
	private String getStockCode(String line, List<StockEntity> stockList) {
		for(StockEntity stock: stockList) {
			/*
			if( stock.getStockAtsCode() != null ) {
				String token = String.format("CLASS %s", stock.getStockAtsCode());
				if( line.indexOf(token) != -1 ) {
					return stock.getStockCode();
				}
			}
			*/
		}
		return null;
	}
	
	private boolean isValidOptionDate(List<String> lines, String optionDate) {
		if( lines != null ) {
			for(int i=0; i<lines.size(); i++) {
				String line = lines.get(i);
				if( line.indexOf(optionDate) > 0 ) {
					return true;
				}
			}
		}
		return false;
	}
		
	private List<String> getStockOptionsFile() throws Exception {
		return FileUtils.readToLine(new File("/Volumes/HD2/Temp/stockoption.html"), "UTF-8");
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
