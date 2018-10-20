package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantPriceEntity;

public interface WarrantPriceDao extends BaseDao{


	public List<WarrantPriceEntity> getWarrantPriceList();

}
