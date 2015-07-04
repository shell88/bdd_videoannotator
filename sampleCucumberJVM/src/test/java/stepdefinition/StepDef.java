package stepdefinition;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.concurrent.TimeUnit;

public class StepDef {

  private WebDriver webdriver;
  
  @Before
  public void openWebDriver() {
    webdriver = new FirefoxDriver();
    webdriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    webdriver.manage().window().maximize();
  }

  @After
  public void quitWebDriver() {
    webdriver.quit();
  }
  
  @Given("^i open Google$")
  public void i_open_Google() throws Throwable {
      webdriver.get("http://www.google.com"); 
  }

  @When("^i search for \"(.*?)\",$")
  public void i_search_for(String searchText) throws Throwable {
    WebElement search_field = webdriver.findElement(By.name("q"));
    search_field.sendKeys(searchText);
    search_field.submit();
  }

  @When("^i wait for (\\d+) seconds,$")
  public void i_wait_for_seconds(int waitSeconds) throws Throwable {
    Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
  }

  @Then("^i want to see a link to the \"(.*?)\" on github\\.$")
  public void i_want_to_see_a_link_to_the_on_github(String searchText) throws Throwable {
    org.junit.Assert.assertTrue(webdriver.getPageSource().contains(searchText));
  }
  
}
