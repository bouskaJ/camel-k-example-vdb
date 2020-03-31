Feature: Camel integration with Virtual Database

  Scenario: Camel application is able to integrate with Virtual Database
    Given integration vdb is running
    Then integration vdb should print Sending new mission order to agent
    And integration vdb should print You are go for first mission at
    And integration vdb should print you are go for next mission at