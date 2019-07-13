package com.lunstudio.stocktechnicalanalysis.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.service.WarrantSrv;

@Component
public class InitWarrantPrice {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private WarrantSrv warrantSrv;

	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			InitWarrantPrice instance = context.getBean(InitWarrantPrice.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws Exception {
		List<String> dataList = this.getWarrantDataList(args[0]);
		List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		List<WarrantPriceEntity> warrantPriceList = new ArrayList<WarrantPriceEntity>();
		for (String line : dataList) {
			String[] data = this.getData(line);
			
			if (this.isNumeric(data[0])) {
				if (stockCodeList.contains(data[19]) ) {
					//Filter date
					/*
					if( !"2019-04-08".equals(data[2]) ) {
						continue;
					}
					*/
					WarrantPriceEntity warrantPriceEntity = new WarrantPriceEntity();
					warrantPriceEntity.setWarrantCode(data[0]);
					warrantPriceEntity.setWarrantIssuer(data[18]);
					warrantPriceEntity.setWarrantListDate(Date.valueOf(data[22]));
					warrantPriceEntity.setWarrantMaturityDate(Date.valueOf(data[24]));
					warrantPriceEntity.setWarrantRatio(new BigDecimal(data[27]));
					warrantPriceEntity.setWarrantStrikePrice(new BigDecimal(data[26]));
					if ("Call".equals(data[20].trim())) {
						warrantPriceEntity.setWarrantType(WarrantPriceEntity.WARRANT_TYPE_CALL);
					} else if ("Put".equals(data[20].trim())) {
						warrantPriceEntity.setWarrantType(WarrantPriceEntity.WARRANT_TYPE_PUT);
					}
					warrantPriceEntity.setWarrantUnderlying(data[19]);
					warrantPriceEntity.setClosePrice(this.getWarrantPrice(data[15]));
					warrantPriceEntity.setDayHigh(this.getWarrantPrice(data[13]));
					warrantPriceEntity.setDayLow(this.getWarrantPrice(data[14]));
					warrantPriceEntity.setDelta(this.getDelta(data[10]));
					warrantPriceEntity.setImpVol(this.getImpVol(data[11]));
					warrantPriceEntity.setIssueSize(Long.parseLong(data[9]));
					warrantPriceEntity.setQustanding(this.getWarrantPrice(data[8]));
					warrantPriceEntity.setTradeDate(Date.valueOf(data[2]));
					warrantPriceEntity.setTurnover(this.getTurnover(data[17]));
					if( warrantPriceEntity.getClosePrice() != null ) {
						warrantPriceList.add(warrantPriceEntity);
					}
				}
			}
		}
		logger.info(String.format("No.of warrant: %d", warrantPriceList.size()));
		this.warrantSrv.saveWarrantPriceList(warrantPriceList);
		return;
	}

	private BigDecimal getTurnover(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return (new BigDecimal(val.replaceAll(",", ""))).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
		}
	}

	private BigDecimal getWarrantPrice(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private BigDecimal getImpVol(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private BigDecimal getDelta(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private List<String> getWarrantDataList(String filePath) throws Exception {
		List<String> dataList = new ArrayList<String>();
		File file = new File(filePath);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16"));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			dataList.add(line);
		}
		bufferedReader.close();
		return dataList;
	}

	private boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private String[] getData(String line) throws Exception {
		line = line.replace("\"", "");
		String[] data = new String[29];
		data = line.split("\\t");
		return data;
	}
}
