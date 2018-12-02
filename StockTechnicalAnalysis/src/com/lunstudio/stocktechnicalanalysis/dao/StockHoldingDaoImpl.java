package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.StockHoldingEntity;

import org.hibernate.Session;


@Repository ("stockHoldingDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockHoldingDaoImpl extends BaseDaoImpl implements StockHoldingDao {
	@Override
	public Date getLastHoldingDate(String stockCode) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockHoldingEntity> query = builder.createQuery(StockHoldingEntity.class);
        Root<StockHoldingEntity> stockHoldingRoot = query.from(StockHoldingEntity.class);
        query.select(stockHoldingRoot);
        
        Predicate primaryKey = builder.and(
        		builder.equal(stockHoldingRoot.get("stockCode"), stockCode)
        	);
        query.where(primaryKey);
        query.orderBy(builder.desc(stockHoldingRoot.get("holdingDate")));
        StockHoldingEntity holding = null;
        try {
        	holding = session.createQuery(query).setMaxResults(1).getSingleResult();
        } catch(NoResultException e) {
        	return null;
        }
       	return holding.getHoldingDate();
	}
}
