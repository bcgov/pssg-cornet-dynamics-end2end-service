<<<<<<< HEAD
<<<<<<< master
=======
>>>>>>> Dev
package CTD_Extent_Report;

import java.sql.Date;
import java.util.Calendar;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class extentreport {

	public static ExtentTest test;
	public static ExtentTest parentTest;
	public static ExtentTest childTest;
	public static ExtentHtmlReporter htmlreporter;
	public static ExtentReports extent;
	public static ExtentColor colour;
	public static String Filename=System.getProperty("user.dir")+"/test-output/HtmlTestResults.html";
	@BeforeSuite
	public void setup()
	{
		htmlreporter=new ExtentHtmlReporter(Filename);
		extent=new ExtentReports();
		extent.attachReporter(htmlreporter);
		htmlreporter.config().setReportName("Regression Testing");
		htmlreporter.config().setTestViewChartLocation(ChartLocation.TOP); 
        htmlreporter.config().setTheme(Theme.DARK);
        htmlreporter.config().setChartVisibilityOnOpen(true);
        
	}
	@AfterSuite
	public void extentteardown()
	{
		extent.flush();
	}
}
<<<<<<< HEAD
=======
package CTD_Extent_Report;

import java.sql.Date;
import java.util.Calendar;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class extentreport {

	public static ExtentTest test;
	public static ExtentTest parentTest;
	public static ExtentTest childTest;
	public static ExtentHtmlReporter htmlreporter;
	public static ExtentReports extent;
	public static ExtentColor colour;
	public static String Filename=System.getProperty("user.dir")+"/test-output/HtmlTestResults.html";
	@BeforeSuite
	public void setup()
	{
		htmlreporter=new ExtentHtmlReporter(Filename);
		extent=new ExtentReports();
		extent.attachReporter(htmlreporter);
		htmlreporter.config().setReportName("Regression Testing");
		htmlreporter.config().setTestViewChartLocation(ChartLocation.TOP); 
        htmlreporter.config().setTheme(Theme.DARK);
        htmlreporter.config().setChartVisibilityOnOpen(true);
        
	}
	@AfterSuite
	public void extentteardown()
	{
		extent.flush();
	}
}
>>>>>>> Sync with Master
=======
>>>>>>> Dev
