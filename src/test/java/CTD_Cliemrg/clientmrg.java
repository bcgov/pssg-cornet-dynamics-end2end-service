package CTD_Cliemrg;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import CTD_ContractTests.delete_events;
import CTD_Extent_Report.extentreport;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class clientmrg extends extentreport {

	public WebDriver driver;
	public String username;
	public String password;
	public Connection insconn;
	public Statement f;
	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public CTD_PerformanceTest.performanceTests_CTD insv = new CTD_PerformanceTest.performanceTests_CTD();
	public CTD_Dynamics.dynamics dy = new CTD_Dynamics.dynamics();
	public delete_events delete_events = new delete_events();

	@BeforeClass
	public void jdbc() throws ClassNotFoundException, IOException {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			insconn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
			f = insconn.createStatement();
		} catch (SQLException e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test
	public void webdriver() throws InterruptedException, ClassNotFoundException, SQLException, IOException {

		parentTest = extent.createTest(prop.getProperty("parent_extent"));
		childTest = parentTest.createNode(prop.getProperty("child_extent"));

		String dy_edpt_clie = prop.getProperty("dy_edpt_clie");

		// Login
		By uname_ele = By.id(prop.getProperty("clie_login_useri"));
		By pass_ele = By.id(prop.getProperty("clie_login_pass"));
		By submit_ele = By.id(prop.getProperty("clie_login_submit"));

		// landing page
		By homedropdown_ele = By.name(prop.getProperty("homedropdown_ele"));
		By homesettings_ele = By.xpath("//*[@title='" + prop.getProperty("homesettings_ele") + "']");
		By scrolltoappsbutton_ele = By.xpath("//div[@id='" + prop.getProperty("scrolltoappsbutton_ele") + "']/a");
		By gtapps_ele = By.xpath("//*[@title='" + prop.getProperty("gtapps_ele") + "']");

		// App page
		By frame_ele = By.id("" + prop.getProperty("frame_ele") + "");
		By dy_ac_edpt = By.xpath("//a[@href='" + prop.getProperty("dy_ac_edpt") + "']");

		// CI page
		By menu_ele = By.id(prop.getProperty("menu_ele"));
		By submenu_ele = By
				.xpath("//div[@id='" + prop.getProperty("submenu_ele") + "']/div/div/nav/div/div/div/div[2]/button");
		By ciinnermenu_ele = By.xpath("//div[@id='" + prop.getProperty("submenu_ele") + "']/div[2]/div/ul/li[3]");
		By cimenu_ele = By.xpath("//span[text()='" + prop.getProperty("cimenu_ele") + "']");
		By createnewCI = By.xpath("//li[contains(@title, '" + prop.getProperty("createnewCI") + "')]/button");

		// New CI page
		By stgindhol = By.xpath("//div[@id='" + prop.getProperty("stgindhol") + "']");
		By stgindstg = By.xpath("//div[@id='" + prop.getProperty("stgindhol") + "']//input");

		insv.jdbc();
		LinkedHashSet<LinkedHashSet<String>> tstoneinsprep = insv.tstoneinsprep(2);
		Iterator<LinkedHashSet<String>> itcliains = tstoneinsprep.iterator();
		LinkedHashSet<String> cliainscno = itcliains.next();
		LinkedHashSet<String> cliainsid = itcliains.next();

		System.out.println(cliainscno);
		System.out.println(cliainsid);

		LinkedList<String> cliainsfname = insv.genString(2);
		LinkedList<String> cliainslname = insv.genString(2);
		Iterator<String> cnoit = cliainscno.iterator();
		Iterator<String> lnameit = cliainslname.iterator();
		Iterator<String> fnameit = cliainsfname.iterator();
		Iterator<String> idstringit = cliainsid.iterator();
		insv.exectstoneins(2, cliainsid, cliainsfname, cliainslname);

		String idstring = null;
		String cno = null;

		String srname = null;
		String fname = null;
		String access_token = null;

		int i = 1;

		while (i <= 2) {
			idstring = idstringit.next();
			cno = cnoit.next();
			srname = lnameit.next();
			fname = fnameit.next();
			access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();
			Thread.sleep(60000L);
			System.out.println(cno + " " + srname + " " + access_token);
			insv.createdtime(access_token, cno, 0, "lastname", srname, "tstoneins", 1);
			i++;
		}

		cnoit = cliainscno.iterator();
		cno = cnoit.next();
		// System.out.println();
		idstringit = cliainsid.iterator();
		idstring = idstringit.next();
		ResultSet clntmrg = f.executeQuery(prop.getProperty("clntmrg_clna") + "" + idstring + "");
		clntmrg.next();
		String nm = clntmrg.getString("gn");
		System.out.println(nm);

		String insert_into_csdischarges_oneinsert = prop.getProperty("insert_into_csdischarges_oneinsert");
		String query = insert_into_csdischarges_oneinsert.replaceAll("\\{clid\\}", idstring);
		f.executeQuery(query);
		insconn.commit();

		String one_insert = prop.getProperty("trans_insmovecs_move_oneinsert");
		String two_insert = prop.getProperty("trans_insmovecs_move_twoinsert");
		query = one_insert.replaceAll("\\{id\\}", idstring);

		f.executeQuery(query);
		insconn.commit();

		query = two_insert.replaceAll("\\{id\\}", idstring);

		f.executeQuery(query);
		insconn.commit();

		WebDriverManager.chromedriver().setup();

		// driver
		driver = new ChromeDriver();
		driver.manage().window().maximize();

		// timeout
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		// login
		driver.get(dy_edpt_clie);
		driver.findElement(uname_ele).sendKeys(prop.getProperty("username"));
		driver.findElement(pass_ele).sendKeys(prop.getProperty("password"));
		driver.findElement(submit_ele).click();

		// Navigate to AC app
		WebDriverWait driverwait = new WebDriverWait(driver, 20);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(homedropdown_ele));
		driver.findElement(homedropdown_ele).click();
		driver.findElement(homesettings_ele).click();
		// driver.findElement(scrolltoappsbutton_ele).click();
		driverwait.until(ExpectedConditions.elementToBeClickable(gtapps_ele));
		driver.findElement(gtapps_ele).click();
		driver.switchTo().frame(driver.findElement(frame_ele));
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(dy_ac_edpt));
		WebElement ele = driver.findElement(dy_ac_edpt);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);
		// driver.switchTo().defaultContent();
		int idcount = 0;
		while (idcount < 3) {
			try {
				driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-3")));
				break;
			} catch (TimeoutException e) {
			}
			idcount++;
		}

		driver.findElement(By.id("id-3")).click();
		driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div/div/nav/div/div/div/div[2]/button")).click();
		// driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[3]")).click();
		Thread.sleep(4000L);

		ele = driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[3]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		Thread.sleep(2000L);

		ele = driver.findElement(By.xpath("//span[text()='Incidents']"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		driverwait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//li[contains(@title, 'new Incident')]/button")));

		int attempts = 0;
		while (attempts < 2) {
			try {
				ele = driver.findElement(By.xpath("//li[contains(@title, 'new Incident')]/button"));
				executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", ele);
				break;
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}

		// driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='MscrmControls.Containers.ProcessBreadCrumb-stageIndicatorHolderbd6140f4-cb78-4d90-bb16-c7cb7fe5a51b']")));
		ele = driver.findElement(By.xpath(
				"//div[@id='MscrmControls.Containers.ProcessBreadCrumb-stageIndicatorHolderbd6140f4-cb78-4d90-bb16-c7cb7fe5a51b']"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);
		Thread.sleep(4000L);

		ele = driver.findElement(By.xpath(
				"//div[@id='MscrmControls.Containers.ProcessStageControl-processHeaderStageFlyoutContainer_bd6140f4-cb78-4d90-bb16-c7cb7fe5a51b']//input"));

		// executor = (JavascriptExecutor)driver;
		// executor.executeScript("arguments[0].click();", ele);
		ele.click();
		ele.sendKeys("Suresh Gajendran");
		Thread.sleep(2000L);
		ele.sendKeys(Keys.DOWN, Keys.ENTER);
		Thread.sleep(6000L);
		driver.findElement(By.xpath("//li[contains(@title,'Save this Incident - Custody')]")).click();
		Thread.sleep(6000L);
		driverwait = new WebDriverWait(driver, 60);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//div[@id='MscrmControls.Containers.ProcessBreadCrumb-stageIndicatorHolderbd6140f4-cb78-4d90-bb16-c7cb7fe5a51b']")));

		Thread.sleep(4000L);

		idcount = 0;
		while (idcount < 3) {

			try {
				ele = driver.findElement(By.xpath(
						"//div[@id='MscrmControls.Containers.ProcessBreadCrumb-stageIndicatorHolderbd6140f4-cb78-4d90-bb16-c7cb7fe5a51b']"));
				executor = (JavascriptExecutor) driver;
				executor.executeScript("arguments[0].click();", ele);
				ele = driver.findElement(By.xpath("//button[@title='Next Stage']"));
				break;
			} catch (NoSuchElementException e) {

			} catch (TimeoutException t) {

			}
			idcount++;
		}

		Thread.sleep(2000L);
		// executor = (JavascriptExecutor)driver;
		// executor.executeScript("arguments[0].click();", ele);
		ele.click();
		driverwait.until(ExpectedConditions.elementToBeClickable(By.id("tab1")));
		driver.findElement(By.id("tab1")).click();
		driverwait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='text']")));

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		LocalDateTime lt = LocalDateTime.now();
		System.out.println(dtf.format(lt));

		ele = driver.findElement(By.xpath("//input[@type='text']"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);
		// executor.executeScript("arguments[0].setAttribute('value','07/24/2019');",
		// ele);
		ele = driver.findElement(By.xpath("//td[@aria-label='" + dtf.format(lt) + "']"));
		ele.click();
		Thread.sleep(3000L);
		driverwait.until(ExpectedConditions.elementToBeClickable(By.id("tab4")));
		driver.findElement(By.id("tab4")).click();
		driverwait.until(ExpectedConditions.elementToBeClickable(By.id(
				"id-9cc95fe1-6f0c-43ba-bec8-77cc4e5b41a3-25-ssg_incidentdetails-ssg_incidentdetails.fieldControl-text-box-text")));
		driver.findElement(By.id(
				"id-9cc95fe1-6f0c-43ba-bec8-77cc4e5b41a3-25-ssg_incidentdetails-ssg_incidentdetails.fieldControl-text-box-text"))
				.sendKeys("ff");
		ele = driver.findElement(By.id(
				"id-9cc95fe1-6f0c-43ba-bec8-77cc4e5b41a3-27-ssg_clientlogentrycompleted-ssg_clientlogentrycompleted.fieldControl-option-set-select"));
		Select s = new Select(ele);
		s.selectByVisibleText("Yes");
		Thread.sleep(3000L);
		// driver.findElement(By.xpath("//li[contains(@title,'Save this Incident -
		// Custody')]")).click();
		idcount = 0;

		while (idcount < 3) {
			try {
				driver.findElement(By.id("tab2")).click();
				break;
			} catch (NoSuchElementException e) {

			}
			idcount++;
		}

		// driver.findElement(By.id("tab2")).click();
		driverwait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@aria-label='Location  Lookup']")));
		driver.findElement(By.xpath("//*[@aria-label='Location  Lookup']")).sendKeys("L");
		driver.findElement(By.xpath("//*[@aria-label='Location  one']")).click();
		Thread.sleep(3000L);
		driver.findElement(By.id("tab3")).click();
		driver.findElement(By.xpath("//*[@aria-label='Add New Involved Client - Custody Incident']")).click();
		// driver.findElement(By.id("Location Lookup")).click();
		// driver.findElement(By.id("Location Lookup")).sendKeys("L");
		Thread.sleep(3000L);
		System.out.println("Custody" + cno);
		driver.findElement(By.xpath(
				"//input[contains(@id, 'id-108fec8c-b60b-428a-8cb1-f2ae94e9808b-1-ssg_clientid-ssg_clientid.fieldControl-LookupResultsDropdown_ssg_clientid_')]"))
				.sendKeys(cno);
		idcount = 0;
		while (idcount < 3) {
			try {
				driver.findElement(By.xpath(
						"//*[contains(@id, 'id-108fec8c-b60b-428a-8cb1-f2ae94e9808b-1-ssg_clientid-ssg_clientid.fieldControl-ssg_csnumber1')]"))
						.click();
				break;
			} catch (NoSuchElementException no) {
				Thread.sleep(3000L);
			}
			idcount++;
		}
		ele = driver.findElement(By.xpath("//*[@aria-label='Role']"));
		s = new Select(ele);
		s.selectByVisibleText("Instigator");
		Thread.sleep(1000L);
		driver.findElement(By.xpath("//*[@aria-label='Save']")).click();

		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-3")));
		driver.findElement(By.id("id-3")).click();
		driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div/div/nav/div/div/div/div[2]/button")).click();
		// driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[3]")).click();
		Thread.sleep(4000L);

		ele = driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[4]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		driver.findElement(By.xpath("//li[@id='sitemap-entity-NewSubArea_54aab8f8']")).click();
		Thread.sleep(1000L);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(@title, 'Create a new Inmate Assessment record.')]")));
		driver.findElement(By.xpath("//*[contains(@title, 'Create a new Inmate Assessment record.')]")).click();
		Thread.sleep(2000L);
		System.out.println("IA" + cno);
		driver.findElement(By.xpath("//*[@aria-label='Reporting Location  Lookup']")).sendKeys("F");
		driver.findElement(By.xpath("//*[@aria-label='Ford Mountain Correctional Centre']")).click();
		driver.findElement(By.xpath("//*[@aria-label='Client  Lookup']")).sendKeys(cno);
		driver.findElement(By.xpath("//*[contains (@id,'ssg_contactid-ssg_contactid.fieldControl-ssg_csnumber1')]"))
				.click();
		driver.findElement(By.xpath("//*[@aria-label='Save']")).click();

		// Thread.sleep(2000L);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-3")));
		driver.findElement(By.id("id-3")).click();
		driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div/div/nav/div/div/div/div[2]/button")).click();
		// driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[3]")).click();
		Thread.sleep(4000L);
		System.out.println("CP" + cno);
		ele = driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[2]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		driver.findElement(By.xpath("//div[@title='Continuous Periods of Separate Confinement']")).click();
		driver.findElement(
				By.xpath("//*[contains(@title, 'Create a new Continuous Period of Separate Confinement record')]"))
				.click();

		driver.findElement(By.xpath("//*[@aria-label='Client  Lookup']")).sendKeys(cno);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[contains(@id, '-ssg_client-ssg_client.fieldControl-ssg_csnumber1')]")));
		driver.findElement(By.xpath("//*[contains(@id, '-ssg_client-ssg_client.fieldControl-ssg_csnumber1')]")).click();
		driver.findElement(By.xpath("//*[contains(@title, 'Save this Continuous Period of Separate Confinement.')]"))
				.click();
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-3")));
		driver.findElement(By.id("id-3")).click();
		driver.findElement(
				By.xpath("//button[@title='Expand to see recently used Continuous Periods of Separate Confinement.']"))
				.click();
		driver.findElement(By.xpath("//li[contains(@aria-label, '" + cno + "')]")).click();

		int count = 0;
		int maxtries = 4;
		exitnosee: while (true)
			try {
				driver.findElement(By.xpath("//*[@aria-label='Add New Separate Confinement Type']")).click();
				ele = driver.findElement(By.xpath("//*[@title='Separate Confinement Type']"));
				break exitnosee;
			} catch (NoSuchElementException nosee) {
				if (++count == maxtries) {
					throw nosee;
				}
			}
		s = new Select(ele);
		s.selectByVisibleText("Section 27 (Disciplinary Segregation)");
		System.out.println("CPT" + cno);
		driver.findElement(By.xpath("//*[@aria-label='Client  Lookup']")).sendKeys(cno);
		// driver.findElement(By.xpath("//*[@id='id-9a8fa0b3-826f-4dcb-aa23-b00c6a94eb06-3-ssg_inmateid-ssg_inmateid.fieldControl-ssg_csnumber1']")).click();
		driver.findElement(By.xpath("//*[@aria-label='Start Date/Time']")).click();
		driver.findElement(By.xpath("//td[@aria-label='" + dtf.format(lt) + "']")).click();
		driver.findElement(By.xpath("//*[@aria-label='Save']")).click();
		Thread.sleep(3000L);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-3")));
		driver.findElement(By.id("id-3")).click();
		driver.findElement(
				By.xpath("//button[@title='Expand to see recently used Continuous Periods of Separate Confinement.']"))
				.click();

		ele = driver.findElement(By.xpath("//li[contains(@aria-label, '" + cno + "')]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		driver.findElement(By.xpath("//button[@aria-label= 'Add New SC Daily Tracking']")).click();
		Thread.sleep(3000L);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@aria-label='Save']")));
		driver.findElement(By.xpath("//*[contains(@title, 'Save this SC Daily Tracking.')]")).click();
		Thread.sleep(3000L);
		((JavascriptExecutor) driver).executeScript("window.open()");
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		driver.get("https://cb-icap.dev.jag.gov.bc.ca");
		driverwait = new WebDriverWait(driver, 20);
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.name("TabHome")));
		driver.findElement(By.name("TabHome")).click();
		driver.findElement(By.xpath("//*[@title='Settings']")).click();
		// driver.findElement(By.xpath("//div[@id='detailActionGroupControl_leftNavContainer']/a")).click();
		driverwait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@title='Go to My Apps']")));
		driver.findElement(By.xpath("//*[@title='Go to My Apps']")).click();
		driver.switchTo().frame(driver.findElement(By.id("contentIFrame0")));
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//a[@href='https://cb-icap.dev.jag.gov.bc.ca/main.aspx?appid=02afe8c7-f34f-444d-be29-a00483bdfee4']")));
		ele = driver.findElement(By.xpath(
				"//a[@href='https://cb-icap.dev.jag.gov.bc.ca/main.aspx?appid=02afe8c7-f34f-444d-be29-a00483bdfee4']"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);
		driver.findElement(By.id("id-3")).click();
		driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div/div/nav/div/div/div/div[2]/button")).click();
		Thread.sleep(4000L);

		ele = driver.findElement(By.xpath("//div[@id='__flyoutRootNode']/div[2]/div/ul/li[3]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		Thread.sleep(2000L);
		driver.findElement(By.xpath("//span[text()='Incidents']")).click();
		Thread.sleep(2000L);
		driver.findElement(By.xpath("//*[contains(@title, 'Create a new Incident - Community record.')]")).click();
		Thread.sleep(2000L);

		idcount = 0;

		while (idcount < 3) {
			try {
				driver.findElement(By.id("tab1")).click();
				break;
			} catch (NoSuchElementException e) {
				Thread.sleep(2000L);
			}
			idcount++;
		}

		driver.findElement(By.xpath("//input[@aria-label='Incident Date & Time']")).click();
		driver.findElement(By.xpath("//td[@aria-label='" + dtf.format(lt) + "']")).click();
		driverwait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Reporting Location  Lookup']")));
		driver.findElement(By.xpath("//input[@aria-label='Reporting Location  Lookup']")).sendKeys("F");

		Thread.sleep(2000L);
		driver.findElement(By.xpath("//li[@aria-label='Fort St. John']")).click();
		Thread.sleep(2000L);
		ele = driver.findElement(By.xpath("//div[@id='ssg_incidentlocationtype_i']/div[3]/div[2]"));
		executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);

		// driver.findElement(By.xpath("//input[@aria-label='Enter a value to filter on,
		// or select enter to show available options.']")).click();
		driver.findElement(By.xpath("//label[@title='Comm Office - Lobby/Reception']")).click();
		s = new Select(driver.findElement(By.xpath("//select[@aria-label='Primary Incident Type']")));
		s.selectByIndex(2);
		driver.findElement(By.xpath(
				"//textarea[contains(@id, 'ssg_incidentdetails-ssg_incidentdetails.fieldControl-text-box-text')]"))
				.click();
		driver.findElement(By.xpath(
				"//textarea[contains(@id, 'ssg_incidentdetails-ssg_incidentdetails.fieldControl-text-box-text')]"))
				.sendKeys("ff");
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();",
				driver.findElement(By.xpath("//div[contains(@id, 'ssg_clientlogdescription')]")));

		driver.findElement(By.xpath("//div[contains(@id, 'ssg_clientlogdescription')]//textarea")).sendKeys("ff");
		Thread.sleep(2000L);
		driver.findElement(By.xpath("//*[@aria-label='Save']")).click();
		Thread.sleep(2000L);
		driverwait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Incident Date & Time']")));
		Thread.sleep(2000L);
		System.out.println("Community" + cno);

		idcount = 0;

		while (idcount < 3) {
			try {
				driverwait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@aria-label='Participants']")));
				driver.findElement(By.xpath("//li[@aria-label='Participants']")).click();
				break;
			} catch (NoSuchElementException e) {
				Thread.sleep(3000L);
			} catch (ElementClickInterceptedException cl) {
				Thread.sleep(3000L);
			}
			idcount++;
		}

		driverwait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//li[@aria-label='Add New Involved Client - Community Incident']")));
		driver.findElement(By.xpath("//li[@aria-label='Add New Involved Client - Community Incident']")).click();
		driverwait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@aria-label='Role']")));
		driver.findElement(By.xpath("//input[@aria-label='Client  Lookup']")).sendKeys(cno);
		Thread.sleep(2000L);
		driverwait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains (@aria-label, '" + nm + "')]")));
		driver.findElement(By.xpath("//li[contains (@aria-label, '" + nm + "')]")).click();
		ele = driver.findElement(By.xpath("//*[@aria-label='Role']"));
		s = new Select(ele);
		s.selectByVisibleText("Instigator");
		Thread.sleep(1000L);
		driver.findElement(By.xpath("//*[@aria-label='Save']")).click();
		Thread.sleep(10000L);
		driver.quit();

		delete_events.delete();

		Thread.sleep(30000L);

		String clmrgq = prop.getProperty("clmrgq").replaceAll("\\{from\\}", idstring);
		idstring = idstringit.next();
		clmrgq = clmrgq.replaceAll("\\{to\\}", idstring);

		System.out.println(clmrgq);
		f.executeQuery(clmrgq);
		insconn.commit();

		int cliemrgi = 1;
		int prc = 0;

		while (cliemrgi <= 10) {
			ResultSet cliemrgprc = f.executeQuery(prop.getProperty("cliemrg_prc") + "'" + idstring + "'");
			if (cliemrgprc != null) {
				prc = 1;
				break;
			}
			Thread.sleep(10000L);
			cliemrgi++;
		}

		if (prc == 0) {
			throw new SkipException(prop.getProperty("clie_mrg_skip_exception"));
		}

		Thread.sleep(100000L);

		// access_token=dy.whenPostJsonUsingHttpClient_thenCorrect();

		LinkedHashSet<String> dy_cl_edpt = new LinkedHashSet<String>();

		dy_cl_edpt.add(prop.getProperty("dy_clinv_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_clcoinc_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_cldyia_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_clscp_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_clsc_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_dt_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_cic_edpt"));
		dy_cl_edpt.add(prop.getProperty("dy_ct_edpt"));

		Iterator<String> itdy_cl_edpt = dy_cl_edpt.iterator();

		int clmrg = 1;

		while (itdy_cl_edpt.hasNext()) {
			cnoit = cliainscno.iterator();
			cno = cnoit.next();
			cno = cnoit.next();
			access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();
			String dy_edpt = itdy_cl_edpt.next().replaceAll("\\{cno\\}", cno);
			System.out.println(dy_edpt);

			Response res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
					"Accept", ContentType.JSON).when().get(dy_edpt).then().extract().response();
			String response = res.asString();
			System.out.println(response);
			JsonPath js = new JsonPath(response);
			// ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));
			ArrayList<Map<String, ?>> val = js.get(prop.getProperty("val_dy"));
			// int value=val.size();
			// System.out.println(value);
			System.out.println(prop.getProperty("val"));
			Map<String, ?> map = val.get(0);
			System.out.println(map);
			if (clmrg == 5) {
				String cno_dy = (String) map.get(prop.getProperty("nu_dy"));
				System.out.println(cno_dy);
				System.out.println(cno);
				Assert.assertEquals(cno_dy, cno);
			} else {
				String cno_dy = (String) map.get(prop.getProperty("cno_dy"));
				System.out.println(cno_dy);
				System.out.println(cno);
				Assert.assertEquals(cno_dy, cno);
			}
			clmrg++;

		}

		
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		insconn.close();
	}

}
