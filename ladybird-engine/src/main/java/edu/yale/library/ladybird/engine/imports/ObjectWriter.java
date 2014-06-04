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
 * @see MediaFunctionProcessor for object_file
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

            final int userId = getUserId(importJobCtx);

            ImportEntityValue importEntityValue = new ImportEntityValue(importRows);
            importEntityValue.print();

            //Go through each column (F1.. fdid=220), and persist object data (i.e. it processes vertically):
            List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();

            logger.debug("Field constants are={}", fieldConstants.toString());

            for (FieldConstant f : fieldConstants) {

                if (f.equals(FunctionConstants.F1)) { //and other functions too?
                    continue;
                }

                logger.debug("Eval fieldconstant={} ", f.getName());

                //(oid -> value), (11222, "name field for this oid");
                Map<ImportEntity.Column, ImportEntity.Column> columnMap = importEntityValue.getContentColumnValuesWithOIds(f);
                //TODO in reality (e.g. FieldDefinion.69) but there's no such abstraction for FieldDefintion, only for FunctionConstant

                Set<ImportEntity.Column> keySet = columnMap.keySet();

                logger.debug("Map key set size={}", keySet.size());

                for (ImportEntity.Column c : keySet) {
                    logger.debug("Eval column={}", c.getField().toString());
                    String oidValue = (String) c.getValue();
                    logger.debug("Oid={}", oidValue);
                    ImportEntity.Column valueForOid = columnMap.get(c); //value for oid ..
                    logger.debug("Field ={}, Value={}", valueForOid.getField().toString(), valueForOid.getValue());


                    if (true) { //assuming each fdid is an acid value

                        final ObjectAcid objectAcid = new ObjectAcid();
                        objectAcid.setFdid(fdidAsInt(f.getName())); //TODO

                        if (oidValue != null || oidValue.length() > 1  ) {
                            objectAcid.setObjectId(Integer.parseInt(oidValue));
                        } else {
                            logger.error("Oid value null or less than 1");
                        }

                        final AuthorityControl authorityControl = new AuthorityControl();
                        authorityControl.setFdid(fdidAsInt(f.getName()));
                        authorityControl.setUserId(userId);
                        authorityControl.setDate(new Date());
                        authorityControl.setValue((String) valueForOid.getValue());
                        int acid = authorityControlDAO.save(authorityControl);

                        objectAcid.setValue(acid); //FIXME the acid value...stupid PK in db

                        objectAcid.setUserId(userId);
                        objectAcid.setDate(new Date());

                        logger.debug("Saving entity={}", objectAcid.toString());
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
     * FiXME this depends on the the fdidsa are loaded (currently through fdid.test.propeties
     * @param s string
     * @return integer value
     */
    private Integer fdidAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // e.g. from Host, note{fdid=68}, get 68
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
}
