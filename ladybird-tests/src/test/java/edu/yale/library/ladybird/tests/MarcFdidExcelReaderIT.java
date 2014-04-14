package edu.yale.library.ladybird.tests;


import edu.yale.library.ladybird.kernel.beans.FieldMarcMapping;
import edu.yale.library.ladybird.persistence.ServicesManager;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class MarcFdidExcelReaderIT {

    private ServicesManager servicesManager;

    @Before
    public void init() {
        servicesManager = new ServicesManager();
    }

    @After
    public void stopDB() throws SQLException {
        servicesManager.stopDB();
    }

    /**
     * Reads from test fdid-marc mapping spreadsheet and verifies its persistence
     *
     * @throws IOException
     * @see FieldMarcMappingDAO
     * @see FieldMarcMappingHibernateDAO
     */
    @Test
    public void shouldReadandSaveMapping() throws IOException {

        final FieldMarcMappingDAO dao = new FieldMarcMappingHibernateDAO();
        servicesManager.startDB();

        MarcFdidSpreadsheetReader reader = new MarcFdidSpreadsheetReader();
        List<FieldMarcMapping> mappingList  = reader.readMarcMapping(FileConstants.TEST_XLS_FILE,
                FileConstants.TEST_SHEET_NUM);
        assertEquals("List size mismatch", mappingList.size(), FileConstants.ROW_COUNT - 1);

        //Save
        for (FieldMarcMapping fieldMarcMapping: mappingList) {
            dao.save(fieldMarcMapping);
        }

        //Read back
        final List<FieldMarcMapping> dbList = dao.findAll();

        assertEquals("List size mismatch", mappingList.size(), dbList.size());

        for (int i = 0; i < mappingList.size(); i++) {
            assertEquals("K1 value mismtach", mappingList.get(i).getK1(), dbList.get(i).getK1());
            //and so on . . .
        }
    }


    /**
     * Test data
     */
    private static class FileConstants {
        static final String TEST_XLS_FILE = "field-marc-mappings.xlsx";
        static final int TEST_SHEET_NUM = 0;
        static final int ROW_COUNT = 87;
    }

}
