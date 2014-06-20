package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.HydraPublish;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
import edu.yale.library.ladybird.persistence.dao.HydraPublishDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.HydraPublishHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.fail;


public class HydraProcessorTest extends AbstractDBTest {

    @Test
    public void shouldWrite() {

        final int oidToTest = 333993;

        try {

            ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();
            objectFileDAO.save(new ObjectFileBuilder().setOid(oidToTest).setDate(new Date()).setHydraPublishId(0).createObjectFile());

            HydraPublishDAO hydraPublishDAO = new HydraPublishHibernateDAO();
            HydraPublish hydraPublish = new HydraPublish();
            hydraPublish.setOid(oidToTest);
            hydraPublish.setAction("publish");
            hydraPublish.setDate(new Date());
            hydraPublishDAO.save(hydraPublish);

            HydraProcessor hydraProcessor = new HydraProcessor();
            hydraProcessor.write(getTestData());

            //verify:
            HydraPublish hydraPublishResult = hydraPublishDAO.findByOid(oidToTest);
            assert (hydraPublishResult.getAction().equalsIgnoreCase("delete"));

        } catch (Exception e) {
            e.printStackTrace();  //TODO
            fail(e.getMessage());
        }
    }

    private ImportEntityValue getTestData() {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        columns.add(getColumn(FunctionConstants.F1, "333993"));
        columns.add(getColumn(FunctionConstants.F40, "DELETE"));

        final ImportEntity.Row row = getRow(columns);
        final ImportEntityValue importEntityValue = new ImportEntityValue(Collections.singletonList(row));
        return importEntityValue;
    }

    private ImportEntity.Column getColumn(final FieldConstant f, final String value) {
        return new ImportEntity().new Column<>(f, value);
    }

    private ImportEntity.Row getRow(final List<ImportEntity.Column> columns) {
        ImportEntity.Row row = new ImportEntity().new Row();
        row.setColumns(columns);
        return row;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
        super.stop();
    }
}
