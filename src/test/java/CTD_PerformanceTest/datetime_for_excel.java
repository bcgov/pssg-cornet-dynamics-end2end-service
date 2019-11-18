package CTD_PerformanceTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class datetime_for_excel {

	
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
