package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.ImportJobNotifications;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.fail;

public class ImportJobNotificationsTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    private static final int USER_ID = 2;
    private  static final int IMPORT_JOB_ID = 1;

    @Before
    public void init() {
        initDB();
    }

    @After
    public void stop() throws SQLException {
        //TODO
    }

    @Inject
    private ImportJobNotificationsDAO dao;

    @Test
    public void shouldCreateEntry(){
        ImportJobNotifications importJobNotification = new ImportJobNotifications();
        importJobNotification.setUserId(USER_ID);
        importJobNotification.setImportJobId(IMPORT_JOB_ID);
        importJobNotification.setNotified((byte) 1);

        try {
            dao.save(importJobNotification);
            List<ImportJobNotifications> importJobNotificationsList = dao.findAll();
            assert (importJobNotificationsList.size() == IMPORT_JOB_ID);
            assert (importJobNotificationsList.get(0).getUserId() == USER_ID);
            assert (dao.findByUserAndJobId(USER_ID, IMPORT_JOB_ID).size() == IMPORT_JOB_ID);
            assert (dao.findAllUnsent().size() == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
