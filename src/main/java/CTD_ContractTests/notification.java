package CTD_ContractTests;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import java.io.FileWriter;
import java.io.IOException;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.Response;
import io.restassured.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;

import io.restassured.response.Response;

public class notification extends extentreport {

	String Filepath = System.getProperty("user.dir");

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Fileprop = System.getProperty("user.dir") + "/Properties/cornet.properties";

	public String generateStringFromResource(String path) throws IOException {

		return new String(Files.readAllBytes(Paths.get(path)));

	}

	public void event_notification() throws IOException {
		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		String File = Filepath + "/Dynamic_Adapter_RequestJSON/notification.txt";
		String bodypayload = generateStringFromResource(File);

		String username = prop.getProperty("notification_username");
		String password = prop.getProperty("notification_password");

		//System.out.println(username);
		//System.out.println(password);

		given().auth().preemptive().basic(username, password).header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("notif_edpt")).then().assertThat().statusCode(200);

		/*
		 * String response=res.asString();
		 * 
		 * String[][] requestandresponse = { { "Request", "Response" }, { "<pre>" +
		 * JsonFormatter.prettyPrint(bodypayload) + "</pre>", "<pre>" +
		 * JsonFormatter.prettyPrint(response) + "</pre>" } }; Markup m =
		 * MarkupHelper.createTable(requestandresponse);
		 * 
		 * childTest.log(Status.INFO, m); String
		 * request_dynamic_adapter=JsonFormatter.prettyPrint(response);
		 * 
		 * 
		 * File file = new File(System.getProperty("user.dir") +
		 * "/Dynamic_Adapter_RequestJSON/notification.txt"); FileWriter filewriter = new
		 * FileWriter(file); filewriter.write(request_dynamic_adapter);
		 * filewriter.close();
		 */

	}

}
