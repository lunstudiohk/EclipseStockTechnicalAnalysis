package com.lunstudio.stocktechnicalanalysis.service;

import java.util.List;

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
}
