package CTD_ContractTests;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.testng.SkipException;
import org.testng.annotations.Test;

public class delete_events {
	
	public String username;
	public String password;
	public Connection conn;
	public FileInputStream fis;
	public Properties prop=new Properties();
	public String Filepath= System.getProperty("user.dir")+ "/Properties/cornet.properties";
	
	@Test
	public void delete() throws SQLException, ClassNotFoundException {
		try {
			
			fis=new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
		
		
		Statement l = conn.createStatement();
		
		
		try
		{
		
		l.executeQuery(prop.getProperty("clear_message_elements"));
		conn.commit();
		l.executeQuery(prop.getProperty("clear_event_messages"));
		conn.commit();
		}
		catch (SQLException e)
		{
			throw new SkipException("Cannot delete event and message elements");
		}
		finally
		{
			l.close();
			
		}
	}

}
