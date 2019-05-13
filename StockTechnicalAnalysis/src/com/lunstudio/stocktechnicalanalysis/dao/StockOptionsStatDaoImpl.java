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

import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsStatEntity;

@Repository ("stockOptionsStatDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockOptionsStatDaoImpl extends BaseDaoImpl implements StockOptionsStatDao {

	@Override
	public List<StockOptionsStatEntity> getStockOptionStatList(String stockCode, Date startDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockOptionsStatEntity> query = builder.createQuery(StockOptionsStatEntity.class);
        Root<StockOptionsStatEntity> stockRoot = query.from(StockOptionsStatEntity.class);
        query.select(stockRoot);
        
        Predicate primaryKey = builder.and(
        		builder.equal(stockRoot.get("stockCode"), stockCode),
        		builder.greaterThanOrEqualTo(stockRoot.get("tradeDate"), startDate)
        	);
        query.where(primaryKey);
	    query.orderBy(builder.asc(stockRoot.get("tradeDate")));
	    return session.createQuery(query).getResultList();
	}

}
