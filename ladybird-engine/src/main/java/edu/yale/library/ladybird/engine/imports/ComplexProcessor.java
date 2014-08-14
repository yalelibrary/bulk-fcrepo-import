package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ComplexProcessor {
    private Logger logger = LoggerFactory.getLogger(ComplexProcessor.class);

    final ObjectDAO objectDAO = new ObjectHibernateDAO();

    public void processF4(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rows = importEntityValue.getContentRows();
        final List<Object> changedObjectList = new ArrayList<>();

        int tmp = 0;

        for (int i = 0; i < rows.size(); i++) {
            try {
                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                Integer f4 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F4, i));
                //Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));

                //Set parent oid and parent property:
                if (f4 == 0) {
                    object.setParent(true);
                    object.setP_oid(0);
                    tmp = f1;
                    changedObjectList.add(object);
                } else {
                    object.setParent(false);
                    object.setP_oid(tmp);
                    changedObjectList.add(object);
                }
            } catch (Exception e) {
                logger.trace(e.getMessage()); //ignore
            }
        }
        logger.debug(changedObjectList.toString());
        objectDAO.saveOrUpdateList(changedObjectList);
    }

    /**
     * Logic needs to be adjusted per F1 requirements.
     * @param importEntityValue importEntityValue
     */
    public void processF5(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        final List<Object> changedObjectLit = new ArrayList<>();

        for (int i = 0; i < rowList.size(); i++) {
            try {
                logger.trace("Finding by oid={}", asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                Integer f5 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F5, i));
                Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));

                //Set parent oid and parent property:
                if (f5 == 0 || f1.equals(f5)) {
                    object.setParent(true);
                    object.setP_oid(0);
                    object.setZindex(f6);
                    changedObjectLit.add(object);
                } else {
                    object.setParent(false);
                    object.setP_oid(f5);
                    object.setZindex(f6);
                    changedObjectLit.add(object);
                }
            } catch (Exception e) {
                logger.error("Error={}", e);
            }
        }
        logger.debug(changedObjectLit.toString());
        objectDAO.saveOrUpdateList(changedObjectLit);
    }

    /**
     * Logic needs to be adjusted per F1 requirements.
     * @param importEntityValue importEntityValue
     */
    public void processF7(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        final List<Object> changedObjectLit = new ArrayList<>();

        for (int i = 0; i < rowList.size(); i++) {
            try {
                logger.trace("Finding by oid={}", asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));
                Integer f7 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F7, i));
                Integer f8 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F8, i));

                //Set parent oid and parent property:
                if (f7.equals(f8)) {
                    object.setParent(true);
                    object.setP_oid(0);
                    object.setZindex(f6);
                    changedObjectLit.add(object);
                } else {
                    object.setParent(false);
                    object.setP_oid(f8); //oid to be looked up, or is f8 written as is?
                    object.setZindex(f6);
                    changedObjectLit.add(object);
                }
            } catch (Exception e) {
                logger.error("Error={}", e);
            }
        }
        logger.debug(changedObjectLit.toString());
        objectDAO.saveOrUpdateList(changedObjectLit);
    }

    private Integer asInt(String s) {
        return Integer.parseInt(s);
    }
}
