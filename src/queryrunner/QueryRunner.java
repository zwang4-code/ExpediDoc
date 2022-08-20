/**
 * Group 6 - Milestone 3
 * Date: May 31, 2021
 * Students: Zi Wang, Dominic Burgi, Luoshan Zhang
 *
 * @author Professor M. Mckee, Zi Wang, Dominic Burgi, Luoshan Zhang
 */

package queryrunner;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    public QueryRunner() {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error = "";

        // Queries
        aggregateProviderRatings();
        aggregateFacilityRatings();
        getInsuranceCost();
        getAvailableTimeSlots();
        filterProviderByLanguage();
        getAllLocations();
        getPatientTotalAppts();
        showProviderProfile();
        showProviderAppts();
        getProviderAvailability();
        showWhoHasSymptom();
        showSpecificLocations();
        pickAffordableInsurance();
        showCancelledAppts();

        // Stored procedure
        filterProvider();
        findNearbyFacilities();

        // Action query
        updatePatientInfo();
    }

    /**
     * Query 1: Aggregate Provider Ratings
     * Shows the average ratings of all the providers within a user-specified specialty
     * in descending order, along with their names and credentials.
     */
    private void aggregateProviderRatings() {
        m_queryArray.add(new QueryData(
                "select specialty_type as Specialty, provider_lname as \"Last " +
                        "Name\", provider_fname as \"First Name\", provider_credential" +
                        " \"Credential\", avg(patient_appt_rating) as \"Rating\"\n" +
                        "from Provider P, ApptTimeSlots S, Appointments A, " +
                        "SpecialtyType ST\nwhere P.provider_id = S.provider_id\nand " +
                        "S.appt_time_id = A.appt_time_id\n and P.specialty_type_id = " +
                        "ST.specialty_type_id\nand ST.specialty_type_id = ?\nand " +
                        "A.appt_status_id = 3\ngroup by P.provider_id\norder by " +
                        "Rating desc, provider_lname, provider_fname",
                new String[]{"Specialty Type"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 2: Aggregate Facility Rating
     * Shows the average rating of each healthcare facility ranked in descending order.
     */
    private void aggregateFacilityRatings() {
        m_queryArray.add(new QueryData(
                "select location_name \"Location Name\", avg(patient_appt_rating)" +
                        " as Rating\nfrom Location L, Provider P, ApptTimeSlots S, " +
                        "Appointments A\nwhere L.location_id = P.location_id \nand " +
                        "P.provider_id = S.provider_id\nand S.appt_time_id = " +
                        "A.appt_time_id\nand A.appt_status_id = 3\ngroup by " +
                        "L.location_id\norder by Rating desc",
                null, null, false, false));
    }

    /**
     * Query 3: Get Insurance Cost
     * Shows the copay and deductible amounts for a user-specified insurance.
     */
    private void getInsuranceCost() {
        m_queryArray.add(new QueryData(
                "select insurance_type as Insurance, concat(\"$\", copay) as " +
                        "Copay, concat(\"$\", deductible) as Deductible\nfrom " +
                        "InsuranceType\nwhere insurance_type_id = ?\norder by copay, " +
                        "deductible",
                new String[]{"Insurance Type"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 4: Get Available Time Slots
     * Shows all the available appointment time slots within a user-specified date range.
     */
    private void getAvailableTimeSlots() {
        m_queryArray.add(new QueryData(
                "select appt_date as \"Date\", start_at \"Begins\", end_at " +
                        "\"Ends\"\nfrom ApptTimeSlots ATS, TimeSlot TS\nwhere " +
                        "ATS.time_slot_id = TS.time_slot_id\nand ATS.time_slot_id not " +
                        "in (select appt_time_id from Appointments)\nand appt_date " +
                        "between ? and ?\norder by appt_date, start_at",
                new String[]{"Appt Start Date", "Appt End Date"}, new boolean[]{false, false}, false, true));
    }

    /**
     * Query 5: Filter Provider by Language
     * Shows the names of the all the providers who can speak a user-specified language.
     */
    private void filterProviderByLanguage() {
        m_queryArray.add(new QueryData(
                "select provider_lname \"Last Name\", provider_fname \"First " +
                        "Name\", language_type \"Language\"\nfrom Provider P, " +
                        "ProviderLanguages PL, LanguageType L\nwhere " +
                        "P.provider_id = PL.provider_id\nand " +
                        "PL.language_type_id = L.language_type_id \nand " +
                        "L.language_type_id = ?",
                new String[]{"Language Type"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 6: Get All Locations
     * Shows the names, addresses, zip codes, and states of all the healthcare facilities
     * within a user-specified organization.
     */
    private void getAllLocations() {
        m_queryArray.add(new QueryData(
                "select organization_name \"Organization\", location_name " +
                        "\"Location\", street_address \"Address\", location_zip_code " +
                        "\"Zip Code\", state \"State\"\nfrom Location L, Organization " +
                        "O\nwhere L.organization_id = O.organization_id\nand " +
                        "O.organization_id = ?",
                new String[]{"Organization"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 7: Get Patient Total Appointments
     * Shows the total number of appointments created by each patient ordered in
     * descending order.
     */
    private void getPatientTotalAppts() {
        m_queryArray.add(new QueryData(
                "select patient_lname \"Last Name\", patient_fname \"First " +
                        "Name\", count(appt_time_id) as 'Total_Appointments_Created'\n" +
                        "from Appointments join Patient using (patient_id)\ngroup " +
                        "by patient_id\norder by Total_Appointments_Created desc",
                null, null, false, false));
    }

    /**
     * Query 8: Show Provider Profile
     * Shows the name, credential gender, biography, photo, practice location, and
     * specialty of a user-specified provider.
     */
    private void showProviderProfile() {
        m_queryArray.add(new QueryData(
                "select provider_fname \"First Name\", provider_lname \"Last " +
                        "Name\", provider_credential \"Credential\",\nprovider_gender " +
                        "\"Gender\", provider_bio \"Bio\", provider_picture \"Photo\"," +
                        " location_name \"Facility\", specialty_type \"Specialty\"\n" +
                        "from Provider P, Location L, Specialtytype S\nwhere " +
                        "P.location_id = L.location_id\nand " +
                        "P.specialty_type_id = S.specialty_type_id\nand " +
                        "provider_id = ?",
                new String[]{"Provider"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 9: Show Provider Appointments
     * Shows the dates, time slots, patient names, patient DOB, and patient's chief
     * complaints of all the appointments scheduled with a user-specified provider.
     */
    // DO we want to specify a date range to be more realistic?
    private void showProviderAppts() {
        m_queryArray.add(new QueryData(
                "select appt_date \"Date\", start_at \"Begins\", end_at \"Ends" +
                        "\",\n  patient_lname \"Patient Last Name\", patient_fname " +
                        "\"Patient First Name\",\n patient_dob \"Patient D.O.B.\", " +
                        "chief_complaint \"Patient Complaint\"\nfrom Patient P, " +
                        "Appointments A, ApptTimeSlots ATS, TimeSlot TS \nwhere " +
                        "P.patient_id = A.patient_id \nand A.appt_time_id = " +
                        "ATS.appt_time_id \nand ATS.time_slot_id = TS.time_slot_id \n" +
                        "and ATS.provider_id = ?\norder by appt_date, start_at",
                new String[]{"Provider"}, new boolean[]{false}, false, true));
    }

    /**
     * Query 10: Get Provider Availability
     * Shows a specific provider's name and available appointment times within a
     * user-specified date range.
     */
    private void getProviderAvailability() {
        m_queryArray.add(new QueryData(
                "select provider_lname \"Provider\", appt_date \"Date\", " +
                        "start_at \"Begins\", end_at \"Ends\"\nfrom ApptTimeSlots " +
                        "ATS, TimeSlot TS, Provider P\nwhere P.provider_id = ?\n" +
                        "and appt_date between ? and ?\nand ATS.time_slot_id = " +
                        "TS.time_slot_id \nand appt_time_id not in (select " +
                        "appt_time_id from Appointments)\norder by appt_date, start_at",
                new String[]{"Provider", "Appt Start Date", "Appt End Date"},
                new boolean[]{false, false, false}, false, true));
    }

    /**
     * Query 11: Show Who Has Symptom
     * Shows the names, DOB, and chief complaints of all the patients whose chief
     * complaints contain a user-specific word.
     */
    private void showWhoHasSymptom() {
        m_queryArray.add(new QueryData(
                "select patient_lname \"Patient Last Name\", patient_fname " +
                        "\"Patient First Name\",\n  patient_dob \"Patient D.O.B.\", " +
                        "chief_complaint \"Chief Complaint\"\nfrom Patient P, " +
                        "Appointments A\nwhere P.patient_id = A.patient_id\nand " +
                        "(chief_complaint like ?)" +
                        "order by patient_lname, patient_fname",
                new String[]{"Chief Complaint"}, new boolean[]{true}, false, true));
    }

    /**
     * Query 12: Show Specific Locations
     * Shows the names, addresses, zip codes, states, and organizations of all the
     * healthcare facilities with location names containing a user-specified word
     * within a user-specified state.
     */
    private void showSpecificLocations() {
        m_queryArray.add(new QueryData(
                "select location_name \"Location\", street_address \"Address\"," +
                        " location_zip_code \"Zip Code\", state \"State\", " +
                        "organization_name \"Organization\"\nfrom location L, " +
                        "organization O\nwhere L.organization_id = O.organization_id\n" +
                        "and (L.location_name like ?)\nand state = ?",
                new String[]{"Location Name", "State"},
                new boolean[]{true, false},
                false, true));
    }

    /**
     * Query 13: Pick Affordable Insurance
     * Shows the names of all the insurances with the copay and deductible within a
     * user-specified range and shows the names of the providers who accept those
     * insurances.
     */
    private void pickAffordableInsurance() {
        m_queryArray.add(new QueryData(
                "select i.insurance_type \"Insurance\", p.provider_lname " +
                        "\"Provider Last Name\",\n  p.provider_fname \"Provider First " +
                        "Name\", p.provider_gender \"Provider Gender\",\n  concat(\"$" +
                        "\", i.copay) as \"Copay\", concat(\"$\", i.deductible) as " +
                        "\"Deductible\"\nfrom InsuranceType i, Provider p, " +
                        "ProviderInsurances p_i \nwhere i.insurance_type_id = " +
                        "p_i.insurance_type_id \nand p.provider_id = p_i.provider_id \n" +
                        "and i.copay between ? and ? \nand i.deductible between ? " +
                        "and ?\norder by i.insurance_type;\n",
                new String[]{"Copay Min", "Copay Max", "Deductible Min", "Deductible Max"},
                new boolean[]{false, false, false, false},
                false, true));
    }

    /**
     * Query 14: Show Cancelled Appointments
     * Shows the names, phone numbers, and emails of all the patients who have cancelled
     * their appointments at a user-specified healthcare facility and the names of the
     * providers those appointments were originally scheduled with.
     */
    private void showCancelledAppts() {
        // queryArray 14        ????? To verify: should there be '' around status_description '?'??
        m_queryArray.add(new QueryData(
                "select pa.patient_lname \"Patient Last Name\", " +
                        "pa.patient_fname \"Patient First Name\", pa.patient_phone " +
                        "\"Patient Phone\",\n pa.patient_email \"Patient Email\", " +
                        "pro.provider_lname \"Provider Last Name\", pro.provider_fname " +
                        "\"Provider First Name\"\nfrom patient pa, provider pro, " +
                        "location l, appointments app, appttimeslots ats, " +
                        "apptstatus as a_s\nwhere pa.patient_id = app.patient_id\n" +
                        "and ats.provider_id = pro.provider_id\nand app.appt_status_id " +
                        "= a_s.appt_status_id\nand app.appt_time_id = " +
                        "ats.appt_time_id\nand pro.location_id = l.location_id\n" +
                        "and a_s.appt_status_description = 'cancel'\nand " +
                        "l.location_id = ?\norder by pa.patient_lname, pa.patient_fname",
                new String[]{"Location"}, new boolean[]{false}, false, true));
    }

    /***
     * Query 15: Stored Procedure 1 - Filter Provider
     * Show the names, credentials, and genders, and practice locations, addresses,
     * zip codes, and states of all the providers who practice within a user-specified
     * specialty, speak a user-specified language, and accept a user-specified type of
     * insurance.
     */
    private void filterProvider() {
        m_queryArray.add(new QueryData(
                "select provider_lname \"Last Name\", provider_fname \"First " +
                        "Name\", provider_credential \"Credential\", provider_gender " +
                        "\"Gender\",\nlocation_name \"Location\", street_address " +
                        "\"Address\", location_zip_code \"Zip Code\", state \"State\"," +
                        " specialty_type \"Specialty\",\nlanguage_type \"Language\", " +
                        "insurance_type \"Insurance\"\nfrom Provider P, SpecialtyType " +
                        "ST, Location L, ProviderInsurances PIN, InsuranceType IT, " +
                        "ProviderLanguages PL, LanguageType LT\n\nwhere P.location_id " +
                        "= L.location_id \nand P.specialty_type_id = " +
                        "ST.specialty_type_id \nand P.provider_id = PL.provider_id \n" +
                        "and PL.language_type_id = LT.language_type_id \nand " +
                        "P.provider_id = PIN.provider_id \nand PIN.insurance_type_id =" +
                        " IT.insurance_type_id \n" +
                        "\nand P.specialty_type_id = ? \nand PL.language_type_id = ?" +
                        "\nand IT.insurance_type_id = ?\n\ngroup by P.provider_id",
                new String[]{"Specialty", "Language", "Insurance"},
                new boolean[]{false, false, false}, false, true));
    }

    /***
     * Query 16: Stored Procedure 2 - Find Nearby Locations
     * Shows the names of all the healthcare facilities within a user-specified distance
     * from a user-specified location.
     */
    private void findNearbyFacilities() {
        m_queryArray.add(new QueryData("SELECT location_name as Facility, " +
                "ROUND(SQRT(POWER((location_lat * 69 - CONVERT(?, FLOAT) * 69), 2) +\n" +
                "POWER((location_lon * 54 - CONVERT (?, FLOAT) * 54), 2)), 2) as " +
                "Distance_Miles\nFROM location\nHAVING Distance_Miles <= " +
                "CONVERT(?, FLOAT)\nORDER BY Distance_Miles asc",
                new String[]{"Patient Latitude", "Patient Longitude", "Mile Limit"},
                new boolean[]{false, false, false}, false, true));
    }

    /***
     * Query 17: Action Query - Update Patient Info
     * Updates the last name and phone number of a user-specified patient.
     */
    private void updatePatientInfo() {
        m_queryArray.add(new QueryData("UPDATE patient SET patient_lname = ?, " +
                "patient_phone = ?\nwhere patient_id = ?",
                new String[]{"Patient Lname", "Patient Phone", "Patient ID"},
                new boolean[] {false, false, false}, true, true));
    }

    /**
     * Gets the total number of queries.
     * @return The total number of queries.
     */
    public int GetTotalQueries() {
        return m_queryArray.size();
    }

    /**
     * Gets the parameter amount for a query.
     *
     * @param queryChoice the query choice
     * @return the index of the query in the array
     */
    public int GetParameterAmtForQuery(int queryChoice) {
        QueryData e = m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }

    /**
     * Gets the parameter text.
     *
     * @param queryChoice the query choice
     * @param parmnum     the parmnum
     * @return the string
     */
    public String GetParamText(int queryChoice, int parmnum) {
        QueryData e = m_queryArray.get(queryChoice);
        return e.GetParamText(parmnum);
    }


    /**
     * Function will return how many rows were updated as a result
     * of the update query
     *
     * @return Returns how many rows were updated
     */

    public int GetUpdateAmount() {
        return m_updateAmount;
    }

    /**
     * Function will return ALL of the Column Headers from the query
     *
     * @return Returns array of column headers
     */
    public String[] GetQueryHeaders() {
        return m_jdbcData.GetHeaders();
    }

    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     *
     * @return multi-dimensional array of String data based on the resultset
     * from the query
     */
    public String[][] GetQueryData() {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication() {
        return m_projectTeamApplication;
    }

    public boolean isActionQuery(int queryChoice) {
        QueryData e = m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }

    /**
     * Checks if is parameter query.
     *
     * @param queryChoice the query choice
     * @return true, if is parameter query
     */
    public boolean isParameterQuery(int queryChoice) {
        QueryData e = m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }

    /**
     * Execute query.
     *
     * @param queryChoice the query choice
     * @param parms       the parms
     * @return true, if successful
     */
    public boolean ExecuteQuery(int queryChoice, String[] parms) {
        boolean bOK = true;
        QueryData e = m_queryArray.get(queryChoice);
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }

    /**
     * Execute update.
     *
     * @param queryChoice the query choice
     * @param parms       the parms
     * @return true, if successful
     */
    public boolean ExecuteUpdate(int queryChoice, String[] parms) {
        boolean bOK = true;
        QueryData e = m_queryArray.get(queryChoice);
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }

    /**
     * Connect.
     *
     * @param szHost     the sz host
     * @param szUser     the sz user
     * @param szPass     the sz pass
     * @param szDatabase the sz database
     * @return true, if successful
     */
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase) {
        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return bConnect;
    }

    /**
     * Disconnect.
     *
     * @return true, if successful
     */
    public boolean Disconnect() {
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }

    /**
     * Gets the error.
     *
     * @return the string
     */
    public String GetError() {
        return m_error;
    }

    /**
     * The jdbc data.
     */
    private QueryJDBC m_jdbcData;

    /**
     * The error.
     */
    private String m_error;


    private String m_projectTeamApplication;

    /**
     * The query array.
     */
    private ArrayList<QueryData> m_queryArray;

    /**
     * The update amount.
     */
    private int m_updateAmount;

    /**
     * The getQueryTitle method returns the title of the query.
     * @param queryNumber The query choice.
     * @return The title of the query.
     */
    public String getQueryTitle(int queryNumber) {
        switch (queryNumber + 1) {
            case 1:   return "Aggregate Provider Ratings";
            case 2:   return "Aggregate Facility Ratings";
            case 3:   return "Get Insurance Cost";
            case 4:   return "Get Available Time Slots";
            case 5:   return "Filter Provider by Language";
            case 6:   return "Get All Locations";
            case 7:   return "Get Patient's Total Appointments";
            case 8:   return "Show Provider Profile";
            case 9:   return "Show Provider Appointments";
            case 10:  return "Get Provider Availability";
            case 11:  return "Show Who Has Symptom";
            case 12:  return "Show Specific Locations";
            case 13:  return "Pick Affordable Insurance";
            case 14:  return "Show Cancelled Appointments";
            case 15:  return "Stored Procedure - Filter Provider";
            case 16:  return "Stored Procedure - Find Nearby Facilities";
            case 17:  return "Action Query - Update patient info";
        }
        return "";
    }

    /**
     * The getParameterOptions method returns the parameter choices.
     * @param paramText The selected parameter.
     * @return The parameter choices.
     */
    public static String getParameterOptions(String paramText) {
        StringBuilder paramOptions = new StringBuilder();

        if (paramText != null) {
            switch (paramText) {
                case "Specialty Type":
                    paramOptions.append("3 - Cardiologist\n");
                    paramOptions.append("8 - Family Physicians\n");
                    paramOptions.append("10 - Geriatric Medicine Specialists\n");
                    return paramOptions.toString();
                case "Insurance Type":
                    paramOptions.append("1 - Borer, Rau and Miller\n");
                    paramOptions.append("2 - Blick-Sauer\n");
                    paramOptions.append("47 - Frami Group\n");
                    return paramOptions.toString();
                case "Appt Start Date":
                    paramOptions.append("20210311\n");
                    paramOptions.append("20210312\n");
                    paramOptions.append("20210313\n");
                    return paramOptions.toString();
                case "Appt End Date":
                    paramOptions.append("20210517\n");
                    paramOptions.append("20210518\n");
                    paramOptions.append("20210519\n");
                    return paramOptions.toString();
                case "Language Type":
                    paramOptions.append("1 - Spanish\n");
                    paramOptions.append("2 - English\n");
                    paramOptions.append("8 - Korean\n");
                    return paramOptions.toString();
                case "Organization":
                    paramOptions.append("1 - MultiCare\n");
                    paramOptions.append("2 - Swedish\n");
                    paramOptions.append("5 - Essent Healthcare\n");
                    return paramOptions.toString();
                case "Provider":
                    paramOptions.append("1 - Vladamir Wenderott\n");
                    paramOptions.append("2 - Storm Astling\n");
                    paramOptions.append("3 - Naomi Gaylard\n");

                    return paramOptions.toString();
                case "Chief Complaint":
                    paramOptions.append("Abdominal\n");
                    paramOptions.append("Head\n");
                    paramOptions.append("Foot\n");
                    return paramOptions.toString();
                case "Location Name":
                    paramOptions.append("Natural\n");
                    paramOptions.append("Cardinal\n");
                    paramOptions.append("American\n");
                    return paramOptions.toString();
                case "State":
                    paramOptions.append("Enter a state (2 letter abbreviation): ");
                    return paramOptions.toString();

                case "Copay Min":
                    paramOptions.append("15\n");
                    paramOptions.append("25\n");
                    paramOptions.append("35\n");
                    return paramOptions.toString();
                case "Copay Max":
                    paramOptions.append("80\n");
                    paramOptions.append("90\n");
                    paramOptions.append("100\n");
                    return paramOptions.toString();
                case "Deductible Min":
                    paramOptions.append("300\n");
                    paramOptions.append("400\n");
                    paramOptions.append("500\n");
                    return paramOptions.toString();
                case "Deductible Max":
                    paramOptions.append("1000\n");
                    paramOptions.append("1500\n");
                    paramOptions.append("1800\n");
                    return paramOptions.toString();
                case "Location":
                    paramOptions.append("783 - EQ Maxon Corp\n");
                    return paramOptions.toString();
                case "Patient Latitude":
                    paramOptions.append("40.2\n");
                    paramOptions.append("40.3\n");
                    paramOptions.append("40.4\n");
                    return paramOptions.toString();
                case "Patient Longitude":
                    paramOptions.append("-79.8\n");
                    paramOptions.append("-79.9\n");
                    paramOptions.append("-80.0\n");
                    return paramOptions.toString();
                case "Mile Limit":
                    paramOptions.append("5\n");
                    paramOptions.append("10\n");
                    paramOptions.append("15\n");
                    return paramOptions.toString();
                case "Specialty":
                    paramOptions.append("3 - Cardiologist\n");
                    return paramOptions.toString();
                case "Language":
                    paramOptions.append("8 - Korean\n");
                    return paramOptions.toString();
                case "Insurance":
                    paramOptions.append("47 - Frami Group\n");
                    return paramOptions.toString();
                case "Patient Lname":
                    paramOptions.append("Enter patient's last name: ");
                    return paramOptions.toString();
                case "Patient Phone":
                    paramOptions.append("Enter patient's phone number: ");
                    return paramOptions.toString();
                case "Patient ID":
                    paramOptions.append("Enter patient's ID: ");
                    return paramOptions.toString();
            }
        }
        return "";
    }

    /**
     * The main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final QueryRunner queryrunner = new QueryRunner();
        final String DATABASE = "group6_milestone3";

        if (args.length == 0) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(queryrunner).setVisible(true);
                }
            });
        } else {
            if (args[0].equals("-console")) {
                Scanner keyboard = new Scanner(System.in);

                boolean bOK;

                String czHost, szUser, szPass;
                System.out.print("Enter MySQL hostname: ");
                czHost = keyboard.nextLine();
                System.out.print("Enter MySQL user: ");
                szUser = keyboard.nextLine();
                System.out.print("Enter MySQL password: ");
                szPass = keyboard.nextLine();

                bOK = queryrunner.Connect(czHost, szUser, szPass, DATABASE);

                if (bOK == false)
                    System.out.println(queryrunner.GetError());
                else {
                    int totalQueries;

                    totalQueries = queryrunner.GetTotalQueries();
                    for (int queryNumber = 0; queryNumber < totalQueries; queryNumber++) {

                        String[] paramArray = {};
                        String[] headers;
                        String[][] table;

                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        System.out.print("Query " + (queryNumber + 1) + ": ");
                        System.out.println(queryrunner.getQueryTitle(queryNumber));

                        if (queryrunner.isParameterQuery(queryNumber)) {
                            int amt = queryrunner.GetParameterAmtForQuery(queryNumber);
                            paramArray = new String[amt];
                            for (int j = 0; j < amt; j++) {

                                String paramText = queryrunner.GetParamText(queryNumber, j);
                                System.out.println(ANSI.ANSI_GREEN + paramText + ": ");
                                System.out.println(getParameterOptions(paramText));
                                System.out.print(ANSI.ANSI_RESET);

                                System.out.print("Enter your choice or answer: ");
                                paramArray[j] = keyboard.next();
                                keyboard.nextLine();
                            }
                        } else {
                            System.out.println("No parameter needed.");
                        }

                        // Check if query is action query
                        if (queryrunner.isActionQuery(queryNumber)) {
                            bOK = queryrunner.ExecuteUpdate(queryNumber, paramArray);
                            if (bOK == true) {
                                System.out.println("Rows affected = " + queryrunner.GetUpdateAmount());
                            } else {
                                System.out.println(queryrunner.GetError());
                            }

                        } else {
                            bOK = queryrunner.ExecuteQuery(queryNumber, paramArray);
                            if (bOK == true) {
                                headers = queryrunner.GetQueryHeaders();
                                table = queryrunner.GetQueryData();
                                System.out.print("\nQuery " + (queryNumber + 1) + " Result: " + ANSI.ANSI_PURPLE + "\n");

                                int[] maxWidth = new int[table[0].length];
                                String[] widthFormat = new String[table[0].length];

                                for (int col = 0; col < table[0].length; col++) {
                                    maxWidth[col] = table[0][col].length();
                                    for (int row = 1; row < table.length; row++) {
                                        if (headers[col].length() > maxWidth[col])
                                            maxWidth[col] = headers[col].length();
                                        if (table[row][col].length() > maxWidth[col])
                                            maxWidth[col] = table[row][col].length();
                                    }
                                    widthFormat[col] = "%-" + (Math.min(maxWidth[col], 50) + 3) + "s";
                                }

                                for (int col = 0; col < headers.length; col++) {
                                    System.out.printf(widthFormat[col], headers[col]);
                                }
                                System.out.println();

                                // Print dashes for aesthetic reason
                                for (int col = 0; col < headers.length; col++) {
                                    StringBuilder dashes = new StringBuilder();
                                    for (int i = 0; i < headers[col].length(); i++)
                                        dashes.append("-");
                                    System.out.printf(widthFormat[col], dashes.toString());
                                }
                                System.out.println();

                                // Step 3: Print table content: set max = 8 rows
                                for (int row = 0; row < Math.min(table.length, 8); row++) {
                                    for (int col = 0; col < table[row].length; col++) {
                                        String content = table[row][col];
                                        if (content.length() > 50)
                                                content = content.substring(0, 45) + "...";
                                        System.out.printf(widthFormat[col], content);
                                    }
                                    System.out.println();
                                }

                                System.out.println(ANSI.ANSI_RESET);    // Reset the color to default
                            } else {
                                System.out.println(queryrunner.GetError());
                            }
                        }
                    }
                }
                bOK = queryrunner.Disconnect();
                if (bOK == false) {
                    System.out.println(queryrunner.GetError());
                }
            }
        }
    }
}