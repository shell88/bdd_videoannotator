#encoding: utf-8

Feature: SampleSearch on Google

Scenario: GoogleSearch
Given i open Google
When i search for "Behaviour Driven Development",
And i wait for 2 seconds,
Then i want to see a link to the "bdd_videoannotator_project" on github.