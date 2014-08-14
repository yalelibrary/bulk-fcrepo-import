package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ImageReferenceProcessorTest extends AbstractDBTest {

    @Test
    public void shouldProcessF300() {
        ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();
        ObjectFile obj = new ObjectFile();

        obj.setDate(new Date());
        obj.setOid(1);
        int id = objectFileDAO.save(obj);
        assert (id == 1);

        ImageReferenceProcessor imageReferenceProcessor = new ImageReferenceProcessor();
        imageReferenceProcessor.write(getTestData());

        ObjectFile obj2 = objectFileDAO.findByOid(id);
        assert (obj2.getFilePath().equals("http://www.nasa.gov"));
    }

    private ImportEntityValue getTestData() {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        columns.add(getColumn(FunctionConstants.F1, "1"));
        columns.add(getColumn(FunctionConstants.F300, "http://www.nasa.gov"));

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
