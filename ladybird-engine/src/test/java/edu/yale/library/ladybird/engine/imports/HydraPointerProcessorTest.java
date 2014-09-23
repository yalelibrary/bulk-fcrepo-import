package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;

public class HydraPointerProcessorTest extends AbstractDBTest {


    @Test
    public void shouldProcess() {
        try {

            ObjectDAO objectDAO = new ObjectHibernateDAO();
            Object obj = new ObjectBuilder().createObject();
            obj.setDate(new Date());
            int id = objectDAO.save(obj);

            HydraPointerProcessor hydraPointerProcessor = new HydraPointerProcessor();
            hydraPointerProcessor.write(getTestData(id));

            Object obj2 = objectDAO.findByOid(id);
            assert (obj2.getOid() == id);

            final int oidToTest = id;

            assert (obj2.getRoid() == oidToTest);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private ImportEntityValue getTestData(int oid) {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        columns.add(getColumn(FunctionConstants.F1, String.valueOf(oid)));
        columns.add(getColumn(FunctionConstants.F11, String.valueOf(oid)));

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
        //super.stop();
    }
}
