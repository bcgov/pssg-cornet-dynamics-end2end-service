package CTD_E2E;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Dynamics_val extends extentreport {

	@Test
	public void val(String access_token, String edpt, String path_element_to_verify, String value, String element_type)
			throws InterruptedException {
		int count = 0;
		int maxtries = 5;
		String response_val = null;

		exit: while (true) {

			Response res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
					"Accept", ContentType.JSON).when().get(edpt).then().extract().response();
			String response = res.asString();
			System.out.println(response);

			int Status_code = res.getStatusCode();
			// System.out.println(Status_code);

			if (Status_code != 200) {
				++count;

				switch (count) {
				case 1:
					Thread.sleep(10000L);
					break;

				case 2:
					Thread.sleep(20000L);
					break;

				case 3:
					Thread.sleep(30000L);
					break;

				case 4:
					Thread.sleep(40000L);
					break;
				}

				if (count == maxtries) {
					childTest.log(Status.ERROR, "Statuscode: " + String.valueOf(Status_code));
					throw new SkipException(
							"waited for 100 seconds and could not get a 200 status code from Dynamics. Please verify the problem and complete the test");
				}

			} else {
				JsonPath js = new JsonPath(response);

				switch (element_type) {
				case "int":
					int response_vali = js.get(path_element_to_verify);
					response_val = String.valueOf(response_vali);
					break exit;

				case "String":
					response_val = js.get(path_element_to_verify);
					break exit;

				case "BigDecimal":
					BigDecimal response_valb = js.get(path_element_to_verify);
					response_val = String.valueOf(response_valb);
					break exit;
				}
			}
		}

		if (response_val != null) {
			Assert.assertEquals(response_val, value, "validation to verify that the changes were updated on Dynamics");
		} else {
			childTest.log(Status.INFO, "Element Key not found in dynamics response");
			Assert.assertTrue(false, "Waited 2 mins and Element Key not found in dynamics response");
		}

	}
}
