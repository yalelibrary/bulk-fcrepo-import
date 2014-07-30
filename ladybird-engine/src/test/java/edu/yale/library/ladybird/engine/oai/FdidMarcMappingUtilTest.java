package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.FieldMarcMappingBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FdidMarcMappingUtilTest {

    @Test
    public void shouldGetMarc21Field() {

        //mock:
        FdidMarcMappingUtil fieldMarcMappingUtil = new FdidMarcMappingUtil();
        fieldMarcMappingUtil.setFieldMarcMappingDAO(new FieldMarcMappingDAO() {

            private List<FieldMarcMapping> list = new ArrayList<>();

            @Override
            public FieldMarcMapping findByFdid(int fdid) {

                for (FieldMarcMapping f: list) {
                    if (f.getFdid() == fdid) {
                        return f;
                    }
                }

                return new FieldMarcMapping();
            }

            @Override
            public List<FieldMarcMapping> find(int rowNum, int count) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<FieldMarcMapping> findAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer save(FieldMarcMapping entity) {
                list.add(entity);
                return list.size() - 1;
            }

            @Override
            public void saveOrUpdateList(List<FieldMarcMapping> itemList) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int count() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void delete(List<FieldMarcMapping> entities) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void saveOrUpdateItem(FieldMarcMapping item) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void updateItem(FieldMarcMapping item) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void saveList(List<FieldMarcMapping> itemList) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        FieldMarcMapping fieldMarcMapping = new FieldMarcMappingBuilder().setK1("245").setFdid(70).createFieldMarcMapping();
        fieldMarcMappingUtil.getFieldMarcMappingDAO().save(fieldMarcMapping);

        FieldConstant f = new FieldDefinition(70, "Mapped Field #1");
        Marc21Field marc21Field = fieldMarcMappingUtil.toMarc21Field(f);

        //logger.debug("Value={}", marc21Field.toString());

        assert (marc21Field == Marc21Field._245);

        FieldMarcMapping fieldMarcMapping2 = new FieldMarcMappingBuilder().setK1("500").setFdid(69).createFieldMarcMapping();
        fieldMarcMappingUtil.getFieldMarcMappingDAO().save(fieldMarcMapping2);

        FieldConstant f3 = new FieldDefinition(69, "Mapped Field #2");
        Marc21Field marc21Field3 = fieldMarcMappingUtil.toMarc21Field(f3);

        assert (marc21Field3 == Marc21Field._500);

        FieldConstant f2 = new FieldDefinition(88, "UnMapped Field");
        Marc21Field marc21Field2 = fieldMarcMappingUtil.toMarc21Field(f2);

        //logger.debug("Value={}", marc21Field2.toString());
        assert (marc21Field2 == Marc21Field.UNK);
    }
}
