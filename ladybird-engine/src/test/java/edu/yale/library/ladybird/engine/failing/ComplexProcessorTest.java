package edu.yale.library.ladybird.engine.failing;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.imports.ComplexProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;


public class ComplexProcessorTest extends AbstractDBTest {

    final ObjectDAO dao = new ObjectHibernateDAO();

    @Test
    public void shouldProcessComplexF4() {
        Object parent = new ObjectBuilder().setDate(new Date()).createObject();
        Object child = new ObjectBuilder().setDate(new Date()).createObject();

        Object parent2 = new ObjectBuilder().setDate(new Date()).createObject();
        Object child2 = new ObjectBuilder().setDate(new Date()).createObject();

        int p1 = dao.save(parent);
        int c1p1 = dao.save(child);
        int p2 = dao.save(parent2);
        int c2p2 = dao.save(child2);

        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF4(getF4TestData(p1, c1p1, p2, c2p2));

        Object parentDB = dao.findByOid(p1);
        Object childDB = dao.findByOid(c1p1);
        Object parentDB2 = dao.findByOid(p2);
        Object childDB2 = dao.findByOid(c2p2);

        assert (parentDB.getP_oid() == 0);
        assert (parentDB.isParent());
        assert (childDB.getP_oid() == p1);
        assert (childDB.isChild());
        assert (parentDB2.getP_oid() == 0);
        assert (parentDB2.isParent());
        assert (childDB2.getP_oid() == p2);
        assert (childDB2.isChild());
    }

    @Test
    public void shouldProcessComplexF5() {
        final Date date = new Date();
        Object parent = new ObjectBuilder().setDate(date).createObject();
        Object child = new ObjectBuilder().setDate(date).createObject();

        Object parent2 = new ObjectBuilder().setDate(date).createObject();
        Object child2 = new ObjectBuilder().setDate(date).createObject();

        int p1 = dao.save(parent); //e.g. 0
        int c1p1 = dao.save(child); //e.g. 1
        int p2 = dao.save(parent2);
        int c2p2 = dao.save(child2);

        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF5(getF5TestData(p1, c1p1, p2, c2p2));

        Object parentDB = dao.findByOid(p1); //e.g. 0
        Object childDB = dao.findByOid(c1p1); //e.g 1

        System.out.println(childDB.toString());

        assert (parentDB.getP_oid() == 0);
        assert (parentDB.isParent());
        assert (childDB.getP_oid() == p1);
        assert (childDB.isChild());

        Object parentDB2 = dao.findByOid(p2);
        Object childDB2 = dao.findByOid(c2p2);

        assert (parentDB2.getP_oid() == 0);
        assert (parentDB2.isParent());
        assert (childDB2.getP_oid() == p2);
        assert (childDB2.isChild());
    }

    @Ignore("fixme")
    @Test
    public void shouldProcessComplexF7() {
        Object parent = new ObjectBuilder().setDate(new Date()).createObject();
        Object child = new ObjectBuilder().setDate(new Date()).createObject();

        Object parent2 = new ObjectBuilder().setDate(new Date()).createObject();
        Object child2 = new ObjectBuilder().setDate(new Date()).createObject();

        dao.save(parent);
        dao.save(child);
        dao.save(parent2);
        dao.save(child2);

        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF7(getF7TestData());

        Object parentDB = dao.findByOid(1);
        assert (parentDB != null);
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

    //F4
    private ImportEntityValue getF4TestData(int p1, int c1p1, int p2, int c2p2) {
        final ImportEntity.Row exHeadRow = getRow(getExheadCols(FunctionConstants.F1, FunctionConstants.F4, FunctionConstants.F6));
        assert (exHeadRow.getColumns().size() == 3);

        //content row 1 (parent)
        final List<ImportEntity.Column> cols1 = new ArrayList<>();
        cols1.add(getCol(FunctionConstants.F1, String.valueOf(p1))); //parent oid
        cols1.add(getCol(FunctionConstants.F4, "0"));
        cols1.add(getCol(FunctionConstants.F6, "0"));

        final ImportEntity.Row row1 = getRow(cols1);

        //content row 2 (child)
        final List<ImportEntity.Column> cols2 = new ArrayList<>();
        cols2.add(getCol(FunctionConstants.F1, String.valueOf(c1p1))); //child oid
        cols2.add(getCol(FunctionConstants.F4, String.valueOf(p1))); //parent oid
        cols2.add(getCol(FunctionConstants.F6, "1"));

        final ImportEntity.Row row2 = getRow(cols2);

        //content row 3 (parent 2)
        final List<ImportEntity.Column> cols3 = new ArrayList<>();
        cols3.add(getCol(FunctionConstants.F1, String.valueOf(p2))); //child oid
        cols3.add(getCol(FunctionConstants.F4, "0")); //parent oid
        cols3.add(getCol(FunctionConstants.F6, "0"));

        final ImportEntity.Row row3 = getRow(cols3);

        //content row 4 (child of parent 2)
        final List<ImportEntity.Column> cols4 = new ArrayList<>();
        cols4.add(getCol(FunctionConstants.F1, String.valueOf(c2p2))); //child oid
        cols4.add(getCol(FunctionConstants.F4, valueOf(p2))); //parent oid
        cols4.add(getCol(FunctionConstants.F6, "1"));

        final ImportEntity.Row row4 = getRow(cols4);

        //spreadsheet:
        final List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(exHeadRow);
        spreadsheetRows.add(row1);
        spreadsheetRows.add(row2);
        spreadsheetRows.add(row3);
        spreadsheetRows.add(row4);

        return  new ImportEntityValue(spreadsheetRows);
    }

    //F5
    private ImportEntityValue getF5TestData(int p1, int c1p1, int p2, int c2p2) {
        final ImportEntity.Row exHeadRow = getRow(getExheadCols(FunctionConstants.F1, FunctionConstants.F5, FunctionConstants.F6));
        assert (exHeadRow.getColumns().size() == 3); //not sure if f1 should be present for all

        //content row 1 (parent)
        final List<ImportEntity.Column> cols1 = new ArrayList<>();
        cols1.add(getCol(FunctionConstants.F1, String.valueOf(p1))); //parent oid
        cols1.add(getCol(FunctionConstants.F5, String.valueOf(p1))); //parent oid
        cols1.add(getCol(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow1 = getRow(cols1);

        //content row 2 (child)
        final List<ImportEntity.Column> cols2 = new ArrayList<>();
        cols2.add(getCol(FunctionConstants.F1, String.valueOf(c1p1))); //parent oid
        cols2.add(getCol(FunctionConstants.F5, String.valueOf(p1))); //parent oid
        cols2.add(getCol(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow2 = getRow(cols2);

        //content row 3 (parent 2)
        final List<ImportEntity.Column> cols3 = new ArrayList<>();
        cols3.add(getCol(FunctionConstants.F1, String.valueOf(p2))); //parent oid
        cols3.add(getCol(FunctionConstants.F5, String.valueOf(p2))); //parent oid
        cols3.add(getCol(FunctionConstants.F6, "0"));

        final ImportEntity.Row contentRow3 = getRow(cols3);

        //content row 4 (child of parent 2)
        final List<ImportEntity.Column> cols4 = new ArrayList<>();
        cols4.add(getCol(FunctionConstants.F1, String.valueOf(c2p2))); //parent oid
        cols4.add(getCol(FunctionConstants.F5, String.valueOf(p2))); //parent oid
        cols4.add(getCol(FunctionConstants.F6, "1"));

        final ImportEntity.Row contentRow4 = getRow(cols4);

        //spreadsheet:
        final List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(exHeadRow);
        spreadsheetRows.add(contentRow1);
        spreadsheetRows.add(contentRow2);
        spreadsheetRows.add(contentRow3);
        spreadsheetRows.add(contentRow4);

        return  new ImportEntityValue(spreadsheetRows);
    }

    //F7
    private ImportEntityValue getF7TestData() {
        final ImportEntity.Row exHeadRow = getRow(getExheadCols(FunctionConstants.F1, FunctionConstants.F6, FunctionConstants.F7, FunctionConstants.F8));
        assert (exHeadRow.getColumns().size() == 4); //not sure if f1 should be present for all

        //content row 1 (parent)
        final List<ImportEntity.Column> cols1 = new ArrayList<>();
        cols1.add(getCol(FunctionConstants.F1, "1")); //parent oid
        cols1.add(getCol(FunctionConstants.F6, "0")); //parent id
        cols1.add(getCol(FunctionConstants.F7, "1")); //random id
        cols1.add(getCol(FunctionConstants.F8, "0")); //parent id

        final ImportEntity.Row row1 = getRow(cols1);

        //content row 2 (child)
        final List<ImportEntity.Column> cols2 = new ArrayList<>();
        cols2.add(getCol(FunctionConstants.F1, "2")); //parent oid
        cols2.add(getCol(FunctionConstants.F6, "1"));
        cols2.add(getCol(FunctionConstants.F7, "2"));
        cols2.add(getCol(FunctionConstants.F8, "1"));

        final ImportEntity.Row row2 = getRow(cols2);

        //content row 3 (parent 2)
        final List<ImportEntity.Column> cols3 = new ArrayList<>();
        cols3.add(getCol(FunctionConstants.F1, "3")); //parent oid
        cols3.add(getCol(FunctionConstants.F6, "0"));
        cols3.add(getCol(FunctionConstants.F7, "3"));
        cols3.add(getCol(FunctionConstants.F8, "0"));

        final ImportEntity.Row row3 = getRow(cols3);

        //content row 4 (child of parent 2)
        final List<ImportEntity.Column> cols4 = new ArrayList<>();
        cols4.add(getCol(FunctionConstants.F1, "4")); //parent oid
        cols4.add(getCol(FunctionConstants.F6, "1"));
        cols4.add(getCol(FunctionConstants.F7, "4"));
        cols4.add(getCol(FunctionConstants.F8, "3"));

        final ImportEntity.Row row4 = getRow(cols4);

        //spreadsheet:
        final List<ImportEntity.Row> spreadsheetRows = new ArrayList<>();
        spreadsheetRows.add(exHeadRow);
        spreadsheetRows.add(row1);
        spreadsheetRows.add(row2);
        spreadsheetRows.add(row3);
        spreadsheetRows.add(row4);

        return  new ImportEntityValue(spreadsheetRows);
    }


    private List<ImportEntity.Column> getExheadCols(final FieldConstant... f) {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        for (FieldConstant fieldConstant: f) {
            columns.add(new ImportEntity().new Column<>(fieldConstant, ""));
        }
        return columns;
    }

    private ImportEntity.Column getCol(final FieldConstant f, final String value) {
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
