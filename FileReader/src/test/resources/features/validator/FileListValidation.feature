Feature: File list validation

  Scenario: Valid list with all files included
    Given a list of "EMIS/bulk_95047_Admin_Organisation_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv, EMIS/bulk_95047_Admin_Patient_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv, EMIS/bulk_95047_CareRecord_Observation_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv"
    When I validate the list
    Then the validation should return true

  Scenario: Invalid list with missing files
    Given a list of "EMIS\invalid_name.csv, EMIS\invalid_name_2.csv"
    When I validate the list
    Then the validation should return false