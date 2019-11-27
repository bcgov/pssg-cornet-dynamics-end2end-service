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

public class iaterms extends extentreport{
	
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
	public void iaterms()
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

				terms(idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	
	public void terms(String clie_id, String CNO) throws SQLException, ClassNotFoundException
	{
		Statement s=conn.createStatement();
		String eidString=null;
		ResultSet rsp=null;
		
		s.executeQuery(prop.getProperty("iainsertterms").replaceAll("\\{clie_id\\}", clie_id));
		conn.commit();
		
		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("insertterms passed", true);
			} else {
				Assert.assertTrue("insertterms failed", false);
			}

		} else {
			Assert.assertTrue("insertterms failed", false);
		}

		deleteevent.delete();
		
		String reportidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("selectiaterms").replaceAll("\\{clie_id\\}", clie_id));
		if (up.next()) {

			reportidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updateend_dt").replaceAll("\\{termid\\}", reportidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
						.replaceAll("\\{CNO\\}", CNO));
				if (rsp.next()) {
					Assert.assertTrue("updateterms passed", true);
				} else {
					Assert.assertTrue("updateterms failed", false);
				}

			} else {
				Assert.assertTrue("insertterms failed", false);
			}

		}

		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("updateend_dtnnull").replaceAll("\\{termid\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("updateterms passed", true);
			} else {
				Assert.assertTrue("updateterms failed", false);
			}

		} else {
			Assert.assertTrue("insertterms failed", false);
		}
		
		deleteevent.delete();
		
		s.executeQuery(prop.getProperty("deleteiaterms").replaceAll("\\{termid\\}", reportidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselecteltreports").replaceAll("\\{id\\}", eidString)
					.replaceAll("\\{CNO\\}", CNO));
			if (rsp.next()) {
				Assert.assertTrue("deleteterms passed", true);
			} else {
				Assert.assertTrue("deleteterms failed", false);
			}

		} else {
			Assert.assertTrue("deleteterms failed", false);
		}
		
		deleteevent.delete();
		
	}
	
	

}
