package CTD_ContractTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;

public class Stat_Event_verify_event extends extentreport {
	String username;
	String password;
	java.sql.Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	public void databaseconn() {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test
	public String exampleGetTest(String TIMEST, String TYPE, String STA_ID, String fd, int i)
			throws ParseException, IOException, SQLException {

		databaseconn();
		Statement s = conn.createStatement();
		Statement f = conn.createStatement();
		ResultSet rs = null;
		ResultSet event_message_elements_rs = null;
		
		try {
			String gd = null;

			int fd_present = 0;
			int gd_present = 0;
			int st_id_present = 0;

			BigDecimal id = null;
			String idstring = null;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(TIMEST));
			calendar.add(Calendar.MINUTE, 1);
			Date d = calendar.getTime();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getDefault());
			String datetime = formatter.format(d);

			rs = s.executeQuery(prop.getProperty("exampleGetTest_oneselect_onepart") + "'" + TIMEST + "'"
					+ prop.getProperty("exampleGetTest_oneselect_twopart") + "'" + datetime + "'"
					+ prop.getProperty("exampleGetTest_oneselect_threepart") + "'" + TYPE + "'");


			exit: while (rs.next()) {
				fd_present = 0;
				gd_present = 0;
				st_id_present = 0;

				id = rs.getBigDecimal("event_id");
				idstring = String.valueOf(id);

				event_message_elements_rs = f
						.executeQuery(prop.getProperty("exampleGetTest_usingidselect") + idstring + "");

				while (event_message_elements_rs.next()) {


					String dt_elt = event_message_elements_rs.getString(prop.getProperty("dt_elt"));
					
					if (dt_elt.equals(prop.getProperty("evnt_msg_fd"))) {
						if (i == 1) {
							fd_present = 1;
							fd = "NULL";

						} else {
							if (event_message_elements_rs.getString(prop.getProperty("dt_txt")).length() > 1) {
								fd_present = 1;
							}

						}
					}
					if (dt_elt.equals(prop.getProperty("gd"))) {
						gd = event_message_elements_rs.getString(prop.getProperty("dt_txt"));
						if (gd.length() > 0) {
							gd_present = 1;
						}

					}

					if (dt_elt.equals(prop.getProperty("sd"))) {
						if (event_message_elements_rs.getString("data_value_txt").equals(STA_ID))// validating for
																									// client no
						{

							st_id_present = 1;
						}

					}

					if (st_id_present == 1 && fd_present == 1 && gd_present == 1) {

						break exit;

					}

				}

			}

			if (st_id_present == 1 && fd_present == 1 && gd_present == 1) {

				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));
				Assert.assertTrue(true, "Event Triggered");

			} else {
				childTest.log(Status.INFO,
						MarkupHelper.createLabel("No Event was triggered for the requested details", colour.BLUE));
				Assert.assertTrue(false, "No Event Triggered");
			}

			childTest.log(Status.INFO, prop.getProperty("gdlog")+""+ gd + "");
			return gd;
		} finally {
			rs.close();
			s.close();
			f.close();
			event_message_elements_rs.close();
			teardown();
		}
	}

	private void teardown() throws SQLException {
		// TODO Auto-generated method stub
		conn.close();
	}

}
