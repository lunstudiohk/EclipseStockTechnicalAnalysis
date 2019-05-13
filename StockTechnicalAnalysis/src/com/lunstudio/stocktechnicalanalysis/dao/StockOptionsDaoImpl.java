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

import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;

@Repository ("stockOptionsDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StockOptionsDaoImpl extends BaseDaoImpl implements StockOptionsDao {

	@Override
	public List<StockOptionsEntity> getOptionList(String stockCode, Date startDate) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<StockOptionsEntity> query = builder.createQuery(StockOptionsEntity.class);
        Root<StockOptionsEntity> optionPriceRoot = query.from(StockOptionsEntity.class);
        query.select(optionPriceRoot);
        
        Predicate primaryKey = builder.and(
        		builder.equal(optionPriceRoot.get("stockCode"), stockCode),
        		builder.greaterThan(optionPriceRoot.get("volume"), 0),
        		builder.lessThanOrEqualTo(optionPriceRoot.get("month"), 1)
        	);
        query.where(primaryKey);
        return session.createQuery(query).list(); 
	}

}
