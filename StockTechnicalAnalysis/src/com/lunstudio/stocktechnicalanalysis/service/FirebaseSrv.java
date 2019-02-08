package com.lunstudio.stocktechnicalanalysis.service;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;

@Service
public class FirebaseSrv {

	private static final Logger logger = LogManager.getLogger();

	private boolean isDeleted = false;
	
	public void updateToFirebase(DatabaseReference ref, Map<String, Object> dataMap) throws Exception {
		Task<?> task = ref.updateChildren(dataMap);
		while( !task.isComplete() ) {
			logger.info("Incomplete");
			Thread.sleep(1000);
		}
		if( task.isSuccessful() ) {
			logger.info("Data saved successfully.");
		} else {
			if( task.getException() != null ) {
				logger.info(String.format("Process Failed: %s", task.getException().getMessage()));
			} else {
				logger.info("Process Failed");
			}
		}
		return;
	}
	
	public void setValueToFirebase(DatabaseReference ref, Object obj) throws Exception {
		Task<?> task = ref.setValue(obj);
		while( !task.isComplete() ) {
			logger.info("Incomplete");
			Thread.sleep(1000);
		}
		if( task.isSuccessful() ) {
			logger.info("Set Value successfully.");
		} else {
			if( task.getException() != null ) {
				logger.info(String.format("Process Failed: %s", task.getException().getMessage()));
			} else {
				logger.info("Process Failed");
			}
		}
		return;
	}
	
	public void deleteFromFirebase(DatabaseReference ref, String key, String value) throws Exception {
		this.isDeleted = false;
		ref.orderByChild(key).equalTo(value).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				
			}
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
					snapshot.getRef().removeValue();
                }
				isDeleted = true;
			}
		});
		while( !this.isDeleted ) {
			logger.info("Incomplete");
			Thread.sleep(1000);
		}
		return;
	}
	
	
}
