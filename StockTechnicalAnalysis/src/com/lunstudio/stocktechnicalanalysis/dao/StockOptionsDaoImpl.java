package com.lunstudio.stocktechnicalanalysis.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.StockOptionsEntity;
import com.lunstudio.stocktechnicalanalysis.valueobject.OptionAmountVo;

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

	public List<OptionAmountVo> getOptionAmountList(String stockCode, Date startDate) throws Exception {
		List<OptionAmountVo> optionList = new ArrayList<OptionAmountVo>();
		Session session = this.sessionFactory.getCurrentSession();
        String hql = "SELECT tradeDate, optionType, Sum(Round(openInterest * dayClose,0)), Sum(Round(dayVolume * dayClose, 0)) "
        		+ "FROM StockOptionsEntity "
        		+ "WHERE stockCode = :stockCode AND tradeDate >= :tradeDate And dayVolume > 0 "
        		+ "GROUP BY tradeDate, optionType ORDER BY tradeDate Asc";
        Query query = session.createQuery(hql);
		query.setParameter("stockCode",	stockCode);
		query.setParameter("tradeDate", startDate);
		List<Object[]> result = query.list();
		OptionAmountVo vo = null;
		for(Object[] obj: result) {
			if( vo == null ) {
				vo = new OptionAmountVo();
				vo.setStockCode(stockCode);
				vo.setTradeDate((Date)obj[0]);
			}
			if( "C".equals(obj[1]) ) {
				vo.setOptionCallOpenInterest( Long.valueOf(obj[2].toString()));
				vo.setOptionCallVolume( Long.valueOf(obj[3].toString()));
			} else {
				vo.setOptionPutOpenInterest( Long.valueOf(obj[2].toString()));
				vo.setOptionPutVolume( Long.valueOf(obj[3].toString()));
			}
			if( vo.getOptionCallOpenInterest() != null && vo.getOptionPutOpenInterest() != null ) {
				optionList.add(vo);
				vo = null;
			}
		}
		
		return optionList;
	}
	
}
