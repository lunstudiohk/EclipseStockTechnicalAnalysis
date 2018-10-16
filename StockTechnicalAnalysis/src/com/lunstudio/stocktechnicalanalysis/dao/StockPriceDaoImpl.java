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

	/*
	@Override
	public StockPriceEntity findByStockTradeDate(String stockCode, Date tradeDate, String priceType) {
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
	public StockPriceEntity getLatestStockPriceEntity(String stockCode, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.and(builder.equal(stockPriceRoot.get("stockCode"), stockCode), builder.equal(stockPriceRoot.get("priceType"), priceType)));
	    query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
		return session.createQuery(query).setMaxResults(1).getSingleResult();
	}
	
	@Override
	public List<StockPriceEntity> getStockPriceAfterTradeDate(String stockCode, Date tradeDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.greaterThan(stockPriceRoot.get("tradeDate"), tradeDate));
	    query.orderBy(builder.asc(stockPriceRoot.get("tradeDate")));
    		return session.createQuery(query).getResultList();
	}
	
	@Override
	public List<StockPriceEntity> getStockPriceListInDesc(String stockCode, Integer count, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.and(builder.equal(stockPriceRoot.get("stockCode"), stockCode), builder.equal(stockPriceRoot.get("priceType"), priceType)));
	    query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
	    if( count == null || count == 0 ) {
	    		return session.createQuery(query).getResultList();
	    } else {
	    		return session.createQuery(query).setMaxResults(count).getResultList();
	    }
	}

	@Override
	public List<StockPriceEntity> getStockPriceListInAsc(String stockCode, Integer count, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.and(builder.equal(stockPriceRoot.get("stockCode"), stockCode), builder.equal(stockPriceRoot.get("priceType"), priceType)));
	    query.orderBy(builder.asc(stockPriceRoot.get("tradeDate")));
	    if( count == null || count == 0 ) {
	    	return session.createQuery(query).getResultList();
	    } else {
	    	return session.createQuery(query).setMaxResults(count).getResultList();
	    }
	}

	@Override
	public StockPriceEntity getLastStockPriceInPeriod(String stockCode, Integer period, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(builder.and(builder.equal(stockPriceRoot.get("stockCode"), stockCode), builder.equal(stockPriceRoot.get("priceType"), priceType)));
	    query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
		List<StockPriceEntity> stockPriceEntityList = session.createQuery(query).setMaxResults(period).getResultList();
		if(stockPriceEntityList != null && !stockPriceEntityList.isEmpty() ) {
			return stockPriceEntityList.get(stockPriceEntityList.size()-1);
		} else {
			return null;
		}
	}

	@Override
	public Long getTradeDateCount(String stockCode, Date startDate, Date endDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(builder.count(stockPriceRoot));
        if( endDate != null ) {
        		query.where(builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        				builder.equal(stockPriceRoot.get("priceType"), priceType),
        				builder.greaterThanOrEqualTo(stockPriceRoot.get("tradeDate"), startDate),
        				builder.lessThanOrEqualTo(stockPriceRoot.get("tradeDate"), endDate));
        } else {
        		query.where(builder.equal(stockPriceRoot.get("stockCode"), stockCode),
        				builder.greaterThanOrEqualTo(stockPriceRoot.get("tradeDate"), startDate));
        }
        return session.createQuery(query).getSingleResult();
	}

	@Override
	public void deleteByStockCode(String stockCode) {
		Session session = this.sessionFactory.getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<StockPriceEntity> delete = builder.createCriteriaDelete(StockPriceEntity.class);
        Root<StockPriceEntity> stockTradeRoot = delete.from(StockPriceEntity.class);
        delete.where(builder.equal(stockTradeRoot.get("stockCode"), stockCode));
        session.createQuery(delete).executeUpdate();
        return;
	}

	@Override
	public List<StockPriceEntity> getLastStockPriceListInAsc(String stockCode, Integer period, String priceType) {
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
	public List<Date> getLastTradeDateList(Date endDate, Integer count, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Date> query = builder.createQuery(Date.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot.get("tradeDate")).distinct(true);
        if( endDate != null ) {
        	query.where(builder.and(builder.lessThanOrEqualTo(stockPriceRoot.get("tradeDate"), endDate), builder.equal(stockPriceRoot.get("priceType"), priceType)));
        }
	    query.orderBy(builder.desc(stockPriceRoot.get("tradeDate")));
        return session.createQuery(query).setMaxResults(count).getResultList();
	}

	@Override
	public List<StockPriceEntity> getStockPriceEntityListInDate(Date startDate, Date endDate, String priceType) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
        query.select(stockPriceRoot);
        query.where(
        		builder.equal(stockPriceRoot.get("priceType"), priceType),
        		builder.lessThanOrEqualTo(stockPriceRoot.get("tradeDate"), endDate),
        		builder.greaterThanOrEqualTo(stockPriceRoot.get("tradeDate"), startDate)
        );
	    query.orderBy(builder.asc(stockPriceRoot.get("tradeDate")));
		return session.createQuery(query).getResultList();
	}

	@Override
	public StockPriceEntity findByStockTradeDate(String stockCode, Date tradeDate) {
		return this.findByStockTradeDate(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public StockPriceEntity getLatestStockPriceEntity(String stockCode) {
		return this.getLatestStockPriceEntity(stockCode, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<StockPriceEntity> getStockPriceAfterTradeDate(String stockCode, Date tradeDate) {
		return this.getStockPriceAfterTradeDate(stockCode, tradeDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<StockPriceEntity> getStockPriceListInDesc(String stockCode, Integer count) {
		return this.getStockPriceListInDesc(stockCode, count, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<StockPriceEntity> getStockPriceListInAsc(String stockCode, Integer count) {
		return this.getStockPriceListInAsc(stockCode, count, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public StockPriceEntity getLastStockPriceInPeriod(String stockCode, Integer period) {
		return this.getLastStockPriceInPeriod(stockCode, period, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<StockPriceEntity> getLastStockPriceListInAsc(String stockCode, Integer period) {
		return this.getLastStockPriceListInAsc(stockCode, period, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public Long getTradeDateCount(String stockCode, Date startDate, Date endDate) {
		return this.getTradeDateCount(stockCode, startDate, endDate, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<Date> getLastTradeDateList(Date endDate, Integer count) {
		return this.getLastTradeDateList(endDate, count, StockPriceEntity.PRICE_TYPE_DAILY);
	}

	@Override
	public List<StockPriceEntity> getStockPriceEntityListInDate(Date startDate, Date endDate) {
		return this.getStockPriceEntityListInDate(startDate, endDate, StockPriceEntity.PRICE_TYPE_DAILY);
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
	public List<StockPriceEntity> getZeroStockPriceList() {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockPriceEntity> query = builder.createQuery(StockPriceEntity.class);
        Root<StockPriceEntity> stockPriceRoot = query.from(StockPriceEntity.class);
       	query.where(builder.or(
       			builder.lessThanOrEqualTo(stockPriceRoot.get("closePrice"), 0), 
       			builder.lessThanOrEqualTo(stockPriceRoot.get("openPrice"), 0),
       			builder.lessThanOrEqualTo(stockPriceRoot.get("dayHigh"), 0),
       			builder.lessThanOrEqualTo(stockPriceRoot.get("dayLow"), 0)
       	));
        return session.createQuery(query).getResultList();
	}
	*/
	
}
