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

import com.lunstudio.stocktechnicalanalysis.entity.IndexFuturesEntity;
import com.lunstudio.stocktechnicalanalysis.entity.StockPriceEntity;

@Repository ("indexFuturesDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class IndexFuturesDaoImpl extends BaseDaoImpl implements IndexFuturesDao {

	@Override
	public List<IndexFuturesEntity> getFutureList(Date startDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<IndexFuturesEntity> query = builder.createQuery(IndexFuturesEntity.class);
        Root<IndexFuturesEntity> futureRoot = query.from(IndexFuturesEntity.class);
        query.select(futureRoot);
        
        Predicate primaryKey = builder.and(
        		builder.greaterThanOrEqualTo(futureRoot.get("tradeDate"), startDate),
        		builder.lessThan(futureRoot.get("month"), 2)
        	);
        query.where(primaryKey);
        return session.createQuery(query).list(); 
	}

}
