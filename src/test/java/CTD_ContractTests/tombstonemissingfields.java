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

import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_Dynamics.dynamics;
import CTD_E2E.Dynamics_val;
import CTD_Extent_Report.extentreport;

public class tombstonemissingfields extends extentreport {

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

	@Test
	public void testHandler()
			throws ClassNotFoundException, SQLException, InterruptedException, ParseException, IOException {
		int i = 0;

		while (i < 3) {
			tombstone(i);
			i++;
		}
	}

	public void tombstone(int i)
			throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException {
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

		// Thread.sleep(120000L);

		deleteevent.delete();

		insert_csclienames(idstring, CNO, i);

	}

	private void insert_csclienames(String idstring, String CNO, int i)
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

				insert_locations(i, idstring, CNO);

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}

	public void insert_locations(int i, String id, String CNO)
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		ResultSet rs = null;
		String TIMEST = null;
		BigDecimal locid;
		String locidstring = null;

		switch (i) {
		case 0:
			childTest = parentTest.createNode("insert IREC");
			System.out.println(prop.getProperty("insert_locations_rec").replaceAll("\\{id\\}", id));
			s.executeQuery(prop.getProperty("insert_locations_rec").replaceAll("\\{id\\}", id));
			conn.commit();
			rs = s.executeQuery(prop.getProperty("get_inserted_location_rec"));

			if (rs.next()) {
				locid = rs.getBigDecimal("id");
				locidstring = String.valueOf(locid);
				TIMEST = rs.getString("TIMEST");
			}

			break;

		case 1:
			childTest = parentTest.createNode("insert IPEN");
			s.executeQuery(prop.getProperty("insert_locations_pen").replaceAll("\\{id\\}", id));
			conn.commit();
			rs = s.executeQuery(prop.getProperty("get_inserted_location_pen"));

			if (rs.next()) {
				locid = rs.getBigDecimal("id");
				locidstring = String.valueOf(locid);
				TIMEST = rs.getString("TIMEST");
			}
			break;

		case 2:
			childTest = parentTest.createNode("insert IREL");
			s.executeQuery(prop.getProperty("insert_locations_rel").replaceAll("\\{id\\}", id));
			conn.commit();
			rs = s.executeQuery(prop.getProperty("get_inserted_location_rel"));

			if (rs.next()) {
				locid = rs.getBigDecimal("id");
				locidstring = String.valueOf(locid);
				TIMEST = rs.getString("TIMEST");
			}
			break;

		}

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
		childTest.log(Status.PASS,
				MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

		// childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event
		// Notification", colour.BLUE));
		// n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		new or_get();
		String datetime = or_get.tstonemissing_or(CNO, gd, null, null, null, null, "458.0005", "Active - In", null);

		// childTest.log(Status.INFO,
		// MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		// da.da_tstonea(null);

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

		updatefrom(CNO, locidstring);
		updateto(CNO, locidstring, i);

		if (i == 2) {
			insertterms(id, CNO);
		}

	}

	private void updatefrom(String CNO, String locid)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		Statement s = conn.createStatement();

		childTest = parentTest.createNode("Update to a not required value");

		System.out.println(prop.getProperty("upfrom").replaceAll("\\{locid\\}", locid));

		s.executeQuery(prop.getProperty("upfrom").replaceAll("\\{locid\\}", locid));
		conn.commit();
		String TIMEST = null;

		ResultSet rsp = s.executeQuery(prop.getProperty("upfromselect").replaceAll("\\{locid\\}", locid));

		if (rsp.next()) {
			TIMEST = rsp.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, null, null, null, null, null, "Active - In", "458.0005");

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
	}

	private void updateto(String CNO, String locid, int i)
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		ResultSet rsut = null;
		String TIMEST = null;

		switch (i) {
		case 0:
			childTest = parentTest.createNode("Update to IREC");
			s.executeQuery(prop.getProperty("uptorec").replaceAll("\\{locid\\}", locid));
			conn.commit();

			rsut = s.executeQuery(prop.getProperty("upfromselect").replaceAll("\\{locid\\}", locid));

			if (rsut.next()) {
				TIMEST = rsut.getString("TIMEST");
			}

			break;

		case 1:
			childTest = parentTest.createNode("Update to IPEN");
			s.executeQuery(prop.getProperty("uptopen").replaceAll("\\{locid\\}", locid));
			conn.commit();

			rsut = s.executeQuery(prop.getProperty("upfromselect").replaceAll("\\{locid\\}", locid));

			if (rsut.next()) {
				TIMEST = rsut.getString("TIMEST");
			}

			break;

		case 2:
			childTest = parentTest.createNode("Update to IREL");
			s.executeQuery(prop.getProperty("uptorel").replaceAll("\\{locid\\}", locid));
			conn.commit();

			rsut = s.executeQuery(prop.getProperty("upfromselect").replaceAll("\\{locid\\}", locid));

			if (rsut.next()) {
				TIMEST = rsut.getString("TIMEST");
			}

			break;

		}

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
		childTest.log(Status.PASS,
				MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

		/*
		 * childTest.log(Status.INFO,
		 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		 * n.event_notification();
		 */

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		new or_get();
		String datetime = or_get.tstonemissing_or(CNO, gd, null, null, null, null, "458.0005", "Active - In", null);

		/*
		 * childTest.log(Status.INFO,
		 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		 * da.da_tstonea(null);
		 */

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

	}

	private void insertterms(String id, String CNO)
			throws IOException, SQLException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String TIMEST = null;
		BigDecimal termid = null;

		childTest = parentTest.createNode("insert terms");

		s.executeQuery(prop.getProperty("insertterms").replaceAll("\\{clie_id\\}", id));
		conn.commit();

		ResultSet trs = s.executeQuery(prop.getProperty("selectterms").replaceAll("\\{clie_id\\}", id));

		if (trs.next()) {
			TIMEST = trs.getString("TIMEST");
			termid = trs.getBigDecimal("id");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "CLIENT_IA", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, null, null, String.valueOf(termid), null, "458.0005",
					"Active - In", null);

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			updateend_dt(termid, CNO);
			key_date_insert(CNO, id, termid);

		}

	}

	private void updateend_dt(BigDecimal termid, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String TIMEST = null;

		childTest = parentTest.createNode("update terms end date");

		s.executeQuery(prop.getProperty("updateend_dt").replaceAll("\\{termid\\}", String.valueOf(termid)));
		conn.commit();

		ResultSet tsrp = s
				.executeQuery(prop.getProperty("selectend_dt").replaceAll("\\{termid\\}", String.valueOf(termid)));

		if (tsrp.next()) {
			TIMEST = tsrp.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, "2018-05-28", null, String.valueOf(termid), null, null,
					"Active - In", null);

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}

	}

	private void key_date_insert(String CNO, String clie_id, BigDecimal termid)
			throws IOException, SQLException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String TIMEST = null;
		BigDecimal keyDateId = null;
		String keyDateIdString = null;

		childTest = parentTest.createNode("insert key date");

		s.executeQuery(prop.getProperty("insertkeydate").replaceAll("\\{id\\}", String.valueOf(clie_id)));
		conn.commit();

		ResultSet tsrp = s
				.executeQuery(prop.getProperty("selectkeydate").replaceAll("\\{clie_id\\}", String.valueOf(clie_id)));

		if (tsrp.next()) {

			keyDateId = tsrp.getBigDecimal("id");
			keyDateIdString = String.valueOf(keyDateId);
			TIMEST = tsrp.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, "2018-05-28", "2004-APR-04", String.valueOf(termid),
					null, null, "Active - In", null);

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			updateDT(keyDateIdString, termid, CNO);

		}

	}

	private void updateDT(String keyDateIdString, BigDecimal termid, String CNO)
			throws ClassNotFoundException, SQLException, ParseException, IOException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();
		String TIMEST = null;

		childTest = parentTest.createNode("update dt - key date");

		s.executeQuery(prop.getProperty("updateDt").replaceAll("\\{key_id\\}", String.valueOf(keyDateIdString)));
		conn.commit();

		ResultSet tsrp = s.executeQuery(
				prop.getProperty("selectUpdatedKeyDate").replaceAll("\\{key_id\\}", String.valueOf(keyDateIdString)));

		if (tsrp.next()) {

			TIMEST = tsrp.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, "2018-05-28", "2018-APR-04", String.valueOf(termid),
					null, null, "Active - In", null);

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			update_kdty(keyDateIdString, termid, CNO);

		}

	}

	private void update_kdty(String keyDateIdString, BigDecimal termid, String CNO) throws IOException, SQLException, ParseException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement s = conn.createStatement();
		String TIMEST = null;

		childTest = parentTest.createNode("update kdty");

		s.executeQuery(prop.getProperty("updateKDTY").replaceAll("\\{key_id\\}", String.valueOf(keyDateIdString)));
		conn.commit();

		ResultSet tsrp = s.executeQuery(
				prop.getProperty("selectUpdatedKeyDate").replaceAll("\\{key_id\\}", String.valueOf(keyDateIdString)));

		if (tsrp.next()) {

			TIMEST = tsrp.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, "NULL");
			childTest.log(Status.PASS,
					MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			 * n.event_notification();
			 */

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			new or_get();
			String datetime = or_get.tstonemissing_or(CNO, gd, "2018-05-28", null, String.valueOf(termid), null, null,
					"Active - In", null);

			/*
			 * childTest.log(Status.INFO,
			 * MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			 * da.da_tstonea(null);
			 */

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, "TOMBSTONE", CNO, null, datetime);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
	}
}
