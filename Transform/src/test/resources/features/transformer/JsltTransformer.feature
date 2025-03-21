Feature: Jslt Transform

  Scenario: Finds correct transform file using headers
    Given a message with header values "EMIS" "Observation"
    When I look for the correct transform file
    Then a file is found with content
    """
    import "Utilities.jslt" as utils


    {
      "@id" : formatUuid(.ObservationGuid, "@id"),
      "@type" : "Observation"
    }

    """