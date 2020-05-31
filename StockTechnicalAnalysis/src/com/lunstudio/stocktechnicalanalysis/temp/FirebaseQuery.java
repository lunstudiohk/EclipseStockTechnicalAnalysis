package com.lunstudio.stocktechnicalanalysis.temp;

import java.util.Iterator;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lunstudio.stocktechnicalanalysis.batch.UpdateStockPriceToFirebase;
import com.lunstudio.stocktechnicalanalysis.firebase.FirebaseDao;
import com.lunstudio.stocktechnicalanalysis.service.FirebaseSrv;

@Component
public class FirebaseQuery {

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private FirebaseSrv firebaseSrv;
	
	public static void main(String[] args) {
		try{
			String configPath = System.getProperty("spring.config");
			FileSystemXmlApplicationContext context = 
					new FileSystemXmlApplicationContext(configPath);
			FirebaseQuery instance = context.getBean(FirebaseQuery.class);
			instance.start(args);
			context.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		System.exit(0);
		return;
	}

	//Delete All
	/*
	private void start(String[] args) throws Exception {
		this.firebaseSrv.setValueToFirebase(FirebaseDao.getInstance().getCbbcPriceDataRef(), null);
		return;
	}
	*/
	
	private void start(String[] args) throws Exception {
		logger.info("Start.....");
		final Semaphore semaphore = new Semaphore(0);
		
		//DatabaseReference ref = FirebaseDao.getInstance().getStockSignalRef();
		//ref.orderByChild("stock").equalTo("HKG:0700").addValueEventListener(new ValueEventListener() {
		
		//DatabaseReference ref = FirebaseDao.getInstance().getStockPriceRef();
		//ref.orderByChild("date").equalTo("2020-01-03").addValueEventListener(new ValueEventListener() {
		
		DatabaseReference ref = FirebaseDao.getInstance().getStockTradeDateRef();
		ref.orderByKey().limitToLast(50).addValueEventListener(new ValueEventListener() {
		
		//DatabaseReference ref = FirebaseDao.getInstance().getStockSignalRef();
		//ref.orderByChild("stock").equalTo("INDEXHANGSENG:HSI").limitToLast(50).addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
				while(it.hasNext()) {
					DataSnapshot children = it.next();
					logger.info(children);
				}
				//logger.info(dataSnapshot.getValue());
				semaphore.release();
			}

		});
		semaphore.acquire();
		return;
	}
}
