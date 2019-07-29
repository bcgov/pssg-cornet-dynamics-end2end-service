package CTD_E2E;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.hssf.usermodel.*;

import org.apache.xmlbeans.XmlCursor;
import org.testng.annotations.Test;

import com.sun.jmx.snmp.Timestamp;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class perfchart {

	
	public String gettimest() {

		Date date = new Date();

		long time = date.getTime();
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(time);
		//System.out.println(datetime);
		return datetime;

	}
}
