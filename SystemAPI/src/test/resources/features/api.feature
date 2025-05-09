Feature: Received file is added to queue

  Scenario: User supplied a file to be queued
    When User supplies a file to the import api
    When their credentials are validated
    When their organisation is validated
    When the file type is validated
    When the file is checked for corruption
    Then the user is authorized
    Then the organisation is valid
    Then the file type is valid
    Then the file is not corrupted
    Then file is passed to the queue

  Scenario: User supplied an invalid file to the import api
    When User supplies a file to the import api
    Then an invalid file error is thrown

  Scenario: User checks status in queue (position in queue)
    When User supplies an id to the status api
    When the item is found in the queue
    Then item position in queue is returned

  Scenario: User checks status in queue (running)
    When User supplies an id to the status api
    When the item is found to be running
    Then item status is returned

  Scenario: User checks status in queue (errored)
    When User supplies an id to the status api
    When the item has errored
    Then item status is returned

  Scenario: User checks status in queue (completed)
    When User supplies an id to the status api
    When the item has completed
    Then item status is returned

  Scenario: User pauses item in queue (success)
    When User supplies id to pause api
    When the item is found in queue
    Then item is moved to pause storage
    Then return success

  Scenario: User pauses item in queue (failure)
    When User supplies id to pause api
    When the item is not found in queue
    Then an error is thrown
    Then return error

  Scenario: User cancels item in queue (success)
    When User supplies id to cancel api
    When item is found in queue
    Then item is removed from queue
    Then return success

  Scenario: User cancels item in queue (failure)
    When User supplies id to cancel api
    When item is not found in queue
    Then an error is thrown
    Then return error
