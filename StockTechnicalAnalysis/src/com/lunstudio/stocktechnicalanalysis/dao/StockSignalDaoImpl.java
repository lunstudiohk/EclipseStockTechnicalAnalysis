package com.lunstudio.stocktechnicalanalysis.dao;

import java.sql.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalEntity;

@Repository ("stockSignalDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockSignalDaoImpl extends BaseDaoImpl implements StockSignalDao {

	@Override
	public List<StockSignalEntity> getStockSignalList(String stockCode, Date startDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockSignalEntity> query = builder.createQuery(StockSignalEntity.class);
        Root<StockSignalEntity> signalRoot = query.from(StockSignalEntity.class);
        query.select(signalRoot);
        
        if( stockCode != null && startDate != null) {
        	Predicate primaryKey = builder.and(
        			builder.equal(signalRoot.get("stockCode"), stockCode),
                	builder.greaterThanOrEqualTo(signalRoot.get("tradeDate"), startDate)
        			);
        	query.where(primaryKey);
        } else if( stockCode != null && startDate == null) {
        	Predicate primaryKey = builder.equal(signalRoot.get("stockCode"), stockCode);
        	query.where(primaryKey);
        } else if( stockCode == null && startDate != null) {
        	Predicate primaryKey = builder.greaterThanOrEqualTo(signalRoot.get("tradeDate"), startDate);
        	query.where(primaryKey);
        }
        return session.createQuery(query).list(); 
	}

	@Override
	public List<StockSignalEntity> getIncompletedStockSignalList() throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockSignalEntity> query = builder.createQuery(StockSignalEntity.class);
        Root<StockSignalEntity> signalRoot = query.from(StockSignalEntity.class);
        query.select(signalRoot);
       	Predicate primaryKey = builder.lessThan(signalRoot.get("completed"), signalRoot.get("period"));
       	query.where(primaryKey);
        return session.createQuery(query).list(); 
	}

	@Override
	public List<StockSignalEntity> getStockSignalListOnDate(Date date) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockSignalEntity> query = builder.createQuery(StockSignalEntity.class);
        Root<StockSignalEntity> signalRoot = query.from(StockSignalEntity.class);
        query.select(signalRoot);
        
       	Predicate primaryKey = builder.equal(signalRoot.get("tradeDate"), date);
        query.where(primaryKey);
        return session.createQuery(query).list(); 
	}

}
