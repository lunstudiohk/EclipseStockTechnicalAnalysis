package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

import org.springframework.transaction.annotation.Propagation;

//@SuppressWarnings("static-access")
@Repository ("stockPriceDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockPriceDaoImpl extends BaseDaoImpl implements StockPriceDao {

	public StockPriceEntity getStockPrice(String stockCode, Date tradeDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        
        Predicate primaryKey = builder.and(
        		builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.equal(stockPriceRoot.get("tradeDate"), tradeDate)
        	);
        query.where(primaryKey);
        List<StockPriceEntity> tmpList = session.createQuery(query).list(); 
        if( tmpList != null && tmpList.size() > 0 ) {
        	return tmpList.get(0);
        } else {
        	return null;
        }
	}

	@Override
	public List<StockPriceEntity> getLastStockPriceList(String stockCode, Integer period, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.and(builder.equal(stockPriceRoot.get("stockCode"), stockCode), builder.equal(stockPriceRoot.get("priceType"), priceType)));
	    query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
		List<StockPriceEntity> stockPriceEntityList = null;
		if( period != null ) {
			stockPriceEntityList = session.createQuery(query).setMaxResults(period).getResultList();
		} else {
			stockPriceEntityList = session.createQuery(query).getResultList();
		}
		List<StockPriceEntity> sortedStockPriceEntityList = new ArrayList<StockPriceEntity>(stockPriceEntityList.size());
		if(stockPriceEntityList != null && !stockPriceEntityList.isEmpty() ) {
			for(StockPriceEntity entity : stockPriceEntityList ) {
				sortedStockPriceEntityList.add(0, entity);
			}
			return sortedStockPriceEntityList;
		} else {
			return null;
		}
	}
	
	@Override
	public List<StockPriceEntity> getStockPriceEntityListInDate(String stockCode, Date startDate, Date endDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(
        		builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.lessThanOrEqualTo(stockPriceRoot.get("tradeDate"), endDate),
        		builder.greaterThanOrEqualTo(stockPriceRoot.get("tradeDate"), startDate)
        );
	    query.orderBy(builder.asc(stockPriceRoot.get("tradeDate")));
		return session.createQuery(query).getResultList();
	}

	@Override
	public List<StockPriceEntity> getStockPriceList(Date tradeDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);        
        Predicate primaryKey = builder.and(
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.equal(stockPriceRoot.get("tradeDate"), tradeDate)
        	);
        query.where(primaryKey);
        return session.createQuery(query).list(); 
	}

	@Override
	public StockPriceEntity getPreviousStockPrice(String stockCode, Date tradeDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        
        Predicate primaryKey = builder.and(
        		builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.lessThan(stockPriceRoot.get("tradeDate"), tradeDate)
        	);
        query.where(primaryKey);
        query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
        List<StockPriceEntity> tmpList = session.createQuery(query).setMaxResults(1).list(); 
        if( tmpList != null && tmpList.size() > 0 ) {
        	return tmpList.get(0);
        } else {
        	return null;
        }
	}
}
