package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectAcidHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectStringHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Populates object metadata (acid/strings) tables.
 * @see MediaFunctionProcessor for object_file population
  */
public class ObjectWriter {

    private Logger logger = LoggerFactory.getLogger(ObjectWriter.class);

    //TODO inject DAO(s)

    final ObjectDAO objectDAO = new ObjectHibernateDAO();
    final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();


    /**
     * Populates object metadata tables
     *
     * @param importJobCtx context
     * @see edu.yale.library.ladybird.engine.cron.DefaultExportJob#execute(org.quartz.JobExecutionContext)  for where it's being called
     *
     * TODO pass ImportEntity ?
     */
    public void write(ImportJobCtx importJobCtx) {
        logger.debug("Writing object metadata");
        try {
            List<ImportEntity.Row> importRows = importJobCtx.getImportJobList();
            logger.debug("Import Row size={}", importRows.size());

            logger.debug("Import Rows={}", importRows.toString());

            final int userId = getUserId(importJobCtx);

            ImportEntityValue importEntityValue = new ImportEntityValue(importRows);

            //Go through each column (F1.. fdid=220), and persist object data (i.e. it processes vertically):

            List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();

            logger.debug("Field constants for this sheet are={}", fieldConstants.toString());

            for (FieldConstant f : fieldConstants) {

                if (f.equals(FunctionConstants.F1)) { //and other functions too?
                    continue;
                }

                logger.debug("Evaluating FieldConstant={} ", f.getName());

                Map<ImportEntity.Column, ImportEntity.Column> columnMap = importEntityValue.getContentColumnValuesWithOIds(f);

                logger.debug("Column value is={}", columnMap.toString());

                //TODO in reality (e.g. FieldDefinion.69) but there's no such abstraction for FieldDefintion, only for FunctionConstant

                Set<ImportEntity.Column> keySet = columnMap.keySet();

                logger.debug("Map key set size={}", keySet.size());

                for (ImportEntity.Column c : keySet) {

                    logger.debug("Evaluating Column={}", c.toString());

                    String oid = (String) c.getValue();

                    ImportEntity.Column fdidValueForOid = columnMap.get(c);

                    logger.debug("Oid={}, Field Name={}, Field Value={}", oid, fdidValueForOid.getField().getName(), fdidValueForOid.getValue());


                    //TODO (currently assuming each fdid is an acid value)
                    if (true) {

                        final ObjectAcid objectAcid = new ObjectAcid();
                        objectAcid.setFdid(fdidAsInt(f.getName()));

                        if (oid != null || oid != "") {
                            objectAcid.setObjectId(Integer.parseInt(oid));
                        } else {
                            logger.error("Oid value null or less than 1");
                        }

                        //persist acid:
                        final AuthorityControl authorityControl = new AuthorityControl();
                        authorityControl.setFdid(fdidAsInt(f.getName()));
                        authorityControl.setUserId(userId);
                        authorityControl.setDate(new Date());
                        authorityControl.setValue((String) fdidValueForOid.getValue());
                        int acid = authorityControlDAO.save(authorityControl);

                        //persist object acid
                        objectAcid.setValue(acid); //TODO acid PK
                        objectAcid.setUserId(userId);
                        objectAcid.setDate(new Date());
                        //logger.debug("Saving entity={}", objectAcid.toString());
                        objectAcidDAO.save(objectAcid);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error writing to object metadata tables={}", e);
        }
        logger.debug("Done.");
    }

    /**
     * converter helper
     * FiXME this depends on the the fdids are loaded (currently through fdid.test.propeties at boot)
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     */
    private Integer fdidAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String[] parsedString = s.split("fdid=");
            return Integer.parseInt(parsedString[1].replace("}",""));
        }
    }

    /** helper
     *
     * @param importJobCtx import job context
     * @return user id
     */
    private int getUserId(final ImportJobCtx importJobCtx) {
        final Monitor monitor = importJobCtx.getMonitor();
        final User user = monitor.getUser();
        final int userId = user.getUserId();
        return userId;
    }
    private void printMap(Map<ImportEntity.Column, ImportEntity.Column> columnMap) {
        Set<ImportEntity.Column> keySet = columnMap.keySet();

        for(ImportEntity.Column key: keySet) {
            String keyString = key.getField().getName();
            String valueString = columnMap.get(key).getValue().toString();
            logger.debug("key={} value={}", keyString, valueString);
        }
    }
}
