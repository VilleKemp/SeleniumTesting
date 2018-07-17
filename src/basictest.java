
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
  private int LOOPS = 10;
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
	    driver.findElement(By.name("username")).sendKeys("testuser1");
	    driver.findElement(By.name("password")).click();
	    driver.findElement(By.name("password")).clear();
	    driver.findElement(By.name("password")).sendKeys("password123");
	    driver.findElement(By.name("user-info-php-submit-button")).click();
	    Boolean visible = driver.findElement(By.id("id-bad-cred-tr")).isDisplayed();
	    //Fuzzing wise not the smartest assert. Basically this looks if account exists.
	    assertTrue("Error text is visible",visible.equals(false));
  }
  @Test
  public void testMutillidaeRepeater() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=repeater.php");
    driver.findElement(By.name("string_to_repeat")).click();
    driver.findElement(By.name("string_to_repeat")).clear();
    driver.findElement(By.name("string_to_repeat")).sendKeys("looptiloop");
    driver.findElement(By.name("times_to_repeat_string")).click();
    driver.findElement(By.name("times_to_repeat_string")).clear();
    driver.findElement(By.name("times_to_repeat_string")).sendKeys("20");
    driver.findElement(By.name("repeater-php-submit-button")).click();
  }
  
  @Test
  public void testMutillidaeHTML5() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=html5-storage.php");
    driver.findElement(By.id("idDOMStorageKeyInput")).click();
    driver.findElement(By.id("idDOMStorageKeyInput")).clear();
    driver.findElement(By.id("idDOMStorageKeyInput")).sendKeys("super secret new ky");
    driver.findElement(By.id("idDOMStorageItemInput")).clear();
    driver.findElement(By.id("idDOMStorageItemInput")).sendKeys("asdas");
    driver.findElement(By.xpath("//input[@value='Add New']")).click();
    driver.findElement(By.id("idDOMStorageKeyInput")).click();
    driver.findElement(By.id("idDOMStorageKeyInput")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | id=idDOMStorageKeyInput | ]]
    driver.findElement(By.id("idDOMStorageKeyInput")).click();
    driver.findElement(By.id("idDOMStorageKeyInput")).clear();
    driver.findElement(By.id("idDOMStorageKeyInput")).sendKeys("it was supposed to be a key");
    driver.findElement(By.id("idDOMStorageItemInput")).clear();
    driver.findElement(By.id("idDOMStorageItemInput")).sendKeys("not asd asd");
    driver.findElement(By.id("idDOMStorageItemInput")).sendKeys(Keys.ENTER);
    driver.findElement(By.xpath("//input[@value='Add New']")).click();
  }
  @Test
  public void testMutillidaeRegister() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=register.php");
    driver.findElement(By.name("username")).click();
    driver.findElement(By.name("username")).clear();
    driver.findElement(By.name("username")).sendKeys("new user");
    driver.findElement(By.name("password")).clear();
    driver.findElement(By.name("password")).sendKeys("passiwordi");
    driver.findElement(By.name("confirm_password")).click();
    driver.findElement(By.name("confirm_password")).clear();
    driver.findElement(By.name("confirm_password")).sendKeys("passiwordi");
    driver.findElement(By.name("my_signature")).clear();
    driver.findElement(By.name("my_signature")).sendKeys("hasdiagbksbjai");
    driver.findElement(By.name("register-php-submit-button")).click();
  }
  
  @Test
  public void testPetshopCreateUser() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div[2]/div/div[2]/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div[2]/div/div[2]/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).clear();
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div[2]/div/div[2]/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).sendKeys("{\n  \"id\": 2,\n  \"username\": \"newu\",\n  \"firstName\": \"newfn\",\n  \"lastName\": \"newln\",\n  \"email\": \"email@email.email\",\n  \"password\": \"whysovisible\",\n  \"phone\": \"bananaphone\",\n  \"userStatus\": 1\n}");
    driver.findElement(By.xpath("//div[@id='operations-user-createUser']/div[2]/div/div[3]/button")).click();
  }
  
  @Test
  public void testPetshopGetUser() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-getUserByName']/div/span[2]/a/span")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-getUserByName']/div[2]/div/div/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("newu");
    driver.findElement(By.xpath("//div[@id='operations-user-getUserByName']/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='newu']")).click();
    driver.findElement(By.xpath("//input[@value='newu']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("Idontexists");
    driver.findElement(By.xpath("//div[@id='operations-user-getUserByName']/div[2]/div/div[2]/button")).click();
  }
  @Test
  public void testPetshopUpdateUser() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("newu");
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[2]/div[2]/div/div/textarea")).clear();
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[2]/div[2]/div/div/textarea")).sendKeys("{\n  \"id\": 2222,\n  \"username\": \"string\",\n  \"firstName\": \"string\",\n  \"lastName\": \"string\",\n  \"email\": \"string\",\n  \"password\": \"string\",\n  \"phone\": \"string\",\n  \"userStatus\": 0\n}");
    driver.findElement(By.xpath("//div[@id='operations-user-updateUser']/div[2]/div/div[3]/button")).click();
  }
  @Test
  public void testPetstoreDeleteUser() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/span")).click();
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-deleteUser']/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-user-deleteUser']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("newu");
    driver.findElement(By.xpath("//div[@id='operations-user-deleteUser']/div[2]/div/div[3]/button")).click();
  }
  
  @Test
  //fuzz url to find hidden directories
  public void testDirectoryBrowsing() throws Exception{
	  
	  driver.get(TEST_ENVIRONMENT+'/'+ "incorrectlocation");
	  WebElement header1 = driver.findElement(By.tagName("h1"));
	  String elementval = header1.getText();
	  //check if the element h1 has the string that indicates that you can browse directories
	 // assertTrue("Directory http://localhost/mutillidae/" + fuzzdata+ "does not exist",elementval.equals("Index of /mutillidae/"+fuzzdata));
	  
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
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).sendKeys("{\n  \"id\": "+ "21" +",\n  \"category\": {\n    \"id\":"+"21" +",\n    \"name\": \"string\"\n  },\n  \"name\": \" " + "testirekku" +"\",\n  \"photoUrls\": [\n    \"string\"\n  ],\n  \"tags\": [\n    {\n      \"id\": 21,\n      \"name\": \"string\"\n    }\n  ],\n  \"status\": \"available\"\n}");
    driver.findElement(By.xpath("//div[@id='operations-pet-addPet']/div[2]/div/div[2]/button")).click();
  }
  
  
  @Test
  public void testPetstoreUpdate() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div[2]/div/div/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("21");
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("newtestpet");
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("available");
    driver.findElement(By.xpath("//div[@id='operations-pet-updatePetWithForm']/div[2]/div/div[2]/button")).click();
  }

  @Test
  public void testPetstoreget() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//input[@value='']")).click();
    driver.findElement(By.xpath("//input[@value='']")).clear();
    driver.findElement(By.xpath("//input[@value='']")).sendKeys("21");
    driver.findElement(By.xpath("//div[@id='operations-pet-getPetById']/div[2]/div/div[3]/button")).click();
  }
  @Test
  public void testPetstorestoreget() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-getInventory']/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-getInventory']/div[2]/div/div[2]/div/div[2]/button")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-getInventory']/div[2]/div/div[3]/button")).click();
  }
  
  
  @Test
  public void testPetstorestorepost() throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select")).click();
    new Select(driver.findElement(By.xpath("//div[@id='swagger-ui']/section/div[2]/div[2]/div[2]/section/label/select"))).selectByVisibleText("http");
    driver.findElement(By.xpath("//option[@value='http']")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div[2]/div/div/div/div[2]/button")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).click();
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).clear();
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div[2]/div/div/div[2]/table/tbody/tr/td[2]/div[2]/div/div/textarea")).sendKeys("{\n  \"id\": 21,\n  \"petId\": 21,\n  \"quantity\": 1,\n  \"shipDate\": \"2018-07-16T06:46:47.009Z\",\n  \"status\": \"placed\",\n  \"complete\": false\n}");
    driver.findElement(By.xpath("//div[@id='operations-store-placeOrder']/div[2]/div/div[2]/button")).click();
  }
  
  @Test
  public void testMutillidaeXMLValidator() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=xml-validator.php");
    driver.findElement(By.id("idXMLTextArea")).click();
    driver.findElement(By.id("idXMLTextArea")).clear();
    driver.findElement(By.id("idXMLTextArea")).sendKeys("<somexml><message>Hello World</message></somexml>");
    driver.findElement(By.name("xml-validator-php-submit-button")).click();
  }
  
  @Test
  public void testMutillidaeSeeBlogs() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=view-someones-blog.php");
    driver.findElement(By.id("id_author_select")).click();
    new Select(driver.findElement(By.id("id_author_select"))).selectByVisibleText("james");
    driver.findElement(By.xpath("//option[@value='james']")).click();
    driver.findElement(By.name("view-someones-blog-php-submit-button")).click();
    driver.findElement(By.id("id_author_select")).click();
    new Select(driver.findElement(By.id("id_author_select"))).selectByVisibleText("adrian");
    driver.findElement(By.xpath("//option[@value='adrian']")).click();
    driver.findElement(By.name("view-someones-blog-php-submit-button")).click();
  }
  @Test
  public void testMutillidaeMakeBlog() throws Exception {
    driver.get("http://localhost:81/mutillidae/index.php?page=view-someones-blog.php");
    driver.findElement(By.xpath("//a/span")).click();
    driver.findElement(By.name("blog_entry")).clear();
    driver.findElement(By.name("blog_entry")).sendKeys("this is a test blog. Hello");
    driver.findElement(By.name("add-to-your-blog-php-submit-button")).click();
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
