# encoding: utf-8
Feature: In order to support Cucumber-JVM, an easy to use adapter should be provided.

  Scenario Outline: ResultsConversion
    Given i have an instance of the BDD-Adapter for Cucumber-JVM without a server connection
    When Cucumber reports <cucumber_result>
    Then the BDD-Adapter should convert it to <server_result>

    Examples: 
      | cucumber_result         | server_result |
      | "FAILED#AssertionError" | "FAILURE"     |
      | "FAILED"                | "ERROR"       |
      | "PASSED"                | "SUCCESS"     |
      | "SKIPPED"               | "SKIPPED"     |
      | "UNDEFINED"             | "SKIPPED"     |
      | "PENDING"               | "FAILURE"     |
      | "NOTACUCUMBERSTATUS"    | "ERROR"       |

  Scenario: Collect backgroundsteps
    Given i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection
    And I have a feature file:
      """
      Feature: test
      Background:
      Given I have a background step
      And I have also a second background step
      
      Scenario: Test
      Given I have a TestScenario
      """
    When I run Cucumber-JVM
    Then the Adapter should send following steps for the scenario "Test":
      """
      Given I have a background step
      And I have also a second background step
      Given I have a TestScenario
      """
    And the Adapter should send "SKIPPED" for all steps to the server

  Scenario: Merging Scenariooutlines to single scenario
    Given i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection
    And I have a feature file:
      """
      Feature: test
      
      Background: testbackground
      Given I have a backgroundstep
      And a second backgroundstep
      
      Scenario Outline: Test
      Given I have a step with <value1> and <value2>
      Examples:
      |value1    | value2   |
      |"Test1-1" | "Test1-2"|
      |"Test2-1" | "Test2-2"|
      """
    When I run Cucumber-JVM
    Then the Adapter should send following steps for the scenario "Test":
      """
      Given I have a backgroundstep
      And a second backgroundstep
      Given I have a step with "Test1-1" and "Test1-2"
      Given I have a backgroundstep
      And a second backgroundstep
      Given I have a step with "Test2-1" and "Test2-2"
      """    
   
  Scenario: Feature-File with Scenario and ScenarioOutline
   Given i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection
   And I have a feature file:
      """
      Feature: test
      
      Scenario Outline: AnExampleScenario
      Given I have a step with <value1> and <value2>
      Examples:
      |value1     | value2   |
      |"Test1-1"  | "Test1-2"|
      |"Test2-1"  | "Test2-2"|
      
      Scenario: AnotherScenario
      Given I have a scenario with a step
      And there is also a second step
      """
   When I run Cucumber-JVM
   Then the Adapter should send following steps for the scenario "AnExampleScenario":
      """
      Given I have a step with "Test1-1" and "Test1-2"
      Given I have a step with "Test2-1" and "Test2-2"
      """    
   And the Adapter should send following steps for the scenario "AnotherScenario":
     """
     Given I have a scenario with a step
     And there is also a second step
     """


  Scenario: Datatables
    Given i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection
    And I have a feature file:
      """
      Feature: test
      Scenario: Test with datatable
      Given I have a step with a datatable:
      |Col1         |Col2       |
      |"String1"    |32.4       |
      |"String2"    |12.2       |
      And I have a second step with a datatable:
      |Col3         |Col4       |
      |1            |"String"   |
      """
    When I run Cucumber-JVM
    Then the Adapter should report the scenario "Test with datatable"
    And the Adapter should send the steptext: "Given I have a step with a datatable:" with the datatable at position 1
      """
      |Col1         |Col2       |
      |"String1"    |32.4       |
      |"String2"    |12.2       |
      """
    And the Adapter should send the steptext: "And I have a second step with a datatable:" with the datatable at position 2
      """
      |Col3         |Col4       |
      |1            |"String"   |
      """

  Scenario: Hooks
    Given i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection
    And I have a feature file:
      """
      Feature: test
      Scenario: testhook
      Given I have a samplestep
      """
    And i have a step definition file with following methods:
      """
      @Before
      public void beforeHook(){}
      
      @After
      public void afterHook(){}
      
      @Given("^I have a samplestep$")
      public void i_have_a_samplestep(){}
      """
    When I run Cucumber-JVM
    Then the Adapter should report the scenario "testhook"
    And the Adapter should send the step "beforeHook" with Result "SUCCESS"
    And the Adapter should send the step "afterHook" with Result "SUCCESS"
