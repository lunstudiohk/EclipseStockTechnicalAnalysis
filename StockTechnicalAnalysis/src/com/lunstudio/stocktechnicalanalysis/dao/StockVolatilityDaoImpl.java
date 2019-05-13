package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.StockVolatilityEntity;

@Repository ("stockVolatilityDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockVolatilityDaoImpl extends BaseDaoImpl implements StockVolatilityDao {

	@Override
	public List<StockVolatilityEntity> getStockVolatilityEntityListOnDate(Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockVolatilityEntity> query = builder.createQuery(StockVolatilityEntity.class);
        Root<StockVolatilityEntity> stockVolatilityRoot = query.from(StockVolatilityEntity.class);
        query.select(stockVolatilityRoot);
        
        query.where(
        		builder.equal(stockVolatilityRoot.get("tradeDate"), tradeDate)
        );
		List<StockVolatilityEntity> stockVolatilityList = session.createQuery(query).getResultList();
		return stockVolatilityList;
	}

	@Override
	public List<StockVolatilityEntity> getStockVolatilityEntityListAfterDate(String stockCode, Date tradeDate) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockVolatilityEntity> query = builder.createQuery(StockVolatilityEntity.class);
        Root<StockVolatilityEntity> stockVolatilityRoot = query.from(StockVolatilityEntity.class);
        query.select(stockVolatilityRoot);
        
        query.where(
        		builder.equal(stockVolatilityRoot.get("stockCode"), stockCode),
        		builder.greaterThanOrEqualTo(stockVolatilityRoot.get("tradeDate"), tradeDate)
        );
		List<StockVolatilityEntity> stockVolatilityList = session.createQuery(query).getResultList();
		return stockVolatilityList;
	}

}
