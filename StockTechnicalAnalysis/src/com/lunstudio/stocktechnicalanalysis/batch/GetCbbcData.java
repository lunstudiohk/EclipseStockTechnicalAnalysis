package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

@Component
public class GetCbbcData {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSrv stockSrv;

	@Autowired
	private CbbcSrv cbbcSrv;

	public static void main(String[] args) {
		try {
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(configPath);
			GetCbbcData instance = context.getBean(GetCbbcData.class);
			instance.start();
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		List<String> dataList = this.getCbbcData();
		List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		List<CbbcPriceEntity> cbbcPriceList = new ArrayList<CbbcPriceEntity>();
		Date tradeDate = DateUtils.getCsvDate(this.getUpdateDate(dataList.get(0)));

		for (int i = 2; i < dataList.size() - 3; i++) {
			String[] data = this.getData(dataList.get(i));
			if (stockCodeList.contains(data[2])) {
				CbbcPriceEntity cbbcPriceEntity = new CbbcPriceEntity();
				cbbcPriceEntity.setCbbcCode(data[0]);
				cbbcPriceEntity.setCbbcIssuer(data[1]);
				cbbcPriceEntity.setCbbcListDate(DateUtils.getHkexDate(data[6]));
				cbbcPriceEntity.setCbbcMaturityDate(DateUtils.getHkexDate(data[7]));
				cbbcPriceEntity.setCbbcRatio(new BigDecimal(data[11]));
				cbbcPriceEntity.setCbbcStrikeLevel(new BigDecimal(data[9]));
				cbbcPriceEntity.setCbbcCallLevel(new BigDecimal(data[10]));
				cbbcPriceEntity.setCbbcType(data[3].trim());
				cbbcPriceEntity.setCbbcUnderlying(data[2]);
				cbbcPriceEntity.setClosePrice(this.getCbbcPrice(data[17]));
				cbbcPriceEntity.setDayHigh(this.getCbbcPrice(data[15]));
				cbbcPriceEntity.setDayLow(this.getCbbcPrice(data[16]));
				cbbcPriceEntity.setIssueSize(Long.parseLong(data[12].replaceAll(",", "")));
				cbbcPriceEntity.setQustanding(this.getCbbcPrice(data[13]));
				cbbcPriceEntity.setTradeDate(tradeDate);
				//cbbcPriceEntity.setTurnover(this.getCbbcPrice(data[18]));
				if( cbbcPriceEntity.getClosePrice() != null ) {
					cbbcPriceList.add(cbbcPriceEntity);
				}
			}
		}
		logger.info("No. of Cbbc Price List : " + cbbcPriceList.size());
		this.cbbcSrv.saveCbbcPriceList(cbbcPriceList);

		return;
	}

	private List<String> getCbbcData() throws Exception {
		List<String> dataList = new ArrayList<String>();
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		WebResponse resp = wc.getResponse("https://www.hkex.com.hk/chi/cbbc/search/listsearch_c.asp");
		WebLink downloadLink = resp.getLinkWithName("downloadlink");
		WebRequest clickRequest = downloadLink.getRequest();
		WebResponse csv = wc.getResponse(clickRequest);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csv.getInputStream(), "UTF-16"));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			dataList.add(line);
		}
		bufferedReader.close();
		return dataList;
	}
	
	private BigDecimal getCbbcPrice(String val) throws Exception {
		if (val.trim().equals("N/A") || val.trim().equals("-")) {
			return null;
		} else {
			return new BigDecimal(val.replaceAll(",", ""));
		}
	}

	private String getUpdateDate(String line) throws Exception {
		return line.substring(9, 19);
	}

	private String[] getData(String line) throws Exception {
		String[] data = new String[21];
		data = line.split("\\t");
		return data;
	}
}
