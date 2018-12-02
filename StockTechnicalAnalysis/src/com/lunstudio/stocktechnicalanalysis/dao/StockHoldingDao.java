package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;

public interface StockHoldingDao extends BaseDao{

	public Date getLastHoldingDate(String stockCode);
}
