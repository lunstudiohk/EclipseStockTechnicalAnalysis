package com.lunstudio.stocktechnicalanalysis.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;

@Component
public class InitCbbcPrice {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private CbbcSrv cbbcSrv;

	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			InitCbbcPrice instance = context.getBean(InitCbbcPrice.class);
			instance.start(args);
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws Exception {
		List<String> dataList = this.getCbbcDataList(args[0]);
		List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		List<CbbcPriceEntity> cbbcPriceList = new ArrayList<CbbcPriceEntity>();
		for (String line : dataList) {
			String[] data = this.getData(line);
			if (this.isNumeric(data[0])) {
				if (stockCodeList.contains(data[17]) ) {
					CbbcPriceEntity cbbcPriceEntity = new CbbcPriceEntity();
					cbbcPriceEntity.setCbbcCode(data[0]);
					cbbcPriceEntity.setCbbcIssuer(data[16].trim());
					cbbcPriceEntity.setCbbcListDate(Date.valueOf(data[21]));
					cbbcPriceEntity.setCbbcMaturityDate(Date.valueOf(data[23]));
					cbbcPriceEntity.setCbbcRatio(new BigDecimal(data[28]));
					cbbcPriceEntity.setCbbcStrikeLevel(new BigDecimal(data[26]));
					cbbcPriceEntity.setCbbcCallLevel(new BigDecimal(data[27]));
					cbbcPriceEntity.setCbbcType(data[18].trim());
					cbbcPriceEntity.setCbbcUnderlying(data[17]);
					cbbcPriceEntity.setClosePrice(this.getCbbcPrice(data[13]));
					cbbcPriceEntity.setDayHigh(this.getCbbcPrice(data[11]));
					cbbcPriceEntity.setDayLow(this.getCbbcPrice(data[12]));
					cbbcPriceEntity.setIssueSize(Long.parseLong(data[9]));
					cbbcPriceEntity.setQustanding(this.getCbbcPrice(data[8]));
					cbbcPriceEntity.setTradeDate(Date.valueOf(data[2]));
					//cbbcPriceEntity.setTurnover(this.getCbbcPrice(data[15]));
					if( cbbcPriceEntity.getClosePrice() != null ) {
						cbbcPriceList.add(cbbcPriceEntity);
					}
				}
			}
		}
		logger.info("No. of Cbbc Price List : " + cbbcPriceList.size());
		cbbcSrv.saveCbbcPriceList(cbbcPriceList);
		return;
	}

	private List<String> getCbbcDataList(String filePath) throws Exception {
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
	
	private BigDecimal getCbbcPrice(String val) throws Exception {
		if( val.trim().equals("N/A") ||  val.trim().equals("-") ) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
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
		String[] data = new String[30];
		data = line.split("\\t");
		return data;
	}
}
