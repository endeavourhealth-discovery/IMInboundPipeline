Feature: System service

  Scenario: Can read environment variables
    When getProperty is called with 'spring.application.name'
    Then the result should be 'CommonAPI'
