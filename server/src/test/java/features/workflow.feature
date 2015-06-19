# encoding: utf-8
Feature: To support a wide range of BDD-frameworks a platform independent server shoud provide handy methods
  to collect step annotations.

  Scenario: Add Steps to buffer (Cucumber-JVM)
    Given I start a Scenario with description text "HelloScenario"
    When I add a Step "hello step",
    And I add a Step "hello step2",
    And I add the result "SUCCESS",
    And I add the result "SUCCESS",
    And I stop the Scenario
    Then I should get a video with file named "HelloScenario"
    And I should get an annotation file named "HelloScenario" containing the added steps

  Scenario: AddResultsDirectly
    Given I start a Scenario with description text "HelloScenario"
    When I add a Step "hello step" with result "SUCCESS" after 3 Seconds,
    When I add a Step "hello step2" with result "ERROR" after 2 Seconds,
    And I stop the Scenario
    Then I should get a video with file named "HelloScenario"
    And I should get an annotation file named "HelloScenario" containing the added steps

  Scenario: Use of datatables
    Given I start a Scenario with description text "HelloScenario"
    When I add a Step "hello step" with Result "SUCCESS" and following Sample-Data:
      | Number | String   |
      | 1      | "test"   |
      | 2.34   | "^regex" |
      | 2,34   | null     |
    And I stop the Scenario
    Then I should get an annotation file named "HelloScenario" containing the added steps
