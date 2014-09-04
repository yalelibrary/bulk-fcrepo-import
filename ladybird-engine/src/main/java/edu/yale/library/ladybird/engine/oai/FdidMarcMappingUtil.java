package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.FieldMarcMappingBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FdidMarcMappingUtil {

    private static Logger logger = LoggerFactory.getLogger(FdidMarcMappingUtil.class);

    FieldMarcMappingDAO fieldMarcMappingDAO;

    /** for now simple initialization from a text file */
    public void setInitialFieldMarcDb() throws Exception {
        InputStream f = this.getClass().getResourceAsStream("/marc-mappings-int.txt");

        if (fieldMarcMappingDAO.count() > 1) {
            logger.info("Fdid marc mapping init already.");
            return;
        }

        try {
            Scanner sc = new Scanner(f);
            String s;

            while ((s = sc.nextLine()) != null) {
                String[] t = s.split("\t");
                fieldMarcMappingDAO.save(newFdid(t[0], Integer.parseInt(t[3]))); //e.g. 245, 70
            }
        } catch (NoSuchElementException e1) {
            logger.trace(e1.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    /** helper */
    private FieldMarcMapping newFdid(String k1, int fdid) {
        return new FieldMarcMappingBuilder().setK1(k1).setK2("a").setDate(new Date()).setFdid(fdid).createFieldMarcMapping();
    }

    /**
     * FIXME needs to be static perhaps, but has a reference to DAO
     * Returns Marc21Field mapped value or Marc21Field.UNK
     * @param fieldConstant
     * @return
     */
    public Marc21Field toMarc21Field(FieldConstant fieldConstant) {
        try {
            //Try converting to (integer) fdid:
            int fdid = FieldDefinition.fdidAsInt(fieldConstant.getName());

            if (fieldMarcMappingDAO == null) { //TODO
                fieldMarcMappingDAO = new FieldMarcMappingHibernateDAO();
            }

            FieldMarcMapping fieldMarcMapping = fieldMarcMappingDAO.findByFdid(fdid);
            Marc21Field marc21Field = Marc21Field.valueOfTag(fieldMarcMapping.getK1());
            return marc21Field;
        } catch (Exception e) {
            logger.error(e.getMessage()); //ignore
        }

        return Marc21Field.UNK; // if error
    }

    /**
     * Builds a map of Marc21Field and FieldMarcMapping, with key being the k1 field
     * (e.g. Marc21Field._245 => FieldMarcMapping(1, new Date(), 245, k2, fdid)
     * @see FieldMarcMapping
     * @see Marc21Field
     * @return Mapping of Marc21Field to FIeldMarcMapping
     */
    public Map<Marc21Field, FieldMarcMapping> buildMarcFdidMap(List<FieldMarcMapping> fieldMarcMappingList) {
        logger.trace("Field marc mapping list size={}", fieldMarcMappingList);

        final List<String> debugList = new ArrayList<>(); //print warning
        final Map<Marc21Field, FieldMarcMapping> marc21FieldMap = new HashMap<>(); //e.g. 880 -> FieldMarcMapping

        for (final FieldMarcMapping f : fieldMarcMappingList) {
            try {
                marc21FieldMap.put(Marc21Field.valueOf("_" + f.getK1()), f);
            } catch (IllegalArgumentException e) { //No matching enum
                debugList.add(f.getK1());
            }
        }
        logger.debug("Enums not found for={}", debugList);
        return marc21FieldMap;
    }

    /** For testing */
    public FieldMarcMappingDAO getFieldMarcMappingDAO() {
        return fieldMarcMappingDAO;
    }

    /** For testing */
    public void setFieldMarcMappingDAO(final FieldMarcMappingDAO fieldMarcMappingDAO) {
        this.fieldMarcMappingDAO = fieldMarcMappingDAO;
    }
}
