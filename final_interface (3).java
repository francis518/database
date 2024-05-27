import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

//This class contains the main method and runs the console which looks for user input
//There's a simple switch case on user imput to call the appropriate methods
public class final_interface {

    private SQLServer server;

    public final_interface() {
        // startup
        server = new SQLServer();
    }

    public static void main(String[] args) {
        final_interface obj = new final_interface();
        if (obj != null) {
            System.out.println("Interface ready: enter a command or h for help  ");
            System.out.print("accident db> ");
            obj.runConsole();
        }
        System.out.println("Exiting...");

    }// main

    // Switch case on user input
    // Executes various methods and awaits for more input
    private void runConsole() {
        Scanner console = new Scanner(System.in);
        String line = "";
        String[] parts;
        String arg = "";

        // Keep waiting for user input unless the user types "q" to quit
        while (console.hasNextLine() && !(line = console.nextLine()).equals("q")) {

            parts = line.split("\\s+");// split the line on whitespace(s)
            if (line.indexOf(" ") > 0)
                arg = line.substring(line.indexOf(" ")).trim();

            switch (parts[0]) {
                case "h":
                    if (parts.length > 1)
                        server.printHelp(line.substring(line.indexOf(" ")).trim());
                    else
                        server.printHelp();
                    break;

                // get Accidents from a year until now or a year range
                // WIth 3 arguments, returns the number of accidents in the year range with a
                // certain limit
                // With 2 arguments, returns a default 1000 accidents in the year range
                // With 1 argument, returns a default 1000 accidents in the year
                case "ctAccYY":
                    if (parts.length == 2)
                        server.getAccidents(arg, "0", 1000);
                    else if (parts.length == 3)
                        server.getAccidents(parts[1].trim(), parts[2].trim(), 1000);
                    else if (parts.length == 4) {
                        int limit = Integer.parseInt(parts[3]);
                        server.getAccidents(parts[1].trim(), parts[2].trim(), limit);
                    } else {
                        System.out.println("Require argument(s) year(s) for this command");
                    }
                    break;

                // gets the number of accidents for each make
                // With 2 arguments, returns a top 10 list of makes specified by the character
                // provided as the 2nd argument
                // With 1 argument, returns a list of all makes and the number of accidents for
                // each in descending order
                case "al":
                    try {
                        if (parts.length == 1) {
                            server.accVehicle();
                        } else if (parts.length == 2) {
                            String orderChar = parts[1];
                            server.accVehicle(orderChar);
                        } else {
                            System.out.println("Invalid number of arguments.");
                        }

                    } catch (SQLException e) {

                        System.out.println(e);
                    }
                    break;

                // Gets the number of accidents for certain time ranges
                case "time":
                    try {
                        server.timeRange();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    break;

                // Gets the number of accidents for road types, returns a whole list
                case "rd":
                    try {
                        server.roadType();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    break;

                // Gets the number of accidents for a specific road type and weather conditions
                case "na":
                    String road = selectStreetType();
                    try {
                        server.getNumAccidents(road);
                    } catch (SQLException e) {
                        System.out.println("system failure");
                    }
                    break;

                // Gets the number of accidents for a specific severity, outputs the lsit of all
                // severities for respective age bands
                case "sv":
                    try {
                        server.severity();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    break;

                // Gets the number of accidents for a specific road type and weather conditions
                case "af":
                    road = selectStreetType();
                    String weather = selectWeatherCondition();

                    try {
                        server.affected(road, weather);

                    } catch (SQLException e) {
                        System.out.println("error");
                    }
                    break;

                // Gets the accidents that a specific license plate is involved in
                case "car":
                    int plateID = getNumber(console);
                    try {
                        server.car(plateID);
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    break;

                // Returns the number of accidents occured on each junction type
                case "junct":
                    try {
                        server.junctions();
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    break;

                // Gets the number of accidents for a specific gender and an age band of drivers
                case "dr":
                    String age = selectAgeRange();
                    String sex = selectGender();
                    try {
                        server.driver(age, sex);
                    } catch (SQLException e) {
                        System.out.println("system failure");
                    }
                    break;

                // DESINTEGRATES the database
                case "dl":
                    server.delete();
                    break;

                // Repopulates the database
                case "rePop":
                    server.rePop();
                    break;

                default:
                    System.out.println("Unknown command: " + line + "\nUse h for help");
            }
            System.out.print("accident db> ");
        }
        console.close();
    }// runConsole

    private static int getNumber(Scanner scan) {
        System.out.println("Input a number between and including 1 - 69152");
        int choice = 0;
        try{
            choice = Integer.parseInt(scan.nextLine());
        }
        catch(Exception e){
            System.out.println(e);
            return getNumber(scan);
        }
        if (choice < 1 || choice > 69152) {
            return getNumber(scan);
        }
        return choice;
    }

    private static String selectGender() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a sex:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.print("Enter your choice (1-2): ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                return "Male";
            case 2:
                return "Female";
            default:
                System.out.println("Invalid choice");
                return selectGender();
        }
    }

    private static String selectAgeRange() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a age range:");
        System.out.println("1. 16 - 20");
        System.out.println("2. 21 - 25");
        System.out.println("3. 26 - 35");
        System.out.println("4. 36 - 45");
        System.out.println("5. 46 - 55");
        System.out.println("6. 56 - 65");
        System.out.println("7. Over 75");
        System.out.print("Enter your choice (1-7): ");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                return "16 - 20";
            case 2:
                return "21 - 25";
            case 3:
                return "26 - 35";
            case 4:
                return "36 - 45";
            case 5:
                return "46 - 55";
            case 6:
                return "56 - 65";
            case 7:
                return "Over 75";
            default:
                System.out.println("Invalid choice");
                return selectAgeRange();
        }
    }

    private static String selectWeatherCondition() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a weather condition:");
        System.out.println("1. Fine no high winds");
        System.out.println("2. Raining no high winds");
        System.out.println("3. Other");
        System.out.println("4. Snowing no high winds");
        System.out.println("5. Fine + high winds");
        System.out.println("6. Fog or mist");
        System.out.println("7. Raining + high winds");
        System.out.print("Enter your choice (1-7): ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                return "Fine no high winds";
            case 2:
                return "Raining no high winds";
            case 3:
                return "Other";
            case 4:
                return "Snowing no high winds";
            case 5:
                return "Fine + high winds";
            case 6:
                return "Fog or mist";
            case 7:
                return "Raining + high winds";
            default:
                System.out.println("Invalid choice");
                return selectWeatherCondition();
        }
    }

    private static String selectStreetType() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a street type:");
        System.out.println("1. Single carriageway");
        System.out.println("2. One way street");
        System.out.println("3. Dual carriageway");
        System.out.println("4. Roundabout");
        System.out.print("Enter your choice (1-4): ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                return "Single carriageway";
            case 2:
                return "One way street";
            case 3:
                return "Dual carriageway";
            case 4:
                return "Roundabout";
            default:
                System.out.println("Invalid choice");
                return selectStreetType();
        }
    }
}

class SQLServer {

    // Connect to your database.
    // Replace server name, username, and password with your credentials
    private Connection connection;
    private String sqlBuilder = "final_dbBuild.sql";

    public SQLServer() {

        Properties prop = new Properties();
        String fileName = "auth.cfg";
        try {
            FileInputStream configFile = new FileInputStream(fileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file.");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }
        String username = (prop.getProperty("username"));
        String password = (prop.getProperty("password"));

        if (username == null || password == null) {
            System.out.println("Username or password not provided.");
            System.exit(1);
        }

        String connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password=" + password + ";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";

        try {
            connection = DriverManager.getConnection(connectionUrl);
            // dbBuilder pop = new dbBuilder(connection, sqlBuilder);
            // pop.build();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }// constructor

    public void printHelp() {

        System.out.println("\nALL AVAILABLE COMMANDS:");
        System.out.println("-----------------------------------------");
        System.out.println("ctAccYY : returns the specified accidents with the given year range ");
        printHelpAl();
        System.out.println("time : gives the number of accidents that occured during certain time ranges");
        printHelpRd();
        System.out.println("af : gives the number of accidents on certain roadtypes, during certain weather conditions");
        System.out.println("dr : gives the licenseID for all driver in a certain age range, and sex ");
        printHelpNa();
        printHelpSv();
        System.out.println("car : returns the accidents vehicle with plateId entered by user is involved");
        System.out.println("junct : shows the break down of number of accidents at different types of junctions");
        printHelpDelete();
        printHelpRePop();
        System.out.println("enter h cmdName to get help for that command");
        System.out.println("q - to exit database\n");

    }// printHelp

    public void printHelp(String method) {
        String temp = method.toLowerCase().trim();
        if (temp.equals("ctAccYY")) {
            printHelpgetAccYY();
        } else if (temp.equals("al")) {
            printHelpAl();
        } else if (temp.equals("time")) {
            printHelpTime();
        } else if (temp.equals("rd")) {
            printHelpRd();
        } else if (temp.equals("af")) {
            printHelpAf();
        } else if (temp.equals("na")) {
            printHelpNa();
        } else if (temp.equals("sv")) {
            printHelpSv();
        } else if (temp.equals("car")) {
            printHelpCar();
        } else if (temp.equals("junct")) {
            printHelpJunct();
        } else if (temp.equals("dr")) {
            printHelpDr();
        }
        else if(temp.equals("dl")){
            printHelpDelete();
        }
        else if(temp.equals("rePop")){
            printHelpRePop();
        }
    }

    public void printHelpgetAccYY() {
        System.out.println(
                "ctAccYY year1 year2 limit: gets the accidents that occured between year1 and year2 (both inclusive)");
        System.out.println("If year2 is not provided, accidents in year1 are returned");
        System.out.println("Year range is 2005 - 2017");
        System.out.println("return \'limit\' number of results. default set to 1000\n-----end-----\n");
    }

    public void printHelpAl() {
        System.out.println("al : produces a table that sums up the number of accidents that each make of vehicles was involved in");
    }

    public void printHelpTime() {
        System.out.println("time : Return the number of accidents occured in each time range");
        System.out.println("The time ranges are: ");
        System.out.println("00:00 - 06:00");
        System.out.println("06:00 - 12:00");
        System.out.println("12:00 - 18:00");
        System.out.println("18:00 - 24:00\n-----end-----\n");
    }

    public void printHelpRd() {
        System.out.println("rd : gives the number of accidents that occured on each road types");
    }

    public void printHelpAf() {
        
        System.out.println("af : returns the number of accidents on the roadtype and waether condition given by user");
        System.out.println("After the command \'af\' is given, the console asks the user to select the road type followed by weather conditions selection");
        System.out.println("The road/street types are:");
        System.out.println("1. Single carriageway");
        System.out.println("2. One way street");
        System.out.println("3. Dual carriageway");
        System.out.println("4. Roundabout\n----\n");
        System.out.println("The weather conditions are:");
        System.out.println("1. Fine no high winds");
        System.out.println("2. Raining no high winds");
        System.out.println("3. Other");
        System.out.println("4. Snowing no high winds");
        System.out.println("5. Fine + high winds");
        System.out.println("6. Fog or mist");
        System.out.println("7. Raining + high winds\n-----end------\n");

    }

    public void printHelpDr() {
        
        System.out.println("dr : returns all drivers within the age range and sex selected by the user");
        System.out.println("After the command \'dr\' is given, the console asks the user to select age range followed by the sex");
        System.out.println("The age ranges are:");
        System.out.println("1. 16 - 20");
        System.out.println("2. 21 - 25");
        System.out.println("3. 26 - 35");
        System.out.println("4. 36 - 45");
        System.out.println("5. 46 - 55");
        System.out.println("6. 56 - 65");
        System.out.println("7. Over 75\n----\n");
        System.out.println("The options for sex are:");
        System.out.println("1. Male");
        System.out.println("2. Female\n-----end-----\n");
    }

    public void printHelpNa() {
        System.out.println("na : gives the number of accidents on a choosen roadtype");
    }

    public void printHelpSv() {
        System.out.println("sv : this command gives you the number of accidents per severity per age bands");

    }

    public void printHelpCar() {
        System.out.println("car : this command returns all accidents for a plateId entered by the user");
        System.out.println("Usage: after the command \'car\' is entered, the console will prompt the user to enter a plateId (positive integer)");
        System.out.println("If the plateId entered is less than 1 or greater than 69152, the console asks for the input again\n----end-----\n");
    }

    public void printHelpJunct() {
        
        System.out.println("Usage : entering the command \'junct\' returns the number of accidents that occured for each type of junction");
        System.out.println("The types of junctions are: ");
        System.out.println("Crossroads\n"
        + "Data missing or out of range\n"
        +"Mini-roundabout\n"
        +"More than 4 arms (not roundabout)\n"
        +"Not at junction or within 20 metres\n"
        +"Other junction\n"
        +"Private drive or entrance\n"
        +"Roundabout\n"
        +"Slip road\n"
        +"T or staggered junction\n----end-----\n");
    }

    public void printHelpDelete() {
        System.out.println("dl : deletes everything from the database");
    }

    public void printHelpRePop() {
        System.out.println("rePop : repopulates the database");
    }

    // Multi output command that returns all accidents in a given time period
    // First, if there's a 3rd argument, the command will return up to that number
    // of accidents between years ar1 and arg2
    // With 2 argumenrs where arg1 < arg2, the command will return 10000 accidents
    // that occured between years arg1 and arg2
    // With just 1 argument passed, the command will return a default limit of 10000
    // accidents that occured only during the provided year arg1
    public void getAccidents(String year1, String year2, int limit) {
        String query = "SELECT TOP(?) accident_index, severity, date_occured, time_occured from accident where date_occured >= ? and date_occured <= ?; ";
        try {
            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setInt(1, limit);
            pstm.setString(2, year1);
            if (Integer.parseInt(year2) == 0) {
                year2 = year1.trim() + "-12-31";// select 1000 accidents that occured in the same year
                pstm.setString(3, year2);
            } else {
                pstm.setString(3, year2);// select accidents that occured from year 1 to year 2
            }
            ResultSet rs = pstm.executeQuery();
            System.out.println(String.format("%-10s | %-8s | %10s | %12s |%n", "IND", "Severity", "Date", "Time"));
            while (rs.next()) {
                System.out.println(String.format("%-10s | %-8s | %10s | %12s |%n", rs.getString("accident_index"),
                        rs.getString("severity"), rs.getString("date_occured"), rs.getString("time_occured")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //
    public void getNumAccidents(String roadType) throws SQLException {
        String numAccidents = "SELECT COUNT(*) FROM accident AS a JOIN location AS l ON a.loc_longitude = l.longitude AND a.loc_latitude = l.latitude JOIN road AS r ON l.rd_class = r.first_rd_class AND l.rd_num = r.first_rd_num WHERE r.road_type = '"
                + roadType + "'";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(numAccidents);
        System.out.println("Number of Accidents");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

    }

    public void affected(String roadType, String weather) throws SQLException {

        String affected = "SELECT COUNT(*) num_accidents FROM accident AS a JOIN location AS l ON a.loc_longitude = l.longitude AND a.loc_latitude = l.latitude JOIN road AS r ON l.rd_class = r.first_rd_class AND l.rd_num = r.first_rd_num WHERE r.road_type like '"
                + roadType + "'AND a.weather_conditions like'" + weather + "';";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(affected);
        System.out.println("Number of Accidents");
        while (rs.next()) {
            System.out.println(rs.getInt("num_accidents"));
        }

    }

    public void driver(String ageRange, String sex) throws SQLException {
        String driver = "SELECT * from driver WHERE age_band LIKE \'" + ageRange + "\' AND sex LIKE \'" + sex + "\'";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(driver);
        System.out.println(String.format("%-20s %-20s %-20s", "sex", "age_band", "licenseID"));
        while (rs.next()) {
            System.out.println(String.format("%-20s %-20s %-20s", rs.getString(1), rs.getString(2), rs.getString(3)));
        }

    }

    public void accVehicle(String orderChar) throws SQLException {
        String accVehicle = "SELECT TOP(10) CONVERT(CHAR(80), v.make), COUNT(*) FROM vehicle AS v JOIN involved AS i ON v.plateID = i.plateID WHERE CONVERT(CHAR(80), v.make) LIKE UPPER(? + '%') GROUP BY CONVERT(CHAR(80), v.make) ORDER BY COUNT(*) DESC";
        PreparedStatement pstmt = connection.prepareStatement(accVehicle);
        pstmt.setString(1, orderChar);
        ResultSet rs = pstmt.executeQuery();
        System.out.println(String.format("%-20s %-20s", "Vehicle make", "numAccidents"));
        while (rs.next()) {
            System.out.println(String.format("%-20s %-20s", rs.getString(1), rs.getString(2)));
        }
    }

    public void accVehicle() throws SQLException {
        String accVehicle = "SELECT CONVERT(CHAR(80), v.make), COUNT(*) FROM vehicle AS v JOIN involved AS i ON v.plateID = i.plateID GROUP BY CONVERT(CHAR(80), v.make) ORDER BY COUNT(*) DESC";
        PreparedStatement pstmt = connection.prepareStatement(accVehicle);
        ResultSet rs = pstmt.executeQuery();
        System.out.println(String.format("%-20s %-20s", "Vehicle make", "numAccidents"));
        while (rs.next()) {
            System.out.println(String.format("%-20s %-20s", rs.getString(1), rs.getString(2)));
        }
    }

    public void timeRange() throws SQLException {
        String times = "SELECT SUM(CASE WHEN time_occured >= '00:00:00' AND time_occured < '06:00:00' THEN 1 ELSE 0 END) AS '00:00-06:00', SUM(CASE WHEN time_occured >= '06:00:00' AND time_occured < '12:00:00' THEN 1 ELSE 0 END) AS '06:00-12:00', SUM(CASE WHEN time_occured >= '12:00:00' AND time_occured < '18:00:00' THEN 1 ELSE 0 END) AS '12:00-18:00', SUM(CASE WHEN time_occured >= '18:00:00' AND time_occured < '23:59:59' THEN 1 ELSE 0 END) AS '18:00-24:00' FROM accident";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(times);
        System.out.println(
                String.format("%-20s %-20s %-20s %-20s", "00:00-06:00", "06:00-12:00", "12:00-18:00", "18:00-24:00"));
        while (rs.next()) {
            System.out.println(String.format("%-20s %-20s %-20s %-20s", rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4)));
        }

    }

    public void roadType() throws SQLException {
        String road = "SELECT r.road_type, COUNT(a.accident_index) FROM road AS r LEFT JOIN location AS l ON r.first_rd_class = l.rd_class AND r.first_rd_num = l.rd_num LEFT JOIN accident AS a ON l.longitude = a.loc_longitude AND l.latitude = a.loc_latitude GROUP BY r.road_type";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(road);
        System.out.println(String.format("%-20s %-20s", "Road Type", "numAccidents"));
        while (rs.next()) {
            System.out.println(String.format("%-20s %-20s", rs.getString(1), rs.getString(2)));
        }
    }

    public void severity() throws SQLException {
        String severe = "SELECT CONVERT(char(80),d.age_band), CONVERT(char(80),a.severity), COUNT(*) AS num_accidents FROM accident a JOIN causes c ON a.accident_index = c.accident_index JOIN driver d ON c.license_id = d.license_id GROUP BY CONVERT(char(80),d.age_band),CONVERT(char(80),a.severity)";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(severe);
        String header = String.format("%-20s %-20s %-20s", "age_band", "Severity", "NumAccidents");
        System.out.println(header);
        while (rs.next()) {
            String age = rs.getString(1).trim();
            String severity = rs.getString(2).trim();
            String num = rs.getString(3).trim();
            String output = String.format("%-20s %-20s %-20s", age, severity, num);
            System.out.println(output);
        }
    }

    public void car(int plateID) throws SQLException {
        String carDetails = "SELECT a.accident_index, a.severity, a.date_occured, a.local_auhtority_dist, a.local_authority_hwy, a.police_force,  a.road_conditions, a.weather_conditions, a.time_occured  FROM accident a  JOIN involved i ON a.accident_index = i.accident_index  JOIN vehicle v ON i.plateID = v.plateID WHERE v.plateID = "
                + plateID;
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(carDetails);
        System.out.println(String.format("%-8s %-12s %-24s %-24s %-22s %-16s %-20s %-12s",
                "Severity", "Acc Date",
                "authority dist", "authority hwy", "Police force",
                "Road conditions", "Weather", "Time"));
        while (rs.next()) {
            System.out.println(String.format("%-8s %-12s %-24s %-24s %-22s %-14s %-20s %-12s",
                    rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getString(8), rs.getString(9)));
        }
    }

    public void junctions() throws SQLException {
        String junctions = "SELECT CONVERT(CHAR(80),l.junct_detail), COUNT(*) AS num_accidents FROM location l JOIN accident a ON l.latitude = a.loc_latitude AND l.longitude = a.loc_longitude GROUP BY CONVERT(CHAR(80),l.junct_detail) ORDER BY num_accidents DESC";
        Statement pstmt = connection.createStatement();
        ResultSet rs = pstmt.executeQuery(junctions);
        System.out.println(String.format("%-35s %-15s", "Junction Detail", "Number of Accidents"));
        System.out.println("-----------------------------------------");
        while (rs.next()) {

            System.out.println(String.format("%-35s %-15d", rs.getString(1), rs.getInt(2)));
        }

    }

    public void rePop() {
        try {
            dbBuilder obj = new dbBuilder(connection, sqlBuilder);
            obj.build();
        } catch (Exception e) {
            System.out.println(e);
        }
    }// rePop

    public void delete() {
        try {
            connection.setAutoCommit(false);
            String query = "drop table if exists involved; drop table if exists drives; drop table if exists causes; drop table if exists driver; drop table if exists vehicle; drop table if exists accident; drop table if exists location; drop table if exists road;";
            Statement stmnt = connection.createStatement();
            stmnt.executeQuery(query);
            connection.commit();
            stmnt.close();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }// delete

}
