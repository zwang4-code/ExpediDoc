/**
 * Group 6 - Milestone 3
 * Date: May 31, 2021
 * Students: Zi Wang, Dominic Burgi, Luoshan Zhang
 *
 * @author Professor M. Mckee
 */

package queryrunner;

/**
 * The Class QueryData creates query objects
 * with the associated parameters.
 */
public class QueryData {

    /**
     * Instantiates a new query data.
     *
     * @param query the query
     * @param parms the parameters
     * @param likeparms the parameters using like
     * @param isAction the is action
     * @param isParm the is parameters
     */
    QueryData(String query, String[] parms, boolean [] likeparms, boolean isAction, boolean isParm)
    {
        m_queryString = query;
        m_arrayParms = parms;
        m_arrayLikeParms = likeparms;
        m_isAction = isAction;
        m_isParms = isParm;        
    }

    /**
     * Gets the query string.
     *
     * @return the string
     */
    String GetQueryString()
    {
        return m_queryString;
    }

    /**
     * Gets the amount of parameters.
     *
     * @return the parameters
     */
    int GetParmAmount()
    {
        if (m_arrayParms == null)
            return 0;
        else
            return m_arrayParms.length;
    }

    /**
     * Gets the parameters text.
     *
     * @param index the index
     * @return the string
     */
    String GetParamText(int index) {
        return m_arrayParms[index];
    }

    /**
     * Gets the like parameters.
     *
     * @param index the index
     * @return true, if successful
     */
    boolean GetLikeParam(int index) {
        return m_arrayLikeParms[index];
    }

    /**
     * Gets the all parameters that use like.
     *
     * @return the boolean[]
     */
    boolean [] GetAllLikeParams()
    {
        return m_arrayLikeParms;
    }

    /**
     * Checks if it is an action query.
     *
     * @return true, if it is an action query
     */
    boolean IsQueryAction()
    {
        return m_isAction;
    }

    /**
     * Checks if it is a parameter query.
     *
     * @return true, if it contains parameters
     */
    boolean IsQueryParm()
    {
        return m_isParms;
    }

    /** The query string. */
    private String m_queryString;

    /** The array of all parameters. */
    private String [] m_arrayParms;

    /** Does it have action. */
    private boolean m_isAction;

    /** Does it have parameters */
    private boolean m_isParms;

    /** The array contains like parameters */
    private boolean [] m_arrayLikeParms;
}
