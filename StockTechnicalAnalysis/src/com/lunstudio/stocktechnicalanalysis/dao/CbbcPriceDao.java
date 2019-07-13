package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.CbbcPriceEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.CbbcAmountVo;

public interface CbbcPriceDao extends BaseDao{

	public List<CbbcPriceEntity> getCbbcPriceList(Date tradeDate);

	public List<CbbcPriceEntity> getCbbcPriceList(String cbbcUnderlying, Date tradeDate);

	public List<CbbcAmountVo> getCbbcAmountList(String stockCode, Date startDate);
}
