package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockOptionsDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;

@Service
public class OptionsSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockOptionsDao stockOptionsDao;
	
	public void saveStockOptions(List<StockOptionsEntity> optionsList) {
		this.stockOptionsDao.save(optionsList, optionsList.size());
		return;
	}

	public Map<Date, OptionAmountVo> getOptionAmountDateMap(String stockCode, Date startDate, Map<Date, StockPriceEntity> stockPriceDateMap) throws Exception {
		Map<Date, OptionAmountVo> optionAmountDateMap = new HashMap<Date, OptionAmountVo>();
		List<OptionAmountVo> optionAmountList = this.stockOptionsDao.getOptionAmountList(stockCode, startDate);
		for(OptionAmountVo amountVo: optionAmountList) {
			optionAmountDateMap.put(amountVo.getTradeDate(), amountVo);
		}
		return optionAmountDateMap;
	}
}
