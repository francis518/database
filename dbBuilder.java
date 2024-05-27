/*Database builder
 *Takes in the SQLite database that it will populate and an SQL file name/path that it will run.
 * 
 * Usage:
 *  dbBuilder builder = new dbBuilder(db, "build_script.sql");
 *  builder.build();
 * 
 * A buffered reader traverses each line in the script and executes the SQL statement.
 * Every new query in the script must be in a single line.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class dbBuilder {

    private Connection connection;
    private String script;

    public dbBuilder(Connection connection, String script) {
        this.connection = connection;
        this.script = script;
    }

    public void build() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(script));
            String line;
            PreparedStatement statement = null;
            while ((line = reader.readLine()) != null) {
                // Check if the encountered line is a comment
                if (!(line.startsWith("--") || line.trim().equals(""))) {
                    statement = connection.prepareStatement(line);
                    statement.execute();
                }
            }

            reader.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}