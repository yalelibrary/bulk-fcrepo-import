package edu.yale.library.ladybird.engine.file;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.exports.ExportReader;
import edu.yale.library.ladybird.engine.exports.ExportReaderOaiMerger;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.engine.imports.ImportContext;
import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.oai.ImportSourceDataReader;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldDefinitionBuilder;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import edu.yale.library.ladybird.kernel.GuiceModule;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * FIXME It's an abstract DB test because the DAO for shouldGetMultiMapValue() is not currently being injected, e.g.
 */
public class ExportReaderIT extends AbstractDBTest {


    //TODO
    @Test
    public void shouldReadRowsFromImportTable() {
        KernelBootstrap kernelBootstrap = new KernelBootstrap();
        kernelBootstrap.setAbstractModule(new GuiceModule());

        ExportEngineQueue.addJob(new ExportRequestEvent());


        ExportReader exportReader = new ExportReader();
        ImportContext importContext = exportReader.read();
        assert (importContext.getImportRowsList().size() == 0);
    }

    @Test
    public void shouldConvertFunctionStringToFieldConst() {
        final FieldConstant f = FieldConstantUtil.toFieldConstant("F1");
        assert (f != null);
        assertEquals("Value mismatch", f.getName(), "F1");

        try {
            final FunctionConstants f1  = FunctionConstants.valueOf("F1");
            assertEquals("Value mismatch", f1.getName(), "F1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetMultiMapValue() {
        initMarcMappingDB(); //FIXME Inst. db because ExportReader creates a new FdidMarcMappingUtil object

        final List<FieldConstant> globalFConstantsList = fakeGlobalApplicationFieldConstants();

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        final Multimap<Marc21Field, Map<String, String>> map = importSourceDataReader.buildMultimap(fakeImportSourceData());

        //logger.debug("Map={}", map.toString());


        ExportReaderOaiMerger exportReaderOaiMerger = new ExportReaderOaiMerger();


        for (FieldConstant f: globalFConstantsList) {
            Marc21Field marc21Field = new FdidMarcMappingUtil().toMarc21Field(f);
            String s = exportReaderOaiMerger.getMultimapMarc21Field(marc21Field, map);
            //logger.debug("Value={}", s);
            assertEquals("Value mismatch", s, "Test value");
        }
    }

    private void initMarcMappingDB() {
        FdidMarcMappingUtil fdidMarcMappingUtil = new FdidMarcMappingUtil();
        try {
            fdidMarcMappingUtil.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO()); //TODO
            fdidMarcMappingUtil.setInitialFieldMarcDb();
        } catch (Exception e) {
            e.printStackTrace();  //TODO
        }
    }

    private List<ImportSourceData> fakeImportSourceData() {
        List<ImportSourceData> list = new ArrayList<>();

        ImportSourceData importSourceData = new ImportSourceDataBuilder().setK1("245").setK2("a").setAttr("a").setValue("Test value").setAttrVal("a").createImportSourceData();
        list.add(importSourceData);

        return list;
    }

    private List<FieldConstant> fakeGlobalApplicationFieldConstants() {
        final List<FieldConstant> fieldConstants = new ArrayList<>();
        final FieldDefinition fieldDefintion = new FieldDefinitionBuilder()
                .setFdid(70).setAcid(105457).setDate(new Date()).setHandle("Fake field").createFieldDefinition();
        fieldConstants.add(fieldDefintion);
        return fieldConstants;
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
