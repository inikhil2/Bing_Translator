package TestNG;
import java.io.*;
import java.util.List;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NewTest {
	static String from;
	static String to;
	static String ext;
   	
	 @BeforeClass
	 public void setUp() {
	  System.out.println("Lets go");
	 }
	 
	 @Test
	 public String transTest(String a, String b, String c)  throws IOException, InterruptedException, JSONException  {
	   
		//via UI
	   from=a;to=b;ext=c;
	   WebDriver driver = new FirefoxDriver(); 
	   driver.get("http://www.bing.com/translator");
	   int j=0;
	   /*File file =new File("src/test.txt");
	   FileReader fr= new FileReader (file);
	   @SuppressWarnings("resource")
	   BufferedReader bufferedReader = new BufferedReader(fr);
	   String line; 
	   while ((line = bufferedReader.readLine()) != null) {
			bing = line.split(",");
			j=0;
			ext=bing[2];*/
			//from drop down
			if(from.charAt(0)==','){
				driver.findElement(By.id("srcText")).sendKeys(ext);
				
			}
			else
			{
				List<WebElement> table =driver.findElements(By.className("LS_Header")); 
				List<WebElement> from_to = driver.findElements(By.tagName("td")); 
				table.get(0).click();
				while(!(from_to.get(j).getText().equalsIgnoreCase(from))){
					j+=1;
				}
				
				Thread.sleep(2000);
				from_to.get(j).click();
				from=from_to.get(j).getAttribute("value");
				driver.findElement(By.id("srcText")).clear();
				driver.findElement(By.id("srcText")).sendKeys(ext);
				
			}
			
				//to drop down
			List<WebElement> table =driver.findElements(By.className("LS_Header")); 
			List<WebElement> from_to = driver.findElements(By.tagName("td")); 	
			table.get(1).click();
			Thread.sleep(1000);
			j=0;
			while(!(from_to.get(j).getText().equalsIgnoreCase(to))){
				j+=1;
			}
			Thread.sleep(2000);
			from_to.get(j).click();
			Thread.sleep(2000);
			driver.findElement(By.id("TranslateButton")).click();
			Thread.sleep(1000);
			String compare1= driver.findElement(By.id("destText")).getText();
			Thread.sleep(1000);
	        to=from_to.get(j).getAttribute("value");
			//via API
			
			//reset	
	        Thread.sleep(2000);
	        driver.quit();
	        return(from+","+to+","+compare1);
	        
	        
	 }
	 	
	 
	 
	 @AfterClass
	 public void finish(){
	 
	 //driver.quit();
	 }
}

