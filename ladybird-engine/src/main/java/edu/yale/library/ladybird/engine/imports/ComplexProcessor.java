package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ComplexProcessor {
    private Logger logger = LoggerFactory.getLogger(ComplexProcessor.class);

    final ObjectDAO objectDAO = new ObjectHibernateDAO();

    public void processF4(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rows = importEntityValue.getContentRows();
        final List<Object> listToUpdate = new ArrayList<>();

        //int tmp = 0;

        for (int i = 0; i < rows.size(); i++) {

            try {
                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                checkNotNull(object, "Object must not be null. Row=" + i);

                final Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                final Integer f4 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F4, i));
                final Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));

                logger.trace("For oid={} f1={} f4={} f6={}", object.getOid(), f1, f4, f6);

                //Set parent oid and parent property:
                if (f4 == 0) {

                    checkState(f6 == 0, "Parent in hierachy has F4 and F6 should have set to 0");

                    object.setParent(true);
                    object.setP_oid(0);
                    //tmp = f1;
                    listToUpdate.add(object);
                } else {
                    object.setParent(false);

                    //checkState(tmp > 0, "Designated parent must have an oid in row=" + i);
                    checkNotNull(objectDAO.findByOid(f4), "Designated parent must have an oid in row=" + i);

                    object.setP_oid(f4);
                    listToUpdate.add(object);
                }
            } catch (Exception e) {
                logger.error("Exception in row={}", i, e); //ignore
            }
        }
        logger.debug("List to update={}", listToUpdate);

        objectDAO.saveOrUpdateList(listToUpdate);
    }

    /**
     * Logic needs to be adjusted per F1 requirements.
     * @param importEntityValue importEntityValue
     */
    public void processF5(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        final List<Object> objectsToUpdate = new ArrayList<>();
        final Map<Integer, Integer> parentOidMap = new HashMap<>(); //contains F5, F1 pair to link parent child

        for (int i = 0; i < rowList.size(); i++) {
            try {
                logger.trace("Eval={}", asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                // N.B using int
                final Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                final Integer f5 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F5, i));
                final Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));

                logger.trace("Given f1={} f5={} f6={}", f1, f5, f6);

                checkState(f1 > 0, "F1 must be greater than 0");
                checkState(f5 > -1, "F5 must be greater than 0");
                checkState(f6 > -1, "F6 must be greater than 0");

                //Set parent oid and parent property:
                if (f6 == 0) {
                    logger.trace("Identified parent since f6 is 0 in row={}", i);

                    if (parentOidMap.containsKey(f5)) {
                        logger.error("Already contains key={}. Won't update", f5);
                        continue;
                    }

                    object.setParent(true);
                    object.setP_oid(0);
                    object.setZindex(f6);

                    objectsToUpdate.add(object);

                    parentOidMap.put(f5, f1);
                    logger.trace("Set parent. Put value={} for key={}", f1, f5);
                } else {
                    logger.trace("Identified child since f6 is not 0 in row={}", i);

                    object.setParent(false);

                    if (parentOidMap.containsKey(f5)) {
                        int parentOid = parentOidMap.get(f5);
                        object.setP_oid(parentOid); //get f1 of parent
                        object.setZindex(f6);
                        objectsToUpdate.add(object);
                    } else {
                        logger.error("Error case. Parent not found. Won't update this object");
                    }
                }
            } catch (Exception e) {
                logger.error("Exception in row={}", i, e); //N.B. exception is ignored. might have a usecase for report
            }
        }
        logger.trace("Complex parent oid Map size={}", parentOidMap.size());

        objectDAO.saveOrUpdateList(objectsToUpdate);

        logger.trace("Updated complex list={}", objectsToUpdate);
    }

    /**
     * Logic needs to be adjusted per F1 requirements.
     * @param importEntityValue importEntityValue
     */
    public void processF7(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        final List<Object> updateList = new ArrayList<>();
        final Map<Integer, Integer> parentOidMap = new HashMap<>(); //contains F7, F1 pair to link parent child

        for (int i = 0; i < rowList.size(); i++) {
            try {
                final Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                final Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));
                final Integer f7 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F7, i));
                final Integer f8 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F8, i));

                checkState (f1 > 0, "F1 must be greater than 0");
                checkState(f6 > -1, "F6 cannot be negative");
                checkState(f7 > 0, "F7 must be greather than 0");
                checkState(f8 > -1, "F8 cannot be negative");

                final Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));
                checkNotNull(object, "Object cannot be null in row=" + i);

                if (parentOidMap.containsKey(f7)) {
                    logger.error("Already contains key={}. F7 should be unique. Won't process row={}.", f7, i);
                    continue;
                }

                if (f6 ==0 && f8 ==0) { //if (f7.equals(f8)) {
                    object.setParent(true);
                    object.setP_oid(0);
                    object.setZindex(f6);
                    updateList.add(object);
                } else {
                    if (parentOidMap.containsKey(f8)) {
                        final int parentOid = parentOidMap.get(f8);
                        object.setP_oid(parentOid);
                        object.setZindex(f6);
                        object.setParent(false);
                        updateList.add(object);
                    } else {
                        logger.error("Error case. Parent not found. Won't update this object in row= " + i);
                    }
                }

                parentOidMap.put(f7, f1);

            } catch (Exception e) {
                logger.error("Exception in row={}", i, e); //ignore
            }
        }

        logger.debug("Update complex object list={}", updateList);
        logger.debug("Complex parent oid Map size={}", parentOidMap.size());

        objectDAO.saveOrUpdateList(updateList);
    }

    private Integer asInt(String s) {
        return Integer.parseInt(s);
    }
}
