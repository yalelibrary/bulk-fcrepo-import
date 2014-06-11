package edu.yale.library.ladybird.engine.imports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Persists data to import source data tables
 */
public class ImportSourceDataWriter {

    private Logger logger = LoggerFactory.getLogger(ImportSourceDataWriter.class);

    final ImportSourceDataDAO dao = new ImportSourceDataHibernateDAO(); //TODO

     /**
     * Persists MARC data to import source data tables
     * @param list
     * @param importId
     */
    public void persistMarcData(final List<LocalIdMarcImportSource> list,
                                final int importId) {

        for (LocalIdMarcImportSource item: list) {
            Multimap<Marc21Field, ImportSourceData> m  = item.getValue();

            Set<Marc21Field> marc21FieldsKeySet = m.keySet();

            for (Marc21Field marc21Field : marc21FieldsKeySet) {
                Collection<ImportSourceData> importSourceDataList = m.get(marc21Field);

                for (ImportSourceData importSourceData: importSourceDataList) {
                    dao.save(importSourceData);
                }
            }
        }
    }
}
