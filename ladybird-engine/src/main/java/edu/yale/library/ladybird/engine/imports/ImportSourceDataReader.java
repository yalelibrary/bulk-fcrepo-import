package edu.yale.library.ladybird.engine.imports;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdMarcValue;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.engine.oai.DatafieldType;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.MarcReadingException;
import edu.yale.library.ladybird.engine.oai.OaiHttpClient;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.engine.oai.Record;
import edu.yale.library.ladybird.engine.oai.SubfieldType;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for reading import source data into LocalIdMarcValue
 */
public class ImportSourceDataReader {

    private Logger logger = LoggerFactory.getLogger(ImportSourceDataReader.class);

    final ImportSourceDataDAO importSourceDataDAO = new ImportSourceDataHibernateDAO();

    // --------------------------------------------------------------
    // Used by ImportWriter
    //---------------------------------------------------------------

    /**
     * Hits OAI feed and gets a Record
     *
     * @param bibIds
     * @return List<LocalIdMarcImportSource> a list of data structures containing the mappings
     */
    public List<LocalIdMarcImportSource> readBibIdMarcData(final OaiProvider oaiProvider,
                                                           final List<LocalIdentifier<String>> bibIds,
                                                           final int importId) {
        //Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdMarcValues
        final List<LocalIdMarcImportSource> list = new ArrayList<>();

        logger.debug("Reading marc data for the bibIds");

        for (final LocalIdentifier<String> localIdentifier : bibIds) {
            final OaiHttpClient oaiClient = new OaiHttpClient(oaiProvider);
            try {
                final Record recordForBibId = oaiClient.readMarc(localIdentifier.getId()); //Read OAI feed
                final Multimap<Marc21Field, ImportSourceData> marc21Values = buildMultiMap(localIdentifier, recordForBibId, importId);

                LocalIdMarcImportSource localIdMarcImportSource = new LocalIdMarcImportSource();
                localIdMarcImportSource.setBibId(localIdentifier);
                localIdMarcImportSource.setValue(marc21Values);
                list.add(localIdMarcImportSource);
            } catch (IOException e) {
                logger.error("Error reading source", e);
            } catch (MarcReadingException e) {
                logger.error("Error reading oai marc record", e);
            }
        }
        logger.debug("Returning list size={}", list.size());
        return list;
    }

    /**
     * TODO test
     * @see ImportSourceDataReader#readBibIdMarcData(edu.yale.library.ladybird.engine.oai.OaiProvider, java.util.List, int)
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

            //Get subfields:
            final List<SubfieldType> subfieldTypeList = type.getSubfield();

            //Get k2 values
            for (final SubfieldType s : subfieldTypeList) {
                final String code = s.getCode(); //e.g "a", "c", "d"
                final String codeValue = s.getValue(); //e.g. "(oOCoLC) ocn709288147"

                final ImportSourceData importSourceData =
                        new ImportSourceDataBuilder().setK1(tag).setK2(code).setValue(codeValue)
                                .setLocalidentifier(localIdentifier.getId())
                                .setDate(new Date())
                                .setImportSourceId(importId)
                                .createImportSourceData();
                attrMap.put(Marc21Field.getMar21FieldForString(tag), importSourceData);
            }
        }
        return attrMap;
    }

    // --------------------------------------------------------------
    // Used by ExportReader
    //---------------------------------------------------------------

    /**
     * TODO test
     * Converts to List<LocalIdMarcValue>  , v=map(s,t), where s=Mar21Tag,t=Marc21DataFieldType
     * @param importId import id of job
     * @return list of localIdMarcValues
     */
    public List<LocalIdMarcValue> getLocalIdMarcValueList(final int importId) {

        List<LocalIdMarcValue> list = new ArrayList<>();

        //find all the entries for this import
        List<ImportSourceData> importSourceDataList = importSourceDataDAO.findByImportId(importId);

        for (ImportSourceData importSourceData: importSourceDataList) {
            //for each entry, fetch the bibId
            String bibId = importSourceData.getLocalidentifier();

            //find all the entries for this bib and this import
            List<ImportSourceData> valuesForBibId = importSourceDataDAO.findByImportIdAndLocalIdentifier(importId, bibId);

            //Get map representation for this bibids values
            Multimap multimap = marshallMarcData(valuesForBibId);

            //construct LocalIdMarcValue
            LocalIdMarcValue localIdMarcValue = new LocalIdMarcValue();
            localIdMarcValue.setBibId(new LocalIdentifier<>(bibId));
            localIdMarcValue.setValue(multimap);

            list.add(localIdMarcValue);
        }
        return list;
    }

    /**
     * Re-creates multimap from table
     *
     * @see #getLocalIdMarcValueList(int) for invocation
     * @param importSourceDataList list of importsourcedata
     * @return <code> MultiMap(245 => {f,"dd"} {a,"title"} {6,"1797"}). </code>
     */
    public Multimap<Marc21Field, Map<String, String>> marshallMarcData(final List<ImportSourceData> importSourceDataList) {
        logger.trace("Marshalling marc data from import source data.");

        //populate map (e.g. (245,{a,"text"}), (245, {b,"text b"}). This map will then be read.
        final Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create(); //k=tag, v={subfield,value}

        for (int i = 0; i < importSourceDataList.size(); i++) {
            final ImportSourceData entry = importSourceDataList.get(i);
            final String k1 = entry.getK1();

            switch (k1) {
                case "880":
                    logger.trace("Ignoring field 880");
                    break;
                default:
                    logger.trace("Putting field={} value={}", k1, entry.getValue());
                    final Map<String, String> attrValue = new HashMap<>();
                    attrValue.put(entry.getK2(), entry.getValue());
                    map.put(Marc21Field.valueOfTag(k1), attrValue);
                    break;
            }
        }
        return map;
    }

}
