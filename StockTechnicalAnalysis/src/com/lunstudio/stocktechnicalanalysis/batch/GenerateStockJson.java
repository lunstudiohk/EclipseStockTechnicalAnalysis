package com.lunstudio.stocktechnicalanalysis.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.firebase.StockPrice;
import com.lunstudio.stocktechnicalanalysis.service.StockPriceSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.FileUtils;

@Component
public class GenerateStockJson {
	private static final Logger logger = LogManager.getLogger();
	
	@Autowired
	private StockSrv stockSrv;
	
	@Autowired
	private StockPriceSrv stockPriceSrv;

	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			GenerateStockJson instance = context.getBean(GenerateStockJson.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	private void start(String[] args) throws Exception {
		List<StockEntity> stockList = this.stockSrv.getStockInfoList();
		for(StockEntity stock : stockList) {
			Map<String, Object> stockPriceMap = new HashMap<String, Object>();
			List<StockPrice> stockPriceList = this.stockPriceSrv.getFirbaseStockPriceList(stock.getStockCode(), 250);
			for(StockPrice stockPrice : stockPriceList) {
				String key = String.format("%s%s", stock.getStockShortCode(), DateUtils.getShortDateString(stockPrice.getDate()));
				stockPriceMap.put(key, stockPrice.getData());
			}
			JSONObject json = new JSONObject();
		    json.putAll(stockPriceMap);
		    //System.out.println(json.toJSONString());
		    FileUtils.writeToFile("/Volumes/Project/Data/Project/StockAnalysis/HTML/StockSignal/json/"+stock.getStockHkexCode()+".json", json.toJSONString());
		    return;
		}
		return;
	}
	
}
