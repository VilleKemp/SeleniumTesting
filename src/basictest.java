
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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class basictest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private String TEST_ENVIRONMENT = "http://localhost:80/mutillidae";
  private String PROXY_URL = "http://localhost:8080/proxy/8081/har";
  private int LOOPS = 4;
  private String FUZZ_STRING = "fuzzthisstring.com";
  
  private Boolean proxyInUse = true;
  private String proxyAddress = "localhost";
  private int proxyPort = 8081;
  
  public JSONObject getHar() throws Exception {
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
  //fuzz a string with radamsa
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
	  driver.get("http://localhost/mutillidae/index.php?page=user-info.php");
	    driver.findElement(By.name("username")).click();
	    driver.findElement(By.name("username")).clear();
	    driver.findElement(By.name("username")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.name("password")).click();
	    driver.findElement(By.name("password")).clear();
	    driver.findElement(By.name("password")).sendKeys(fuzzWithRadamsa(FUZZ_STRING));
	    driver.findElement(By.name("user-info-php-submit-button")).click();	    
  }
  
  @Test
  //fuzz url to find hidden directories
  public void testDirectoryBrowsing() throws Exception{
	  
	  driver.get("http://localhost/mutillidae/"+ fuzzWithRadamsa("index"));
	
	  
  }
  
  
  
  @Test
  //loops a test several times
  public void testLooping() throws Exception{
	for(int i=1;i<LOOPS;i++) {
	  //testDirectoryBrowsing();
	  } 
	  
  }

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
