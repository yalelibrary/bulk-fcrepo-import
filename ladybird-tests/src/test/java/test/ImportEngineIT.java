package test;

import edu.yale.library.ServicesManager;
import edu.yale.library.beans.ImportJob;
import edu.yale.library.beans.ImportJobContents;
import edu.yale.library.beans.ImportJobExhead;
import edu.yale.library.dao.ImportJobContentsDAO;
import edu.yale.library.dao.ImportJobDAO;
import edu.yale.library.dao.ImportJobExheadDAO;
import edu.yale.library.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.engine.imports.*;
import edu.yale.library.engine.imports.ImportEngine;
import edu.yale.library.engine.imports.DefaultImportEngine;
import edu.yale.library.engine.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests full cycle for read/write import.
 */
public class ImportEngineIT
{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    ServicesManager servicesManager;

    /** Contains test fdids corresponding to test excel file (instead of via db) */
    final static String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    /**
     * Full cycle read write
     * @throws Exception
     */
    @Test
    public void execute() throws Exception
    {
        //start the engine
        servicesManager.startDB(); //TODO

        setApplicationData(); //TODO tmp. Inst app. rules for test (since db state is cleaned)

        ImportEngine engine = new DefaultImportEngine();

        logger.debug("Reading test spreadsheet rows");
        List<ImportEntity.Row> rows = engine.read(getTestSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        logger.debug("Read spreadseet rows");

        assertEquals("Rows size mismatch", rows.size(), FileConstants.ROW_COUNT);
        assertEquals("Columns size mismatch", rows.get(0).getColumns().size(), FileConstants.COL_COUNT);
        assertEquals("Column value mismatch", rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");

        logger.debug("Writing rows to tables");
        engine.write(rows);

        //Now read back:

        //Job itself:
        ImportJobDAO importJobHibernateDAO = new ImportJobHibernateDAO();
        List<ImportJob> l = importJobHibernateDAO.findAll();
        assertEquals("Import job count mismatch", l.size(), 1);

        //Headers (aka exhead):
        ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        List<ImportJobExhead> jobExheads = importJobExheadDAO.findAll();
        assertEquals("Import job exhead count mismatch", jobExheads.size(), FileConstants.COL_COUNT);

        //Contents (aka imjobcontents):
        ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        List<ImportJobContents> importJobContents = importJobContentsDAO.findAll();

        /* assert that data written to DB equals rows times cols (-1 for each since exhead is row 0 and F1 is col 1)
           from the spreadsheet. */

        assertEquals("Import job contents size mismatch. ", importJobContents.size(),
                (FileConstants.ROW_COUNT - 1) * (FileConstants.COL_COUNT - 1));
    }

    public SpreadsheetFile getTestSpreadsheeet()
    {
        /*
        SpreadsheetFile file = new SpreadsheetFile();
        file.setFileName(FileConstants.TEST_XLS_FILE);
        file.setAltName("Test spreadsheet.");
        file.setPath(FileConstants.TEST_XLS_FILE);
        return file;*/

        SpreadsheetFile file = new SpreadsheetFile(FileConstants.TEST_XLS_FILE, "Test spreadsheet",
                FileConstants.TEST_XLS_FILE, getClass().getClassLoader().getResourceAsStream(FileConstants.TEST_XLS_FILE));

        return file;
    }

    /**
     * Sets business logic data
     */
    public void setApplicationData()
    {
        initFieldDefMap(); //set default fdids
    }

    /**
     * Inits fdids
     */
    public void initFieldDefMap()
    {
        try
        {
            new FieldDefinitionValue().setFieldDefMap(getTextFieldDefsMap());
        }
        catch(IOException|NullPointerException e)
        {
            logger.error("Test fdids could not be loaded", e);
            fail();
        }
    }

    /**
     * Helps in initing fdids via a tex tfile
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public Map getTextFieldDefsMap() throws IOException, NullPointerException
    {
        Map<String, FieldConstant> fdidsMap = new HashMap();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt: properties.stringPropertyNames())
            //TODO put Int as key?
            fdidsMap.put(properties.getProperty(fdidInt).toString(), getFdid(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt).toString()));

        //test file entries should equal the number of spreadsheet columns:
        assertEquals("Wrong number of test fdids", fdidsMap.size(),
                FileConstants.FDID_COL_COUNT);

        return fdidsMap;
    }

    public void setJdbcFieldDefsMap()
    {
        //TODO (set fdid in test db?)
    }

    public FieldDefinitionValue getFdid(int fdid, String s)
    {
        return new FieldDefinitionValue(fdid, s);
    }

    /**
     * Test file constants
     */
    public class FileConstants
    {

        final static String TEST_XLS_FILE = "4654-pt1-READY-FOR-INGEST-A.xlsx";

        final static int ROW_COUNT = 78;

        final static int COL_COUNT = 31; //Actual number

        final static int FDID_COL_COUNT = 30; //COL_COUNT (regular fdids) minus a FunctionConstants (F1)

    }

    @Before
    public void init()
    {
        servicesManager = new ServicesManager();
    }

    @After
    public void stopDB() throws SQLException
    {
        servicesManager.stopDB();
    }

}
