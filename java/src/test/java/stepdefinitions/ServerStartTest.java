package stepdefinitions;

import static org.junit.Assert.assertFalse;

import com.github.shell88.bddvideoannotator.javaadapters.ServerConnector;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;


public class ServerStartTest {

  private ServerConnector connector;

  @Given("^I start the server from the provided client package$")
  public void i_start_the_server_from_the_provided_client_package()
      throws Throwable {
    connector = new ServerConnector();
    connector.startServerProcess();
   
  }

  @Then("^i must be able to connect to the server functions without an error$")
  public void i_must_be_able_to_connect_to_the_server_functions_without_an_error()
      throws Throwable {
      connector.getJavaClient().changeOutputDirectory(".");

  }

  @Then("^i must be able to stop the server$")
  public void i_must_be_able_to_stop_the_server() throws Throwable {

    assertFalse("ServerProcess is still running", connector.stopServerProcess());

  }

}
