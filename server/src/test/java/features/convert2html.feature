# encoding: utf-8
Feature: In order to avoid install a seperate tool like ELAN, it should be possible to convert the 
OutputFiles of the server to an HTML-based report.

  Scenario: Convert a folder to a single HTML report
  Given I have a folder with annotation files and videos from the annotation server
  When I convert these to HTML
  Then I should get an index.html including all stepdata and html5-compatible videos.
  
