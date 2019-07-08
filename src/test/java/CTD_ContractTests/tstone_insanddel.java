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

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_ContractTests.Adapter;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class tstone_insanddel extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc = new inscscliefn();

	Markup m;

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
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
	public void insertcl()
			throws SQLException, ClassNotFoundException, ParseException, IOException, InterruptedException {
		parentTest = extent.createTest(prop.getProperty("tinsertcl_parentextent"));
		parentTest.assignCategory(prop.getProperty("tinsertcl_category"));

		System.out.println("..");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;
		String TYPE = "TOMBSTONE";
		String TIMEST = null;

		childTest = parentTest.createNode("Insert");

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
			TIMEST = rs.getString("TIMEST");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		insert(idstring, CNO, TYPE, TIMEST);

		deleteevent.delete();

		parentTest = extent.createTest(prop.getProperty("tinsertcl_parenttwoextent"));
		parentTest.assignCategory(prop.getProperty("tinsertcl_category"));
		childTest = parentTest.createNode("Insert");
		insert_csclienames(idstring, TYPE, CNO);

		childTest = parentTest.createNode(prop.getProperty("up_bt_dt"));
		upbtdt(idstring, TYPE, CNO);

		childTest = parentTest.createNode(prop.getProperty("up_surnm"));
		upsurnm(idstring, TYPE, CNO);

		childTest = parentTest.createNode(prop.getProperty("up_gn"));
		upgn(idstring, TYPE, CNO);

		childTest = parentTest.createNode(prop.getProperty("up_cn"));
		upcn(idstring, TYPE, CNO);

		parentTest = extent.createTest(prop.getProperty("tlinsertcl_parentfourextent"));
		parentTest.assignCategory(prop.getProperty("tinsertcl_category"));
		upse(idstring, TYPE, CNO);

		parentTest = extent.createTest(prop.getProperty("tlinsertcl_parentfiveextent"));
		parentTest.assignCategory(prop.getProperty("tinsertcl_category"));
		upge(idstring, TYPE, CNO);

		parentTest = extent.createTest(prop.getProperty("tlinsertcl_parentthreeextent"));
		parentTest.assignCategory(prop.getProperty("tinsertcl_category"));
		insert_csperchars(idstring, TYPE, CNO);

	}

	private void upge(String idstring, String TYPE, String CNO) throws SQLException, ParseException, IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		Statement s = conn.createStatement();
		String gn=null;

		String TIMEST = null;
		BigDecimal id = null;
		int i = 1;

		while (i <= 3) {
			switch (i) {
			case 1:
				childTest=parentTest.createNode(prop.getProperty("upge_onechild"));
				s.executeQuery(prop.getProperty("upge_oneupdate") + "" + idstring + "");
				conn.commit();
				gn="1";
				break;
			case 2:
				childTest=parentTest.createNode(prop.getProperty("upge_twochild"));
				s.executeQuery(prop.getProperty("upge_twoupdate") + "" + idstring + "");
				conn.commit();
				gn="2";
				break;
			case 3:
				childTest=parentTest.createNode(prop.getProperty("upge_threechild"));
				s.executeQuery(prop.getProperty("upge_threeupdate") + "" + idstring + "");
				conn.commit();
				gn=null;
				break;
				
			}

			ResultSet seup = s.executeQuery(prop.getProperty("upse_oneselect") + "" + idstring + "");

			if (seup.next()) {
				TIMEST = seup.getString("TIMEST");

				String[][] responsetable = { { "id", "upd_dt" }, { idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				childTest.log(Status.INFO, m);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				or_get or = new or_get();
				String datetime = or_get.tstone_or(CNO, gd, 1);

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_tstonea(gn);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
				childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

				Thread.sleep(30000);

				deleteevent.delete();
				
				i++;

			}

		}

		
	}

	private void upse(String idstring, String TYPE, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		String TIMEST = null;
		BigDecimal id = null;
		int i = 1;

		while (i <= 2) {
			switch (i) {
			case 1:
				childTest=parentTest.createNode("N to Y");
				s.executeQuery(prop.getProperty("upse_oneupdate") + "" + idstring + "");
				conn.commit();
				break;
			case 2:
				childTest=parentTest.createNode("Y to N");
				s.executeQuery(prop.getProperty("upse_twoupdate") + "" + idstring + "");
				conn.commit();
				break;
			}

			ResultSet seup = s.executeQuery(prop.getProperty("upse_oneselect") + "" + idstring + "");

			if (seup.next()) {
				TIMEST = seup.getString("TIMEST");

				String[][] responsetable = { { "id", "upd_dt" }, { idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				childTest.log(Status.INFO, m);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				or_get or = new or_get();
				String datetime = or_get.tstone_or(CNO, gd, 1);

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_tstonea(null);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
				childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

				Thread.sleep(30000);

				deleteevent.delete();
				
				i++;

			}

		}

	}

	private void upbtdt(String idstring, String TYPE, String CNO)
			throws SQLException, ClassNotFoundException, ParseException, IOException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		String TIMEST = null;
		BigDecimal id = null;
		String clieidstring = null;

		s.executeQuery(prop.getProperty("upbtdt_oneupdate") + "" + idstring + "");
		conn.commit();

		ResultSet btdtup = s.executeQuery(prop.getProperty("upbtdt_oneselect") + "" + idstring + "");

		if (btdtup.next()) {
			id = btdtup.getBigDecimal("id");
			clieidstring = String.valueOf(id);
			TIMEST = btdtup.getString("TIMEST");

			String[][] responsetable = { { "id", "clie_id", "upd_dt" }, { clieidstring, idstring, TIMEST } };
			m = MarkupHelper.createTable(responsetable);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();
			String datetime = or_get.tstone_or(CNO, gd, 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_tstonea(null);

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

	}

	private void upsurnm(String idstring, String TYPE, String CNO)
			throws SQLException, ClassNotFoundException, ParseException, IOException, InterruptedException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		String TIMEST = null;
		BigDecimal id = null;
		String clieidstring = null;

		s.executeQuery(prop.getProperty("upsurnm_oneupdate") + "" + idstring + "");
		conn.commit();

		ResultSet surnmup = s.executeQuery(prop.getProperty("upbtdt_oneselect") + "" + idstring + "");

		if (surnmup.next()) {
			id = surnmup.getBigDecimal("id");
			clieidstring = String.valueOf(id);
			TIMEST = surnmup.getString("TIMEST");

			String[][] responsetable = { { "id", "clie_id", "upd_dt" }, { clieidstring, idstring, TIMEST } };
			m = MarkupHelper.createTable(responsetable);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();
			String datetime = or_get.tstone_or(CNO, gd, 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_tstonea(null);

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}

	}

	private void upgn(String idstring, String TYPE, String CNO)
			throws SQLException, ClassNotFoundException, ParseException, IOException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		String TIMEST = null;
		BigDecimal id = null;
		String clieidstring = null;

		s.executeQuery(prop.getProperty("upgn_oneupdate") + "" + idstring + "");
		conn.commit();

		ResultSet gnup = s.executeQuery(prop.getProperty("upbtdt_oneselect") + "" + idstring + "");

		if (gnup.next()) {
			id = gnup.getBigDecimal("id");
			clieidstring = String.valueOf(id);
			TIMEST = gnup.getString("TIMEST");

			String[][] responsetable = { { "id", "clie_id", "upd_dt" }, { clieidstring, idstring, TIMEST } };
			m = MarkupHelper.createTable(responsetable);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();
			String datetime = or_get.tstone_or(CNO, gd, 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_tstonea(null);

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

	}

	private void upcn(String idstring, String TYPE, String CNO)
			throws SQLException, ClassNotFoundException, ParseException, IOException, InterruptedException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		String TIMEST = null;
		BigDecimal id = null;
		String clieidstring = null;

		s.executeQuery(prop.getProperty("upcn_oneupdate") + "" + idstring + "");
		conn.commit();

		Thread.sleep(30000);

		s.executeQuery(prop.getProperty("upcny_oneupdate") + "" + idstring + "");
		conn.commit();

		ResultSet cnup = s.executeQuery(prop.getProperty("upbtdt_oneselect") + "" + idstring + "");

		if (cnup.next()) {
			id = cnup.getBigDecimal("id");
			clieidstring = String.valueOf(id);
			TIMEST = cnup.getString("TIMEST");

			String[][] responsetable = { { "id", "clie_id", "upd_dt" }, { clieidstring, idstring, TIMEST } };
			m = MarkupHelper.createTable(responsetable);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();
			String datetime = or_get.tstone_or(CNO, gd, 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_tstonea(null);

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}

	}

	private void insert(String idstring, String CNO, String TYPE, String TIMEST)
			throws ParseException, IOException, SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub

		String[][] CNOid = { { prop.getProperty("CNO"), "ID", "TIMEST" }, { CNO, idstring, TIMEST } };
		m = MarkupHelper.createTable(CNOid);
		childTest.log(Status.INFO, m);

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
		childTest.log(Status.PASS,
				MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		or_get or = new or_get();
		String datetime = or_get.tstone_or(CNO, gd, 0);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_tstonea(null);

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

	}

	private void insert_csclienames(String idstring, String TYPE, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
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
				childTest.log(Status.INFO, m);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				or_get or = new or_get();
				String datetime = or_get.tstone_or(CNO, gd, 1);

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_tstonea(null);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
				childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

				deleteevent.delete();

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	private void insert_csperchars(String idstring, String TYPE, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();

		int i;

		for (i = 1; i <= 5; i++) {
			switch (i) {
			case 1:
				childTest = parentTest.createNode(prop.getProperty("insrc_extent"));
				t.executeQuery(prop.getProperty("insert_csperchars_rcins") + "" + "(" + idstring + ", 'CAU', 'RAC')");
				conn.commit();
				inscsperchars(idstring, TYPE, CNO, 1);
				break;

			case 2:
				childTest = parentTest.createNode(prop.getProperty("insab_extent"));
				t.executeQuery(prop.getProperty("insert_csperchars_rcins") + "" + "(" + idstring + ", 'Y', 'ABO')");
				conn.commit();
				inscsperchars(idstring, TYPE, CNO, 2);
				break;

			case 3:
				childTest = parentTest.createNode(prop.getProperty("insfa_extent"));
				t.executeQuery(prop.getProperty("insert_csperchars_rcins") + "" + "(" + idstring + ", 'Y', 'FSNA')");
				conn.commit();
				inscsperchars(idstring, TYPE, CNO, 3);
				break;

			case 4:
				childTest = parentTest.createNode(prop.getProperty("insmt_extent"));
				t.executeQuery(prop.getProperty("insert_csperchars_rcins") + "" + "(" + idstring + ", 'Y', 'MET')");
				conn.commit();
				inscsperchars(idstring, TYPE, CNO, 4);
				break;

			case 5:
				childTest = parentTest.createNode(prop.getProperty("insiu_extent"));
				t.executeQuery(prop.getProperty("insert_csperchars_rcins") + "" + "(" + idstring + ", 'Y', 'INU')");
				conn.commit();
				inscsperchars(idstring, TYPE, CNO, 5);
				break;
			}

		}

	}

	private void inscsperchars(String idstring, String TYPE, String CNO, int i)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement b = conn.createStatement();

		BigDecimal id = null;
		String TIMEST = null;
		String personalcharsidstring = null;

		ResultSet incsperchars = b.executeQuery(prop.getProperty("inscsperchars_oneselect") + "" + idstring + "");

		if (incsperchars.next()) {
			id = incsperchars.getBigDecimal("id");
			personalcharsidstring = String.valueOf(id);
			TIMEST = incsperchars.getString("TIMEST");

			String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { personalcharsidstring, idstring, TIMEST } };
			m = MarkupHelper.createTable(responsetable);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();
			String datetime = or_get.tstone_or(CNO, gd, 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_tstonea(null);

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			upcspercharsfrom(personalcharsidstring, TYPE, CNO, i);
			String TIMESTdel = upcspercharsto(personalcharsidstring, TYPE, CNO, i);

			childTest = parentTest.createNode("Deleting");
			deletecsperchars(personalcharsidstring, TYPE, TIMESTdel, CNO);

		}

	}

	private void upcspercharsfrom(String personalcharsidstring, String TYPE, String CNO, int i)
			throws ClassNotFoundException, SQLException, ParseException, IOException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();
		System.out.println(i);

		String clid = null;
		Markup m;
		ResultSet rschanged = null;
		String TIMEST;
		String pct_cdchanged = null;
		String pcd_cdchanged = null;

		try {

			switch (i) {
			case 1:
				childTest = parentTest.createNode(prop.getProperty("upfromrc"));
				break;
			case 2:
				childTest = parentTest.createNode(prop.getProperty("upfromab"));
				break;
			case 3:
				childTest = parentTest.createNode(prop.getProperty("upfromfna"));
				break;
			case 4:
				childTest = parentTest.createNode(prop.getProperty("upfrominu"));
				break;
			case 5:
				childTest = parentTest.createNode(prop.getProperty("uptomet"));
				break;
			}

			s.executeQuery(prop.getProperty("upfrom_oneupdate") + "'" + personalcharsidstring + "'");
			conn.commit();

			rschanged = s.executeQuery(prop.getProperty("upfrom_oneselect") + "" + personalcharsidstring + "");

			if (rschanged.next()) {
				String gdcd = null;
				String gnd = null;
				TIMEST = rschanged.getString("TIMEST");
				clid = rschanged.getString("clid");
				pct_cdchanged = rschanged.getString("pct_cd");
				pcd_cdchanged = rschanged.getString("pcd_cd");
				gdcd = rschanged.getString("gdcd");

				if (gdcd != null) {
					if (gdcd.equals("M")) {
						gnd = "1";
					} else {
						gnd = "2";
					}
				}

				childTest.log(Status.INFO, "Successfully updated as");
				String[][] pct_cdchangesto = {
						{ prop.getProperty("CNO"), prop.getProperty("clntid"), "ID", prop.getProperty("pct_cd"),
								prop.getProperty("pcd_cd"), "TIMEST" },
						{ CNO, clid, personalcharsidstring, pct_cdchanged, pcd_cdchanged } };
				m = MarkupHelper.createTable(pct_cdchangesto);
				childTest.log(Status.INFO, m);

				tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				or_get or = new or_get();
				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				String datetime = or.tstone_or(CNO, gd, 1);

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_tstonea(gnd);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));

				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
				childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

				deleteevent.delete();
			}
		} finally {
			rschanged.close();
			s.close();

		}
	}

	private String upcspercharsto(String personalcharsidstring, String TYPE, String CNO, int i)
			throws SQLException, ClassNotFoundException, ParseException, IOException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();

		System.out.println(i);

		Markup m;
		ResultSet rschanged = null;
		String TIMEST = null;
		String pct_cdchanged = null;
		String pcd_cdchanged = null;
		String clidchanged = null;

		try {

			switch (i) {
			case 1:
				childTest = parentTest.createNode(prop.getProperty("uptorc"));
				s.execute(prop.getProperty("pct_cd_rc_oneupdate") + "'" + personalcharsidstring + "'");
				conn.commit();
				break;
			case 2:
				childTest = parentTest.createNode(prop.getProperty("uptoab"));
				s.execute(prop.getProperty("pct_cd_rc_twoupdate") + "'" + personalcharsidstring + "'");
				conn.commit();
				break;
			case 3:
				childTest = parentTest.createNode(prop.getProperty("uptofna"));
				s.execute(prop.getProperty("pct_cd_rc_threeupdate") + "'" + personalcharsidstring + "'");
				conn.commit();
				break;
			case 4:
				childTest = parentTest.createNode(prop.getProperty("uptoinu"));
				s.execute(prop.getProperty("pct_cd_rc_fourupdate") + "'" + personalcharsidstring + "'");
				conn.commit();
				break;
			case 5:
				childTest = parentTest.createNode(prop.getProperty("uptomet"));
				s.execute(prop.getProperty("pct_cd_rc_oneupdate") + "'" + personalcharsidstring + "'");
				conn.commit();
				break;
			}

			rschanged = s.executeQuery(prop.getProperty("pct_cd_rc_oneselect") + "" + personalcharsidstring + "");
			if (rschanged.next()) {
				String gdcd = null;
				String gnd = null;
				CNO = rschanged.getString("CNO");
				TIMEST = rschanged.getString("TIMEST");
				pct_cdchanged = rschanged.getString("pct_cd");
				pcd_cdchanged = rschanged.getString("pcd_cd");
				clidchanged = rschanged.getString("clid");
				gdcd = rschanged.getString("gdcd");

				if (gdcd != null) {
					if (gdcd.equals("M")) {
						gnd = "1";
					} else {
						gnd = "2";
					}
				}

				childTest.log(Status.INFO, "Updated Successfully as");

				String[][] pcty_cdchangesto = {
						{ prop.getProperty("CNO"), prop.getProperty("clntid"), "ID", prop.getProperty("pct_cd"),
								prop.getProperty("pcd_cd"), "TIMEST" },
						{ CNO, clidchanged, personalcharsidstring, pct_cdchanged, pcd_cdchanged, TIMEST } };
				m = MarkupHelper.createTable(pcty_cdchangesto);
				childTest.log(Status.INFO, m);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
				childTest.log(Status.PASS,
						MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				or_get or = new or_get();
				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				String datetime = or.tstone_or(CNO, gd, 1);

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_tstonea(gnd);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
				childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

				deleteevent.delete();

			}
			return TIMEST;
		} catch (SQLException e) {
			throw e;
		} catch (NullPointerException np) {
			throw np;
		} finally {
			rschanged.close();
			s.close();
		}
	}

	private void deletecsperchars(String personalcharsidstring, String TYPE, String TIMEST, String CNO)
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		// TODO Auto-generated method stub

		Statement c = conn.createStatement();
		c.executeQuery(prop.getProperty("deletecsperchars_onedel") + "" + personalcharsidstring + "");
		conn.commit();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String guid = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, "NULL");
		childTest.log(Status.PASS,
				MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		or_get or = new or_get();
		String datetime = or_get.tstone_or(CNO, guid, 1);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_tstonea(null);

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, TYPE, CNO, null, datetime);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

	}

	@AfterMethod
	public void tearDown(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			parentTest.log(Status.FAIL,
					"Test Fail: Log defect <a href=\"http://abcd.efg.com\" target=\"_blank\">Log Defect</a>");
			parentTest.fail(result.getThrowable());

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			parentTest.log(Status.PASS, MarkupHelper.createLabel("Test passed", colour.GREEN));
		} else if (result.getStatus() == ITestResult.SKIP) {
			parentTest.log(Status.PASS, MarkupHelper.createLabel("Test skipped", colour.YELLOW));
			parentTest.log(Status.SKIP, result.getThrowable());
		}
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
