package com.lunstudio.stocktechnicalanalysis.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockOptionsDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;

@Service
public class OptionsSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockOptionsDao stockOptionsDao;
	
	public void saveStockOptions(List<StockOptionsEntity> optionsList) {
		this.stockOptionsDao.save(optionsList, optionsList.size());
		return;
	}

}
