Feature: File Validation

  Scenario: Valid CSV file with correct headers
    Given a file named "EMIS/bulk_95047_Admin_Organisation_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv"
    And it contains headers '"OrganisationGuid","CDB","OrganisationName","ODSCode","ParentOrganisationGuid","CCGOrganisationGuid","OrganisationType","OpenDate","CloseDate","MainLocationGuid","ProcessingId"'
    When I validate the file
    Then the validation should return true

  Scenario: Valid CSV file with missing required headers
    Given a file named "EMIS/bulk_95047_Admin_Organisation_20231017043213_F95EE3AF-0B9D-40EB-8B28-8E858EF0091F.csv"
    And it contains headers "OrganisationGuid, CDB, OrganisationName, ODSCode"
    When I validate the file
    Then the validation should return false

  Scenario: Invalid file
    Given a file named "EMIS/invalid_name.csv"
    When I validate the file
    Then the validation should return false
