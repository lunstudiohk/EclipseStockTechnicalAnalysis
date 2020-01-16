package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	private static final String TEMPLATE = "/Template.html";
		
	private static final String NAVIGATION_TOKEN = "<!-- Navigation -->";
	private static final String CONTENT_TOKEN = "<!-- Content -->";
	
	private static final Logger logger = LogManager.getLogger();

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
		this.generateDateHtml(50, args[0], args[1]);
		this.generateStockHtml(50, args[0], args[1]);
		this.generateStockPriceJson(args[2]);
		return;
	}
	
	private void generateStockPriceJson(String outputPath) throws Exception {
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
		return;
	}
	
	private void generateDateHtml(Integer size, String inputPath, String outputPath) throws Exception {
		String template = FileUtils.readFromFile(inputPath+TEMPLATE);
		List<Date> dateList = this.stockPriceSrv.getLastDailyStockPriceTradeDateList(StockSrv.INDEXHANGSENGHSCEI, size, StockPriceSrv.ORDER_BY_DESC);
		for(Date date : dateList) {
			String output = template.replace(NAVIGATION_TOKEN, this.getDateNavigationHtml(date, size));
			output = output.replace(CONTENT_TOKEN, this.getDateContentHtml(date));
			FileUtils.writeToFile(String.format("%s/%s.html", outputPath,date.toString()), output);
		}
		return;
	}
	
	private void generateStockHtml(Integer size, String inputPath, String outputPath) throws Exception {
		String template = FileUtils.readFromFile(inputPath+TEMPLATE);
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			String output = template.replace(NAVIGATION_TOKEN, this.getStockNavigationHtml(stock, stockList));
			output = output.replace(CONTENT_TOKEN, this.getStockContentHtml(stock));
			FileUtils.writeToFile(String.format("%s/%s.html", outputPath, stock.getStockHkexCode()), output);
		}
		return;
	}

	
	
	private String trimStockCode(String stockCode) throws Exception {
		if( stockCode.startsWith("0") ) {
			return stockCode.substring(1);
		} else {
			return stockCode;
		}
	}
	private String getStockNavigationHtml(StockEntity stock, List<StockEntity> stockList) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("<nav class=\"navbar navbar-expand-sm bg-dark navbar-dark fixed-top\">");
		buf.append(String.format("<a class=\"navbar-brand\">%s %s</a>", this.trimStockCode(stock.getStockHkexCode()), stock.getStockCname()));
		buf.append("<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#collapsibleNavbar\">");
		buf.append("<span class=\"navbar-toggler-icon\"></span>");
		buf.append("</button>");
		buf.append("<div class=\"collapse navbar-collapse\" id=\"collapsibleNavbar\">");
		buf.append("<ul class=\"navbar-nav\">");
		for(StockEntity theStock : stockList) {
			buf.append("<li class=\"nav-item\">");
			buf.append("");
			buf.append(String.format("<a class=\"nav-link\" href=\"./%s.html\">%s %s</a>", theStock.getStockHkexCode(), this.trimStockCode(theStock.getStockHkexCode()), theStock.getStockCname()));
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
		buf.append("<nav class=\"navbar navbar-expand-sm bg-dark navbar-dark fixed-top\">");
		buf.append(String.format("<a class=\"navbar-brand\">%s</a>", NavigationDateFormatter.format(date)));	// : 買賣策略
		buf.append("<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#collapsibleNavbar\">");
		buf.append("<span class=\"navbar-toggler-icon\"></span>");
		buf.append("</button>");
		buf.append("<div class=\"collapse navbar-collapse\" id=\"collapsibleNavbar\">");
		buf.append("<ul class=\"navbar-nav\">");
		for(Date dateItem : dateList) {
			buf.append("<li class=\"nav-item\">");
			buf.append("");
			buf.append(String.format("<a class=\"nav-link\" href=\"./%s.html\">%s</a>", dateItem.toString(), NavigationDateFormatter.format(dateItem)));
			buf.append("</li>");
		}
		buf.append("</ul>");
		buf.append("</div>");
		buf.append("</nav>");
		return buf.toString();
	}
	
	private String getStockContentHtml(StockEntity stock) throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockSignalEntity> stockSignalList = this.stockSignalSrv.getStockSignalList(stock.getStockCode());
		Map<Date, List<StockSignalEntity>> stockMap = new HashMap<Date, List<StockSignalEntity>>();
		for(StockSignalEntity stockSignal : stockSignalList) {
			if(!stockMap.containsKey(stockSignal.getTradeDate())) {
				stockMap.put(stockSignal.getTradeDate(), new ArrayList<StockSignalEntity>());
			}
			stockMap.get(stockSignal.getTradeDate()).add(stockSignal);
		}
		List<Date> sortedKeys = stockMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		int count = 1;
		for(Date date: sortedKeys) {
			stockSignalList = stockMap.get(date);
			buf.append(this.getStockSignalHtml(null, date, stockSignalList, count++));
			buf.append("\n<!-- ========== End ========== -->\n");
		}
		/*
		count = 1;
		for(Date date: sortedKeys) {
			stockSignalList = stockMap.get(date);
			buf.append(this.getStockSignalDateHtml(stockSignalList, count++));
		}
		*/
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
		
		int count = 1;
		List<String> sortedKeys = stockMap.keySet().stream().sorted().collect(Collectors.toList());
		for(String stockCode: sortedKeys) {
			StockEntity stock = this.stockSrv.getStockInfo(stockCode);
			stockSignalList = stockMap.get(stockCode);
			buf.append(this.getStockSignalHtml(stock, null, stockSignalList, count++));
			buf.append("\n<!-- ========== End ========== -->\n");
		}
		/*
		count = 1;
		for(String stockCode: sortedKeys) {
			stockSignalList = stockMap.get(stockCode);
			buf.append(this.getStockSignalDateHtml(stockSignalList, count++));
		}
		*/
		return buf.toString();
	}

	private String getStockSignalHtml(StockEntity stock, Date date, List<StockSignalEntity>stockSignalList, Integer count) throws Exception {
		StringBuffer buf = new StringBuffer();
		if( count == 1 ) {
			buf.append("<div class=\"card rounded-top border-dark\" style=\"margin-top:60px;\">");
		} else {
			buf.append("<div class=\"card rounded-top border-dark\">");
		}
		buf.append("<div class=\"card-header bg-secondary pt-0 pb-0 pl-2 pr-2 font-weight-bold\" style=\"font-size: 18px;\">");
		if( stock != null ) {
			buf.append(String.format("<a href=\"./%s.html\" class=\"text-white\">%s %s</a>", stock.getStockHkexCode(), stock.getStockHkexCode(), stock.getStockCname()));
			//buf.append("<img src=\"./image/icon-chart.png\" class=\"float-right pt-1\" width=\"24\" height=\"24\">");
			//buf.append("<span class=\"float-right pt-1 font-weight-bold text-danger\" style=\"font-size: 18px;\">&star;</span>");
		} else if( date != null ) {
			buf.append(String.format("<a href=\"./%s.html\" class=\"text-white\">%s</a>", date.toString(), date.toString()));
			//buf.append("<span class=\"float-right pt-1 font-weight-bold text-danger\" style=\"font-size: 18px;\">&star;</span>");
		}
		buf.append("</div></div>");
	
		buf.append("<div class=\"card-body bg-dark pl-1 pr-1 pt-1 pb-2\">");
		buf.append(String.format("<div id=\"signal%s\" class=\"carousel\">", count));
		
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
	    StockSignalDateEntity currentStockSignalDate = null;
	    for(int i=0;i<stockSignalList.size();i++) {
	    	StockSignalEntity stockSignal = stockSignalList.get(i);
	    	List<StockSignalDateEntity> stockSignalDateList = this.stockSignalDateSrv.getStockSignalDateList(stockSignal.getStockCode(), stockSignal.getTradeDate(), stockSignal.getSignalSeq(), stockSignal.getSignalType(), StockSignalDateEntity.DESC);
	    	stockSignal.setStockSignalDateList(stockSignalDateList);
	    	currentStockSignalDate = stockSignalDateList.get(0);
	    	if( i == 0 ) {
	    		buf.append("<div class=\"carousel-item active\">").append("\n");
	    	} else {
	    		buf.append("<div class=\"carousel-item\">").append("\n");
	    	}
	    	buf.append("<table class=\"table table-dark table-striped\">").append("\n");
	    	buf.append("<tbody>\n");
	    	buf.append("<tr>\n");
	    	if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_BUY) ) {
	    		buf.append("<td colspan=\"6\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"50%\"><span class=\"text-success\">買入</span>信號：");
	    		buf.append(BullishSignal.getDailyBullishPrimarySignalDesc(stockSignal)).append("</br>");
	    		buf.append(GeneralSignal.getSecondarySignalDesc(stockSignal));
	    		buf.append("</td>");
	    	} else if( stockSignal.getSignalType().equals(StockSignalEntity.SIGNAL_TYPE_SELL) ) {
	    		buf.append("<td colspan=\"6\" class=\"text-center pl-1 pr-1 pt-1 pb-1\" width=\"50%\"><span class=\"text-danger\">賣出</span>信號：");
	    		buf.append(BearishSignal.getDailyBearishPrimarySignalDesc(stockSignal)).append("</br>"); 
	    		buf.append(GeneralSignal.getSecondarySignalDesc(stockSignal));
	    		buf.append("</td>");
	    	}
	    	buf.append("</td>\n</tr>\n");
	    	// ===================================
	    	buf.append("<tr>\n");
	    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">出現次數<br/>%s次</td>", stockSignal.getCount()));
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">信心指數<br/>" + stockSignal.getConfident().setScale(2, RoundingMode.HALF_UP) + "%</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%\">最小回報<br/>");
	    	if( stockSignal.getTargetReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getTargetReturn(), 2), stockSignal.getTargetReturn().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getTargetReturn(), 2), stockSignal.getTargetReturn().setScale(2, RoundingMode.HALF_UP)));
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
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMedian(), 2), stockSignal.getUpperMedian().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMedian(), 2), stockSignal.getUpperMedian().setScale(2, RoundingMode.HALF_UP)));
	    	}
	    	buf.append("</td>");
	    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最大升幅 [%s]<br/>", stockSignal.getUpperDayMedian()));
	    	if( stockSignal.getUpperMax().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMax(), 2), stockSignal.getUpperMax().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getUpperMax(), 2), stockSignal.getUpperMax().setScale(2, RoundingMode.HALF_UP)));
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
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMedian(), 2), stockSignal.getLowerMedian().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMedian(), 2), stockSignal.getLowerMedian().setScale(2, RoundingMode.HALF_UP)));
	    	}
	    	buf.append("</td>");
	    	buf.append("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\">最大跌幅 [" + stockSignal.getLowerDayMedian() + "]<br/>");
	    	if( stockSignal.getLowerMin().compareTo(BigDecimal.ZERO) >= 0 ) {
	    		buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMin(), 2), stockSignal.getLowerMin().setScale(2, RoundingMode.HALF_UP)));
	    	} else {
	    		buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>",
	    				MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), stockSignal.getLowerMin(), 2), stockSignal.getLowerMin().setScale(2, RoundingMode.HALF_UP)));
	    	}
	    	buf.append("</td>\n</tr>\n");
	    	// ===================================
	    	buf.append("<tr>\n");
	    	buf.append(String.format("<td colspan=\"2\" class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\" data-toggle=\"collapse\" data-target=\"#accordion%s%s\">參考價格 [%s]<br/>", count, i, stockSignal.getCompleted()));
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
	    	// ===================================
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
	    	// ===================================       
	    	buf.append("</tbody></table></div>");
	    }
	    StockSignalEntity stockSignal = stockSignalList.get(0);
	    buf.append("</div></div></div>");
	    buf.append("\n");
	    /*
	    buf.append("<div class=\"card-footer bg-dark pl-1 pr-1 pt-1 pb-1\">");
		buf.append("<table class=\"table table-dark table-striped\">");
		buf.append("<tbody>");
		buf.append("<tr>");
		buf.append(String.format("<td class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">參考價格 [%s]<br/>", stockSignal.getCompleted()));
		buf.append(String.format("<span style=\"font-size: 18px;\">%s</span>", currentStockSignalDate.getSignalPrice().setScale(2, RoundingMode.HALF_UP)));
		buf.append("</td>");
		if( stockSignal.getCompleted() == 0 ) {
			buf.append(String.format("<td class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最高價格 [%s]<br/>", 0));
			buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\">%s</a>", "--"));
			buf.append("</td>");
			buf.append(String.format("<td class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最低價格 [%s]<br/>", 0));
			buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\">%s</a>", "--"));
			buf.append("</td>");
		} else {
			buf.append(String.format("<td class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最高價格 [%s]<br/>", currentStockSignalDate.getHighDay()));
			if( currentStockSignalDate.getHighReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
				buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
					MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getHighReturn(), 2), currentStockSignalDate.getHighReturn().setScale(2, RoundingMode.HALF_UP)));
			} else {
				buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
						MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getHighReturn(), 2), currentStockSignalDate.getHighReturn().setScale(2, RoundingMode.HALF_UP)));
			}
			buf.append("</td>");
			buf.append(String.format("<td class=\"text-center pl-0 pr-0 pt-1 pb-1\" width=\"33%%\">最低價格 [%s]<br/>", currentStockSignalDate.getLowDay()));
			if( currentStockSignalDate.getLowReturn().compareTo(BigDecimal.ZERO) >= 0 ) {
				buf.append(String.format("<a class=\"text-success\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
					MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getLowReturn(), 2), currentStockSignalDate.getLowReturn().setScale(2, RoundingMode.HALF_UP)));
			} else {
				buf.append(String.format("<a class=\"text-danger\" style=\"font-size: 18px;\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"%s\">%s%%</a>", 
						MathUtils.getFinalPrice(currentStockSignalDate.getSignalPrice(), currentStockSignalDate.getLowReturn(), 2), currentStockSignalDate.getLowReturn().setScale(2, RoundingMode.HALF_UP)));
			}
			buf.append("</td>");
		}
		buf.append("</tr></tbody></table></div>");
		*/
		buf.append("</div>\n");
		return buf.toString();
	}
	
}
