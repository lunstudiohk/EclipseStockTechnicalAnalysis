package com.lunstudio.stocktechnicalanalysis.dao;

import java.util.List;

import com.lunstudio.stocktechnicalanalysis.entity.SystemMessageEntity;

public interface SystemMessageDao extends BaseDao {

	public List<SystemMessageEntity> getSystemMessageList() throws Exception;
}
