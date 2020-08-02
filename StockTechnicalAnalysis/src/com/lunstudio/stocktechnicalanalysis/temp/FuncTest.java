package com.lunstudio.stocktechnicalanalysis.temp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lunstudio.stocktechnicalanalysis.batch.UpdateStockPriceToFirebase;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;


@Component
public class FuncTest {

	
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockPriceSrv stockPriceSrv;
	
	@Autowired
	private StockSrv stockSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			FuncTest instance = context.getBean(FuncTest.class);
			instance.print();
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}
	
	private void print() throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		int index = 1;
		for(StockEntity stock : stockList) {
			if( stock.getStockRegion().equals("HK") ) {
				String stockCode = stock.getStockCode();
				String googleStockCode = "HKG:" + stockCode.substring(0, 4);
				String formula = String.format("=GOOGLEFINANCE(A%s,\"all\",TODAY()-50,TODAY())", index++);
				System.out.println(String.format("%s	%s", googleStockCode, formula));
				for(int i=0; i<50; i++) {
					index++;
					System.out.println("");
				}
			}
		}
		return;
	}
	
	private void parse() throws Exception {
		String response = HttpUtils.sendGet("https://www.hkex.com.hk/Market-Data/Securities-Prices/Equities/Equities-Quote?sym=2007&sc_lang=en");
		Document doc = Jsoup.parse(response);
		System.out.println(doc);
		return;
	}
	
	private void stream() {
		long time1=0;
		long time2=0;
		
		for(int j=0; j<10; j++) {
		List<Integer> list1 = new ArrayList<Integer>();
		List<Integer> list2 = new ArrayList<Integer>();
		for(int i=0; i<3000; i++) {
			list1.add(new Random().nextInt(2500));
			list2.add(new Random().nextInt(2500));
		}
		long startTime = System.currentTimeMillis();
		List<Integer> list3 = list1.stream().filter(list2::contains).collect(Collectors.toList());
		long endTime = System.currentTimeMillis();
		//System.out.println(String.format("Total Time-1: %s; List Size: %s", (endTime-startTime), list3.size()));
		time1 += (endTime-startTime);
		startTime = System.currentTimeMillis();
		list1.retainAll(list2);
		if( list3.size() != list1.size() ) {
			System.out.println("error");
		}
		endTime = System.currentTimeMillis();
		time2 += (endTime-startTime);
		//System.out.println(String.format("Total Time-2: %s; List Size: %s", (endTime-startTime), list3.size()));
		}
		System.out.println(String.format("Total Time-1: %s; Time-2: %s", time1, time2));
		return;
	}
	
	private void generatePermutations() {
		List<List<String>> lists = new ArrayList<List<String>>();
		List<String> result = new ArrayList<String>();
		
		List<String> list1 = new ArrayList<String>();
		for(int i=1; i<=30; i++) {
			list1.add(String.format("%03d", i));
		}
		lists.add(list1);
		
		List<String> list2 = new ArrayList<String>();
		for(int i=1; i<=136; i++) {
			list2.add(String.format("%03d", i));
		}
		lists.add(list2);
		
		List<String> list3 = new ArrayList<String>();
		for(int i=1; i<=243; i++) {
			list3.add(String.format("%03d", i));
		}
		lists.add(list3);
		
		List<String> list4 = new ArrayList<String>();
		for(int i=1; i<=26; i++) {
			list4.add(String.format("%03d", i));
		}
		//lists.add(list4);
		
		generatePermutations(lists, result, 0, "");
		
		System.out.println(result.size());
	}
	
	private void generatePermutations(List<List<String>> lists, List<String> result, int depth, String current) {
	    if (depth == lists.size()) {
	        result.add(current);
	        return;
	    }

	    for (int i = 0; i < lists.get(depth).size(); i++) {
	        generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i));
	    }
	}
	
	private void calculate() throws Exception {
		for(int i=11; i<20; i++) {
		BigDecimal decimal = BigDecimal.valueOf(i);
		decimal = decimal.multiply(BigDecimal.valueOf(0.9)).setScale(0, RoundingMode.HALF_UP);
		System.out.println(i + " - Index: " + decimal.intValue());
		}
		return;
	}
	
	private void regression() throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList("0700.HK", null);
		
		for(int i=3900; i<stockPriceList.size(); i++) {
			System.out.println(stockPriceList.get(i).getTradeDate() + " : " + this.getRegressionSlope(stockPriceList, i));
		}
		return;
	}
	
	private double getRegressionSlope(List<StockPriceEntity> stockPriceList, int index) throws Exception {
		SimpleRegression regression = new SimpleRegression();
		int startIndex = index-9;
		for(int i=1; i<=10; i++) {
			System.out.print(stockPriceList.get(startIndex).getClosePrice().doubleValue() + " ; ");
			regression.addData(i, stockPriceList.get(startIndex++).getClosePrice().doubleValue());
		}
		System.out.println("");
		System.out.println(index + ":" + startIndex);
		return regression.getSlope();
	}
	
	private void start1(String[] args) throws Exception {
		List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getDailyStockPriceList("0388.HK", null);
		
		DescriptiveStatistics hollowStick = new DescriptiveStatistics();
		//DescriptiveStatistics filledStick = new DescriptiveStatistics();
		
		hollowStick.setWindowSize(50);
		//filledStick.setWindowSize(50);
		
		BigDecimal body = null;
		for(int i=0; i<stockPriceList.size(); i++) {
			StockPriceEntity stockPrice = stockPriceList.get(i);
			if( stockPrice.getOpenPrice().compareTo(stockPrice.getClosePrice()) < 0 ) {
				body = (stockPrice.getClosePrice().subtract(stockPrice.getOpenPrice())).divide(stockPrice.getOpenPrice(), 5, RoundingMode.HALF_UP);
				hollowStick.addValue(body.doubleValue());
				if( body.doubleValue() > hollowStick.getPercentile(50) * 3 ) {
					System.out.println(String.format("[Hollow Long] %s - B:%.5f M:%.5f S:%.5f", stockPrice.getTradeDate(), body.doubleValue(), hollowStick.getPercentile(50), hollowStick.getStandardDeviation()));
				}
			}
			if( stockPrice.getOpenPrice().compareTo(stockPrice.getClosePrice()) > 0 ) {
				body = (stockPrice.getOpenPrice().subtract(stockPrice.getClosePrice())).divide(stockPrice.getOpenPrice(), 5, RoundingMode.HALF_UP);
				hollowStick.addValue(body.doubleValue());
				if( body.doubleValue() > hollowStick.getPercentile(50) * 3 ) {
					System.out.println(String.format("[Filled Long] %s - B:%.5f M:%.5f S:%.5f", stockPrice.getTradeDate(), body.doubleValue(), hollowStick.getPercentile(50), hollowStick.getStandardDeviation()));
				}
			}
			
			
		}
	}
	
	
	private BarSeries getStockTimeSeries(String stockCode, List<StockPriceEntity> stockPriceEntityList) throws Exception {
		BarSeries series = new BaseBarSeriesBuilder().withName("0700.HK").build();
		for(StockPriceEntity stockPrice : stockPriceEntityList) {
			try {
				ZonedDateTime tradeDate = DateUtils.getLocalDate(stockPrice.getTradeDate()).atStartOfDay(ZoneId.systemDefault());
				series.addBar(tradeDate, stockPrice.getOpenPrice(), stockPrice.getHighPrice(), stockPrice.getLowPrice(), stockPrice.getClosePrice(), stockPrice.getVolume()); 
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return series;
	}
}
