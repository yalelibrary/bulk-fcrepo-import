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
import java.util.Date;
import java.util.List;


public class ComplexProcessorTest extends AbstractDBTest {

    final ObjectDAO dao = new ObjectHibernateDAO();

    @Test
    public void shouldProcessComplexF4() {
        Object parent = new ObjectBuilder().setDate(new Date()).createObject();
        Object child = new ObjectBuilder().setDate(new Date()).createObject();

        Object parent2 = new ObjectBuilder().setDate(new Date()).createObject();
        Object child2 = new ObjectBuilder().setDate(new Date()).createObject();


        dao.save(parent);
        dao.save(child);
        dao.save(parent2);
        dao.save(child2);

        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF4(getF4TestData());

        Object parentDB = dao.findByOid(1);
        Object childDB = dao.findByOid(2);
        Object parentDB2 = dao.findByOid(3);
        Object childDB2 = dao.findByOid(4);

        assert (parentDB.getP_oid() == 0);
        assert (parentDB.isParent());
        assert (childDB.getP_oid() == 1);
        assert (childDB.isChild());
        assert (parentDB2.getP_oid() == 0);
        assert (parentDB2.isParent());
        assert (childDB2.getP_oid() == 3);
        assert (childDB2.isChild());
    }

    @Test
    public void shouldProcessComplexF5() {
        Object parent = new ObjectBuilder().setDate(new Date()).createObject();
        Object child = new ObjectBuilder().setDate(new Date()).createObject();

        Object parent2 = new ObjectBuilder().setDate(new Date()).createObject();
        Object child2 = new ObjectBuilder().setDate(new Date()).createObject();

        dao.save(parent);
        dao.save(child);
        dao.save(parent2);
        dao.save(child2);

        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF5(getF5TestData());

        Object parentDB = dao.findByOid(1);
        assert (parentDB != null);
        Object childDB = dao.findByOid(2);
        Object parentDB2 = dao.findByOid(3);
        Object childDB2 = dao.findByOid(4);

        System.out.println(parentDB.toString());

        assert (parentDB.getP_oid() == 0);
        assert (parentDB.isParent());
        assert (childDB.getP_oid() == 1);
        assert (childDB.isChild());
        assert (parentDB2.getP_oid() == 0);
        assert (parentDB2.isParent());
        assert (childDB2.getP_oid() == 3);
        assert (childDB2.isChild());
    }

    //F4
    private ImportEntityValue getF4TestData() {
        final ImportEntity.Row exHeadRow = getRow(getExheadCols(FunctionConstants.F1, FunctionConstants.F4, FunctionConstants.F6));
        assert (exHeadRow.getColumns().size() == 3);

        //content row 1 (parent)
        final List<ImportEntity.Column> contentColumns = new ArrayList<>();
        contentColumns.add(getColumn(FunctionConstants.F1, "1")); //parent oid
        contentColumns.add(getColumn(FunctionConstants.F4, "0"));
        contentColumns.add(getColumn(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow1 = getRow(contentColumns);

        //content row 2 (child)
        final List<ImportEntity.Column> contentColumns2 = new ArrayList<>();
        contentColumns2.add(getColumn(FunctionConstants.F1, "2")); //child oid
        contentColumns2.add(getColumn(FunctionConstants.F4, "1")); //parent oid
        contentColumns2.add(getColumn(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow2 = getRow(contentColumns2);

        //content row 3 (parent 2)
        final List<ImportEntity.Column> contentColumns3 = new ArrayList<>();
        contentColumns3.add(getColumn(FunctionConstants.F1, "3")); //child oid
        contentColumns3.add(getColumn(FunctionConstants.F4, "0")); //parent oid
        contentColumns3.add(getColumn(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow3 = getRow(contentColumns3);

        //content row 4 (child of parent 2)
        final List<ImportEntity.Column> contentColumns4 = new ArrayList<>();
        contentColumns4.add(getColumn(FunctionConstants.F1, "4")); //child oid
        contentColumns4.add(getColumn(FunctionConstants.F4, "3")); //parent oid
        contentColumns4.add(getColumn(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow4 = getRow(contentColumns4);

        //spreadsheet:
        final List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(exHeadRow);
        spreadsheetRows.add(contentRow1);
        spreadsheetRows.add(contentRow2);
        spreadsheetRows.add(contentRow3);
        spreadsheetRows.add(contentRow4);

        return  new ImportEntityValue(spreadsheetRows);
    }

    //F5
    private ImportEntityValue getF5TestData() {
        final ImportEntity.Row exHeadRow = getRow(getExheadCols(FunctionConstants.F1, FunctionConstants.F5, FunctionConstants.F6));
        assert (exHeadRow.getColumns().size() == 3); //not sure if f1 should be present for all

        //content row 1 (parent)
        final List<ImportEntity.Column> contentColumns = new ArrayList<>();
        contentColumns.add(getColumn(FunctionConstants.F1, "1")); //parent oid
        contentColumns.add(getColumn(FunctionConstants.F5, "0")); //parent oid
        contentColumns.add(getColumn(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow1 = getRow(contentColumns);

        //content row 2 (child)
        final List<ImportEntity.Column> contentColumns2 = new ArrayList<>();
        contentColumns2.add(getColumn(FunctionConstants.F1, "2")); //parent oid
        contentColumns2.add(getColumn(FunctionConstants.F5, "1")); //parent oid
        contentColumns2.add(getColumn(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow2 = getRow(contentColumns2);

        //content row 3 (parent 2)
        final List<ImportEntity.Column> contentColumns3 = new ArrayList<>();
        contentColumns3.add(getColumn(FunctionConstants.F1, "3")); //parent oid
        contentColumns3.add(getColumn(FunctionConstants.F5, "0")); //parent oid
        contentColumns3.add(getColumn(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow3 = getRow(contentColumns3);

        //content row 4 (child of parent 2)
        final List<ImportEntity.Column> contentColumns4 = new ArrayList<>();
        contentColumns4.add(getColumn(FunctionConstants.F1, "4")); //parent oid
        contentColumns4.add(getColumn(FunctionConstants.F5, "3")); //parent oid
        contentColumns4.add(getColumn(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow4 = getRow(contentColumns4);

        //spreadsheet:
        final List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(exHeadRow);
        spreadsheetRows.add(contentRow1);
        spreadsheetRows.add(contentRow2);
        spreadsheetRows.add(contentRow3);
        spreadsheetRows.add(contentRow4);

        return  new ImportEntityValue(spreadsheetRows);
    }

    private List<ImportEntity.Column> getExheadCols(final FieldConstant... f) {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        for (FieldConstant fieldConstant: f) {
            columns.add(new ImportEntity().new Column<>(fieldConstant, ""));
        }
        return columns;
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
