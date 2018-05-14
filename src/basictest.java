
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class basictest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private String TEST_ENVIRONMENT = "http://localhost:80/mutillidae";
  private int LOOPS = 4;
  private String FUZZ_STRING = "fuzzthisstring.com";
  
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
    driver = new FirefoxDriver();
    baseUrl = "https://www.katalon.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
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
  public void testREST() throws Exception{
	  //"http://localhost/mutillidae/webservices/rest/ws-user-account.php"
  }
  
  @Test
  public void testLooping() throws Exception{
	for(int i=1;i<LOOPS;i++) {
	  testLogIn(); 
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
