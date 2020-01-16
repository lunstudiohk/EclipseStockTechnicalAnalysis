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

import com.lunstudio.stocktechnicalanalysis.entity.StockSignalDateEntity;

@Repository ("stockSignalDateDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockSignalDateDaoImpl extends BaseDaoImpl implements StockSignalDateDao {

	@Override
	public StockSignalDateEntity findByPrimaryKey(String stockCode, Date tradeDate, Integer signalSeq, String signalType, Date signalDate)
			throws Exception {
		
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockSignalDateEntity> query = builder.createQuery(StockSignalDateEntity.class);
        Root<StockSignalDateEntity> signalDateRoot = query.from(StockSignalDateEntity.class);
        query.select(signalDateRoot);
        
       	Predicate primaryKey = builder.and(
       			builder.equal(signalDateRoot.get("stockCode"), stockCode),
               	builder.equal(signalDateRoot.get("tradeDate"), tradeDate),
               	builder.equal(signalDateRoot.get("signalSeq"), signalSeq),
               	builder.equal(signalDateRoot.get("signalType"), signalType),
               	builder.equal(signalDateRoot.get("signalDate"), signalDate)
       		);
       	query.where(primaryKey);
        return session.createQuery(query).uniqueResult(); 
	}

	@Override
	public List<StockSignalDateEntity> getStockSignalDateList(String stockCode, Date tradeDate, Integer signalSeq,
			String signalType, Integer order) throws Exception {
		
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockSignalDateEntity> query = builder.createQuery(StockSignalDateEntity.class);
        Root<StockSignalDateEntity> signalDateRoot = query.from(StockSignalDateEntity.class);
        query.select(signalDateRoot);
        
       	Predicate primaryKey = builder.and(
       			builder.equal(signalDateRoot.get("stockCode"), stockCode),
               	builder.equal(signalDateRoot.get("tradeDate"), tradeDate),
               	builder.equal(signalDateRoot.get("signalSeq"), signalSeq),
               	builder.equal(signalDateRoot.get("signalType"), signalType)
       		);
       	query.where(primaryKey);
       	if( order == StockSignalDateEntity.ASC ) {
       		query.orderBy(builder.asc(signalDateRoot.get("signalDate")));
       	} else {
       		query.orderBy(builder.desc(signalDateRoot.get("signalDate")));
       	}
        return session.createQuery(query).list();
	}

}
