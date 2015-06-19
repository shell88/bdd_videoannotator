# encoding: utf-8
Feature: In order to have usable testlogs the server has to be reliable.

  Scenario Outline: Synchronization between text and video
    Given I start a Scenario with description text "WaitScenario"
    When I add a Step "waitStep1",
    And I add a Step "waitStep2",
    When I add a Result after <waitsec1>
    And I add a Result after <waitsec2>
    And I stop the Scenario
    Then I should get a video and an annotation file named "WaitScenario"
    And the video should have a length of <totalsec>
    And step 1 should be annotated with a duration of <waitsec1>
    And step 2 should be annotated with a duration of <waitsec2>
    And there must be no temporal intersection between the time slots of the steps
    And the video should references the annotation file
    And the annotationfile should contain the correct SHA-1 checksum of the videofile

    Examples: 
      | waitsec1 | waitsec2 | totalsec |
      | 1        | 2        | 3        |
      | 0        | 1        | 1        |
      | 2        | 4        | 6        |
