# JSLT Demo Web App  

This repository contains JSLT transformation files for converting JSON data derived from raw EMIS CSV files. You can test these transformations using the [JSLT Demo Web App](https://www.garshol.priv.no/jslt-demo).  

## How to Test JSLT Transformations  

1. Copy a JSON row from one of the text files in the `TestData` folder.  
2. Paste the JSON into the multi-line text box in the JSLT Demo Web App.  
3. Copy the corresponding JSLT transformation code from the appropriate `.jslt` file.  
4. Paste the JSLT code into the "JSLT" text box in the app.  
5. Click **Run** to execute the transformation.  
6. If the transformation succeeds, the output will be displayed at the top of the web page.  

## File Structure  

- **JSLT Files**: Located in this repository, these define the transformations.  
- **Test Data (`TestData` folder)**: Contains input files in JSON format, derived from raw EMIS CSV files.  
- **CSV Files**: Contain the original EMIS data before conversion to JSON.  

## JSLT Mappings  

Use the following JSLT files to transform corresponding JSON data:  

| JSLT File                      | Input JSON File(s)                  |  
|--------------------------------|------------------------------------|  
| `EMISAppointment.jslt`        | `raw_app_1.txt`, `raw_app_2.txt`  |  
| `EMISCondition.jslt`          | `raw_problems_1.csv`, `raw_problems_2.csv` |  
| `EMISEncounter.jslt`          | `raw_enc_1.txt`, `raw_enc_2.txt`  |  
| `EMISObservation.jslt`        | `raw_obs_1.txt`, `raw_obs_2.txt`  |  
| `EMISOrganisation.jslt`       | `raw_organisation_1.txt`, `raw_organisation_2.txt` |  
| `EMISPatient.jslt`            | `raw_nor_1.txt`, `raw_nor_2.txt`  |  
| `EMISPerson.jslt`             | `raw_users_1.txt`, `raw_users_2.txt`  |  
| `EMISReferral.jslt`           | `raw_referrals_1.txt`, `raw_referrals_2.txt`  |  
| `MedicationAuthorisation.jslt` | `raw_rx_order_1.txt`, `raw_rx_order_2.txt` |  
| `MedicationStatement.jslt`     | `raw_rx_state_1.txt`, `raw_rx_state_2.txt` |  

This structure ensures a straightforward workflow for testing and refining JSLT transformations.

