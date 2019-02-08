package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lunstudio.stocktechnicalanalysis.service.CandleStickSrv;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.WarrantSrv;

@Component
public class FunctionTest {

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
		return;
	}
	
	
	
	public void generateCandlestickPattern(String[] stockCodeList) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			List<StockPriceEntity> stockPriceList = this.stockPriceSrv.getLastDailyStockPriceEntityList(stock.getStockCode(), null);
			List<CandlestickEntity> candlestickList = this.candleStickSrv.generateCandleStick(stockPriceList);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
}
