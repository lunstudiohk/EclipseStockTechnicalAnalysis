package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockSignalDateDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;

@Service
public class StockSignalDateSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockSignalDateDao stockSignalDateDao;
	
	public void saveStockSignalDate(StockSignalDateEntity entity) throws Exception {
		this.stockSignalDateDao.save(entity);
		return;
	}
	
	public void saveStockSignalDateList(List<StockSignalDateEntity> list) throws Exception {
		this.stockSignalDateDao.save(list, 500);
		return;
	}

	public StockSignalDateEntity getStockSignalDate(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Date signalDate) throws Exception {
		return this.stockSignalDateDao.findByPrimaryKey(stockCode, tradeDate, signalSeq, signalType, signalDate);
	}
	
	public List<StockSignalDateEntity> getStockSignalDateList(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Integer order) throws Exception {
		return this.stockSignalDateDao.getStockSignalDateList(stockCode, tradeDate, signalSeq, signalType, order);
	}
}
