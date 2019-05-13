package com.lunstudio.stocktechnicalanalysis.batch;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockEntity;
import com.lunstudio.stocktechnicalanalysis.service.CbbcSrv;
import com.lunstudio.stocktechnicalanalysis.service.StockSrv;
import com.lunstudio.stocktechnicalanalysis.util.DateUtils;
import com.lunstudio.stocktechnicalanalysis.util.HttpUtils;
import com.lunstudio.stocktechnicalanalysis.util.SystemUtils;

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
		//List<String> stockCodeList = this.stockSrv.getStockHkexCodeList();
		Map<String, StockEntity> hkexCodeMap = this.stockSrv.getStockInfoHkexMap();
		List<CbbcPriceEntity> cbbcPriceList = new ArrayList<CbbcPriceEntity>();
		Date tradeDate = DateUtils.getCsvDate(this.getUpdateDate(dataList.get(0)));

		for (int i = 2; i < dataList.size() - 3; i++) {
			String[] data = this.getData(dataList.get(i));
			StockEntity stock = hkexCodeMap.get(data[2]);
			if (stock != null) {
				CbbcPriceEntity cbbcPriceEntity = new CbbcPriceEntity();
				cbbcPriceEntity.setCbbcCode(data[0]);
				cbbcPriceEntity.setCbbcIssuer(data[1]);
				cbbcPriceEntity.setCbbcListDate(DateUtils.getHkexDate(data[6]));
				cbbcPriceEntity.setCbbcMaturityDate(DateUtils.getHkexDate(data[7]));
				cbbcPriceEntity.setCbbcRatio(new BigDecimal(data[11]));
				cbbcPriceEntity.setCbbcStrikeLevel(new BigDecimal(data[9]));
				cbbcPriceEntity.setCbbcCallLevel(new BigDecimal(data[10]));
				cbbcPriceEntity.setCbbcType(data[3].trim());
				cbbcPriceEntity.setCbbcUnderlying(stock.getStockCode());
				cbbcPriceEntity.setClosePrice(this.getCbbcPrice(data[17]));
				cbbcPriceEntity.setDayHigh(this.getCbbcPrice(data[15]));
				cbbcPriceEntity.setDayLow(this.getCbbcPrice(data[16]));
				cbbcPriceEntity.setIssueSize(Long.parseLong(data[12].replaceAll(",", "")));
				cbbcPriceEntity.setQustanding(this.getCbbcPrice(data[13]));
				cbbcPriceEntity.setTradeDate(tradeDate);
				cbbcPriceEntity.setTurnover(this.getCbbcPrice(data[18]));
				//if( cbbcPriceEntity.getClosePrice() != null && cbbcPriceEntity.getQustanding().compareTo(BigDecimal.ZERO) > 0 ) {
					cbbcPriceList.add(cbbcPriceEntity);
				//}
				//logger.info(cbbcPriceEntity);
			}
		}
		logger.info("No. of Cbbc Price List : " + cbbcPriceList.size());
		this.cbbcSrv.saveCbbcPriceList(cbbcPriceList);
		return;
	}

	private List<String> getCbbcData() throws Exception {
		return HttpUtils.downloadCsv(SystemUtils.getCbbcFullListUrl(), "UTF-16");
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
