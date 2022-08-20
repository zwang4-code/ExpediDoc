/**
 * Group 6 - Milestone 3
 * Date: May 31, 2021
 * Students: Zi Wang, Dominic Burgi, Luoshan Zhang
 *
 * @author Professor M. McKee
 */

package queryrunner;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The Class QueryJDBC makes a connection to the database.
 */
public class QueryJDBC {

    /** The connection. */
    public Connection m_conn = null;

    /** The database drive. */
    static final String DB_DRV = "com.mysql.cj.jdbc.Driver";

    /** The connection error. */
    String m_error="";

    /** The url. */
    String m_url;

    /** The user name. */
    String m_user;

    /** The headers. */
    String [] m_headers;

    /** The all rows. */
    String [][] m_allRows;

    /** The update amount. */
    int m_updateAmount = 0;

    /**
     * Instantiates a new query JDBC.
     */
    QueryJDBC ()
    {
        m_updateAmount = 0;
    }

    /**
     * Gets the error.
     *
     * @return the string
     */
    public String GetError()
    {
        return m_error;
    }

    /**
     * Gets the headers.
     *
     * @return the string[] headers
     */
    public String [] GetHeaders()
    {
        return this.m_headers;
    }

    /**
     * Gets the data.
     *
     * @return the string[][] with data
     */
    public String [][] GetData()
    {
        return this.m_allRows;
    }

    /**
     * Gets the update count.
     *
     * @return the amount of rows updated
     */
    public int GetUpdateCount()
    {
        return m_updateAmount;
    }

    /**
     * Execute query.
     *
     * @param szQuery the query
     * @param userInputs the parameters
     * @param likeparms the parameters using like
     * @return true, if successful
     */
    public boolean ExecuteQuery(String szQuery, String [] userInputs, boolean [] likeparms) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int nColAmt;
        boolean bOK = true;

        try {
       
            preparedStatement=this.m_conn.prepareStatement(szQuery);

            int nParamAmount = userInputs.length;

            if (likeparms != null) {
                for (int i = 0; i < nParamAmount; i++) {
                    String parm = userInputs[i];
                    if (likeparms[i] == true) {
                        parm += "%";
                    }
                    preparedStatement.setString(i + 1, parm);
                }
            }

            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData rsmd = resultSet.getMetaData();

            nColAmt = rsmd.getColumnCount();
            m_headers = new String [nColAmt];
            
            for (int i=0; i< nColAmt; i++) {
                m_headers[i] = rsmd.getColumnLabel(i+1);
            }

            int amtRow = 0;
            while(resultSet.next()){
                amtRow++;
            }
            if (amtRow > 0) {
                this.m_allRows= new String [amtRow][nColAmt];
                resultSet.beforeFirst();
                int nCurRow = 0;
                while(resultSet.next()) {
                    for (int i=0; i < nColAmt; i++) {
                       m_allRows[nCurRow][i] = resultSet.getString(i+1);
                    }
                    nCurRow++;
                }                                
            }
            else {
                this.m_allRows= new String [1][nColAmt];               
                for (int i=0; i < nColAmt; i++) {
                   m_allRows[0][i] = "";
                }               
            }
                  
            preparedStatement.close();
            resultSet.close();            
        }

        catch (SQLException ex) {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "SQLState: " + ex.getSQLState();
            this.m_error += "VendorError: " + ex.getErrorCode();
            
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }
        return true;
    }

    /**
     * Execute update.
     *
     * @param szQuery the query
     * @param parms the parameters
     * @return true, if successful
     */
     public boolean ExecuteUpdate(String szQuery, String [] parms) {
        PreparedStatement preparedStatement = null;        

        boolean bOK = true;
        m_updateAmount=0;

        try {
       
            preparedStatement=this.m_conn.prepareStatement(szQuery);            

            int nParamAmount = parms.length;

            for (int i=0; i < nParamAmount; i++) {
                preparedStatement.setString(i+1, parms[i]);
            }
            
            m_updateAmount =preparedStatement.executeUpdate();  
            preparedStatement.close();          
        }

        catch (SQLException ex) {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "SQLState: " + ex.getSQLState();
            this.m_error += "VendorError: " + ex.getErrorCode();
            
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }          
                
        return true;
    }

    /**
     * Connect to database.
     *
     * @param host the host
     * @param user the user
     * @param pass the pass
     * @param database the database
     * @return true, if successful
     */
    public boolean ConnectToDatabase(String host, String user, String pass, String database) {
        String url;
        
        url = "jdbc:mysql://";
        url += host;
        url +=":3306/";
        url += database;   
        url +="?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL = false";
        try {

            Class.forName(DB_DRV).newInstance();
            m_conn = DriverManager.getConnection(url,user,pass);
        } 
        catch (SQLException ex) {
            m_error = "SQLException: " + ex.getMessage() + ex.getSQLState() + ex.getErrorCode();
            return false;
        }          
        catch (Exception ex) {
            m_error = "SQLException: " + ex.getMessage();
            return false;
        }
        return true;
    }

    /**
     * Closes the database connection and throws SQL Exception or Exception if
     * connection was not closed successfully.
     *
     * @return true, if successful
     * @returns false if unsucessful
     */
    public boolean CloseDatabase() {
        try {
            m_conn.close();
        } 
        catch (SQLException ex) {
            m_error = "SQLException: " + ex.getMessage();
            m_error = "SQLState: " + ex.getSQLState();
            m_error = "VendorError: " + ex.getErrorCode();
            return false;
        }          
        catch (Exception ex) {
            m_error = "Error was " + ex.toString();
            return false;
        }
        return true;
    }
}
