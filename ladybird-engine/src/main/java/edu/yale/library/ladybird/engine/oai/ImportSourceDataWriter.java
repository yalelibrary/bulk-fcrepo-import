package edu.yale.library.ladybird.engine.oai;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Persists MARC related data to import source data tables
 * @see ImportSourceDataReader
 */
public class ImportSourceDataWriter {

    final ImportSourceDataDAO dao = new ImportSourceDataHibernateDAO(); //TODO

     /**
     * Persists MARC data to import source data tables. Used by @see ImportWriter
     * @param list list of LocalIdMarcImportSource
     * @param importId import id of the job (not used)
     */
    public void write(final List<LocalIdMarcImportSource> list, final int importId) {

        for (LocalIdMarcImportSource item: list) {
            Multimap<Marc21Field, ImportSourceData> m  = item.getValueMap();

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
