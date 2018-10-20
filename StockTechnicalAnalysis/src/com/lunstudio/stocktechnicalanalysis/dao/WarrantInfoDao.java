package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.WarrantInfoEntity;

public interface WarrantInfoDao extends BaseDao{


	public List<WarrantInfoEntity> getWarrantList();

}
