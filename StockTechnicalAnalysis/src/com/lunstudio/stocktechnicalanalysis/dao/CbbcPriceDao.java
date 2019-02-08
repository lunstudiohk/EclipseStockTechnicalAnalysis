package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;

public interface CbbcPriceDao extends BaseDao{

	public List<CbbcPriceEntity> getCbbcPriceList(Date tradeDate);

}
