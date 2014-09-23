package edu.yale.library.ladybird.engine.failing;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.imports.DeleteProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.ObjectBuilder;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DeleteProcessorTest extends AbstractDBTest {

    @Test
    public void shouldDelete() {
        ObjectDAO objectDAO = new ObjectHibernateDAO();
        edu.yale.library.ladybird.entity.Object obj = new ObjectBuilder().createObject();

        obj.setDate(new Date());

        int oid = objectDAO.save(obj);

        assert (objectDAO.findByOid(oid).getOid().equals(oid));

        DeleteProcessor deleteProcessor = new DeleteProcessor();
        deleteProcessor.process(getTestData(oid));

        assert (objectDAO.findByOid(oid) == null);
    }

    private ImportEntityValue getTestData(int oid) {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        columns.add(getColumn(FunctionConstants.F1, String.valueOf(oid)));
        columns.add(getColumn(FunctionConstants.F00, "CONFIRM"));

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

    @After
    public void stop() throws SQLException {
        //super.stop();
    }

}
