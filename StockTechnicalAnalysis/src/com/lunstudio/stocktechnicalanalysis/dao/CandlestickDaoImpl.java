package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.CandlestickEntity;

@Repository ("candlestickDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CandlestickDaoImpl extends BaseDaoImpl implements CandlestickDao {

	@Override
	public List<CandlestickEntity> getCandlestickList(String stockCode) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CandlestickEntity> query = builder.createQuery(CandlestickEntity.class);
        Root<CandlestickEntity> candlestickRoot = query.from(CandlestickEntity.class);
        query.select(candlestickRoot);
        query.where(builder.and(builder.equal(candlestickRoot.get("stockCode"), stockCode)));
		List<CandlestickEntity> candlestickEntityList = session.createQuery(query).getResultList();
		return candlestickEntityList;
	}

	@Override
	public void deleteCandlestick(String stockCode, Date tradeDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaDelete<CandlestickEntity> query = builder.createCriteriaDelete(CandlestickEntity.class);
        Root<CandlestickEntity> root = query.from(CandlestickEntity.class);
        query.where(builder.and(builder.equal(root.get("stockCode"), stockCode), builder.greaterThanOrEqualTo(root.get("tradeDate"), tradeDate)));
		session.createQuery(query).executeUpdate();
		return;
	}

	@Override
	public List<CandlestickEntity> getCandlestickListFromDate(Date tradeDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CandlestickEntity> query = builder.createQuery(CandlestickEntity.class);
        Root<CandlestickEntity> candlestickRoot = query.from(CandlestickEntity.class);
        query.select(candlestickRoot);
        query.where(builder.and(builder.greaterThanOrEqualTo(candlestickRoot.get("tradeDate"), tradeDate)));
	    query.orderBy(builder.asc(candlestickRoot.get("tradeDate")));
		List<CandlestickEntity> candlestickEntityList = session.createQuery(query).getResultList();
		return candlestickEntityList;
	}

	@Override
	public List<CandlestickEntity> getCandlestickListFromDate(String stockCode, Date tradeDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CandlestickEntity> query = builder.createQuery(CandlestickEntity.class);
        Root<CandlestickEntity> candlestickRoot = query.from(CandlestickEntity.class);
        query.select(candlestickRoot);
        query.where(
        	builder.equal(candlestickRoot.get("stockCode"), stockCode),
        	builder.greaterThanOrEqualTo(candlestickRoot.get("tradeDate"), tradeDate)
        );
        
	    query.orderBy(builder.asc(candlestickRoot.get("tradeDate")));
		List<CandlestickEntity> candlestickEntityList = session.createQuery(query).getResultList();
		return candlestickEntityList;
	}

}
