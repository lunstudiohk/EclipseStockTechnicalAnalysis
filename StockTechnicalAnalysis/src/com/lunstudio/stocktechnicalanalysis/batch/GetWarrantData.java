package com.lunstudio.stocktechnicalanalysis.batch;

import java.io.BufferedReader;
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

import com.lunstudio.stocktechnicalanalysis.entity.*;
import com.lunstudio.stocktechnicalanalysis.service.*;
import com.lunstudio.stocktechnicalanalysis.util.*;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

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

		// List<String> dataList =
		// HttpUtils.downloadCsv(SystemUtils.getWarrantFullListUrl(), "UTF-16");
		List<String> dataList = this.getWarrantData();
		List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		List<String> warrantCodeList = this.warrantSrv.getWarrantCodeList();
		List<WarrantInfoEntity> warrantList = new ArrayList<WarrantInfoEntity>();
		List<WarrantPriceEntity> warrantPriceList = new ArrayList<WarrantPriceEntity>();
		Date tradeDate = DateUtils.getCsvDate(this.getUpdateDate(dataList.get(0)));
		for (int i = 2; i < dataList.size() - 3; i++) {
			String[] data = this.getData(dataList.get(i));
			if (stockCodeList.contains(data[2])) {
				if (!warrantCodeList.contains(data[0])) {
					WarrantInfoEntity warrantInfoEntity = new WarrantInfoEntity();
					try {
						warrantInfoEntity.setWarrantCode(data[0]);
						warrantInfoEntity.setWarrantIssuer(data[1]);
						warrantInfoEntity.setWarrantListDate(DateUtils.getHkexDate(data[5]));
						warrantInfoEntity.setWarrantMaturityDate(DateUtils.getHkexDate(data[6]));
						warrantInfoEntity.setWarrantRatio(new BigDecimal(data[9]));
						warrantInfoEntity.setWarrantStrikePrice(new BigDecimal(data[8]));
						if ("Call".equals(data[3].trim())) {
							warrantInfoEntity.setWarrantType(WarrantInfoEntity.WARRANT_TYPE_CALL);
						} else if ("Put".equals(data[3].trim())) {
							warrantInfoEntity.setWarrantType(WarrantInfoEntity.WARRANT_TYPE_PUT);
						}
						warrantInfoEntity.setWarrantUnderlying(data[2]);
						warrantList.add(warrantInfoEntity);
					} catch (Exception e) {
						logger.error("Warrant Info - Fail to parse line: " + dataList.get(i));
					}
				}
				WarrantPriceEntity warrantPriceEntity = new WarrantPriceEntity();
				try {
					warrantPriceEntity.setClosePrice(this.getWarrantPrice(data[17]));
					warrantPriceEntity.setDayHigh(this.getWarrantPrice(data[15]));
					warrantPriceEntity.setDayLow(this.getWarrantPrice(data[16]));
					warrantPriceEntity.setDelta(this.getDelta(data[12]));
					warrantPriceEntity.setImpVol(this.getImpVol(data[13]));
					// warrantPriceEntity.setIssueSize(new Long(data[10].replaceAll(",", "")));
					warrantPriceEntity.setIssueSize(Long.parseLong(data[10].replaceAll(",", "")));
					warrantPriceEntity.setQustanding(this.getWarrantPrice(data[11]));
					warrantPriceEntity.setTradeDate(tradeDate);
					warrantPriceEntity.setTurnover(this.getWarrantPrice(data[18]));
					warrantPriceEntity.setWarrantCode(data[0]);
					warrantPriceList.add(warrantPriceEntity);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Warrant Price - Fail to parse line: " + dataList.get(i));
				}
			}
		}
		logger.info("No. of warrant Info: " + warrantList.size() + " , Price List : " + warrantPriceList.size());
		// warrantPriceDao.save(warrantPriceList, warrantPriceList.size());
		// warrantInfoDao.save(warrantList, warrantList.size());

		// String filePath = SystemUtils.getWarrantDownloadPath() +
		// DateUtils.getFirebaseDateString(tradeDate) + "W";
		// System.out.println("Backup the file to: " + filePath);
		// FileUtils.writeToFile(dataList, filePath);
		return;
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
		List<String> dataList = new ArrayList<String>();
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
		WebResponse resp = wc.getResponse("https://www.hkex.com.hk/chi/dwrc/search/listsearch_c.asp");
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

}