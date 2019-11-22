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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_Extent_Report.extentreport;

public class createIAEvent extends extentreport {
	
	private String username;
	private String password;
	private Connection conn;
	private FileInputStream fis;
	private Properties prop = new Properties();
	private String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	private delete_events deleteevent = new delete_events();
	
	inscscliefn insc = new inscscliefn();
	
	Markup m;
	
	@BeforeClass
	private void jdbc() throws SQLException, ClassNotFoundException {
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
	
	@Test()
	public void insertnotifications() throws SQLException, ClassNotFoundException, InterruptedException, ParseException, IOException
	{
		Set<String> clie_idstring=tombstone();
		
		Iterator<String> it=clie_idstring.iterator();
		
		String eid;
		
		Statement s=conn.createStatement();
		
		s.executeQuery(prop.getProperty("insnwc").replaceAll("\\{id\\}", it.next()));
		conn.commit();
		
		ResultSet rs=s.executeQuery(prop.getProperty("getevent"));
		
		if(rs.next())
		{
			eid=String.valueOf(rs.getBigDecimal("eid"));
			
			ResultSet elementrs=s.executeQuery(prop.getProperty("getelement").replaceAll("\\{CNO\\}", eid));
			
			if(elementrs.next())
			{
				Assert.assertEquals(it.next(), elementrs.getString("dt"));
			}
			else
			{
				Assert.assertTrue(false);
			}
		}
		
		
		
	}
	
	public Set<String> tombstone() throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException
	{
		parentTest = extent.createTest("Tombstone Event");
		parentTest.assignCategory("End to End");
		
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
		
		insert_csclienames(idstring, CNO );
		
		Set<String> ids=new LinkedHashSet<String>();
		
		ids.add(idstring);
		ids.add(CNO);
		
		return ids;
		
		
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
			
			Set<String> ids=new HashSet<String>();

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);
				
				Thread.sleep(120000L);

				deleteevent.delete();

			}
			
		} finally {
			clinsert.close();
			t.close();

		}

	}

}
