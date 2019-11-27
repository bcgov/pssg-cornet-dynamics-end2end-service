package CTD_ContractTests;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class iachgcount {

	private String username;
	private String password;
	private Connection conn;
	private FileInputStream fis;
	private Properties prop = new Properties();
	private String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	private delete_events deleteevent = new delete_events();

	At_Event_verify_event unprocessed = new At_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();

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
	public void chargecountsro() throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertchgsro"));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("insertchgsro passed", true);
			} else {
				Assert.assertTrue("insertchgsro passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		String chgidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectid"));
		if (up.next()) {

			chgidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatechg").replaceAll("\\{chg_count_id\\}", chgidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
				if (rsp.next()) {
					Assert.assertTrue("updatechg passed", true);
				} else {
					Assert.assertTrue("updatechg passed", false);
				}

			}
			else {
				Assert.assertTrue("insertreports failed", false);
			}

		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatechgsro").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("updatechgsro passed", true);
			} else {
				Assert.assertTrue("updatechgsro passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatesealedy").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("update sealed passed", true);
			} else {
				Assert.assertTrue("update sealed passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

	}

	@Test()
	public void chargecountvio() throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertchgvio"));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("insertchgvio passed", true);
			} else {
				Assert.assertTrue("insertchgvio passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		String chgidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectid"));
		if (up.next()) {

			chgidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatechg").replaceAll("\\{chg_count_id\\}", chgidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
				if (rsp.next()) {
					Assert.assertTrue("updatechg passed", true);
				} else {
					Assert.assertTrue("updatechg passed", false);
				}

			}
			else {
				Assert.assertTrue("insertreports failed", false);
			}

		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatechgvio").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("updatechgvio passed", true);
			} else {
				Assert.assertTrue("updatechgvio passed", false);
			}

		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatesealedy").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("update sealed", true);
			} else {
				Assert.assertTrue("update sealed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

	}

	@Test()
	public void chargecountdrg() throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertchgdrg"));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("insertchgdrg passed", true);
			} else {
				Assert.assertTrue("insertchgdrg passed", false);
			}

		}

		deleteevent.delete();

		String chgidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectid"));
		if (up.next()) {

			chgidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatechg").replaceAll("\\{chg_count_id\\}", chgidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
				if (rsp.next()) {
					Assert.assertTrue("updatechg passed", true);
				} else {
					Assert.assertTrue("updatechg passed", false);
				}

			}
			else {
				Assert.assertTrue("insertreports failed", false);
			}

		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatechgdrg").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("updatechgdrg passed", true);
			} else {
				Assert.assertTrue("updatechgdrg passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatesealedy").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("update sealed", true);
			} else {
				Assert.assertTrue("update sealed", false);
			}

		}

		deleteevent.delete();

	}

	@Test()
	public void chargecountesc() throws SQLException, ClassNotFoundException {
		Statement s = conn.createStatement();
		String eidString;
		ResultSet rsp = null;

		s.executeQuery(prop.getProperty("insertchgesc"));
		conn.commit();

		ResultSet rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("insertchgesc passed", true);
			} else {
				Assert.assertTrue("insertchgesc passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		String chgidstring = null;

		ResultSet up = s.executeQuery(prop.getProperty("iaselectid"));
		if (up.next()) {

			chgidstring = String.valueOf(up.getBigDecimal("id"));
			s.executeQuery(prop.getProperty("updatechg").replaceAll("\\{chg_count_id\\}", chgidstring));
			conn.commit();

			rs = s.executeQuery(prop.getProperty("iaselecteid"));
			if (rs.next()) {
				eidString = String.valueOf(rs.getBigDecimal("eid"));

				rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
				if (rsp.next()) {
					Assert.assertTrue("updatechg passed", true);
				} else {
					Assert.assertTrue("updatechg passed", false);
				}

			}
			else {
				Assert.assertTrue("insertreports failed", false);

		}
		
		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatechgesc").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("updatechgesc passed", true);
			} else {
				Assert.assertTrue("updatechgesc passed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatesealedy").replaceAll("\\{chg_count_id\\}", chgidstring));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("iaselecteid"));
		if (rs.next()) {
			eidString = String.valueOf(rs.getBigDecimal("eid"));

			rsp = s.executeQuery(prop.getProperty("iaselectelt").replaceAll("\\{id\\}", eidString));
			if (rsp.next()) {
				Assert.assertTrue("update sealed", true);
			} else {
				Assert.assertTrue("update sealed", false);
			}

		}
		else {
			Assert.assertTrue("insertreports failed", false);
		}

		deleteevent.delete();

	}
	
	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}
}
