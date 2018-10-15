package com.lunstudio.stockanalysis.dao;

import java.sql.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stockanalysis.entity.StockEntity;

@Repository ("stockDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockDaoImpl extends BaseDaoImpl implements StockDao {


	@Override
	public StockEntity getStock(String stockCode) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockEntity> query = builder.createQuery(StockEntity.class);
        Root<StockEntity> stockInfoRoot = query.from(StockEntity.class);
        query.select(stockInfoRoot);
        	query.where(builder.equal(stockInfoRoot.get("stockCode"), stockCode));	    
		return session.createQuery(query).setMaxResults(1).getSingleResult();
	}
	
	@Override
	public List<StockEntity> getStockList() {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockEntity> query = builder.createQuery(StockEntity.class);
        Root<StockEntity> stockInfoRoot = query.from(StockEntity.class);
        query.select(stockInfoRoot);
	    query.orderBy(builder.asc(stockInfoRoot.get("stockCode")));
		return session.createQuery(query).getResultList();
	}
	
	@Override
	public int updateStockProcessedDate(String stockCode, Date date) {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<StockEntity> updater = builder.createCriteriaUpdate(StockEntity.class);
        Root<StockEntity> stockInfoRoot = updater.from(StockEntity.class);
        updater.set("stockProcessed", date);
        updater.where(builder.equal(stockInfoRoot.get("stockCode"), stockCode));
        return session.createQuery(updater).executeUpdate();
	}
}
