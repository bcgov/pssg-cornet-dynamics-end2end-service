package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.testng.SkipException;
import org.testng.annotations.Test;

public class inscscliefn {
	
	public String username;
	public String password;
	public Connection insertconn;
	
	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	@Test(invocationCount=200)
	public void insert_into_csclie() throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub
		
		fis = new FileInputStream(Filepath);
		prop.load(fis);
		try {

			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			insertconn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);

			Statement s = insertconn.createStatement();

			s.executeQuery(prop.getProperty("inscsclie"));
			insertconn.commit();

		} catch (SQLException e) {
			throw new SkipException(prop.getProperty("insert_into_csclie_skipexception"));
		} finally {
			insertconn.close();
			//deleteevent.delete();
		}

	}

}
