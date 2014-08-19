package edu.yale.library.ladybird.engine.oai;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdMarcValue;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for manipulating (serialization and de-serialization of) import source data.
 * @see ImportSourceDataWriter
 */
public class ImportSourceDataReader {

    private Logger logger = LoggerFactory.getLogger(ImportSourceDataReader.class);

    final ImportSourceDataDAO importSrcDataDAO = new ImportSourceDataHibernateDAO(); //TODO

    // --------------------------------------------------------------
    // Used by ImportWriter
    //---------------------------------------------------------------

    /**
     * Hits OAI feed and gets a Record. A multimap is then used to relate the record's data to the local identifier.
     *
     * @param localIdentifierList list of LocalIdentifier (bibids or barcodes)
     * @return List<LocalIdMarcImportSource> a list of data structures containing the mappings. Ignores exception.
     */
    public List<LocalIdMarcImportSource> readMarc(final OaiProvider oaiProvider,
                                                  final List<LocalIdentifier<String>> localIdentifierList,
                                                  final int importId) throws IOException, MarcReadingException {
        final List<LocalIdMarcImportSource> list = new ArrayList<>();

        for (final LocalIdentifier<String> localId : localIdentifierList) {
            final OaiHttpClient oaiClient = new OaiHttpClient(oaiProvider);
            try {
                logger.trace("Reading marc feed for local identifier={}", localId.getId());

                final Record record = oaiClient.readMarc(localId.getId()); //Read OAI feed

                final Multimap<Marc21Field, ImportSourceData> marc21Values = buildMultiMap(localId, record, importId);

                LocalIdMarcImportSource localIdMarcImportSource = new LocalIdMarcImportSource();
                localIdMarcImportSource.setBibId(localId);
                localIdMarcImportSource.setValueMap(marc21Values);
                list.add(localIdMarcImportSource);
            } catch (IOException|MarcReadingException|IllegalArgumentException e) {
                logger.error("Error reading import source");
                throw e;
            }
        }
        return list;
    }

    /**
     * Helps in building a multimap of Marc21Field and ImportSourceData
     *
     * @see ImportSourceDataReader#readMarc(edu.yale.library.ladybird.engine.oai.OaiProvider, java.util.List, int)
     * @param record MarcRecord
     * @return a map(k,v) where k=Tag, v=ImportSourceData
     */
    public Multimap<Marc21Field, ImportSourceData> buildMultiMap(final LocalIdentifier<String> localIdentifier,
                                                                  final Record record,
                                                                  final int importId) {
        final List<DatafieldType> datafieldTypeList = record.getDatafield();
        final Multimap<Marc21Field, ImportSourceData> attrMap = HashMultimap.create();

        for (final DatafieldType type : datafieldTypeList) {
            final String tag = type.getTag();

            //Get SubFieldType:
            final List<SubfieldType> subfieldTypeList = type.getSubfield();

            //Get k2 values:
            for (final SubfieldType s : subfieldTypeList) {
                final String code = s.getCode(); //e.g "a", "c", "d"
                final String codeValue = s.getValue(); //e.g. "(oOCoLC) ocn709288147"

                final ImportSourceData importSourceData = new ImportSourceDataBuilder().setK1(tag).setK2(code)
                        .setValue(codeValue).setLocalidentifier(localIdentifier.getId()).setDate(new Date())
                        .setImportSourceId(importId).createImportSourceData();

                attrMap.put(Marc21Field.getMar21FieldForString(tag), importSourceData);
            }
        }
        return attrMap;
    }

    // --------------------------------------------------------------
    // Used by ExportReader
    //---------------------------------------------------------------

    /**
     * Converts to List<LocalIdMarcValue>, v=map(s,t), where s=Mar21Tag,t=Marc21DataFieldType
     * @param importId import id of job
     * @return list of localIdMarcValues or empty list. Ignores exceptions.
     */
    public List<LocalIdMarcValue> readImportSourceData(final int importId) {

        try {
            Preconditions.checkNotNull(importId);

            List<LocalIdMarcValue> list = new ArrayList<>();

            //find all the entries for this import
            List<ImportSourceData> importSourceDataList = importSrcDataDAO.findByImportId(importId);

            for (ImportSourceData importSourceData: importSourceDataList) {
                //for each entry, fetch the bibId or barcode
                String localId = importSourceData.getLocalidentifier();

                //find all the entries for this identifier and this import
                List<ImportSourceData> listForId = importSrcDataDAO.findByImportIdAndLocalIdentifier(importId, localId);

                //Get map representation
                Multimap multimap = buildMultimap(listForId);

                //construct LocalIdMarcValue
                LocalIdMarcValue localIdMarcValue = new LocalIdMarcValue();
                localIdMarcValue.setBibId(new LocalIdentifier<>(localId));
                localIdMarcValue.setValueMap(multimap);

                list.add(localIdMarcValue);
            }
            return list;
        } catch (Exception e) {
            logger.trace("Error={}", e);
            return Collections.emptyList(); //ignore
        }
    }

    /**
     * Helps in re-creating multimap from table
     *
     * @see #readImportSourceData(int) for invocation
     * @param importSrcDataList list of ImportSourceData
     * @return <code> MultiMap(245 => {f,"dd"} {a,"title"} {6,"1797"}). </code>
     */
    public Multimap<Marc21Field, Map<String, String>> buildMultimap(final List<ImportSourceData> importSrcDataList) {
        //populate map (e.g. (245,{a,"text"}), (245, {b,"text b"}):
        final Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create(); //k=tag, v={subfield,value}

        for (int i = 0; i < importSrcDataList.size(); i++) {
            final ImportSourceData importEntry = importSrcDataList.get(i);
            final String k1 = importEntry.getK1();

            switch (k1) {
                case "880":
                    logger.trace("Ignoring field 880");
                    break;
                default:
                    logger.trace("Putting in multimap field={} value={}", k1, importEntry.getValue());
                    final Map<String, String> attrValue = new HashMap<>();
                    attrValue.put(importEntry.getK2(), importEntry.getValue());
                    map.put(Marc21Field.valueOfTag(k1), attrValue);
                    break;
            }
        }
        return map;
    }
}
