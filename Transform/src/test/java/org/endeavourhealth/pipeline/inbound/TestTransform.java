package org.endeavourhealth.pipeline.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class TestTransform {
  @Test
  public void testMedicationCourseAuthorisationTransform() throws JsonProcessingException {
    String csvJson = """
        {"IssueRecordGuid":"{6B7F1E22-A389-4285-9DF6-9497465E5296}","PatientGuid":"{712D3357-F8B4-48FA-A46A-FA1F0E6BCF75}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","DrugRecordGuid":"{885F2A07-B3C0-47B3-A0AB-9C4A95C446B7}","EffectiveDate":"2002-09-15","EffectiveDatePrecision":"YMD","EnteredDate":"2002-09-15","EnteredTime":"00:00:00","ClinicianUserInRoleGuid":"{812AF0AD-E71E-41E2-997A-B6787F934928}","EnteredByUserInRoleGuid":"{9F03BEF9-80B5-4080-BF73-7C457C96F70F}","CodeId":"896941000033112","Dosage":"ONE TO BE TAKEN THREE TIMES A DAY WITH FOOD","Quantity":"84.000","QuantityUnit":"tablets","ProblemObservationGuid":"{79107BE5-4D93-4142-8F6F-63D856135ADC}","CourseDurationInDays":"28","EstimatedNhsCost":"2.1500","IsConfidential":"false","EmisCode":"META1787","PatientMessage":"","ScriptPharmacyStamp":"","Compliance":"5.600000024E-1","AverageCompliance":"8.949999809E-1","IsPrescribedAsContraceptive":"false","IsPrivatelyPrescribed":"false","PharmacyMessage":"ph message","PharmacyText":"ph text","ConsultationGuid":"a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d","ExpiryDate":"2004-01-14 00:00:00","ReviewDate":"","Deleted":"false","ProcessingId":"95047"}
      """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("MedicationCourseAuthorisation");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testReferralOutboundTransform() throws JsonProcessingException {
    String csvJson = """
        {"ObservationGuid":"{76625DF3-2F3C-433F-999D-C37C7A91243F}","PatientGuid":"{FE4DE2B1-EF24-4817-A35F-54C18C5A468B}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","ReferralTargetOrganisationGuid":"{44DCEF8A-6C76-459D-A8ED-182703F3A954}","ReferralUrgency":"Routine","ReferralServiceType":"Outpatient","ReferralMode":"Written","ReferralReceivedDate":"","ReferralReceivedTime":"","ReferralEndDate":"","ReferralSourceId":"","ReferralSourceOrganisationGuid":"","ReferralUBRN":"000104458254","ReferralReasonCodeId":"","ReferringCareProfessionalStaffGroupCodeId":"","ReferralEpisodeRTTMeasurementTypeId":"","ReferralEpisodeClosureDate":"","ReferralEpisodeDischargeLetterIssuedDate":"","ReferralClosureReasonCodeId":"","TransportRequired":"None Required","ProcessingId":"95047"}
      """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("ReferralOutbound");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testConditionTransform() throws JsonProcessingException {
    String csvJson = """
      {"ObservationGuid":"{CA0A49A1-880E-4C1D-AD25-E2369BC04B59}","PatientGuid":"{94C33561-51D6-4F1A-A1ED-3AA2B5DAEE24}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","ParentProblemObservationGuid":"","Deleted":"false","Comment":"","EndDate":"2005-04-29","EndDatePrecision":"YMD","ExpectedDuration":"28","LastReviewDate":"2004-08-11","LastReviewDatePrecision":"YMD","LastReviewUserInRoleGuid":"{9F3C10EA-B781-4E9E-A0FA-AF68BCB49CF2}","ParentProblemRelationship":"","ProblemStatusDescription":"Past Problem","SignificanceDescription":"Minor Problem","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Condition");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testMedicationStatementTransform() throws JsonProcessingException {
    String csvJson = """
      {"DrugRecordGuid":"{8869A048-A858-4F63-B177-0997D89C82D0}","PatientGuid":"{B5F1ADD9-5D7E-4435-94BC-595DAD5E8904}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","EffectiveDate":"2006-06-03","EffectiveDatePrecision":"YMD","EnteredDate":"2006-06-03","EnteredTime":"00:00:00","ClinicianUserInRoleGuid":"{812AF0AD-E71E-41E2-997A-B6787F934928}","EnteredByUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","CodeId":"1222341000033110","Dosage":"2 PUFFS PRN","Quantity":"2.000","QuantityUnit":"inhaler","ProblemObservationGuid":"{C75FC301-B8D5-43B7-929C-717CF5A02F3F}","PrescriptionType":"Repeat","IsActive":"true","CancellationDate":"","NumberOfIssues":"2","NumberOfIssuesAuthorised":"","IsConfidential":"false","Deleted":"false","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("MedicationStatement");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testObservationTransform() throws JsonProcessingException {
    String csvJson = """
        {"ObservationGuid":"{744ADB77-7D8F-4B72-8D08-924ECE22F3C1}","PatientGuid":"{72C372A3-1242-4141-8050-FA8545705871}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","EffectiveDate":"2004-12-14","EffectiveDatePrecision":"YMD","EnteredDate":"2007-10-15","EnteredTime":"00:00:00","ClinicianUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","EnteredByUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","ParentObservationGuid":"","CodeId":"406169014","ProblemGuid":"","ConsultationGuid":"{71F89159-BC5B-4CA2-A406-680F3D8538E9}","AssociatedText":"","Value":"","NumericUnit":"","ObservationType":"Observation","NumericRangeLow":"","NumericRangeHigh":"","DocumentGuid":"","Qualifiers":"","Abnormal":"false","AbnormalReason":"","Episode":"None","Deleted":"false","IsConfidential":"false","NumericOperator":"","ProcessingId":"95047"}
    """;
    csvJson = """
      {"ObservationGuid":"{E3B2193E-3C48-4635-98B1-5241D0D40078}","PatientGuid":"{9DAD5344-DFC9-4EC1-B383-035A64EAAF21}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","EffectiveDate":"2007-02-18","EffectiveDatePrecision":"YMD","EnteredDate":"2008-02-06","EnteredTime":"00:00:00","ClinicianUserInRoleGuid":"{812AF0AD-E71E-41E2-997A-B6787F934928}","EnteredByUserInRoleGuid":"{812AF0AD-E71E-41E2-997A-B6787F934928}","ParentObservationGuid":"{4BA2AC7A-45B2-4372-8030-4A32B4B0D720}","CodeId":"238451000006117","ProblemGuid":"","ConsultationGuid":"","AssociatedText":"","Value":"60.000","NumericUnit":"%","ObservationType":"Value","NumericRangeLow":"40.000","NumericRangeHigh":"75.000","DocumentGuid":"","Qualifiers":"","Abnormal":"false","AbnormalReason":"","Episode":"None","Deleted":"false","IsConfidential":"false","NumericOperator":"","ProcessingId":"95047"}
      """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Observation");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testRegistrationTransform() throws JsonProcessingException {
    String csvJson = """
        {"PatientGuid":"{3C94958A-3CA2-4405-A3C1-7C3C401E43AA}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","HistoryDate":"2003-12-18","HistoryTime":"00:00:00","StatusDescription":"Registered","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("GpregistrationAdministrationStatus");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testMobileTransform() throws JsonProcessingException {
    String csvJson = """
        {"PatientGuid":"{F8E1A85B-0B39-4714-B414-69A4EE9E1D73}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","UsualGpUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","Sex":"M","DateOfBirth":"1956-02-18","DateOfDeath":"","Title":"Mr","GivenName":"Charles","MiddleNames":"","Surname":"Hayward","DateOfRegistration":"2008-02-21","NhsNumber":"6035700097","PatientNumber":"3553","PatientTypeDescription":"Regular","DummyType":"false","HouseNameFlatNumber":"","NumberAndStreet":"23 Victoria Road","Village":"Crow Nest","Town":"Chapel Allerton","County":"West Yorkshire","Postcode":"WF8","ResidentialInstituteCode":"","NHSNumberStatus":"","CarerName":"","CarerRelation":"","PersonGuid":"{DEE932F4-1462-4BD0-8C27-539E956D4A08}","DateOfDeactivation":"","Deleted":"false","SpineSensitive":"true","IsConfidential":"false","EmailAddress":"CASPER77@email.cz","HomePhone":"","MobilePhone":"09433097322","ExternalUsualGPGuid":"","ExternalUsualGP":"","ExternalUsualGPOrganisation":"","ContactComments":"","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("MobileTelephoneNumber");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testHomeTransform() throws JsonProcessingException {
    String csvJson = """
        {"PatientGuid":"{F8E1A85B-0B39-4714-B414-69A4EE9E1D73}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","UsualGpUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","Sex":"M","DateOfBirth":"1956-02-18","DateOfDeath":"","Title":"Mr","GivenName":"Charles","MiddleNames":"","Surname":"Hayward","DateOfRegistration":"2008-02-21","NhsNumber":"6035700097","PatientNumber":"3553","PatientTypeDescription":"Regular","DummyType":"false","HouseNameFlatNumber":"","NumberAndStreet":"23 Victoria Road","Village":"Crow Nest","Town":"Chapel Allerton","County":"West Yorkshire","Postcode":"WF8","ResidentialInstituteCode":"","NHSNumberStatus":"","CarerName":"","CarerRelation":"","PersonGuid":"{DEE932F4-1462-4BD0-8C27-539E956D4A08}","DateOfDeactivation":"","Deleted":"false","SpineSensitive":"true","IsConfidential":"false","EmailAddress":"CASPER77@email.cz","HomePhone":"","MobilePhone":"09433097322","ExternalUsualGPGuid":"","ExternalUsualGP":"","ExternalUsualGPOrganisation":"","ContactComments":"","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("HomeTelephoneNumber");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testEmailTransform() throws JsonProcessingException {
    String csvJson = """
        {"PatientGuid":"{F8E1A85B-0B39-4714-B414-69A4EE9E1D73}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","UsualGpUserInRoleGuid":"{3125F0C4-C773-48A6-BF33-10A5106F6448}","Sex":"M","DateOfBirth":"1956-02-18","DateOfDeath":"","Title":"Mr","GivenName":"Charles","MiddleNames":"","Surname":"Hayward","DateOfRegistration":"2008-02-21","NhsNumber":"6035700097","PatientNumber":"3553","PatientTypeDescription":"Regular","DummyType":"false","HouseNameFlatNumber":"","NumberAndStreet":"23 Victoria Road","Village":"Crow Nest","Town":"Chapel Allerton","County":"West Yorkshire","Postcode":"WF8","ResidentialInstituteCode":"","NHSNumberStatus":"","CarerName":"","CarerRelation":"","PersonGuid":"{DEE932F4-1462-4BD0-8C27-539E956D4A08}","DateOfDeactivation":"","Deleted":"false","SpineSensitive":"true","IsConfidential":"false","EmailAddress":"CASPER77@email.cz","HomePhone":"","MobilePhone":"09433097322","ExternalUsualGPGuid":"","ExternalUsualGP":"","ExternalUsualGPOrganisation":"","ContactComments":"","ProcessingId":"95047"}
    """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Email");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testLocationTransform() throws JsonProcessingException {
    String csvJson = """
        {"LocationGuid":"{37088C97-82FC-4B3E-BDBC-28F2C03B6CB8}","LocationName":"Master Practice 1","LocationTypeDescription":"Main Surgery","ParentLocationGuid":"{6A3E8B2D-1F47-4C9A-B0E2-7D5F3C1A4E6B}","OpenDate":"2009-02-16","CloseDate":"","MainContactName":"","FaxNumber":"","EmailAddress":"PSIMON1@SUPANET.COM","PhoneNumber":"01133800000","HouseNameFlatNumber":"Fulford Grange, Micklefield Lane","NumberAndStreet":"Rawdon","Village":"Rawdon","Town":"Leeds","County":"Yorkshire","Postcode":"LS196BA","Deleted":"false","ProcessingId":"95047"}
      """;
    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Location");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testPersonInRoleTransform() throws JsonProcessingException {
    String csvJson = """
      {"UserInRoleGuid":"{51492C43-36B1-46D2-A3B7-2AE85AAF1CA5}","OrganisationGuid":"{157A76D0-E852-4F5E-9644-896FC8E8DB2E}","Title":"Chiropody","GivenName":"","Surname":"Department","JobCategoryCode":"R0050","JobCategoryName":"Consultant","ContractStartDate":"1899-12-31","ContractEndDate":"","RegistrationNumber":"","ProcessingId":"95047"}
      """;
    csvJson = """
      {"UserInRoleGuid":"{7ED610B5-B2C6-402B-B6DD-549D74B2862D}","OrganisationGuid":"{65533888-C283-4361-A44D-C5B69CC73A4A}","Title":"Mr","GivenName":" K  Davey","Surname":"Simon","JobCategoryCode":"R0050","JobCategoryName":"Consultant","ContractStartDate":"1899-01-01","ContractEndDate":"","RegistrationNumber":"","ProcessingId":"95047"}
    """;

    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("PersonInRole");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testOrganisationTransform() throws JsonProcessingException {
    String csvJson = """
      {"OrganisationGuid":"{869F1604-C270-443E-8F21-5A17B553FA2F}","CDB":"","OrganisationName":"Mr Th Dunningham","ODSCode":"A1234567890","ParentOrganisationGuid":"{77949C1B-6113-4914-8F15-A963941520C5}","CCGOrganisationGuid":"","OrganisationType":"","OpenDate":"2010-06-07","CloseDate":"2010-06-08","MainLocationGuid":"2E1C9F4A-8B3D-45C7-92E0-D6A4F1B8C3E7 \s","ProcessingId":"95047"}
      """;

    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Organisation");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
  @Test
  public void testEncounterTransform() throws JsonProcessingException {
    String csvJson = """
      {"ConsultationGuid":"{CEC1D527-2302-43C0-859D-A831EBDBA7FB}","PatientGuid":"{1A0F4F5A-2423-4AE1-9EBC-87EE176FB267}","OrganisationGuid":"{CC6B381E-10E8-492E-A3A2-E4510816B1BE}","EffectiveDate":"2005-12-28","EffectiveDatePrecision":"YMD","EnteredDate":"2008-09-05","EnteredTime":"00:00:00","ClinicianUserInRoleGuid":"{A8FEC350-25CF-4D63-88D7-A3FC6BB98CC6}","EnteredByUserInRoleGuid":"{A8FEC350-25CF-4D63-88D7-A3FC6BB98CC6}","AppointmentSlotGuid":"","ConsultationSourceTerm":"General Practice Surgery","ConsultationSourceCodeId":"1672871000006114","Complete":"true","ConsultationType":"Consultation","Deleted":"false","IsConfidential":"false","ProcessingId":"95047"}
      """;

    ObjectMapper om = new ObjectMapper();
    JsonNode data = om.readTree(csvJson);
    System.out.println("Input:");
    System.out.println(data.toPrettyString());

    Transformer transformer = new Transformer("EMIS");
    transformer.loadTransformation("Encounter");
    JsonNode result = transformer.transform(data);
    System.out.println("Output:");
    System.out.println(result.toPrettyString());
  }
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
        "MobilePhone":"079176565414",
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
