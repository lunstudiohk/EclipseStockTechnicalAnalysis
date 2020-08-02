package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalDateSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSignalSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.signal.BearishSignal;
import com.lunstudio.stocktechnicalanalysis.signal.BullishSignal;
import com.lunstudio.stocktechnicalanalysis.signal.GeneralSignal;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.FileUtils;
import com.lunstudio.stocktechnicalanalysis.util.MathUtils;

@Component
public class GenerateStockSignalHtml {

	private static final String TEMPLATE = "/template/Template.html";
	private static final String INCOMPLETE_TEMPLATE = "/template/InCompleteTemplate.html";
	private static final String INCOMPLETENOTMEET_TEMPLATE = "/template/InCompleteTemplateNotMeet.html";
	
	private static final String JSON_OUTPUT = "/json/";
	private static final String STOCK_OUTPUT = "/stock/";
	private static final String DATE_OUTPUT = "/date/";
	
	private static final String NAVIGATION_TOKEN = "<!-- Navigation -->";
	private static final String CONTENT_TOKEN = "<!-- Content -->";
	private static final String UPDATE_DATE_TOKEN = "<!-- UpdateDate -->";
	
	private static final Logger logger = LogManager.getLogger();

	private static final Integer LIST_TYPE_STOCK = 1;
	private static final Integer LIST_TYPE_DATE = 2;
	private static final Integer LIST_TYPE_INCOMPLETE = 3;
	private static final Integer LIST_TYPE_INCOMPLETE_NOT_MEET = 4;
	private static final Integer DATA_IN_YEAR = -2;
	
	private SimpleDateFormat NavigationDateFormatter = new SimpleDateFormat("yyyy-MM-dd (EE)");
		
	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private StockSignalSrv stockSignalSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private StockSignalDateSrv stockSignalDateSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateStockSignalHtml instance = context.getBean(GenerateStockSignalHtml.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void start(String[] args) throws Exception {
		
		this.generateDateHtml(25, args[0]);
		this.generateStockHtml(args[0]);
		this.generateInCompleteStockHtml(args[0]);
		this.generateInCompleteNotMeetStockHtml(args[0]);
		
		this.generateStockDataJson(25, args[0]);
		return;
	}
	
	private void generateStockDataJson(Integer size, String outputPath) throws Exception {
		/*
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			Map<String, Object> stockPriceMap = new HashMap<String, Object>();
			List<StockPrice> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceList(stock.getStockCode(), 2500);
			for(StockPrice stockPrice : stockPriceList) {
				String key = String.format("%s%s", stock.getStockShortCode(), DateUtils.getShortDateString(stockPrice.getDate()));
				stockPriceMap.put(key, stockPrice.getData());
			}
			JSONObject json = new JSONObject();
		    json.putAll(stockPriceMap);
		    FileUtils.writeToFile(String.format("%s/%s.json", outputPath, stock.getStockHkexCode()), json.toJSONString());
		}
		*/
		JSONObject json = new JSONObject();

		Date updateDate = this.stockPriceSrv.getLastDailyStockPriceTradeDate(StockEntity.HSI);
		
		Map<String, Object> dateMap = this.stockSignalSrv.getStockSignalDateList(size);
		json.put("StockSignalDate", dateMap);
	    FileUtils.writeToFile(String.format("%s/%s.json", outputPath + JSON_OUTPUT, "StockSignalDateList"), json.toJSONString());
    
	    Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, DATA_IN_YEAR);
        Date startDate = new Date(cal.getTimeInMillis());
		Map<String, Object> stockSignalMap = this.stockSignalSrv.getStockSignalList(null, startDate);
		json = new JSONObject();
		json.put("StockSignal", stockSignalMap);
	    FileUtils.writeToFile(String.format("%s/%s.json", outputPath + JSON_OUTPUT, "StockSignalList"), json.toJSONString());

	    
	    List<Date> tradeDateList = this.stockPriceSrv.getLastDailyStockPriceTradeDateList(StockEntity.HSI, 20, stockPriceSrv.ORDER_BY_ASC);
	    try {
	    Map<String, StockEntity> stockMap =  this.stockSrv.getStockInfoMap();
	    Map<String, List<StockSignalDateEntity>> signalDateMap = new HashMap<String, List<StockSignalDateEntity>>();
		List<StockSignalDateEntity> signalDateList = this.stockSignalDateSrv.getStockSignalHistoricalDateList(null, tradeDateList.get(0));
		for(StockSignalDateEntity signalDate : signalDateList) {
			StockEntity stock = stockMap.get(signalDate.getStockCode());
	    	String stockSignalKey = String.format("%s%s%s%s", stock.getStockHkexCode(), DateUtils.getLongDateString(signalDate.getTradeDate()), signalDate.getSignalType(), signalDate.getSignalSeq());
	    	if( !signalDateMap.containsKey(stockSignalKey) ) {
	    		signalDateMap.put(stockSignalKey, new ArrayList<StockSignalDateEntity>());
	    	}
	    	signalDateMap.get(stockSignalKey).add(signalDate);
		}
		for(String signalKey : signalDateMap.keySet()) {
			List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
			List<StockSignalDateEntity> list = signalDateMap.get(signalKey);
			for(StockSignalDateEntity entity : list) {
				if( entity.getTradeDate().compareTo(entity.getSignalDate()) != 0 ) {
				Map<String, Object> map = new HashMap<String, Object>();
					map.put("tradeDate", DateUtils.getLongDateString(entity.getSignalDate()));
					map.put("price", entity.getSignalPrice());
					map.put("highreturn", entity.getHighReturn().setScale(2, RoundingMode.HALF_UP));
					map.put("highday", entity.getHighDay());
					map.put("lowreturn", entity.getLowReturn().setScale(2, RoundingMode.HALF_UP));
					map.put("lowday", entity.getLowDay());
					finalList.add(map);
				}
			}
			json = new JSONObject();
			json.put("SignalDateList", finalList);
		    FileUtils.writeToFile(String.format("%s/%s.json", outputPath + JSON_OUTPUT, signalKey), json.toJSONString());
		}
		
		List<Object> stockIndexCorList = this.stockPriceSrv.getAllStockIndexCorrelationList();
		json = new JSONObject();
		json.put("StockIndexCorrList", stockIndexCorList);
		json.put("UpdateDate", updateDate.toString());
	    FileUtils.writeToFile(String.format("%s/%s.json", outputPath + JSON_OUTPUT, "StockIndexCorList"), json.toJSONString());
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		return;
	}

	private void generateDateHtml(Integer size, String inputPath) throws Exception {
		String template = FileUtils.readFromFile(inputPath+TEMPLATE);
		List<Date> dateList = this.stockPriceSrv.getLastDailyStockPriceTradeDateList(StockEntity.HSI, size, StockPriceSrv.ORDER_BY_DESC);
		for(Date date : dateList) {
			String output = template.replace(NAVIGATION_TOKEN, this.getNavigationBarHtml(date, null) /*; this.getDateNavigationHtml(date, size)*/);
			output = output.replace(CONTENT_TOKEN, this.getDateContentHtml(date));
			FileUtils.writeToFile(String.format("%s/%s.html", inputPath + DATE_OUTPUT, DateUtils.getLongDateString(date)), output);
		}
		return;
	}
	
	private void generateStockHtml(String inputPath) throws Exception {
		String template = FileUtils.readFromFile(inputPath+TEMPLATE);
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, DATA_IN_YEAR);
        Date startDate = new Date(cal.getTimeInMillis());
        
		for(StockEntity stock : stockList) {
			String output = template.replace(NAVIGATION_TOKEN, this.getNavigationBarHtml(null, stock) /*this.getStockNavigationHtml(stock, stockList)*/);
			output = output.replace(CONTENT_TOKEN, this.getStockContentHtml(stock, startDate));
			FileUtils.writeToFile(String.format("%s/%s.html", inputPath + STOCK_OUTPUT, stock.getStockHkexCode()), output);
		}
		return;
	}

	//未完成未達標交易
	private void generateInCompleteNotMeetStockHtml(String inputPath) throws Exception {
		Date theDate = this.stockPriceSrv.getLastDailyStockPriceTradeDate(StockEntity.HSI);
		String template = FileUtils.readFromFile(inputPath+INCOMPLETENOTMEET_TEMPLATE);
		String output = template.replace(UPDATE_DATE_TOKEN, theDate.toString());
		output = output.replace(CONTENT_TOKEN, this.getInCompleteNotMeetContentHtml());	
		FileUtils.writeToFile(String.format("%s/%s.html", inputPath, "InCompleteNotMeet"), output);
		return;
	}
	
	//未完成交易
	private void generateInCompleteStockHtml(String inputPath) throws Exception {
		Date theDate = this.stockPriceSrv.getLastDailyStockPriceTradeDate(StockEntity.HSI);
		String template = FileUtils.readFromFile(inputPath+INCOMPLETE_TEMPLATE);
		String output = template.replace(UPDATE_DATE_TOKEN, theDate.toString());
		output = output.replace(CONTENT_TOKEN, this.getInCompleteContentHtml());
		FileUtils.writeToFile(String.format("%s/%s.html", inputPath, "InComplete"), output);
		return;
	}
	
	
	/*
	private String trimStockCode(String stockCode) throws Exception {
		if( stockCode.startsWith("0") ) {
			return stockCode.substring(1);
		} else {
			return stockCode;
		}
	}
	*/
	/*
	private String getStockNavigationHtml(StockEntity stock, List<StockEntity> stockList) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("<nav class=\"navbar navbar-expand-sm bg-dark navbar-dark fixed-top pt-0 pb-0\">");
		buf.append("<a class=\"navbar-brand\" href=\"/index.html\"><img src=\"/image/home-icon.png\" alt=\"logo\" style=\"width:30px;\"></a>");
		buf.append(String.format("<ul class=\"navbar-nav\"><li class=\"nav-item text-white\" style=\"font-size: 18px;\">%s %s</li></ul>", this.trimStockCode(stock.getStockHkexCode()), stock.getStockCname()));
		buf.append("<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#collapsibleNavbar\">");
		buf.append("<span class=\"navbar-toggler-icon\"></span>");
		buf.append("</button>");
		buf.append("<div class=\"collapse navbar-collapse\" id=\"collapsibleNavbar\">");
		buf.append("<ul class=\"navbar-nav\">");
		for(StockEntity theStock : stockList) {
			buf.append("<li class=\"nav-item\">");
			buf.append("");
			buf.append(String.format("<a class=\"nav-link\" href=\"/stock/%s.html\">%s %s</a>", theStock.getStockHkexCode(), this.trimStockCode(theStock.getStockHkexCode()), theStock.getStockCname()));
			buf.append("</li>");
		}
		buf.append("</ul>");
		buf.append("</div>");
		buf.append("</nav>");
		return buf.toString();
	}
	
	private String getDateNavigationHtml(Date date, Integer size) throws Exception {
		List<Date> dateList = this.stockPriceSrv.getLastDailyStockPriceTradeDateList(StockSrv.INDEXHANGSENGHSCEI, size, StockPriceSrv.ORDER_BY_DESC);
		StringBuffer buf = new StringBuffer();
		buf.append("<nav class=\"navbar navbar-expand-sm bg-dark navbar-dark fixed-top pt-0 pb-0\">");
		buf.append("<a class=\"navbar-brand\" href=\"/index.html\"><img src=\"/image/home-icon.png\" alt=\"logo\" style=\"width:30px;\"></a>");
		buf.append(String.format("<ul class=\"navbar-nav\"><li class=\"nav-item text-white\" style=\"font-size: 18px;\">%s</li></ul>", NavigationDateFormatter.format(date)));
		buf.append("<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#collapsibleNavbar\">");
		buf.append("<span class=\"navbar-toggler-icon\"></span>");
		buf.append("</button>");
		buf.append("<div class=\"collapse navbar-collapse\" id=\"collapsibleNavbar\">");
		buf.append("<ul class=\"navbar-nav\">");
		for(Date dateItem : dateList) {
			buf.append("<li class=\"nav-item\">");
			buf.append("");
			buf.append(String.format("<a class=\"nav-link\" href=\"/date/%s.html\">%s</a>", dateItem.toString(), NavigationDateFormatter.format(dateItem)));
			buf.append("</li>");
		}
		buf.append("</ul>");
		buf.append("</div>");
		buf.append("</nav>");
		return buf.toString();
	}
	*/
	private String getNavigationBarHtml(Date theDate, StockEntity stock) throws Exception {
		StringBuffer buf = new StringBuffer();
		StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(StockEntity.HSI);
		buf.append("<nav class=\"navbar bg-dark navbar-dark fixed-top pt-0 pb-0 pr-0\">").append("\n");
		buf.append("<a href=\"#stockListModal\" class=\"navbar-brand\" data-toggle=\"modal\" data-target=\"#stockListModal\"><img src=\"/image/icon-menu.png\" style=\"width:30px;\"></a>").append("\n");
		buf.append("<ul class=\"navbar-nav\">");
		buf.append("<li class=\"nav-item text-white text-center pt-0 pb-0\">");
		if( theDate != null ) {
			buf.append(String.format("<a href=\"/index.html\" class=\"text-white\" style=\"font-size: 18px;\">%s</a></br>", theDate.toString())).append("\n");
		} else if( stock != null ) {
			buf.append(String.format("<a href=\"/index.html\" class=\"text-white\" style=\"font-size: 18px;\">%s %s</a></br>", stock.getStockHkexCode(), stock.getStockCname())).append("\n");
		}
		buf.append(String.format("<span style=\"font-size: 14px;\">更新日期：%s</span>", stockPrice.getTradeDate().toString()));
		buf.append("</li>");
		buf.append("</ul>");
		buf.append("<a href=\"#dateListModal\" class=\"navbar-brand\" data-toggle=\"modal\" data-target=\"#dateListModal\"><img src=\"/image/icon-calendar.png\" style=\"width:35px;\"></a>").append("\n");
		buf.append("</nav>").append("\n");
		/*
		if( stock != null ) {
			StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(stock.getStockCode());
			buf.append("<div class=\"card bg-dark text-white fixed-top\" style=\"margin-top:42px;\">").append("\n");
			buf.append("<div class=\"card-body pt-1 pb-0 pl-0 pr-0\">").append("\n");
			buf.append(String.format("<span class=\"text-white pl-3\">更新日期：%s</span>", stockPrice.getTradeDate())).append("\n");
			buf.append(String.format("<span class=\"text-white float-right pr-3\">收市價：%s</span>", stockPrice.getClosePrice().setScale(2, RoundingMode.HALF_EVEN))).append("\n");
			buf.append("</div>").append("\n");
			buf.append("</div>").append("\n");
		}
		*/
		return buf.toString();
	}
	
	private String getStockContentHtml(StockEntity stock, Date startDate) throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockSignalEntity> stockSignalList = this.stockSignalSrv.getStockSignalListAfter(stock.getStockCode(), startDate);
		Map<Date, List<StockSignalEntity>> stockMap = new HashMap<Date, List<StockSignalEntity>>();
		for(StockSignalEntity stockSignal : stockSignalList) {
			if(!stockMap.containsKey(stockSignal.getTradeDate())) {
				stockMap.put(stockSignal.getTradeDate(), new ArrayList<StockSignalEntity>());
			}
			stockMap.get(stockSignal.getTradeDate()).add(stockSignal);
		}
		List<Date> sortedKeys = stockMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		int count = 1;
		boolean isShowAllDiv = false;
		for(Date date: sortedKeys) {
			List<StockSignalEntity> signalList = stockMap.get(date);
			if( signalList.get(0).getCompleted() == 20 && !isShowAllDiv ) {
	    		buf.append("<div class=\"pb-0 pt-0\">");
	    		buf.append("<p class=\"pb-0 pt-0 text-white text-center\" data-toggle=\"collapse\" data-target=\"#collapseExpire\">Show Completed</p>");
	    		buf.append("</div>");
	    		buf.append("<div class=\"collapse\" id=\"collapseExpire\">");
	    		isShowAllDiv = true;
			}
			buf.append(this.getStockSignalHtml(stock, date, signalList, count++, LIST_TYPE_STOCK));
			buf.append("\n<!-- ========== End ========== -->\n");
		}
		if(isShowAllDiv) {
			buf.append("</div>");
		}
		buf.insert(0, this.getStockSignalStatistic(stock.getStockCode(), startDate, stockSignalList));

		return buf.toString();
	}
	
	private String getStockSignalStatistic(String stockCode, Date startDate, List<StockSignalEntity> stockSignalList) throws Exception {
		Map<String, StockSignalDateEntity> stockSignalDateMap = this.stockSignalDateSrv.getStockSignalTradeDateMap(stockCode, startDate);
		int buyCompleted = 0;
		int buyCompletedSuccess = 0;
		int buyInComplete = 0;
		int buyInCompleteSuccess = 0;
		int sellCompleted = 0;
		int sellCompletedSuccess = 0;
		int sellInComplete = 0;
		int sellInCompleteSuccess = 0;
		
		for(StockSignalEntity stockSignal : stockSignalList) {
			StockSignalDateEntity signalDate = stockSignalDateMap.get(stockSignal.getKeyString());
			if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
				if( stockSignal.getCompleted() == 20 ) {
					buyCompleted++;
					if( stockSignal.getTargetReturn().compareTo(signalDate.getHighReturn()) <= 0 ) {
						buyCompletedSuccess++;
					}
				} else {
					buyInComplete++;
					if( stockSignal.getCompleted() > 0 && stockSignal.getTargetReturn().compareTo(signalDate.getHighReturn()) <= 0 ) {
						buyInCompleteSuccess++;
					}
				}
			} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
				if( stockSignal.getCompleted() == 20 ) {
					sellCompleted++;
					if( stockSignal.getTargetReturn().compareTo(signalDate.getLowReturn()) >= 0 ) {
						sellCompletedSuccess++;
					}
				} else {
					sellInComplete++;
					if( stockSignal.getCompleted() > 0 && stockSignal.getTargetReturn().compareTo(signalDate.getLowReturn()) >= 0 ) {
						sellInCompleteSuccess++;
					}
				}
			}
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<div class=\"card bg-dark text-white\" style=\"margin-top:55px;\">\n");
		buf.append("<div class=\"card-body pt-1 pb-0 pl-0 pr-0\">\n");
		buf.append("<table class=\"table table-dark table-striped\">\n");
		
		buf.append("<tr>");
		buf.append("<td class=\"text-left pl-1 pr-0 pt-1 pb-1\">已完成</td>");
		buf.append("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\"><span class=\"text-success\">買入：</span></td>");
		buf.append(String.format("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\">%s次</td>", buyCompleted));
		buf.append(String.format("<td class=\"text-left pl-0 pr-0 pt-1 pb-1\">[%s%%]</td>", 
				MathUtils.getPrecentage(new BigDecimal(buyCompletedSuccess), new BigDecimal(buyCompleted)) ));
		buf.append("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\"><span class=\"text-danger\">賣出：</span></td>");          
		buf.append(String.format("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\">%s次</td>", sellCompleted ));
		buf.append(String.format("<td class=\"text-left pl-0 pr-0 pt-1 pb-1\">[%s%%]</td>", 
				MathUtils.getPrecentage(new BigDecimal(sellCompletedSuccess), new BigDecimal(sellCompleted)) ));
		buf.append("</tr>\n");
		
		buf.append("<tr>");
		buf.append("<td class=\"text-left pl-1 pr-0 pt-1 pb-1\">未完成</td>");
		buf.append("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\"><span class=\"text-success\">買入：</span></td>");
		buf.append(String.format("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\">%s次</td>", buyInComplete));
		buf.append(String.format("<td class=\"text-left pl-0 pr-0 pt-1 pb-1\">[%s%%]</td>", 
				MathUtils.getPrecentage(new BigDecimal(buyInCompleteSuccess), new BigDecimal(buyInComplete)) ));
		buf.append("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\"><span class=\"text-danger\">賣出：</span></td>");          
		buf.append(String.format("<td class=\"text-right pl-0 pr-0 pt-1 pb-1\">%s次</td>", sellInComplete ));
		buf.append(String.format("<td class=\"text-left pl-0 pr-0 pt-1 pb-1\">[%s%%]</td>", 
				MathUtils.getPrecentage(new BigDecimal(sellInCompleteSuccess), new BigDecimal(sellInComplete)) ));
		buf.append("</tr>\n");
		buf.append("</table>");
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	
	private String getInCompleteNotMeetContentHtml() throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockSignalEntity> stockSignalList = this.stockSignalSrv.getInCompleteStockSignalList();
		Map<Date, List<StockSignalEntity>> dateStockListMap = new HashMap<Date, List<StockSignalEntity>>();
		for(StockSignalEntity stockSignal : stockSignalList) {
			if( !dateStockListMap.containsKey(stockSignal.getTradeDate()) ) {
				dateStockListMap.put(stockSignal.getTradeDate(), new ArrayList<StockSignalEntity>());
			}
			dateStockListMap.get(stockSignal.getTradeDate()).add(stockSignal);
		}
		List<Date> sortedDate = dateStockListMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		Date startDate = sortedDate.get(sortedDate.size()-1);
		Map<String, StockSignalDateEntity> stockSignalTradeDateMap = this.stockSignalDateSrv.getStockSignalTradeDateMap(startDate);
		int count = 1;
		
		for(Date date: sortedDate) {
			List<StockSignalEntity> dateStockSignalList = dateStockListMap.get(date);
			Map<String, List<StockSignalEntity>> stockMap = new HashMap<String, List<StockSignalEntity>>();
			for(StockSignalEntity stockSignal : dateStockSignalList) {
				if( this.isSignalHitted(stockSignal, stockSignalTradeDateMap) ) {
					continue;
				}
				if(!stockMap.containsKey(stockSignal.getStockCode())) {
					stockMap.put(stockSignal.getStockCode(), new ArrayList<StockSignalEntity>());
				}
				stockMap.get(stockSignal.getStockCode()).add(stockSignal);
			}
			List<String> sortedStockCode = stockMap.keySet().stream().sorted().collect(Collectors.toList());
			for(String stockCode: sortedStockCode) {
				StockEntity stock = this.stockSrv.getStockInfo(stockCode);
				List<StockSignalEntity> signleStockSignalList = stockMap.get(stockCode);
				buf.append(this.getStockSignalHtml(stock, date, signleStockSignalList, count++, LIST_TYPE_INCOMPLETE_NOT_MEET));
				buf.append("\n<!-- ========== End ========== -->\n");
			}
		}
		return buf.toString();
	}
	
	private boolean isSignalHitted(StockSignalEntity stockSignal, Map<String, StockSignalDateEntity> stockSignalTradeDateMap) {
		if( stockSignal.getCompleted() == 0 ) {
			return true;
		}
		StockSignalDateEntity dateEntity = stockSignalTradeDateMap.get(stockSignal.getKeyString());
		if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) && dateEntity.getHighReturn().compareTo(stockSignal.getTargetReturn()) > 0 ) {
			return true;
		} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) && dateEntity.getLowReturn().compareTo(stockSignal.getTargetReturn()) < 0 ) {
			return true;
		}
		return false;
	}
	
	private String getInCompleteContentHtml() throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockSignalEntity> stockSignalList = this.stockSignalSrv.getInCompleteStockSignalList();
		Map<Date, List<StockSignalEntity>> dateStockListMap = new HashMap<Date, List<StockSignalEntity>>();
		for(StockSignalEntity stockSignal : stockSignalList) {
			if( !dateStockListMap.containsKey(stockSignal.getTradeDate()) ) {
				dateStockListMap.put(stockSignal.getTradeDate(), new ArrayList<StockSignalEntity>());
			}
			dateStockListMap.get(stockSignal.getTradeDate()).add(stockSignal);
		}
		List<Date> sortedDate = dateStockListMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		int count = 1;
		//buf.append("<div style=\"margin-top:60px;\"></div>");
		for(Date date: sortedDate) {
			List<StockSignalEntity> dateStockSignalList = dateStockListMap.get(date);
			Map<String, List<StockSignalEntity>> stockMap = new HashMap<String, List<StockSignalEntity>>();
			for(StockSignalEntity stockSignal : dateStockSignalList) {
				if(!stockMap.containsKey(stockSignal.getStockCode())) {
					stockMap.put(stockSignal.getStockCode(), new ArrayList<StockSignalEntity>());
				}
				stockMap.get(stockSignal.getStockCode()).add(stockSignal);
			}
			List<String> sortedStockCode = stockMap.keySet().stream().sorted().collect(Collectors.toList());
			for(String stockCode: sortedStockCode) {
				StockEntity stock = this.stockSrv.getStockInfo(stockCode);
				List<StockSignalEntity> signleStockSignalList = stockMap.get(stockCode);
				buf.append(this.getStockSignalHtml(stock, date, signleStockSignalList, count++, LIST_TYPE_INCOMPLETE));
				buf.append("\n<!-- ========== End ========== -->\n");
			}
		}
		return buf.toString();
	}
	
	private String getDateContentHtml(Date date) throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockSignalEntity> stockSignalList = this.stockSignalSrv.getAllStockSignalListOnDate(date);
		Map<String, List<StockSignalEntity>> stockMap = new HashMap<String, List<StockSignalEntity>>();
		for(StockSignalEntity stockSignal : stockSignalList) {
			if(!stockMap.containsKey(stockSignal.getStockCode())) {
				stockMap.put(stockSignal.getStockCode(), new ArrayList<StockSignalEntity>());
			}
			stockMap.get(stockSignal.getStockCode()).add(stockSignal);
		}
		
		buf.append("<div style=\"margin-top:55px;\"></div>");
		int count = 1;
		List<String> sortedKeys = stockMap.keySet().stream().sorted().collect(Collectors.toList());
		for(String stockCode: sortedKeys) {
			StockEntity stock = this.stockSrv.getStockInfo(stockCode);
			stockSignalList = stockMap.get(stockCode);
			buf.append(this.getStockSignalHtml(stock, null, stockSignalList, count++, LIST_TYPE_DATE));
			buf.append("\n<!-- ========== End ========== -->\n");
		}
		return buf.toString();
	}

	//🉐 127568
	private String getSignalStatusIcon(StockSignalEntity stockSignal, StockSignalDateEntity signalDate) throws Exception {
		if( stockSignal.getCompleted() == 0 ) {
			//return "<span class=\"float-right\">&#128591;</span>";
			return "<span class=\"float-right badge badge-light\">進行中</span>";
		} else {
			//StockSignalDateEntity signalDate = this.stockSignalDateSrv.getStockSignalDate(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), stockSignal.getTradeDate());
			if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
				/*
				if( signalDate.getHighReturn().compareTo(stockSignal.getUpperMedian()) >= 0 ) {
					return "<span class=\"float-right\">&#11088;&#11088;</span>";
				} else if( signalDate.getHighReturn().compareTo(stockSignal.getTargetReturn()) >= 0 ) {
					return "<span class=\"float-right\">&#11088;</span>";
				} else {
					if( stockSignal.getCompleted() == 20 ) {
						return "<span class=\"float-right\">&#10060;</span>";
					} else {
						return "<span class=\"float-right\">&#10067;</span>";
					}
				} 
				*/
				if( signalDate.getHighReturn().compareTo(stockSignal.getTargetReturn()) >= 0 ) {
					return "<span class=\"float-right badge badge-success\">成功</span>";
				}
			} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
				/*
				if( signalDate.getLowReturn().compareTo(stockSignal.getLowerMedian()) <= 0 ) {
					return "<span class=\"float-right\">&#127843;&#127843;</span>";
				} else if( signalDate.getLowReturn().compareTo(stockSignal.getTargetReturn()) <= 0 ) {
					return "<span class=\"float-right\">&#127843;</span>";
				} else {
					if( stockSignal.getCompleted() == 20 ) {
						return "<span class=\"float-right\">&#10060;</span>";
					} else {
						return "<span class=\"float-right\">&#10067;</span>";
					}
				}
				*/
				if( signalDate.getLowReturn().compareTo(stockSignal.getTargetReturn()) <= 0 ) {
					return "<span class=\"float-right badge badge-success\">成功</span>";
				}
			}
		}
		if( stockSignal.getCompleted() == 20 ) {
			return "<span class=\"float-right badge badge-danger\">失敗</span>";
		} else {
			return "<span class=\"float-right badge badge-light\">進行中</span>";	
		}
	}
	
	private String getStockSignalHtml(StockEntity stock, Date date, List<StockSignalEntity>stockSignalList, Integer count, Integer listType) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("\n\n");
		/*
		if( count == 1 ) {
			if( stock == null ) {
				buf.append("<div class=\"card rounded-top border-dark\" style=\"margin-top:0px;\">");
			} else {
				buf.append("<div class=\"card rounded-top border-dark\" style=\"margin-top:45px;\">");
			} 
		} else {
			buf.append("<div class=\"card rounded-top border-dark\">");
		}
		*/
		buf.append("<div class=\"card rounded-top border-dark\" style=\"margin-top:0px;\">");
		buf.append("<div class=\"card-header bg-secondary pt-0 pb-0 pl-2 pr-2 font-weight-bold\" style=\"font-size: 18px;\">");
		StockPriceEntity stockPrice = this.stockPriceSrv.getLatestDailyStockPriceEntity(stock.getStockCode());
		if( listType == LIST_TYPE_DATE ) {
			buf.append(String.format("<a href=\"/stock/%s.html\" class=\"text-white\">%s %s</a>", stock.getStockHkexCode(), stock.getTrimStockHexCode(), stock.getStockCname()));
			buf.append(String.format("<span class=\"float-right text-white\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">收市價：%s</span>", 
					stockPrice.getTradeDate().toString(), stockPrice.getClosePrice().setScale(2, RoundingMode.HALF_UP)));
		} else if( listType == LIST_TYPE_STOCK ) {
			buf.append(String.format("<a href=\"/date/%s.html\" class=\"text-white\">%s</a>", DateUtils.getLongDateString(date), date.toString()));
		} else {
			buf.append(String.format("<a href=\"/stock/%s.html\" class=\"text-white\">%s - %s</a>&nbsp;<a href=\"/date/%s.html\" class=\"text-white float-right\">%s</a>", 
					stock.getStockHkexCode(), stock.getTrimStockHexCode(), stock.getStockCname(), DateUtils.getLongDateString(date), date.toString()) );
			//buf.append("<div class=\"bg-secondary pl-0 pr-0 pt-0 pb-0\">");
			//buf.append(String.format("<span class=\"text-white\" style=\"font-size: 16px;\">更新日期：%s</span>", stockPrice.getTradeDate()));
			//buf.append(String.format("<span class=\"text-white float-right\" style=\"font-size: 16px;\"><marquee>收市價：%s</marquee></span>", stockPrice.getClosePrice().setScale(2, RoundingMode.HALF_UP)));
			//buf.append("</div>");
		}
		buf.append("</div>");
		buf.append("<div class=\"card-body bg-dark pl-1 pr-1 pt-1 pb-2\">");
		buf.append(String.format("<div id=\"signal%s\" class=\"carousel slide\">", count));
		
		if( stockSignalList.size() > 1 ) {
			buf.append("<ul class=\"carousel-indicators\">");
		    for(int i=0;i<stockSignalList.size();i++) {
		    	if( i == 0 ) {
		    		buf.append("<li data-target=\"#signal" + count + "\" data-slide-to=\"0\" class=\"active\"></li>");
		    	} else {
		    		buf.append(String.format("<li data-target=\"#signal" + count + "\" data-slide-to=\"%s\"></li>", i));
		    	}
		    }
		    buf.append("</ul>").append("\n");
		}
	    buf.append("<div class=\"carousel-inner\">").append("\n");
	    //StockSignalDateEntity currentStockSignalDate = null;

	    for(int i=0;i<stockSignalList.size();i++) {
	    	StockSignalEntity stockSignal = stockSignalList.get(i);
		    StockSignalDateEntity currentStockSignalDate = null;//this.stockSignalDateSrv.getStockSignalDate(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), stockSignal.getTradeDate());
		    String min = "";
		    String buyMedian = "";
		    String buyMax = "";
		    String sellMedian = "";
		    String sellMax = "";
		    if( stockSignal.getCompleted() > 0 ) {
				if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
					if( currentStockSignalDate.getHighReturn().compareTo(stockSignal.getUpperMax()) >= 0 ) {
						buyMax = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					} else if( currentStockSignalDate.getHighReturn().compareTo(stockSignal.getUpperMedian()) >= 0 ) {
						buyMedian = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					} else if( currentStockSignalDate.getHighReturn().compareTo(stockSignal.getTargetReturn()) >= 0 ) {
						min = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					} 
				} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
					if( currentStockSignalDate.getLowReturn().compareTo(stockSignal.getLowerMin()) <= 0 ) {
						sellMax = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					} else if( currentStockSignalDate.getLowReturn().compareTo(stockSignal.getLowerMedian()) <= 0 ) {
						sellMedian = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					} else if( currentStockSignalDate.getLowReturn().compareTo(stockSignal.getTargetReturn()) <= 0 ) {
						min = "<span class=\"align-top\" style=\"font-size: 10px;\">&nbsp;&#11088;</span>";
					}
				}
		    }
	    	/*
	    	List<StockSignalDateEntity> stockSignalDateList = this.stockSignalDateSrv.getStockSignalDateList(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), StockSignalDateEntity.DESC);
	    	stockSignal.setStockSignalDateList(stockSignalDateList);
	    	currentStockSignalDate = stockSignalDateList.get(0);
	    	*/
	    	if( i == 0 ) {
	    		buf.append("<div class=\"carousel-item active\">").append("\n");
	    	} else {
	    		buf.append("<div class=\"carousel-item\">").append("\n");
	    	}
	    	buf.append("<table class=\"table table-dark table-striped table-borderless\">").append("\n");
	    	buf.append("<tbody>\n");
	    	
	    	List<String> signalDescList = null;
	    	if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
	    		signalDescList = BullishSignal.getDailyBullishPrimarySignalDesc(stockSignal);
	    	} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
	    		signalDescList = BearishSignal.getDailyBearishPrimarySignalDesc(stockSignal);
	    	}

	    	for(int k=0; k<signalDescList.size(); k++) {
	    		if( k == 0 ) {
	    			buf.append("\n<tr>\n");
	    			if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
	    				buf.append("<td colspan=\"6\" class=\"text-center pl-0 pr-0 pt-1 pb-0\"><span class=\"text-success\">買入</span>信號：");    				
	    			} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
	    				buf.append("<td colspan=\"6\" class=\"text-center pl-1 pr-1 pt-0 pb-0\"><span class=\"text-danger\">賣出</span>信號：");
	    			}
	    			buf.append(signalDescList.get(k));
    				buf.append(this.getSignalStatusIcon(stockSignal, currentStockSignalDate));
    				//buf.append(String.format("<span class=\"float-right\">%s</span>", stockPrice.getClosePrice()));
	    			buf.append("</td>\n");
		    		buf.append("</tr>");	
	    		} else {
	    			buf.append("\n<tr></tr>\n");
	    			buf.append(String.format("<tr><td colspan=\"6\" class=\"text-center pl-1 pr-1 pt-0 pb-0\">%s</tr>", signalDescList.get(k)));
	    		}
	    	}
	    	
	    	// ===================================
	    	buf.append("<tr>\n");
	    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">出現次數<br/>%s次</td>", stockSignal.getCount()));
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">信心指數<br/>" + stockSignal.getConfident().setScale(2, RoundingMode.HALF_UP) + "%</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">最小回報<br/>");
	    	if( stockSignal.getTargetReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getTargetReturn(), 2), stockSignal.getTargetReturn().setScale(2, RoundingMode.HALF_UP), min));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getTargetReturn(), 2), stockSignal.getTargetReturn().setScale(2, RoundingMode.HALF_UP), min));
	    	}
	    	buf.append("</td>\n</tr>\n");
	    	// ===================================
	    	buf.append("<tr>\n");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最小升幅<br/>");
	    	if( stockSignal.getUpperMin().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMin(), 2), stockSignal.getUpperMin().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMin(), 2), stockSignal.getUpperMin().setScale(2, RoundingMode.HALF_UP)));
	    	}
	    	buf.append("</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">升幅中位數<br/>");
	    	if( stockSignal.getUpperMedian().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMedian(), 2), stockSignal.getUpperMedian().setScale(2, RoundingMode.HALF_UP), buyMedian));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMedian(), 2), stockSignal.getUpperMedian().setScale(2, RoundingMode.HALF_UP), buyMedian));
	    	}
	    	buf.append("</td>");
	    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最大升幅 [%s]<br/>", stockSignal.getUpperDayMedian()));
	    	if( stockSignal.getUpperMax().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMax(), 2), stockSignal.getUpperMax().setScale(2, RoundingMode.HALF_UP), buyMax));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMax(), 2), stockSignal.getUpperMax().setScale(2, RoundingMode.HALF_UP), buyMax));
	    	}
	    	buf.append("</td>\n</tr>\n");
	    	// ===================================
	    	buf.append("<tr>\n");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最小跌幅<br/>");
	    	if( stockSignal.getLowerMax().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMax(), 2), stockSignal.getLowerMax().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMax(), 2), stockSignal.getLowerMax().setScale(2, RoundingMode.HALF_UP)));
	    	}
	    	buf.append("</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">跌幅中位數<br/>");
	    	if( stockSignal.getLowerMedian().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMedian(), 2), stockSignal.getLowerMedian().setScale(2, RoundingMode.HALF_UP), sellMedian));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMedian(), 2), stockSignal.getLowerMedian().setScale(2, RoundingMode.HALF_UP), sellMedian));
	    	}
	    	buf.append("</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最大跌幅 [" + stockSignal.getLowerDayMedian() + "]<br/>");
	    	if( stockSignal.getLowerMin().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMin(), 2), stockSignal.getLowerMin().setScale(2, RoundingMode.HALF_UP), sellMax));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>%s",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMin(), 2), stockSignal.getLowerMin().setScale(2, RoundingMode.HALF_UP), sellMax));
	    	}
	    	buf.append("</td>\n</tr>\n");
	    	// ===================================
	    	buf.append("<tr>\n");
	    	String stockSignalKey = String.format("%s%s%s%s", stock.getStockHkexCode(), DateUtils.getLongDateString(stockSignal.getTradeDate()), stockSignal.getSignalType(), stockSignal.getSignalSeq());
	    	if( stockSignal.getCompleted() == 20 ) {
	    		buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">參考價格 [%s]<br/>", stockSignal.getCompleted()));
	    	} else {
	    		buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\" data-toggle=\"modal\" data-target=\"#historicalListModal\" data-id=\"%s\">參考價格 [%s]<br/>", 
	    			stockSignalKey, stockSignal.getCompleted()));
	    	}
	    	buf.append(String.format("<span style=\"font-size: 18px;\">%s</span>", currentStockSignalDate.getSignalPrice().setScale(2, RoundingMode.HALF_UP)));
			buf.append("</td>");
			if( stockSignal.getCompleted() == 0 ) {
				buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">最高價格 [0]<br/>");
				buf.append("<a class=\"text-success\" style=\"font-size: 18px;\">--</a></td>");
				buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">最低價格 [0]<br/>");
				buf.append("<a class=\"text-success\" style=\"font-size: 18px;\">--</a></td>");
			} else {
		    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最高價格 [%s]<br/>", currentStockSignalDate.getHighDay()));
		    	if( currentStockSignalDate.getHighReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
					buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
						MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getHighReturn(), 2), currentStockSignalDate.getHighReturn().setScale(2, RoundingMode.HALF_UP)));
				} else {
					buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
							MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getHighReturn(), 2), currentStockSignalDate.getHighReturn().setScale(2, RoundingMode.HALF_UP)));
				}
				buf.append("</td>");
				
				buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最低價格 [%s]<br/>", currentStockSignalDate.getLowDay()));
		    	if( currentStockSignalDate.getLowReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
					buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
						MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getLowReturn(), 2), currentStockSignalDate.getLowReturn().setScale(2, RoundingMode.HALF_UP)));
				} else {
					buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
							MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getLowReturn(), 2), currentStockSignalDate.getLowReturn().setScale(2, RoundingMode.HALF_UP)));
				}
				buf.append("</td>");
			}
	    	buf.append("\n</tr>\n");
	    	// =============== Signal Date List ====================
	    	/*
	    	buf.append(String.format("<tr class=\"collapse\" id=\"accordion%s%s\">\n", count, i));
			buf.append("<th colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">信號日期</th>");
			buf.append(String.format("<th colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最大升幅 [%s]</th>", stockSignal.getUpperDayMedian()));
			buf.append(String.format("<th colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最大跌幅 [%s]</th>", stockSignal.getLowerDayMedian()));
			buf.append("</tr>\n");
	    	for(int j=1; j<stockSignalDateList.size(); j++) {
	    		StockSignalDateEntity dateEntity = stockSignalDateList.get(j);
	    		buf.append(String.format("<tr class=\"collapse\" id=\"accordion%s%s\">\n", count, i));
	    		buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">");
				buf.append(String.format("<a class=\"text-white\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s</a></td>", 
						dateEntity.getSignalPrice().setScale(2, RoundingMode.HALF_UP), dateEntity.getSignalDate().toString()));
				buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">");
				if( dateEntity.getHighReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
					buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%<span class=\"text-white\"> [%s]</span></a></td>", 
							MathUtils.getFinalPrice(dateEntity.getSignalPrice(), dateEntity.getHighReturn(), 2), dateEntity.getHighReturn().setScale(2, RoundingMode.HALF_UP), dateEntity.getHighDay()));
				} else {
					buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%<span class=\"text-white\"> [%s]</span></a></td>", 
							MathUtils.getFinalPrice(dateEntity.getSignalPrice(), dateEntity.getHighReturn(), 2), dateEntity.getHighReturn().setScale(2, RoundingMode.HALF_UP), dateEntity.getHighDay()));
				}
				buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">");
				if( dateEntity.getLowReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
					buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%<span class=\"text-white\"> [%s]</span></a></td>", 
							MathUtils.getFinalPrice(dateEntity.getSignalPrice(), dateEntity.getLowReturn(), 2), dateEntity.getLowReturn().setScale(2, RoundingMode.HALF_UP), dateEntity.getLowDay()));
				} else {
					buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%<span class=\"text-white\"> [%s]</span></a></td>", 
							MathUtils.getFinalPrice(dateEntity.getSignalPrice(), dateEntity.getLowReturn(), 2), dateEntity.getLowReturn().setScale(2, RoundingMode.HALF_UP), dateEntity.getLowDay()));
				}
	    		buf.append("<tr>\n");
	    	}
	    	*/
	    	// ===================================       
	    	buf.append("</tbody></table></div>");
	    }
	    buf.append("</div></div></div>");
	    buf.append("\n");
		buf.append("</div>\n");
		return buf.toString();
	}
	
}
