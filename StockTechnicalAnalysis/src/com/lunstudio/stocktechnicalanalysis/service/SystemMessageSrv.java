package com.lunstudio.stocktechnicalanalysis.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunstudio.stocktechnicalanalysis.dao.SystemMessageDao;
import com.lunstudio.stocktechnicalanalysis.entity.SystemMessageEntity;

@Service
public class SystemMessageSrv {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private SystemMessageDao systemMessageDao;
	
	public List<SystemMessageEntity> getSystemMessageList() throws Exception {
		return this.systemMessageDao.getSystemMessageList();
	}
	
	public void saveSystemErrorMessage(String messageContent) throws Exception {
		SystemMessageEntity entity = new SystemMessageEntity(SystemMessageEntity.ERROR, messageContent);
		this.systemMessageDao.save(entity);
		return;
	}
	
	public void saveSystemWarningMessage(String messageContent) throws Exception {
		SystemMessageEntity entity = new SystemMessageEntity(SystemMessageEntity.WARNING, messageContent);
		this.systemMessageDao.save(entity);
		return;
	}
	
}
