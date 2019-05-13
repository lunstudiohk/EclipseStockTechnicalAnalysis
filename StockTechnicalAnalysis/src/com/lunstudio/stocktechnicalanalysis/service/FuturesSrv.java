package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.IndexFuturesDao;
import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;

@Service
public class FuturesSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private IndexFuturesDao indexFuturesDao;
	
	public void saveIndexFutures(List<IndexFuturesEntity> futuresList) {
		this.indexFuturesDao.save(futuresList, futuresList.size());
		return;
	}
	
	public Map<Date, IndexFuturesEntity[]> getIndexFutureDateMap(Date startDate) throws Exception {
		Map<Date, IndexFuturesEntity[]> dateMap = new HashMap<Date, IndexFuturesEntity[]>();
		List<IndexFuturesEntity> futureList = this.indexFuturesDao.getFutureList(startDate);
		for(IndexFuturesEntity future : futureList) {
			IndexFuturesEntity[] futures = dateMap.get(future.getTradeDate());
			if( futures == null ) {
				futures = new IndexFuturesEntity[2];
				dateMap.put(future.getTradeDate(), futures);
			}
			futures[future.getMonth()] = future;
		}
		return dateMap;
	}
}
