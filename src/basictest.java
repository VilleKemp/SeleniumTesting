
import static org.junit.Assert.fail;


import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.junit.After;
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
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
//rest-assured imports
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class basictest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private String TEST_ENVIRONMENT = "http://localhost:81/mutillidae";
  private String PROXY_URL = "http://localhost:8080/proxy/8081/har";
  private String REST_URL = "http://localhost:81/mutillidae/webservices/rest/ws-user-account.php?username=";
  private String REST_URL2 = "http://localhost:5000/exercisetracker/api/";
  private int LOOPS = 4;
  private String FUZZ_STRING = "fuzzthisstring.com";
  
  private Boolean proxyInUse = true;
  private String proxyAddress = "localhost";
  private int proxyPort = 8081;
  
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
	  
	  return fuzzed;
  }
  
  @Before
  public void setUp() throws Exception {
	DesiredCapabilities capabilities = DesiredCapabilities.firefox();
    FirefoxOptions options = new FirefoxOptions(capabilities);
    
    if(proxyInUse) {
	    options.addPreference("network.proxy.type", 1);
	    options.addPreference("network.proxy.http", proxyAddress);     
	    options.addPreference("network.proxy.http_port", proxyPort); 
	    options.addPreference("network.proxy.no_proxies_on", "");
    }
    driver = new FirefoxDriver(options);

    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  //Fuzz both login fields
  public void testLogIn() throws Exception {
	  driver.get(TEST_ENVIRONMENT+"/index.php?page=user-info.php");
	    driver.findElement(By.name("username")).click();
	    driver.findElement(By.name("username")).clear();
	    driver.findElement(By.name("username")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.name("password")).click();
	    driver.findElement(By.name("password")).clear();
	    driver.findElement(By.name("password")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.name("user-info-php-submit-button")).click();
	    Boolean visible = driver.findElement(By.id("id-bad-cred-tr")).isDisplayed();
	    //Fuzzing wise not the smartest assert. Basically this looks if account exists.
	    assertTrue("Error text is visible",visible.equals(false));
  }
  
  @Test
  //fuzz url to find hidden directories
  public void testDirectoryBrowsing() throws Exception{
	  String fuzzdata = fuzzWithRadamsa("index");
	  driver.get(TEST_ENVIRONMENT+'/'+ fuzzdata);
	  WebElement header1 = driver.findElement(By.tagName("h1"));
	  String elementval = header1.getText();
	  //check if the element h1 has the string that indicates that you can browse directories
	  assertTrue("Directory http://localhost/mutillidae/" + fuzzdata+ "does not exist",elementval.equals("Index of /mutillidae/"+fuzzdata));
	  
  }

  
  
  
  @Test
  public void testPetstorePostUpdateRandomInput() throws Exception {
	    driver.get("http://localhost:8080/");
	    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div/div")).click();
	    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div[2]/div/div/div/div[2]/button")).click();
	    driver.findElement(By.xpath("//input[@value='']")).click();
	    driver.findElement(By.xpath("//input[@value='235']")).clear();
	    driver.findElement(By.xpath("//input[@value='235']")).sendKeys(fuzzWithRadamsa("22"));
	    driver.findElement(By.xpath("//input[@value='']")).click();
	    driver.findElement(By.xpath("//input[@value='3333']")).clear();
	    driver.findElement(By.xpath("//input[@value='3333']")).sendKeys(fuzzWithRadamsa("33"));
	    driver.findElement(By.xpath("//input[@value='']")).click();
	    driver.findElement(By.xpath("//input[@value='4444']")).clear();
	    driver.findElement(By.xpath("//input[@value='4444']")).sendKeys(fuzzWithRadamsa("444"));
	    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div[2]/div/div[2]/button")).click();
	    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div[2]/div/div[2]/button")).click();
	  }

  @Test
  public void testPetGetRandomInput() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='1234']")).clear();
    driver.findElement(By.xpath("//input[@value='1234']")).sendKeys(fuzzWithRadamsa("1"));
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div[2]/div/div[3]/button")).click();
  }
  
  
  @Test
  public void testPetPostRandomInput() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div/section/div/hgroup/h2")).click();
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div/div[2]/button")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).clear();
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).sendKeys("{\n  \"id\": "+fuzzWithRadamsa("20") +",\n  \"category\": {\n    \"id\":"+fuzzWithRadamsa("20") +",\n    \"name\": \"string\"\n  },\n  \"name\": \" " +fuzzWithRadamsa("petname")+"\",\n  \"photoUrls\": [\n    \"string\"\n  ],\n  \"tags\": [\n    {\n      \"id\": 1,\n      \"name\": \"string\"\n    }\n  ],\n  \"status\": \"available\"\n}");
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div[2]/button")).click();
  }
  
  
  
  @Test
  //if you want to loop some tests multiple times. There is probably a better way to do this
  public void testLooping() throws Exception{
	for(int i=1;i<LOOPS;i++) {
	  //testDirectoryBrowsing();
	  } 
	  
  }
  
  @Test
  //Test for ExerciseTracker T
  public void testEtracker() throws Exception {
	  driver.get("http://localhost:5000/forum/admin/ui.html");
	    driver.findElement(By.id("addUserButton")).click();
	    driver.findElement(By.xpath("(//input[@id='username'])[2]")).click();
	    driver.findElement(By.xpath("(//input[@id='username'])[2]")).clear();
	    driver.findElement(By.xpath("(//input[@id='username'])[2]")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.id("password")).clear();
	    driver.findElement(By.id("password")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.xpath("(//input[@id='description'])[2]")).click();
	    driver.findElement(By.xpath("(//input[@id='description'])[2]")).clear();
	    driver.findElement(By.xpath("(//input[@id='description'])[2]")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.xpath("(//input[@id='avatar'])[2]")).clear();
	    driver.findElement(By.xpath("(//input[@id='avatar'])[2]")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.xpath("(//input[@id='visibility'])[2]")).clear();
	    driver.findElement(By.xpath("(//input[@id='visibility'])[2]")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.id("createUser")).click();
	    assertEquals("User successfully added", closeAlertAndGetItsText());;
  }
 
  
  
//all below is JUnit stuff generated by katalon recorder
  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
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
