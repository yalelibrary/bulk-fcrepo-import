package edu.yale.library.ladybird.engine.failing;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.imports.OidMinter;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OidMinterTest extends AbstractDBTest {

    @Test
    public void shouldMint() {
        OidMinter oidMinter = new OidMinter();
        ImportEntityValue modifiedImportEntityValue = oidMinter.write(getTestData(), 1);
        assert (modifiedImportEntityValue.getHeaderRow().getColumns().size() == 2);
        assertEquals(modifiedImportEntityValue.getContentRows().size(), 1);
        List<ImportEntity.Row> rowsBack = modifiedImportEntityValue.getContentRows();
        ImportEntity.Row firstRow = rowsBack.get(0);
        List<ImportEntity.Column> listcols = firstRow.getColumns();
        assert (listcols.get(0).getValue().equals("The Wizard of Oz."));
    }

    private ImportEntityValue getTestData() {
        final List<ImportEntity.Column> columns = new ArrayList<>();

        columns.add(getColumn(new FieldDefinition(70, "Title"), ""));
        final ImportEntity.Row row = getRow(columns);

        final List<ImportEntity.Column> listColumns = new ArrayList<>();
        listColumns.add(getColumn(new FieldDefinition(70, "Title"), "The Wizard of Oz.")); //content

        final ImportEntity.Row contentRow = getRow(listColumns);

        List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(row);
        spreadsheetRows.add(contentRow);

        final ImportEntityValue importEntityValue = new ImportEntityValue(spreadsheetRows);

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
        super.init();
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
        super.stop();
    }
}
