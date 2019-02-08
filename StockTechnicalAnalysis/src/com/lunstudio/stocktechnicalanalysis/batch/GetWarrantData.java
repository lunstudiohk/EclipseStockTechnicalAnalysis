package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.*;
import com.lunstudio.stocktechnicalanalysis.service.*;
import com.lunstudio.stocktechnicalanalysis.util.*;

/**
 * Get warrant data from hkex website
 * 
 * @author alankam
 *
 */

@Component
public class GetWarrantData {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private WarrantSrv warrantSrv;

	private static final String HTML_RISK_PARAM = "var risk = ";
	
	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			GetWarrantData instance = context.getBean(GetWarrantData.class);
			instance.start();
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start() throws Exception {

		List<String> dataList = this.getWarrantData();
		List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		List<WarrantPriceEntity> warrantPriceList = new ArrayList<WarrantPriceEntity>();
		Date tradeDate = DateUtils.getCsvDate(this.getUpdateDate(dataList.get(0)));
		for (int i = 2; i < dataList.size() - 3; i++) {
			String[] data = this.getData(dataList.get(i));
			if (stockCodeList.contains(data[2])) {
				WarrantPriceEntity warrantPriceEntity = new WarrantPriceEntity();
				warrantPriceEntity.setClosePrice(this.getWarrantPrice(data[17]));
				warrantPriceEntity.setDayHigh(this.getWarrantPrice(data[15]));
				warrantPriceEntity.setDayLow(this.getWarrantPrice(data[16]));
				warrantPriceEntity.setDelta(this.getDelta(data[12]));
				warrantPriceEntity.setImpVol(this.getImpVol(data[13]));
				warrantPriceEntity.setIssueSize(Long.parseLong(data[10].replaceAll(",", "")));
				warrantPriceEntity.setQustanding(this.getWarrantPrice(data[11]));
				warrantPriceEntity.setTradeDate(tradeDate);
				warrantPriceEntity.setTurnover(this.getWarrantPrice(data[18]));
				warrantPriceEntity.setWarrantCode(data[0]);
				warrantPriceEntity.setRiskFactor(this.getWarrantRiskFactor(data[0]));
				warrantPriceEntity.setWarrantIssuer(data[1]);
				warrantPriceEntity.setWarrantListDate(DateUtils.getHkexDate(data[5]));
				warrantPriceEntity.setWarrantMaturityDate(DateUtils.getHkexDate(data[6]));
				warrantPriceEntity.setWarrantRatio(new BigDecimal(data[9]));
				warrantPriceEntity.setWarrantStrikePrice(new BigDecimal(data[8]));
				if ("Call".equals(data[3].trim())) {
					warrantPriceEntity.setWarrantType(WarrantPriceEntity.WARRANT_TYPE_CALL);
				} else if ("Put".equals(data[3].trim())) {
					warrantPriceEntity.setWarrantType(WarrantPriceEntity.WARRANT_TYPE_PUT);
				}
				warrantPriceEntity.setWarrantUnderlying(data[2]);
				if( warrantPriceEntity.getClosePrice() != null ) {
					try {
						warrantPriceEntity.setWarrantValue(this.warrantSrv.getWarrantValue(warrantPriceEntity));
					}catch(Exception e) {
						//e.printStackTrace();
					}
					warrantPriceList.add(warrantPriceEntity);
				}
				Thread.sleep(500);
			}
		}
		logger.info("No. of Warrant Price List : " + warrantPriceList.size());
		
		this.warrantSrv.saveWarrantPriceList(warrantPriceList);
		return;
	}

	private BigDecimal getWarrantRiskFactor(String warrantCode) throws Exception {
		BigDecimal riskFactor = null;
		String line = HttpUtils.sendGet(String.format(SystemUtils.getWarrantCalculatorUrl(), warrantCode), HTML_RISK_PARAM);
		//var risk = '0.348335'*1/100;
		if( line.indexOf(HTML_RISK_PARAM) != -1 ) {
			String[] val = line.split("'");
			riskFactor = new BigDecimal(val[1]);
		}
		return riskFactor;
	}
	
	private BigDecimal getWarrantPrice(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private BigDecimal getImpVol(String val) throws Exception {
		if (val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private BigDecimal getDelta(String val) throws Exception {
		if (val.trim().equals("-")) {
			return null;
		}
		int index1 = val.indexOf("(");

		int index2 = val.indexOf(")");
		if (index1 != -1 && index2 != -1) {
			val = "-" + val.substring(index1 + 1, index2);
		}
		val = val.replaceAll(",", "");
		return new BigDecimal(val);
	}

	private String getUpdateDate(String line) throws Exception {
		return line.substring(9, 19);
	}

	private String[] getData(String line) throws Exception {
		String[] data = new String[21];
		data = line.split("\\t");
		return data;
	}

	private List<String> getWarrantData() throws Exception {
		return HttpUtils.downloadCsv(SystemUtils.getWarrantFullListUrl(), "UTF-16");
	}
	
}
