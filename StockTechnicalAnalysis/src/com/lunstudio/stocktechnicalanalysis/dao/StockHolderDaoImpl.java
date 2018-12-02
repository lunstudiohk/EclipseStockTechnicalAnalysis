package com.lunstudio.stocktechnicalanalysis.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository ("stockHolderDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockHolderDaoImpl extends BaseDaoImpl implements StockHolderDao {
	
}
