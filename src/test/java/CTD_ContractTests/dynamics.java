<<<<<<< HEAD
<<<<<<< master
=======
>>>>>>> Dev
package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class dynamics {
	
	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";

	

	@Test
	public void whenPostJsonUsingHttpClient_thenCorrect() throws ClientProtocolException, IOException {
		fis = new FileInputStream(Filepath);
		prop.load(fis);
		String applicationGroupResource = prop.getProperty("applicationGroupResource");
		String applicationGroupClientId = prop.getProperty("applicationGroupClientId");
		String applicationGroupSecret = prop.getProperty("applicationGroupSecret");
		String serviceAccountUsername = prop.getProperty("serviceAccountUsername");
		String serviceAccountPassword = prop.getProperty("serviceAccountPassword");
		String adfsOauth2Uri = prop.getProperty("adfsOauth2Uri");
		
		Response request = given()
				.config(RestAssured.config()
				.encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs("x-www-form-urlencoded",
				ContentType.URLENC)))
				.contentType("application/x-www-form-urlencoded; charset=UTF-8")
				.header("Accept", "application/json" )
				.header("return-client-request-id", "true")
				.formParam("resource", applicationGroupResource)
				.formParam("client_id", applicationGroupClientId)
				.formParam("client_secret", applicationGroupSecret)
				.formParam("username", serviceAccountUsername)
				.formParam("password", serviceAccountPassword)
				.formParam("scope", "openid")
				.formParam("response_mode", "form_post")
				.formParam("grant_type", "password")
				.when()
				.post(adfsOauth2Uri)
				.then().extract().response();
		
		String response=request.asString();
		JsonPath js=new JsonPath(response);
		String access_token=js.get("access_token");
		System.out.println(access_token);
	}
}
<<<<<<< HEAD
=======
package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


public class dynamics {
	
	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";

	

	@Test
	public void whenPostJsonUsingHttpClient_thenCorrect() throws ClientProtocolException, IOException {
		fis = new FileInputStream(Filepath);
		prop.load(fis);
		String applicationGroupResource = prop.getProperty("applicationGroupResource");
		String applicationGroupClientId = prop.getProperty("applicationGroupClientId");
		String applicationGroupSecret = prop.getProperty("applicationGroupSecret");
		String serviceAccountUsername = prop.getProperty("serviceAccountUsername");
		String serviceAccountPassword = prop.getProperty("serviceAccountPassword");
		String adfsOauth2Uri = prop.getProperty("adfsOauth2Uri");
		
		Response request = given()
				.config(RestAssured.config()
				.encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs("x-www-form-urlencoded",
				ContentType.URLENC)))
				.contentType("application/x-www-form-urlencoded; charset=UTF-8")
				.header("Accept", "application/json" )
				.header("return-client-request-id", "true")
				.formParam("resource", applicationGroupResource)
				.formParam("client_id", applicationGroupClientId)
				.formParam("client_secret", applicationGroupSecret)
				.formParam("username", serviceAccountUsername)
				.formParam("password", serviceAccountPassword)
				.formParam("scope", "openid")
				.formParam("response_mode", "form_post")
				.formParam("grant_type", "password")
				.when()
				.post(adfsOauth2Uri)
				.then().extract().response();
		
		String response=request.asString();
		JsonPath js=new JsonPath(response);
		String access_token=js.get("access_token");
		System.out.println(access_token);
	}
}
>>>>>>> Sync with Master
=======
>>>>>>> Dev
