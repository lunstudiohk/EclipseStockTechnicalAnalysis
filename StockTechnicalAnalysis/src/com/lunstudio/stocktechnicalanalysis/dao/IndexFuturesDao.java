package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;

public interface IndexFuturesDao extends BaseDao {

	public List<IndexFuturesEntity> getFutureList(Date startDate) throws Exception;
}
