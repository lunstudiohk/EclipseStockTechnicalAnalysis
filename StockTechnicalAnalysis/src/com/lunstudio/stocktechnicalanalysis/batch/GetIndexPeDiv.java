package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetIndexPeDiv {

	@Autowired
	private StockPriceSrv stockPriceSrv;

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyy");

	private static final Logger logger = LogManager.getLogger();

	//24Feb20
	
	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			GetIndexPeDiv instance = context.getBean(GetIndexPeDiv.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void start(String[] args) throws Exception {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -10);
		Calendar today = Calendar.getInstance();
		while( start.compareTo(today) <= 0 ) {
			try {
				List<String> data = this.getIndexData(start);
				this.processData(start, data);
			} catch(Exception e) {
				logger.error("Error in date [" + dateFormat.format(start.getTime()) + "] : " + e.getMessage());
			}
			start.add(Calendar.DATE, 1);
			Thread.sleep(3000);
		}
		return;
	}
	
	private List<String> getIndexData(Calendar date) throws Exception {
		return HttpUtils.downloadCsv(String.format(SystemUtils.getIndexPeDivUrl(), dateFormat.format(date.getTime())), "UTF-16");
	}
	
	private void processData(Calendar theDate, List<String> data) throws Exception {
		Date tradeDate = new Date(theDate.getTimeInMillis());
		logger.info("Process HSI PE/DIV: " + tradeDate.toString());
		StockPriceEntity stockPrice = this.stockPriceSrv.getDailyStockPrice(StockEntity.HSI, tradeDate);
		String[] tokens = data.get(2).split("	");
		String peStr = tokens[9].replace("\"", "");
		String dividendStr = tokens[8].replace("\"", "");
		this.stockPriceSrv.saveStockPrice(stockPrice);
		return;
	}
	
/*
	//Download files
	private void start(String[] args) throws Exception {
		Calendar start = Calendar.getInstance();
		if( args != null && args.length > 1 ) {
			start.setTimeInMillis(DateUtils.getDateString(args[1]).getTime());
		}
		Calendar today = Calendar.getInstance();
		while( start.compareTo(today) <= 0 ) {
			System.out.println("Date: " + dateFormat.format(start.getTime()));
			try (BufferedInputStream in = new BufferedInputStream(new URL(String.format(SystemUtils.getIndexPeDivUrl(), dateFormat.format(start.getTime()))).openStream());
					FileOutputStream fileOutputStream = new FileOutputStream(String.format("%s%s.csv", args[0], dateFormat.format(start.getTime())))) {
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
			start.add(Calendar.DATE, 1);
		}
		return;
	}
*/	
}
