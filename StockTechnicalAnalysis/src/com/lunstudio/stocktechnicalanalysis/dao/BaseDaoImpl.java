package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository ("baseDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BaseDaoImpl implements BaseDao{

	@Autowired
	protected SessionFactory sessionFactory;

	protected void saveOrUpdate(Object obj){
		this.sessionFactory.getCurrentSession().saveOrUpdate(obj);
		return;
	}

	@Override
	public <T> void save(List<T> entityList, Integer commitCount) {
		Session session = sessionFactory.getCurrentSession();
		int count = 0;
		for(Object entity : entityList) {
			session.saveOrUpdate(entity);
			if( count++ % commitCount == 0 ) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
		
	}

	@Override
	public <T> void save(T entity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(entity);
		return;
	}

}
