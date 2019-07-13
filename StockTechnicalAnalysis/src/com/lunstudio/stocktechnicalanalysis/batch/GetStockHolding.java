package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockHoldingEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockHoldingSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

@Component
public class GetStockHolding {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockHoldingSrv stockHoldingSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GetStockHolding instance = context.getBean(GetStockHolding.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start(String[] stockCodeList) throws Exception {
		if( stockCodeList != null && stockCodeList.length > 0 ) {
			for(String stockCode : stockCodeList) {
				this.retrieveStockHoldingList(this.stockSrv.getStockInfo(stockCode));
			}
		} else {
			List<StockEntity> stockList = this.stockSrv.getStockInfoList();
			for(StockEntity stock : stockList) {
				this.retrieveStockHoldingList(stock);
				Thread.sleep(2000);
			}
		}
		return;
	}
	
	private void retrieveStockHoldingList(StockEntity stock) throws Exception {
		if( stock.getStockCode().startsWith("HKG:") ) {
			Date lastHoldingDate = this.stockHoldingSrv.getLastHoldingDate(stock.getStockCode());
			Calendar startDate = Calendar.getInstance();
			if( lastHoldingDate != null ) {
				startDate.setTimeInMillis(lastHoldingDate.getTime());
				startDate.add(Calendar.DAY_OF_YEAR, 1);
			} else {
				startDate.add(Calendar.YEAR, -1);
			}
			DateUtils.resetTimestamp(startDate);
			List<StockHoldingEntity> holdingList = this.getShareHolding(stock.getStockCode(), startDate);
			this.stockHoldingSrv.saveStockHoldingList(holdingList);
		}
		
		return;
	}
//https://www.hkexnews.hk/sdw/search/searchsdw_c.aspx
//http://www.hkexnews.hk/sdw/search/searchsdw_c.aspx

	private List<StockHoldingEntity> getShareHolding(String stockCode, Calendar startDate) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		int mb = 1024*1024;
		Calendar today = Calendar.getInstance();
		DateUtils.resetTimestamp(today);
		//System.out.println(String.format("Today: %s", DateUtils.calendarDateToString(today)));
		List<StockHoldingEntity> holdingList = new ArrayList<StockHoldingEntity>();
		int stockNum = Integer.parseInt(stockCode.substring(4));
		String hkexCode = String.format("%05d", stockNum);
		String response = HttpUtils.sendGet(SystemUtils.getShareHoldingUrl());
		Document doc = Jsoup.parse(response);
		Element form = doc.getElementById("form1");
		Elements inputs = form.select("input");
		Map<String, String> params = new HashMap<String, String>();
		for(Element input : inputs) {
			params.put(input.attr("name"), input.attr("value"));
		}
		
		int exceptionCount = 0;
		try {
			while(startDate.compareTo(today) < 0) {
				try {
					if( startDate.get(Calendar.DAY_OF_WEEK) > 1 && startDate.get(Calendar.DAY_OF_WEEK) < 7) {
						int day = startDate.get(Calendar.DAY_OF_MONTH);
						int month = startDate.get(Calendar.MONTH)+1;
						int year = startDate.get(Calendar.YEAR);
						System.out.println(String.format("%s at %02d/%02d/%04d", stockCode, day, month, year));
						String searchingDate = String.format("%04d/%02d/%02d", year, month, day);
						params.put("txtShareholdingDate", searchingDate);
						params.put("txtStockCode", hkexCode);
						params.put("__EVENTTARGET", "btnSearch");
						response = HttpUtils.sendPost(SystemUtils.getShareHoldingUrl(), params);
						doc = Jsoup.parse(response.toString());
						String returnDate = doc.getElementById("hkex_news_header_section").select("div").get(5).select("input").get(0).attr("value");	//YYY/mm/dd
						if( returnDate == null || !returnDate.equals(searchingDate) ) {
							startDate.add(Calendar.DAY_OF_YEAR, 1);
							exceptionCount = 0;
							continue;
						}
						Date holdingDate = new java.sql.Date(startDate.getTimeInMillis());
						holdingList.addAll(this.getStockHoldingList(doc, stockCode, holdingDate));
						System.out.println(String.format("Memory Used: %d - Size:: %d", runtime.freeMemory()/mb, holdingList.size()));
						Thread.sleep(3000);
					}
				} catch(Exception e) {
					Thread.sleep(5000);
					if( exceptionCount++ < 1) {
						continue;
					}
				}
				startDate.add(Calendar.DAY_OF_YEAR, 1);
				exceptionCount = 0;
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}		
		return holdingList;
	}

	private List<StockHoldingEntity> getStockHoldingList(Document doc, String stockCode, Date holdingDate) throws Exception {
		List<StockHoldingEntity> holdingList = new ArrayList<StockHoldingEntity>();
		Element pnlResultNormal = doc.getElementById("pnlResultNormal");
		if( pnlResultNormal != null ) {
			Elements pnlResultNormalTr = pnlResultNormal.select("table").select("tbody").select("tr");//.get(7).select("td").select("table").select("tbody").select("tr");
			for(int i=0; i<pnlResultNormalTr.size(); i++) {
				String holderCode = pnlResultNormalTr.get(i).select("td").get(0).select("div").get(1).text().trim();
				String holderName = pnlResultNormalTr.get(i).select("td").get(1).select("div").get(1).text().trim();
				long shareHolding = NumberFormat.getNumberInstance(java.util.Locale.US).parse(pnlResultNormalTr.get(i).select("td").get(3).select("div").get(1).text().trim()).longValue();
				BigDecimal holdingPrecentage = new BigDecimal(StringUtils.removeEnd(pnlResultNormalTr.get(i).select("td").get(4).select("div").get(1).text().trim(), "%"));
				if( holdingPrecentage.compareTo(BigDecimal.ZERO) > 0 ) {
					if( holderCode.length() > 0 ) {
						//System.out.println(String.format("%s - %s : %d (%.2f)", holderCode, holderName, shareHolding, holdingPrecentage.doubleValue()));
						StockHoldingEntity holding = new StockHoldingEntity();
						holding.setHolderCode(holderCode);
						holding.setHolderName(holderName);
						holding.setStockCode(stockCode);
						holding.setHoldingDate(holdingDate);
						holding.setStockHolding(new BigDecimal(shareHolding));
						holding.setHoldingPercentage(holdingPrecentage);
						holdingList.add(holding);
					}
				} else {
					break;
				}
			}
		}
		return holdingList;
	}
	
	
}


