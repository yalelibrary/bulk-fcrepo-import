package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.FieldMarcMappingBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FdidMarcMappingUtil {

    private static Logger logger = LoggerFactory.getLogger(FdidMarcMappingUtil.class);

    FieldMarcMappingDAO fieldMarcMappingDAO;

    //TODO for now simple int from a text file
    public void setInitialFieldMarcDb() throws Exception {

        InputStream f = this.getClass().getResourceAsStream("/marc-mappings-int.txt");

        try {
            Scanner sc = new Scanner(f);

            if (sc == null) {
                logger.error("File not found");
            }

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
     * Returns Marc21Field mapped value or Marc21Field.UNK
     * @param fieldConstant
     * @return
     */
    public Marc21Field toMarc21Field(FieldConstant fieldConstant) {
        //logger.debug("Finding Marc21Field for={}", fieldConstant);
       /* String f = fieldConstant.getName();
        if (f.equals("70") || f.equals("Title{fdid=70}") || f.equals("fdid=70")) {
            return Marc21Field._245;
        }
        return Marc21Field.UNK;
        */

        try {
            //try converting to integer fdid
            int fdid = FieldConstantRules.fdidAsInt(fieldConstant.getName());
            //logger.debug("Field Contant as Fdid={}", fdid);

            FieldMarcMapping fieldMarcMapping = fieldMarcMappingDAO.findByFdid(fdid);
            Marc21Field marc21Field = Marc21Field.valueOfTag(fieldMarcMapping.getK1());
            //logger.debug("Found value={}", marc21Field);
            return marc21Field;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return Marc21Field.UNK; // if error
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
