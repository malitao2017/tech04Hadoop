package baseKnowledge;
import java.sql.*;  
import java.util.*;  
  
import org.apache.log4j.Logger;  

public class ConnectionPool {
	
	public static void main(String[] args) throws Exception {
		ConnectionPool  connpool = new ConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/foo", "root", "******");
		connpool.initialize();
		Connection conn = connpool.getConnection();
		try
		{
			connpool.testConnection(conn)  ;
		}
		catch(Exception e){
		}
		finally
		{
		     connpool.returnConnection(conn);
		}
	}
	
	
	private String jdbcDriver;  
    private String dbUrl;  
    private String dbUsername;  
    private String dbPassword;  
    private String pingSql = "select 1"; // the test sql statement to ping the target database  
    private int minConnections = 5;  
    private int incrementalConnections = 2;  
    private int maxConnections = 20;  
      
    private Vector<PooledConnection> connections;  
    private ReleasePoolHook hook;  
      
    public static final Logger logger = Logger.getLogger(ConnectionPool.class);  
      
    public ConnectionPool(String driver, String url, String username, String password)  
    {  
        jdbcDriver = driver;  
        dbUrl = url;  
        dbUsername = username;  
        dbPassword = password;  
        hook = new ReleasePoolHook(this);  
    }  
      
    public int getInitialConnections()  
    {  
        return this.minConnections;  
    }  
      
    public void setInitialConnections(int initialConnections)  
    {  
        this.minConnections = initialConnections;  
    }  
      
    public int getIncrementalConnections()  
    {  
        return this.incrementalConnections;  
    }  
      
    public void setIncrementalConnections(int incrementalConnections)  
    {  
        this.incrementalConnections = incrementalConnections;  
    }  
      
    public int getMaxConnections()  
    {  
        return this.maxConnections;  
    }  
      
    public void setMaxConnections(int maxConnections)  
    {  
        this.maxConnections = maxConnections;  
    }  
      
    public String getPingSql()  
    {  
        return this.pingSql;  
    }  
      
    public void setPingSql(String sql)  
    {  
        this.pingSql = sql;  
    }  
      
    /** 
     * intialize the pool 
     * @throws Exception 
     */  
    public synchronized void initialize() throws Exception   
    {  
        if (connections != null)  
        {  
            return;  
        }  
          
        Class.forName(this.jdbcDriver);  
        connections = new Vector();  
          
        createConnections(this.minConnections);  
    }  
      
    private void createConnections(int numConnections) throws SQLException  
    {  
        for (int i=0; i<numConnections; i++)  
        {  
            if (this.maxConnections > 0 && this.connections.size() >= this.maxConnections)  
            {  
                break;  
            }  
            // add a new PooledConnection object  
            try  
            {  
                connections.addElement(new PooledConnection(newConnection()));  
            }  
            catch (SQLException e)  
            {  
                logger.error("create connection failed: ", e);  
                throw new SQLException();  
            }  
            logger.info(" connection created ......");  
        }  
    }  
      
    private Connection newConnection() throws SQLException   
    {  
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);  
        if (connections.size() == 0)  
        {  
            DatabaseMetaData metaData = conn.getMetaData();  
            int driverMaxConnections = metaData.getMaxConnections();  
              
            if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections)  
            {  
                this.maxConnections = driverMaxConnections;  
            }  
        }  
        return conn;  
    }  
      
    public synchronized Connection getConnection() throws SQLException  
    {  
        if (connections == null)  
        {  
            return null;  
        }  
          
        Connection conn = getFreeConnection();  
          
        while (conn == null)  
        {  
            wait(250);  
            conn = getFreeConnection();  
        }  
        return conn;  
    }  
      
    private Connection getFreeConnection() throws SQLException  
    {  
        Connection conn = findFreeConnection();  
        if (conn == null)  
        {  
            createConnections(incrementalConnections);  
            conn = findFreeConnection();  
            if (conn == null)  
            {  
                return null;  
            }  
        }  
        return conn;  
    }  
      
    private Connection findFreeConnection() throws SQLException  
    {  
        Connection conn = null;  
        PooledConnection pConn = null;  
          
        Iterator<PooledConnection> iter = connections.iterator();  
        while (iter.hasNext())  
        {  
            pConn = (PooledConnection)iter.next();  
            if (!pConn.isBusy())  
            {  
                conn = pConn.getConnection();  
                pConn.setBusy(true);  
                  
                if (!testConnection(conn))  
                {  
                    try  
                    {  
                        conn = newConnection();  
                    }  
                    catch(SQLException e)  
                    {  
                        logger.error("create connection failed:", e);  
                        return null;  
                    }  
                    pConn.setConnection(conn);  
                }  
                break;  
            }  
        }  
        return conn;  
    }  
      
    private boolean testConnection(Connection conn)  
    {  
        Statement stmt = null;  
        ResultSet rset = null;  
        try  
        {  
            stmt = conn.createStatement();  
            rset = stmt.executeQuery(this.pingSql);  
        }  
        catch (SQLException ex)  
        {  
            closeConnection(conn);  
            return false;  
        }  
        finally  
        {  
            try  
            {  
                if (rset!= null) rset.close();  
            }  
            catch (SQLException ex) {}  
            try  
            {  
                if (stmt!= null) stmt.close();  
            }  
            catch (SQLException ex) {}  
        }  
        return true;  
    }  
      
    public void returnConnection(Connection conn)  
    {  
        if (connections == null)  
        {  
            logger.warn("connection pool not exists.");  
            return;  
        }  
        PooledConnection pConn = null;  
        Enumeration enumerate = connections.elements();  
        while (enumerate.hasMoreElements())  
        {  
            pConn = (PooledConnection)enumerate.nextElement();  
            if (conn == pConn.getConnection())  
            {  
                pConn.setBusy(false);  
                break;  
            }  
        }  
    }  
      
    public synchronized void refreshConnections() throws SQLException  
    {  
        if (connections == null)  
        {  
            logger.warn("connection pool not exists, can't refresh...");  
            return;  
        }  
          
        PooledConnection pConn = null;  
        Enumeration enumerate = connections.elements();  
        while (enumerate.hasMoreElements())  
        {  
            pConn = (PooledConnection)enumerate.nextElement();  
            if (pConn.isBusy())  
            {  
                wait(5000);  
            }  
            closeConnection(pConn.getConnection());  
            pConn.setConnection(newConnection());  
            pConn.setBusy(false);  
        }  
    }  
      
    public synchronized void closeConnectionPool() throws SQLException  
    {  
        if (connections == null)  
        {  
            logger.warn("conneciton pool not exists, can't close..");  
            return;  
        }  
          
        PooledConnection pConn = null;  
        Enumeration enumerate = connections.elements();  
        while (enumerate.hasMoreElements())  
        {  
            pConn = (PooledConnection)enumerate.nextElement();  
            if (pConn.isBusy())  
            {  
                wait(5000);  
            }  
            closeConnection(pConn.getConnection());  
            connections.removeElement(pConn);  
        }  
        connections = null;  
    }  
      
    private void closeConnection(Connection conn)  
    {  
        try  
        {  
            conn.close();  
        }  
        catch (SQLException ex)  
        {  
            logger.warn("close connection error: ", ex);  
        }  
    }  
      
    private void wait(int mSeconds)  
    {  
        try  
        {  
            Thread.sleep(mSeconds);  
        }  
        catch (InterruptedException e)  
        {  
        }  
    }  
}


class PooledConnection{  
    Connection connection = null;  
    boolean busy = false;  
      
    public PooledConnection(Connection connection)  
    {  
        this.connection = connection;  
    }  
      
    public Connection getConnection()  
    {  
        return connection;  
    }  
      
    public void setConnection(Connection connection)  
    {  
        this.connection = connection;  
    }  
      
    public boolean isBusy()  
    {  
        return busy;  
    }  
      
    public void setBusy(boolean busy)  
    {  
        this.busy = busy;  
    }  
}  

class ReleasePoolHook implements Runnable  
{  
    public static final Logger logger = Logger.getLogger(ReleasePoolHook.class);  
    ConnectionPool connpool;  
  
    public ReleasePoolHook(ConnectionPool pool)  
    {  
        // register it  
        connpool = pool;  
        Runtime.getRuntime().addShutdownHook(new Thread(this));  
        logger.info(">>> shutdown hook registered...");  
    }  
      
    @Override  
    public void run()  
    {  
        // TODO Auto-generated method stub  
        logger.info("\n>>> About to execute: " + ReleasePoolHook.class.getName() + ".run() to clean up before JVM exits.");   
        this.cleanUp();   
        logger.info(">>> Finished execution: " + ReleasePoolHook.class.getName() + ".run()");   
    }  
      
    private void cleanUp()  
    {  
        if (connpool != null)  
        {  
            try  
            {  
                connpool.closeConnectionPool();  
                logger.info("Pool realeased....");  
            }  
            catch (SQLException e)  
            {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                logger.warn("Pool released with exception", e);  
            }  
              
        }  
    }  
  
}  