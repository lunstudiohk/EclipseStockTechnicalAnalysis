package com.lunstudio.stockanalysis.dao;

import java.util.List;

public interface BaseDao {

	static final int ORDER_ASC = 1;
	static final int ORDER_DESC = 2;
	
	static final int UNLIMIT = -1;
	
	public <T> void save(T entity);
	
	public <T> void save(List<T> entityList, Integer commitCount);

}
