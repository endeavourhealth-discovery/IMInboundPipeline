package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class TestTransform {
  @Test
  public void testTransform() throws JsonProcessingException {
    String csvJson = """
      {
        "PatientGuid":"{083A7431-E2EF-4FF2-8569-43A26CD89CA7}",
        "OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}",
        "UsualGpUserInRoleGuid":"{812AF0AD-E71E-41E2-997A-B6787F934928}",
        "Sex":"M",
        "DateOfBirth":"1950-01-29",
        "DateOfDeath":"",
        "Title": "Mr",
        "GivenName":"Nicholas",
        "MiddleNames":"",
        "Surname":"Anderson",
        "DateOfRegistration":"1985-03-04",
        "NhsNumber":"9219976933",
        "PatientNumber":"215",
        "PatientTypeDescription":"Regular",
        "DummyType":"false",
        "HouseNameFlatNumber":"",
        "NumberAndStreet":"3 Queensway",
        "Village":"Moorhead",
        "Town":"Boston Spa",
        "County":"West Yorkshire",
        "Postcode":"WF11",
        "ResidentialInstituteCode":"",
        "NHSNumberStatus":"",
        "CarerName":"",
        "CarerRelation":"",
        "PersonGuid":"{B35D8260-069B-4693-AB2E-C993B7A38C72}",
        "DateOfDeactivation":"",
        "Deleted":"false",
        "SpineSensitive":"true",
        "IsConfidential":"false",
        "EmailAddress":"",
        "HomePhone":"03409636309",
        "MobilePhone":"",
        "ExternalUsualGPGuid":"",
        "ExternalUsualGP":"",
        "ExternalUsualGPOrganisation":"",
        "ContactComments":"",
        "ProcessingId":"95047"      
      }
      """;

    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Patient");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
}
