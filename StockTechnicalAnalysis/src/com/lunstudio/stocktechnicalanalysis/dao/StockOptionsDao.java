package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;

public interface StockOptionsDao extends BaseDao {

	public List<StockOptionsEntity> getOptionList(String stockCode, Date startDate) throws Exception;

	public List<OptionAmountVo> getOptionAmountList(String stockCode, Date startDate) throws Exception;

}
