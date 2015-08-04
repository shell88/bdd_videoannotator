#encoding: utf-8

Feature: To provide an easy to use view the server must be able to export an HTML based report.

Scenario: Sample
Given I have a server that exports to HTML
Given I start a Scenario with description text "WaitScenario"
When I add a Step "waitStep1",
And I add a Result after 10
And I stop the Scenario
Then I should get a HTML report