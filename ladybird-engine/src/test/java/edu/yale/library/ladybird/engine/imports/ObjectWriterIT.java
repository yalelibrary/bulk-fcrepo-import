package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectWriterIT extends AbstractDBTest {
    private Logger logger = LoggerFactory.getLogger(ObjectWriterIT.class);

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }

    /**
     * @see ObjectWriter creates object metadata tables and hits db to ensure that the values are written and
     * corresonding acid value is created
     */
    @Test
    public void shouldWriteToObjectMetadataTabes() {

        //Populate our ImportEntityContext

        //1. monitor
        Monitor monitor = new Monitor();
        User user = new User();
        user.setUserId(0);
        monitor.setUser(user);

        //2. populate list of import rows

        final List<ImportEntity.Row> rowList = new ArrayList<>();


        //a. exhead
        final ImportEntity.Row exHeadRow = new ImportEntity().new Row();
        final List<ImportEntity.Column> exHeadColumns = new ArrayList<>();

        ImportEntity.Column oidExHeadColumn = new ImportEntity().new Column(FunctionConstants.F1, "");
        exHeadColumns.add(oidExHeadColumn);

        FieldDefinition fieldDefinition = new FieldDefinition(69, "69");
        ImportEntity.Column fieldExHeadColumn = new ImportEntity().new Column<>(fieldDefinition, "");

        exHeadColumns.add(fieldExHeadColumn);

        exHeadRow.setColumns(exHeadColumns);

        rowList.add(exHeadRow);

        //b. content row

        final ImportEntity.Row contentRow = new ImportEntity().new Row();
        List<ImportEntity.Column> contentColumns = new ArrayList<>();

        ImportEntity.Column oidContentColumn = new ImportEntity().new Column(FunctionConstants.F1, "777");
        contentColumns.add(oidContentColumn);

        FieldDefinition fieldDefinitionContent = new FieldDefinition(69, "69");
        ImportEntity.Column fieldContentColumn = new ImportEntity().new Column<>(fieldDefinitionContent, "Name of the rose");
        contentColumns.add(fieldContentColumn);

        contentRow.setColumns(contentColumns);

        rowList.add(contentRow);

        //3. Init Object
        ImportEntityContext importEntityContext = new ImportEntityContext();
        importEntityContext.setMonitor(monitor);
        importEntityContext.setImportJobList(rowList);

        // Finally, test the method:
        ObjectWriter objectWriter = new ObjectWriter();
        objectWriter.write(importEntityContext);

        // Now verify:

        ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
        List<ObjectAcid> objectAcidList = objectAcidDAO.findAll();
        assertEquals("Value mismatch", objectAcidList.size(), 1);

        logger.debug(objectAcidList.toString());


        ObjectAcid objectAcid = objectAcidList.get(0);

        assertEquals("Value mismatch", objectAcid.getFdid(), 69);
        assertEquals("Value mismatch", objectAcid.getValue(), 1);

        //Verify the value of this acid:

        AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();
        AuthorityControl acid = authorityControlDAO.findByAcid(1);

        assertEquals("Value mimsatch", acid.getValue(), "Name of the rose");
    }
}
