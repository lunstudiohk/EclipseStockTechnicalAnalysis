package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
//import java.util.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.WarrantAmountVo;

public interface WarrantPriceDao extends BaseDao{

	public List<WarrantPriceEntity> getLastWarrantPriceList(String warrantCode, Integer period);
	
	public List<WarrantPriceEntity> getWarrantPriceList(Date tradeDate);
	
	public List<WarrantPriceEntity> getWarrantPriceList(String warrantUnderlying, Date tradeDate);
	
	public List<WarrantAmountVo> getWarrantAmountList(String warrantUnderlying, Date tradeDate);

}
