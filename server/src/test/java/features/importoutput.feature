# encoding: utf-8
Feature: In order persist the steps from the server, an exporter for EAF should be provided. Also a parser
to reimport the steps from the file should be available for testing purposes.

  Scenario: Export a few steps to a file and reimport it
  Given I have an empty outputFile,
  When I export a feature with one scenario and a few steps using the EAF Exporter,
  When i reimport it the previous exported data should be contained.