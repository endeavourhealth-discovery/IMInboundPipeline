Feature: Transformer Date Parsing

  Scenario: formatDate with Date and Time
    Given a date of "2010-06-07 01:02:03"
    And a format of "yyyy-MM-dd HH:mm:ss"
    When I call formatDate
    Then the date returned should be "2010-06-07T00:00:00.000Z"

  Scenario: formatDate with Date and NO Time
    Given a date of "2010-06-07"
    And a format of "yyyy-MM-dd"
    When I call formatDate
    Then the date returned should be "2010-06-07T00:00:00.000Z"

  Scenario: formatDateTime with Date and Time
    Given a date of "2010-06-07 01:02:03"
    And a format of "yyyy-MM-dd HH:mm:ss"
    When I call formatDateTime
    Then the date returned should be "2010-06-07T01:02:03.000Z"