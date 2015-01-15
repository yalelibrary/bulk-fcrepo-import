package edu.yale.library.ladybird.engine.file;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.EventBus;
import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ObjectMetadataWriter;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.JobRequest;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringVersionHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectWriterIT {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @see edu.yale.library.ladybird.engine.imports.ObjectMetadataWriter creates object metadata tables and hits db to ensure that the values are written and
     * corresonding acid value is created
     */
    @Test
    public void shouldWriteToObjectMetadataTabes() {
        final int fdid1 = 59;
        final int fdid2 = 70;

        EventBus eventBus = new EventBus();
        eventBus.setAbstractModule(new TestModule());

        JobRequest jobRequest = new JobRequest();
        User user = new User();
        user.setUserId(0);
        jobRequest.setUser(user);

        //2. populate list of import rows

        final List<Import.Row> rowList = new ArrayList<>();

        //a. exhead
        final Import.Row exHeadRow = new Import().new Row();
        final List<Import.Column> exHeadColumns = new ArrayList<>();

        Import.Column oidExHeadColumn = new Import().new Column(FunctionConstants.F1, "");
        exHeadColumns.add(oidExHeadColumn);

        FieldDefinition fieldDefinition = new FieldDefinition(fdid1, "69");
        Import.Column fieldExHeadColumn = new Import().new Column<>(fieldDefinition, "");

        FieldDefinition fieldDefinition2 = new FieldDefinition(fdid2, "58");
        Import.Column fieldExHeadColumn2 = new Import().new Column<>(fieldDefinition2, "");

        exHeadColumns.add(fieldExHeadColumn);
        exHeadColumns.add(fieldExHeadColumn2);

        exHeadRow.setColumns(exHeadColumns);

        rowList.add(exHeadRow);

        //b. content row

        final Import.Row contentRow = new Import().new Row();
        List<Import.Column> contentColumns = new ArrayList<>();

        Import.Column oidContentColumn = new Import().new Column(FunctionConstants.F1, "777");
        contentColumns.add(oidContentColumn);

        FieldDefinition fieldDefinitionContent = new FieldDefinition(fdid1, "69");
        Import.Column fieldContentColumn = new Import().new Column<>(fieldDefinitionContent, "Name of the rose");
        contentColumns.add(fieldContentColumn);

        FieldDefinition fieldDefinitionContent2 = new FieldDefinition(fdid2, "58");
        Import.Column fieldContentColumn2 = new Import().new Column<>(fieldDefinitionContent2, "Name of the rose");
        contentColumns.add(fieldContentColumn2);

        contentRow.setColumns(contentColumns);

        rowList.add(contentRow);

        //3. Init Object:
        ImportContext importContext = new ImportContext();
        importContext.setJobRequest(jobRequest);
        importContext.setImportRowsList(rowList);

        // Finally, test the method:
        ObjectMetadataWriter objectMetadataWriter = new ObjectMetadataWriter();
        objectMetadataWriter.write(importContext);

        // Now verify:
        ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
        List<ObjectAcid> objectAcidList = objectAcidDAO.findAll();
        assertEquals("Value mismatch", objectAcidList.size(), 1);

        ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
        List<ObjectString> objectStrings = objectStringDAO.findAll();

        assert (objectStrings.size() == 1);

        ObjectAcid objectAcid = objectAcidList.get(0);

        assertEquals("Value mismatch", objectAcid.getFdid(), fdid1);
        //assertEquals("Value mismatch", objectAcid.getValue(), 1);

        //Verify the value of this acid:
        AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();
        AuthorityControl acid = authorityControlDAO.findByAcid(objectAcid.getValue());

        assertEquals("Value mimsatch", acid.getValue(), "Name of the rose");
    }

    @Before
    public void init() {
        logger.debug("Trying to init db");
        AbstractDBTest.initDB();

        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();
        ObjectStringDAO osDAO = new ObjectStringHibernateDAO();
        ObjectStringVersionDAO osvDAO = new ObjectStringVersionHibernateDAO();
        ObjectAcidVersionDAO oavDAO = new ObjectAcidVersionHibernateDAO();
        ObjectDAO objectDAO = new ObjectHibernateDAO();

        authDAO.deleteAll();
        osvDAO.deleteAll();
        oavDAO.deleteAll();
        oaDAO.deleteAll();
        osDAO.deleteAll();
        objectDAO.deleteAll();

    }

    @After
    public void stop() throws SQLException {
        //AbstractDBTest.stopDB();
        AuthorityControlDAO authDAO = new AuthorityControlHibernateDAO();
        ObjectAcidDAO oaDAO = new ObjectAcidHibernateDAO();
        ObjectStringDAO osDAO = new ObjectStringHibernateDAO();
        ObjectStringVersionDAO osvDAO = new ObjectStringVersionHibernateDAO();
        ObjectAcidVersionDAO oavDAO = new ObjectAcidVersionHibernateDAO();
        ObjectDAO objectDAO = new ObjectHibernateDAO();

        authDAO.deleteAll();
        osvDAO.deleteAll();
        oavDAO.deleteAll();
        oaDAO.deleteAll();
        osDAO.deleteAll();
        objectDAO.deleteAll();
    }

}
