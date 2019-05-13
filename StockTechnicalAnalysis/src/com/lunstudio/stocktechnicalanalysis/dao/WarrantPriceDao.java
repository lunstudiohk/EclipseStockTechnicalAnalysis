package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

public interface WarrantPriceDao extends BaseDao{

	public List<WarrantPriceEntity> getLastWarrantPriceList(String warrantCode, Integer period);
	
	public List<WarrantPriceEntity> getWarrantPriceList(Date tradeDate);
	
	public List<WarrantPriceEntity> getWarrantPriceList(String warrantUnderlying, Date tradeDate);

}
