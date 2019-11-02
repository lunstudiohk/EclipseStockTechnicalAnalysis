package com.lunstudio.stocktechnicalanalysis.temp;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.firebase.tasks.Task;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns;
import com.lunstudio.stocktechnicalanalysis.candlestick.BullishCandlestickPatterns.BullishPatterns;
import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;
import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPriceData;
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.OptionsSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.WarrantSrv;
import com.lunstudio.stocktechnicalanalysis.util.FileUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;

@Component
public class FunctionTest {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	@Autowired
	private CandleStickSrv candleStickSrv;
	
	@Autowired
	private WarrantSrv warrantSrv;
	
	@Autowired
	private CbbcSrv cbbcSrv;
	
	@Autowired
	private FirebaseSrv firebaseSrv;

	@Autowired
	private OptionsSrv optionSrv;
	
	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			FunctionTest instance = context.getBean(FunctionTest.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	
	private void start(String[] args) throws Exception {
		//this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getWarrantPriceSummaryRef(), null);
		//this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getWarrantPriceDataRef(), null);
		//this.stockCbbcAmount(args[0]);
		//this.generateCandlestickPattern(args);
		//this.stimulateStockTrade("HKG:0011");
		//this.getIndexDataFromYahoo();
		//this.updateStockInfo();
		//this.getStockPriceListTest();
		//this.clearFirebaseData();
		//this.generateAmChartData();
		//this.generateEcharttData();
		//this.processIntradayData();
		//this.processAvIntradayData();
		this.optionSrv.getOptionAmountDateMap("HKG:0700", Date.valueOf("2019-01-01"), null);
		
		return;
	}
	
	private void generateEcharttData() throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList("HKG:0700", 500);
		for(StockPriceData stockPrice : stockPriceList) {
			buf.append(String.format("['%s',%s,%s,%s,%s,%s,%s, %s,%s,%s, %s,%s,%s, %s,%s, %s,%s,%s,%s,%s]",
					stockPrice.getT(), stockPrice.getO(), stockPrice.getH(), stockPrice.getL(), stockPrice.getC(), stockPrice.getD(),stockPrice.getV(),
					stockPrice.getSs(), stockPrice.getMs(), stockPrice.getLs(),
					stockPrice.getDm(), stockPrice.getDms(), String.format("%.2f", stockPrice.getDm() - stockPrice.getDms()),
					stockPrice.getSr(), stockPrice.getLr(),
					this.getDisplayString(stockPrice.getIv()), this.getDisplayString(stockPrice.getOpc()), 
					this.getDisplayString(stockPrice.getOpp()), this.getDisplayString(stockPrice.getOpoic()), 
					this.getDisplayString(stockPrice.getOpoip())
					)).append(",").append("\n");
		}
		System.out.println(buf.toString());
		return;
	}
	
	private String getDisplayString(Object obj) {
		if( obj == null ) {
			return "";
		}
		return obj.toString();
	}
	
	private void generateAmChartData() throws Exception {
		StringBuffer buf = new StringBuffer();
		List<StockPriceData> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceDataList("HKG:0700", 500);
		for(StockPriceData stockPrice : stockPriceList) {
			buf.append(String.format("{\"date\":\"%s\",\"o\":\"%s\",\"h\":\"%s\",\"l\":\"%s\",\"c\":\"%s\",\"v\":\"%s\","
					+ "\"s1\":\"%s\",\"s2\":\"%s\",\"s3\":\"%s\","
					+ "\"r1\":\"%s\",\"r2\":\"%s\","
					+ "\"m1\":\"%s\",\"m2\":\"%s\",\"m3\":\"%s\""
					+ "}",
					stockPrice.getT(), stockPrice.getO(), stockPrice.getH(), stockPrice.getL(), stockPrice.getC(), stockPrice.getV(), 
					stockPrice.getSs(), stockPrice.getMs(), stockPrice.getLs(),
					stockPrice.getSr(), stockPrice.getLr(),
					stockPrice.getDm(), stockPrice.getDms(), String.format("%.2f", stockPrice.getDm() - stockPrice.getDms()) 
					)).append(",").append("\n");
		}
		System.out.println(buf.toString());
		return;
	}
	
	
	public void clearFirebaseData() throws Exception {
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getCandlestickDataRef(), "");
		return;
	}
	public void getStockPriceListTest() throws Exception {
		System.out.println(this.stockPriceSrv.getLatestDailyStockPriceEntity("HKG:1171"));
		System.out.println(this.stockPriceSrv.getLatestDailyStockPriceEntity("HKG:0700"));
		return;
	}
	
	
	public void updateStockInfo() throws Exception {
		List<String> dataList = FileUtils.readToLine(new File("/Volumes/HD2/Temp/hkats.csv"), "UTF-8");
		for(String data : dataList) {
			String[] token = data.split(",");
			int code = Integer.parseInt(token[2].substring(1));
			String stockCode = String.format("HKG:%04d", code);
			StockEntity stock = this.stockSrv.getStockInfo(stockCode);
			if( stock != null ) {
				stock.setStockAtsCode(token[0].trim());
			} else {
				stock = new StockEntity();
				stock.setStockCode(stockCode);
				stock.setStockCname(token[1].trim());
				stock.setIsHSCE(false);
				stock.setIsHSI(false);
				stock.setStockAtsCode(token[0].trim());
				stock.setStockHkexCode(String.format("%04d.HK", code));
				stock.setStockYahooCode(String.format("%05d", code));
			}
			System.out.println(stock);
			this.stockSrv.updateStock(stock);			
		}
		return;
	}
	
	
	public void getIndexDataFromYahoo() throws Exception {
		List<String> html = HttpUtils.downloadCsv("https://hk.finance.yahoo.com/quote/%5EHSI/history?p=%5EHSI", "UTF-8");
		for(String line : html) {
			int index = line.indexOf("\"CrumbStore\"");
			if( index != -1) {
				System.out.println(line.substring(index));
			}
		}
		return;
	}
	
	public void generateCandlestickPattern(String[] stockCodeList) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
			List<CandlestickEntity> candlestickList = this.candleStickSrv.generateBullishCandleStick(stockPriceList);
			/*
			for(CandlestickEntity candlestick : candlestickList) {
				System.out.println(candlestick);
			}
			*/
			System.out.println(String.format("%s - Count: %s", stock.getStockCode(), candlestickList.size()));
		}
		return;
	}
	
	
	public void stockCbbcAmount(String tradeDate) throws Exception {
		StockPriceEntity stockPrice = this.stockPriceSrv.getDailyStockPrice("HKG:0700", Date.valueOf(tradeDate));
		List<CbbcPriceEntity> cbbcList = this.cbbcSrv.getCbbcPriceList(Date.valueOf(tradeDate));
		Map<String, BigDecimal> bullCbbcMap = new HashMap<String, BigDecimal>();
		List<String> bullList = new ArrayList<String>();
		Map<String, BigDecimal> bearCbbcMap = new HashMap<String, BigDecimal>();
		List<String> bearList = new ArrayList<String>();
		BigDecimal upperLimit = stockPrice.getClosePrice().multiply(BigDecimal.valueOf(1.1));
		BigDecimal lowerLimit = stockPrice.getClosePrice().multiply(BigDecimal.valueOf(0.9));
		for(CbbcPriceEntity cbbcPrice : cbbcList) {
			if( cbbcPrice.getCbbcUnderlying().equals("00700") && cbbcPrice.getQustanding() != null && cbbcPrice.getQustanding().compareTo(BigDecimal.ZERO) > 0 
					&& cbbcPrice.getCbbcCallLevel().compareTo(upperLimit) < 0 && cbbcPrice.getCbbcCallLevel().compareTo(lowerLimit) > 0 ) {
				if( cbbcPrice.getCbbcType().equals("Bull") ) {
					if( bullCbbcMap.containsKey(cbbcPrice.getCbbcCallLevel().toString())) {
						bullCbbcMap.put(cbbcPrice.getCbbcCallLevel().toString(), bullCbbcMap.get(cbbcPrice.getCbbcCallLevel().toString()).add(cbbcPrice.getCbbcAmount()));
					} else {
						bullCbbcMap.put(cbbcPrice.getCbbcCallLevel().toString(), cbbcPrice.getCbbcAmount());
						bullList.add(cbbcPrice.getCbbcCallLevel().toString());
					}
				} else if( cbbcPrice.getCbbcType().equals("Bear") ) {
					if( bearCbbcMap.containsKey(cbbcPrice.getCbbcCallLevel().toString())) {
						bearCbbcMap.put(cbbcPrice.getCbbcCallLevel().toString(), bearCbbcMap.get(cbbcPrice.getCbbcCallLevel().toString()).add(cbbcPrice.getCbbcAmount()));
					} else {
						bearCbbcMap.put(cbbcPrice.getCbbcCallLevel().toString(), cbbcPrice.getCbbcAmount());
						bearList.add(cbbcPrice.getCbbcCallLevel().toString());
					}
				}
			}
		}
		
		Collections.sort(bearList, new Comparator<String>() {
		    @Override
		    public int compare(String cbbc1, String cbbc2) {
		        return new BigDecimal(cbbc1).compareTo(new BigDecimal(cbbc2));
		    }
		});
		BigDecimal accumAmount = BigDecimal.ZERO;
		for(int i=0; i<bearList.size(); i++) {
			accumAmount = accumAmount.add(bearCbbcMap.get(bearList.get(i)));
			System.out.println(String.format("%s - %s", bearList.get(i), accumAmount));
		}
		System.out.println("====================");
		Collections.sort(bullList, new Comparator<String>() {
		    @Override
		    public int compare(String cbbc1, String cbbc2) {
		        return new BigDecimal(cbbc2).compareTo(new BigDecimal(cbbc1));
		    }
		});
		accumAmount = BigDecimal.ZERO;
		for(int i=0; i<bullList.size(); i++) {
			accumAmount = accumAmount.add(bullCbbcMap.get(bullList.get(i)));
			System.out.println(String.format("%s - %s", bullList.get(i), accumAmount));
		}
		
		return;
	}
	
	public void listCbbcAmount(String tradeDate) throws Exception {
		List<CbbcPriceEntity> cbbcList = this.cbbcSrv.getCbbcPriceList(Date.valueOf(tradeDate));
		Map<String, BigDecimal> stockTotalAmountMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> totalAmountMap = new HashMap<String, BigDecimal>();
		for(CbbcPriceEntity cbbcPrice : cbbcList) {
			BigDecimal totalAmount;
			if( totalAmountMap.containsKey(cbbcPrice.getCbbcType()) ) {
				totalAmount = totalAmountMap.get(cbbcPrice.getCbbcType()).add(cbbcPrice.getCbbcAmount());
			} else {
				totalAmount = cbbcPrice.getCbbcAmount();
			}
			totalAmountMap.put(cbbcPrice.getCbbcType(), totalAmount);
			String key = String.format("%s%s", cbbcPrice.getCbbcUnderlying(), cbbcPrice.getCbbcType());
			if( stockTotalAmountMap.containsKey(key) ) {
				totalAmount = stockTotalAmountMap.get(key).add(cbbcPrice.getCbbcAmount());
			} else {
				totalAmount = cbbcPrice.getCbbcAmount();
			}
			stockTotalAmountMap.put(key, totalAmount);
		}
		System.out.println(totalAmountMap);
		System.out.println(stockTotalAmountMap);
		return;
	}
	
	public void deleteByValue() throws Exception {
		this.firebaseSrv.deleteFromFirebase(FirebaseDao.getInstance().getWarrantPriceDataRef(), "wt", "C");
		return;
	}
	
	public void delete(String nodeName) throws Exception {
		Task task = FirebaseDao.getInstance().getRootRef().child(nodeName).removeValue();
		while(!task.isComplete()) {
			System.out.println(nodeName + " : Incomplete");
			Thread.sleep(1000);
		}
		if( task.isSuccessful() ) {
			System.out.println("Delete Success");
		} else {
			if( task.getException() != null ) {
				System.out.println(task.getException().getMessage());
			} else {
				System.out.println("Delete Fail");
			}
		}
		return;
	}
	
	
	private void processIntradayData() throws Exception {
		Map<Date, Map<Double, Long>> datePriceStat = new HashMap<Date, Map<Double, Long>>();
		String jsonData = FileUtils.readFile(new File("/Volumes/HD2/Temp/intraday0700.json"), "UTF-8");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonRoot = (JSONObject) jsonParser.parse(jsonData);
		JSONObject jsonObject = (JSONObject)jsonRoot.get("intraday");
		for(Object tradeTimestamp : jsonObject.keySet() ) {
			Date tradeDate = Date.valueOf(tradeTimestamp.toString().substring(0, 10));
			JSONObject child = (JSONObject) jsonObject.get(tradeTimestamp);
			Map<Double, Long> priceStat = datePriceStat.get(tradeDate);
			if( priceStat == null ) {
				priceStat = new HashMap<Double, Long>();
				datePriceStat.put(tradeDate, priceStat);
			}
			Double closePrice = Double.valueOf(child.get("close").toString());
			Long volume = Long.valueOf(child.get("volume").toString());
			Long culVolume = priceStat.get(closePrice);
			if( culVolume == null ) {
				priceStat.put(closePrice, volume);
			} else {
				priceStat.put(closePrice, culVolume + volume);
			}
		}
		this.printIntradayPrice(datePriceStat);
		return;
	}
	
	private void processAvIntradayData() throws Exception {
		Long hour12 = Long.valueOf(60 * 1000 * 60 * 12);
		Map<Date, Map<Double, Long>> datePriceStat = new HashMap<Date, Map<Double, Long>>();
		String jsonData = FileUtils.readFile(new File("/Volumes/HD2/Temp/intraday0700av.json"), "UTF-8");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonRoot = (JSONObject) jsonParser.parse(jsonData);
		JSONObject jsonObject = (JSONObject)jsonRoot.get("Time Series (1min)");
		for(Object tradeTimestampUs : jsonObject.keySet() ) {
			Timestamp timestamp = Timestamp.valueOf(tradeTimestampUs.toString());
			Timestamp tradeTimestamp = new Timestamp(timestamp.getTime() + hour12);
			Date tradeDate = Date.valueOf(tradeTimestamp.toString().substring(0, 10));
			JSONObject child = (JSONObject) jsonObject.get(tradeTimestampUs);
			Map<Double, Long> priceStat = datePriceStat.get(tradeDate);
			if( priceStat == null ) {
				priceStat = new HashMap<Double, Long>();
				datePriceStat.put(tradeDate, priceStat);
			}
			Double closePrice = Double.valueOf(child.get("4. close").toString());
			Long volume = Long.valueOf(child.get("5. volume").toString());
			Long culVolume = priceStat.get(closePrice);
			if( culVolume == null ) {
				priceStat.put(closePrice, volume);
			} else {
				priceStat.put(closePrice, culVolume + volume);
			}
		}
		this.printIntradayPrice(datePriceStat);
		return;
	}
	
	private void printIntradayPrice(Map<Date, Map<Double, Long>> datePriceStat) {
		System.out.println("===============================================================================================");
		for(Date tradeDate: datePriceStat.keySet()) {
			System.out.println(tradeDate);
			Double maxVolPrice = null;
			Long maxVolume = Long.valueOf(0);
			Long totalVolume = Long.valueOf(0);
			Double totalAmount = Double.valueOf(0);
			for(Double price: datePriceStat.get(tradeDate).keySet()) {
				if( datePriceStat.get(tradeDate).get(price) > maxVolume ) {
					maxVolume = datePriceStat.get(tradeDate).get(price);
					maxVolPrice = price;
				}
				totalVolume += datePriceStat.get(tradeDate).get(price);
				totalAmount += price * datePriceStat.get(tradeDate).get(price);
			}
			System.out.println(String.format("Total Amount: %.0f, Total Volume: %s, Max. Volume Price: %.2f [%s (%.2f%%)]", 
					totalAmount, totalVolume.toString(), maxVolPrice, maxVolume.toString(), ((double)maxVolume/totalVolume)*100.00));
		}
		return;
	}
	
}