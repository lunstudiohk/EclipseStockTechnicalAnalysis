package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

public interface CandlestickDao extends BaseDao {

	public List<CandlestickEntity> getCandlestickList(String stockCode) throws Exception;
}
