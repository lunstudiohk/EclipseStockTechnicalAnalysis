package com.lunstudio.stocktechnicalanalysis.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.StockHolderDao;
import com.lunstudio.stocktechnicalanalysis.dao.StockHoldingDao;
import com.lunstudio.stocktechnicalanalysis.entity.StockHolderEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockHoldingEntity;

@Service
public class StockHoldingSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private StockHoldingDao stockHoldingDao;
	
	@Autowired
	private StockHolderDao stockHolderDao;

	public Date getLastHoldingDate(String stockCode) {
		return this.stockHoldingDao.getLastHoldingDate(stockCode);
	}
	
	public void saveStockHoldingList(List<StockHoldingEntity> holdingList) {
		List<StockHolderEntity> holderList = new ArrayList<StockHolderEntity>();
		for(StockHoldingEntity holding : holdingList) {
			StockHolderEntity holder = new StockHolderEntity();
			holder.setHolderCode(holding.getHolderCode());
			holder.setHolderName(holding.getHolderName());
			if( !holderList.contains(holder) ) {
				holderList.add(holder);
			}
		}
		this.stockHolderDao.save(holderList, holderList.size());
		this.stockHoldingDao.save(holdingList, holdingList.size());
		return;
	}
}
