#encoding: utf-8

Feature: SampleSearch on Google

Scenario: GoogleSearch
Given I am on "http://www.google.com"
When I fill in "q" with "Behavior Driven Development"
And i wait for 2 seconds
Then I should see "bdd_videoannotator github"