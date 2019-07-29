package CTD_ContractTests;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;
import io.restassured.response.Response;

public class tombstoneanddiscliplinarychargenotification extends extentreport {
	
String Filepath = System.getProperty("user.dir");
	
	public String generateStringFromResource(String path) throws IOException {

		return new String(Files.readAllBytes(Paths.get(path)));

	}
	
	public void event_notification() throws IOException
	{
		
		String File = Filepath + "/Dynamic_Adapter_RequestJSON/notification.txt";
		String bodypayload = generateStringFromResource(File);
		
		Response res=given().header("Content-Type", "Application/json").body(bodypayload).when().post("http://dynamics-adapter-wyck1k-dev.pathfinder.gov.bc.ca/api/dynamics/notification/").
		then().assertThat().statusCode(200).extract().response();
		
		String response=res.asString();
		
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		
		childTest.log(Status.INFO, m);
		String request_dynamic_adapter=JsonFormatter.prettyPrint(response);
		
		
		File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/notification.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();

	}


}
