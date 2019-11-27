package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Properties;

import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_Extent_Report.extentreport;

public class iareports extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	// public String access_token;
	public String edpt;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc = new inscscliefn();

	// public dynamics dy=new dynamics();
	// public Dynamics_val dval=new Dynamics_val();

	Markup m;

	@BeforeTest()
	public void connection() {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// access_token=dy.whenPostJsonUsingHttpClient_thenCorrect();
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test()
	public void psrreport()
			throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienames(idstring, CNO);

	}

	private void insert_csclienames(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReports(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReports(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreportpsr").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreports passed", true);
			} else {
				Assert.assertTrue("insertreports failed", false);
			}

		} else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereports passed", true);
				} else {
					Assert.assertTrue("updatereports failed", false);
				}

			} else {
				Assert.assertTrue("insertreports failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereporttopsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereports passed", true);
			} else {
				Assert.assertTrue("updatereports failed", false);
			}

		} else {
			Assert.assertTrue("insertreports failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereports passed", true);
			} else {
				Assert.assertTrue("updatereports failed", false);
			}

		} else {
			Assert.assertTrue("insertreports failed", false);
		}


		deleteevent.delete();


	}
	
	@Test()
	public void abrreport()
			throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienamesabr(idstring, CNO);

	}

	private void insert_csclienamesabr(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReportsabr(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReportsabr(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreportabr").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreportsabr passed", true);
			} else {
				Assert.assertTrue("insertreportsabr failed", false);
			}

		} else {
			Assert.assertTrue("insertreportsabr failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereportsfromabr passed", true);
				} else {
					Assert.assertTrue("updatereportsfromabr failed", false);
				}

			} else {
				Assert.assertTrue("updatereportsfromabr failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereporttoabr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereportstoabr passed", true);
			} else {
				Assert.assertTrue("updatereportstoabr failed", false);
			}

		} else {
			Assert.assertTrue("updatereportstoabr failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deletereportabr passed", true);
			} else {
				Assert.assertTrue("deletereportabr failed", false);
			}

		} else {
			Assert.assertTrue("deletereportabr failed", false);
		}


		deleteevent.delete();


	}
	
	@Test()
	public void report811()
			throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienames811(idstring, CNO);

	}

	private void insert_csclienames811(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReports811(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReports811(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreport811").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreports811 passed", true);
			} else {
				Assert.assertTrue("insertreports811 failed", false);
			}

		} else {
			Assert.assertTrue("insertreports811 failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereportsfrom811 passed", true);
				} else {
					Assert.assertTrue("updatereportsfrom811 failed", false);
				}

			} else {
				Assert.assertTrue("updatereportsfrom811 failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereportto811").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereportsto811 passed", true);
			} else {
				Assert.assertTrue("updatereportsto811 failed", false);
			}

		} else {
			Assert.assertTrue("updatereportsto811 failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deletereport811 passed", true);
			} else {
				Assert.assertTrue("deletereport811 failed", false);
			}

		} else {
			Assert.assertTrue("deletereport811 failed", false);
		}


		deleteevent.delete();


	}
	
	@Test()
	public void report733() throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienames733(idstring, CNO);

	}

	private void insert_csclienames733(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReports733(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReports733(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreport733").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreports733 passed", true);
			} else {
				Assert.assertTrue("insertreports733 failed", false);
			}

		} else {
			Assert.assertTrue("insertreports733 failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereportsfrom733 passed", true);
				} else {
					Assert.assertTrue("updatereportsfrom733 failed", false);
				}

			} else {
				Assert.assertTrue("updatereportsfrom733 failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereportto733").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereportsto733 passed", true);
			} else {
				Assert.assertTrue("updatereportsto733 failed", false);
			}

		} else {
			Assert.assertTrue("updatereportsto733 failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deletereport733 passed", true);
			} else {
				Assert.assertTrue("deletereport733 failed", false);
			}

		} else {
			Assert.assertTrue("deletereport733 failed", false);
		}


		deleteevent.delete();


	}
	
	@Test()
	public void reportabb() throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienamesabb(idstring, CNO);

	}

	private void insert_csclienamesabb(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReportsabb(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReportsabb(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreportabb").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreportsabb passed", true);
			} else {
				Assert.assertTrue("insertreportsabb failed", false);
			}

		} else {
			Assert.assertTrue("insertreportsabb failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereportsfromabb passed", true);
				} else {
					Assert.assertTrue("updatereportsfromabb failed", false);
				}

			} else {
				Assert.assertTrue("updatereportsfromabb failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereporttoabb").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereportstoabb passed", true);
			} else {
				Assert.assertTrue("updatereportstoabb failed", false);
			}

		} else {
			Assert.assertTrue("updatereportstoabb failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deletereportabb passed", true);
			} else {
				Assert.assertTrue("deletereportabb failed", false);
			}

		} else {
			Assert.assertTrue("deletereportabb failed", false);
		}


		deleteevent.delete();


	}
	
	@Test()
	public void reportbcs() throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
		parentTest = extent.createTest("ia event create reports");
		// parentTest.assignCategory("End to End");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienamesbcs(idstring, CNO);

	}

	private void insert_csclienamesbcs(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);

				// Thread.sleep(120000L);

				deleteevent.delete();

				insertReportsbcs(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insertReportsbcs(String clie_id, String CNO) throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertreportbcs").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertreportsbcs passed", true);
			} else {
				Assert.assertTrue("insertreportsbcs failed", false);
			}

		} else {
			Assert.assertTrue("insertreportsbcs failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectidreport").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatereportfrom").replaceAll("\\{id\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updatereportsfrombcs passed", true);
				} else {
					Assert.assertTrue("updatereportsfrombcs failed", false);
				}

			} else {
				Assert.assertTrue("updatereportsfrombcs failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updatereporttobcs").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updatereportstobcs passed", true);
			} else {
				Assert.assertTrue("updatereportstobcs failed", false);
			}

		} else {
			Assert.assertTrue("updatereportstobcs failed", false);
		}


		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deletereportpsr").replaceAll("\\{id\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deletereportbcs passed", true);
			} else {
				Assert.assertTrue("deletereportbcs failed", false);
			}

		} else {
			Assert.assertTrue("deletereportbcs failed", false);
		}


		deleteevent.delete();


	}
	
	
}
