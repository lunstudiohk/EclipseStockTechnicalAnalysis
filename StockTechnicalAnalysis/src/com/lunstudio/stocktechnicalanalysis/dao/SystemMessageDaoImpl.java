package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lunstudio.stocktechnicalanalysis.entity.SystemMessageEntity;

@Repository ("systemMessagekDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SystemMessageDaoImpl extends BaseDaoImpl implements SystemMessageDao{

	@Override
	public List<SystemMessageEntity> getSystemMessageList() throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
	    CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SystemMessageEntity> query = builder.createQuery(SystemMessageEntity.class);
        Root<SystemMessageEntity> systemMessageRoot = query.from(SystemMessageEntity.class);
        query.select(systemMessageRoot);
	    query.orderBy(builder.desc(systemMessageRoot.get("createTimestamp")));
		List<SystemMessageEntity> systemMessageList = session.createQuery(query).getResultList();
		return systemMessageList;
	}

}
