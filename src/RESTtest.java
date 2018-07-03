import java.io.BufferedReader;
	import java.io.Console;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.util.concurrent.TimeUnit;

	import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
	import org.junit.Test;
	import org.openqa.selenium.Alert;
	import org.openqa.selenium.By;
	import org.openqa.selenium.Keys;
	import org.openqa.selenium.NoAlertPresentException;
	import org.openqa.selenium.NoSuchElementException;
	import org.openqa.selenium.Proxy;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.firefox.FirefoxOptions;
	import org.openqa.selenium.firefox.FirefoxProfile;
	import org.openqa.selenium.remote.CapabilityType;
	import org.openqa.selenium.remote.DesiredCapabilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.module.jsv.JsonSchemaValidator.*;
import java.io.BufferedReader;
	import java.io.InputStreamReader;
	import java.net.HttpURLConnection;
	import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
	//rest-assured imports
	import static io.restassured.RestAssured.*;
	
public class RESTtest {
	
	 private WebDriver driver;
	  private String baseUrl;
	  private boolean acceptNextAlert = true;
	  private StringBuffer verificationErrors = new StringBuffer();
	  private String TEST_ENVIRONMENT = "http://localhost:80/mutillidae";
	  private String PROXY_URL = "http://localhost:8080/proxy/8081/har";
	  private String REST_URL = "http://localhost:80/mutillidae/webservices/rest/ws-user-account.php?username=";
	  private String REST_URL2 = "http://localhost:5000/exercisetracker/api/";
	  private int LOOPS = 4;
	  private String FUZZ_STRING = "fuzzthisstring.com";
	  
	  private Boolean proxyInUse = true;
	  private String proxyAddress = "localhost";
	  private int proxyPort = 8081;
	  
	  
	  
	  private JSONObject createJSON() throws JSONException {
		  JSONObject jstring= new JSONObject()
				  .put("username", fuzzWithRadamsa("username"))
				  .put("password", fuzzWithRadamsa("password"))
				  .put("description", fuzzWithRadamsa("description"))
				  .put("avatar", fuzzWithRadamsa("avatar"))
				  .put("visibility", fuzzWithRadamsa("visibility"));
		  
		  
		  return jstring;
	  }
	  //returns HAR in JSONObject
	  private JSONObject getHar() throws Exception {
			String url = PROXY_URL;
		        URL obj = new URL(url);
		        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		        // optional default is GET
		        con.setRequestMethod("GET");
		        //add request header
		        con.setRequestProperty("User-Agent", "Mozilla/5.0");
		        int responseCode = con.getResponseCode();
		        System.out.println("\nSending 'GET' request to URL : " + url);
		        System.out.println("Response Code : " + responseCode);
		        BufferedReader in = new BufferedReader(
		                new InputStreamReader(con.getInputStream()));
		        String inputLine;
		        StringBuffer response = new StringBuffer();
		        while ((inputLine = in.readLine()) != null) {
		        	response.append(inputLine);
		        }
		        in.close();
		        
		        //Read JSON response and print
		        JSONObject myResponse = new JSONObject(response.toString());
		        return myResponse; 
			    }
	//  JSONObject ob = getHar();
	//  System.out.print(ob.getJSONObject("log").getString("version")); 
	  
	  //use if site requires a popup login
	  public void login(String uname, String pwd){
		  String URL = "https://" + uname + ":" + pwd + "@" + TEST_ENVIRONMENT;
		  driver.get(URL);
		}
	  //execute commandline commands
	  private static String executeCommandLine(String[] cmdLine) {
		  //give the command in the following form
		//String[] command = {"sh", "-c" ,"echo 'fuziafsfakjfb' | radamsa"};
	      String output = "";
	      try {
	          Process p = Runtime.getRuntime().exec(cmdLine);
	          BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	          String line;
	          while ((line = input.readLine()) != null) {
	              output += (line + '\n');
	          }
	          input.close();
	      } catch(IOException ex) {
	          System.out.println(ex.getStackTrace());
	      }
	      return output;
	} 
	  //fuzz a string with radamsa. Runs radamsa in terminal
	  private String fuzzWithRadamsa(String notfuzz) {
		  
		  String[] radamsaCommand = {"sh", "-c" ,"echo '"+ notfuzz + "'|radamsa"};
		  String fuzzed = executeCommandLine(radamsaCommand);
		  System.out.println("Radamsa fuzzed "+notfuzz+" into "+fuzzed);
		  //String fuzzed=notfuzz;
		  return fuzzed;
	  }
	  
	  @Before
	  public void setUp() throws Exception {
		
	  }

	
	  //REST TESTS. Move to other file later
	  
	  @Test
	  public void testEREST() throws Exception{
		  //ExerciseTracker api testing
		    given().
		    contentType("application/json\r\n").  //!! Rest-assured is dumb and you have to add \r\n to the end
		    when().
		    get(REST_URL2+"users/").
		    then().statusCode(200); 
	  }
	  
	  @Test
	  public void testETAddUser() throws Exception{
		  //ExerciseTracker api testing
		  JSONObject message = createJSON();
		
		    System.out.print(message.toString());
			RestAssured.baseURI =REST_URL2;
			RequestSpecification request = RestAssured.given();//.config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("JSON", ContentType.TEXT)));
			request.contentType("application/json\r\n");
			request.body(message.toString());
			Response response = request.post("users/");
			System.out.println(response.asString());
			int statusCode = response.getStatusCode();
			System.out.println(statusCode);
			Assert.assertEquals(statusCode, "200");
			
		    
	  }
	  
	  
	  
	  
	  
	//all below is JUnit stuff generated by katalon recorder
	  @After
	  public void tearDown() throws Exception {
	    
	  }

	  private boolean isElementPresent(By by) {
	    try {
	      driver.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	  private boolean isAlertPresent() {
	    try {
	      driver.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }

	  private String closeAlertAndGetItsText() {
	    try {
	      Alert alert = driver.switchTo().alert();
	      String alertText = alert.getText();
	      if (acceptNextAlert) {
	        alert.accept();
	      } else {
	        alert.dismiss();
	      }
	      return alertText;
	    } finally {
	      acceptNextAlert = true;
	    }
	  }
	
}
