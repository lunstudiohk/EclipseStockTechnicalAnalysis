package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.OptionsSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetIndexOptions {
	
	private static final Logger logger = LogManager.getLogger();

	private static final String SEPARATOR = ",";

	private static final String DASH = "-";
	
	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyMMdd");
	
	private final static Integer RETRIEVE_SIZE = 5;

	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private OptionsSrv optionsSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetIndexOptions instance = context.getBean(GetIndexOptions.class);
			instance.start();
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void start() throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(StockEntity.HSI, RETRIEVE_SIZE);
		List<StockOptionsEntity> optionsList = new ArrayList<StockOptionsEntity>();	
		Calendar currentDate = Calendar.getInstance();
		for(StockPriceEntity stockPrice : stockPriceList) {
			Date date = stockPrice.getTradeDate();
			currentDate.setTime(date);
			logger.info(String.format("Get Data on trade date: %s", date));
			List<String> lines = this.getIndexOptionsFile(currentDate);
			if( lines != null ) {
				try {
					Date tradeDate = this.getBusinessDay(lines.get(24));
					logger.info(String.format("Processing trade date: %s", tradeDate));
					optionsList.addAll(this.getOptionsData(StockEntity.HSI, lines, tradeDate));
				}catch(Exception e) {
					logger.error(String.format("Invalid Trade Date: %s", date));
				}
			}
			//Save Options List
			logger.info(String.format("Number of options record: %s", optionsList.size()));
			if( optionsList.size() > 0 ) {
				this.optionsSrv.saveStockOptions(optionsList);
			}
			Thread.sleep(10000);
		}
		
		
		return;
	}
	
	private List<String> getIndexOptionsFile(Calendar date) throws Exception {
		//if(true) return null;
		//170102
		//https://www.hkex.com.hk/eng/stat/dmstat/dayrpt/hsif180228.zip
		String dateStr = dateFormatter.format(date.getTime());
		URL url = new URL(String.format(SystemUtils.getHangSengIndexOptionsUrl(), dateStr));
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
		java.util.Date date = format.parse(data[12].substring(1));
		return new java.sql.Date(date.getTime());
	}
	
	private List<StockOptionsEntity> getOptionsData(String indexCode, List<String> lines, Date tradeDate) throws Exception {
		List<StockOptionsEntity> optionsList = new ArrayList<StockOptionsEntity>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(tradeDate);
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy");
		for(int i=0; i<3; i++) {
			cal.setTime(tradeDate);
			cal.add(Calendar.MONDAY, i);
			String targetMonth = formatter.format(cal.getTime()).toUpperCase();
			for(String line : lines) {
				if( line.startsWith(targetMonth) ) {
					String[] data = line.split(SEPARATOR);
					if( Integer.parseInt(data[18]) > 0 ) {
						StockOptionsEntity options = new StockOptionsEntity();
						options.setStockCode(indexCode);
						options.setTradeDate(tradeDate);
						options.setMonth(i);
						options.setStrikePrice(new BigDecimal(data[1]));
						options.setOptionType(data[2]);
						
						if( !DASH.equals(data[3]) ) {
							options.setNightOpen(new BigDecimal(data[3]));
						}
						if( !DASH.equals(data[4]) ) {
							options.setNightHigh(new BigDecimal(data[4]));
						}
						if( !DASH.equals(data[5]) ) {
							options.setNightLow(new BigDecimal(data[5]));
						}
						if( !DASH.equals(data[6]) ) {
							options.setNightClose(new BigDecimal(data[6]));
						}
						if( !DASH.equals(data[7]) ) {
							options.setNightVolume(Integer.parseInt(data[7]));
						}
						if( !DASH.equals(data[8]) ) {
							options.setDayOpen(new BigDecimal(data[8]));
						}
						if( !DASH.equals(data[9]) ) {
							options.setDayHigh(new BigDecimal(data[9]));
						}
						if( !DASH.equals(data[10]) ) {
							options.setDayLow(new BigDecimal(data[10]));
						}
						if( !DASH.equals(data[11]) ) {
							options.setDayClose(new BigDecimal(data[11]));
						}
						if( !DASH.equals(data[12]) ) {
							options.setDayChange(new BigDecimal(data[12]));
						}
						if( !DASH.equals(data[13]) ) {
							options.setIv(new BigDecimal(data[13]));
						}
						if( !DASH.equals(data[14]) ) {
							options.setDayVolume(Integer.parseInt(data[14]));
						}
						
						if( !DASH.equals(data[15]) ) {
							options.setContractHigh(Integer.parseInt(data[15]));
						}
						if( !DASH.equals(data[16]) ) {
							options.setContractLow(Integer.parseInt(data[16]));
						}
						if( !DASH.equals(data[17]) ) {
							options.setVolume(Integer.parseInt(data[17]));
						}
						if( !DASH.equals(data[18]) ) {
							options.setOpenInterest(Integer.parseInt(data[18]));
						}
						if( !DASH.equals(data[19]) ) {
							options.setOpenInterestChange(Integer.parseInt(data[19]));
						}
						optionsList.add(options);
					}
				}
			}
		}
		return optionsList;
	}
	
}
