package com.melot.change.pg2es;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PgRoller {

	private int start = 1000000;
	
	private static int skipRange = 1500;
	
	private int end = start + skipRange;
	
	private DataSource ds;
	
	@Scheduled(cron = "0/10 * * * * ?")
	// cron接受cron表达式，根据cron表达式确定定时规则
	public void testCron() {
		try {
			ds = createDataSource("jdbc:postgresql://pg44.kktv2.com:5432/appstable", "kkstable", "kkstable");
			Connection conn = ds.getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement("update public.base_user_info set pre_c = 1 where user_id between ? and ?");
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			int rows = pstmt.executeUpdate();
//			conn.commit();
			start += skipRange;
			end += skipRange;
			System.out.println("succes update range :" + start + " to " + end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static DataSource createDataSource(String uri, String user,
			String pwd) throws Exception {
		PGPoolingDataSource ds = new PGPoolingDataSource();
		ds.setUrl(uri);
		ds.setUser(user);
		ds.setPassword(pwd);
		return ds;
	}

}
