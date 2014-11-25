package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Populates object metadata (acid/strings) tables.
 *
 * @see MediaFunctionProcessor for object_file population
 */
public class ObjectMetadataWriter {

    private Logger logger = LoggerFactory.getLogger(ObjectMetadataWriter.class);

    //TODO inject

    final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();

    /**
     * Populates object metadata tables
     *
     * @param importEntityContext context
     * @see edu.yale.library.ladybird.engine.cron.DefaultExportJob#execute(org.quartz.JobExecutionContext) for call
     * TODO pass ImportEntity directly
     */
    public void write(ImportEntityContext importEntityContext) {
        try {
            List<ImportEntity.Row> importRows = importEntityContext.getImportJobList();
            ImportEntityValue importEntityValue = new ImportEntityValue(importRows);
            final int userId = getUserId(importEntityContext);

            logger.trace("Writing object metadata. Import Row size={}, UserId{}", importRows.size(), userId);

            //Go through each column (F1.. fdid=220), and persist object data (i.e. it processes vertically):

            final List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();

            logger.trace("Field constants for this sheet are={}", fieldConstants.toString());

            for (FieldConstant f : fieldConstants) {

                if (FunctionConstants.isFunction(f.getName())) { //skip functions. functions have no metadata
                    continue;
                }

                //logger.trace("Evaluating FieldConstant={} ", f.getName());

                Map<ImportEntity.Column, ImportEntity.Column> columnMap = importEntityValue.getContentColumnValuesWithOIds(f);

                //logger.trace("All column values for this field are={}", columnMap.toString());

                Set<ImportEntity.Column> keys = columnMap.keySet();

                //logger.trace("Column map key set size={}", keys.size());

                for (ImportEntity.Column c : keys) {
                    String oidStr = (String) c.getValue();
                    ImportEntity.Column fdidForOid = columnMap.get(c);

                    //logger.debug("Processing (key oid) oid={}, field name={}, field value={}", oid,
                            //fdidForOid.getField().getName(), fdidForOid.getValue());

                    //Save object metadata
                    final String value = (String) fdidForOid.getValue();
                    final int fdid = FieldDefinition.fdidAsInt(f.getName());

                    //Either an acid or a string:
                    if (getTableType(fdid).equals(SCHEMA.OBJECT_ACID)) {
                        //1a. reference existing or new acid:

                        //first see if the acid already exists (to get rid of multiple acids landing into acid list view)
                        List<AuthorityControl> existingAcidList = authorityControlDAO.findByFdidAndStringValue(fdid, value);

                        int acid;

                        if (existingAcidList.isEmpty()) {
                            final AuthorityControl acidObj = new AuthorityControlBuilder().createAuthorityControl();
                            acidObj.setFdid(fdid);
                            acidObj.setUserId(userId);
                            acidObj.setDate(new Date());
                            acidObj.setValue(value);
                            acid = authorityControlDAO.save(acidObj);
                        } else if (existingAcidList.size() == 1) {
                            acid = existingAcidList.get(0).getAcid();
                        } else {
                            logger.error("More than one acid value found for fdid={}", fdid);  //throw
                            throw new Exception("Error with acid fdid multi value for fdid=" + fdid);
                        }

                        //see if an objectacid already exists (this is used to replace the value when a spreadsheet update is done)
                        ObjectAcid existingObjectAcid = objectAcidDAO.findByOidAndFdid(Integer.parseInt(oidStr), fdid);

                        if (existingObjectAcid != null) {
                            logger.trace("An object acid value already exists for oid={} fdid={}", oidStr, fdid);
                            objectAcidDAO.delete(Collections.singletonList(existingObjectAcid));
                            logger.trace("Deleted={}", existingObjectAcid);
                        }

                        //1b. persist object acid
                        final ObjectAcid objAcid = new ObjectAcidBuilder().createObjectAcid();
                        objAcid.setFdid(fdid);

                        if (oidStr != null) { //TODO
                            objAcid.setObjectId(Integer.parseInt(oidStr));
                        }

                        objAcid.setValue(acid); //TODO acid PK
                        objAcid.setUserId(userId);
                        objAcid.setDate(new Date());
                        objectAcidDAO.save(objAcid);
                        logger.trace("Saved={}", objectAcidDAO);
                    } else {
                        int oid = Integer.parseInt(oidStr);

                        //see if an objectstring already exists (this is used to replace the value when a spreadsheet update is done)
                        final ObjectString exObjectString = objectStringDAO.findByOidAndFdid(oid, fdid);

                        if (exObjectString != null) {
                            logger.trace("A string value already exists for oid={} fdid={}", oid, fdid);
                            objectStringDAO.delete(Collections.singletonList(exObjectString));
                            logger.trace("Deleted={}", exObjectString);
                        }

                        final ObjectString objString = new ObjectStringBuilder().createObjectString();
                        objString.setFdid(fdid);
                        objString.setUserId(userId);
                        objString.setDate(new Date());
                        objString.setValue(value);
                        objString.setOid(oid);
                        objectStringDAO.save(objString);
                        logger.trace("Saved={}", objString);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error writing to object metadata tables", e);
        }
    }

    /**
     * Retruns the type of table the metadata should be written to
     * @param fdid int fdid
     * @return table type (e.g. object_string or object_acid)
     */
    public SCHEMA getTableType(int fdid) {
        return FieldConstantUtil.isString(fdid) ? SCHEMA.OBJECT_STRING : SCHEMA.OBJECT_ACID;
    }

    private enum SCHEMA {
        OBJECT_STRING,
        OBJECT_ACID,
        OBJECT_LONGSTRING
    }

    private int getUserId(final ImportEntityContext importEntityContext) {
        final Monitor monitor = importEntityContext.getMonitor();
        return monitor.getUser().getUserId();
    }

}
