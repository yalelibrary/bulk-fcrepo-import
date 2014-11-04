package edu.yale.library.ladybird.engine.file;


import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.imports.ImageReferenceProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ImageReferenceProcessorTest extends AbstractDBTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Ignore("until mocked")
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

        objectFileDAO.deleteAll();
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
        logger.debug("Trying to init db");
        AbstractDBTest.initDB();
    }

    @After
    public void stop() throws SQLException {
        //AbstractDBTest.stopDB();
    }

}
