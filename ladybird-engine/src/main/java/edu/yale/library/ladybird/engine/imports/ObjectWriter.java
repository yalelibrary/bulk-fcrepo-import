package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.exports.ImportEntityContext;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
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
 *
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

                logger.trace("Evaluating FieldConstant={} ", f.getName());

                Map<ImportEntity.Column, ImportEntity.Column> columnMap = importEntityValue.getContentColumnValuesWithOIds(f);

                logger.trace("All column values for this field are={}", columnMap.toString());

                Set<ImportEntity.Column> keys = columnMap.keySet();

                logger.trace("Column map key set size={}", keys.size());

                for (ImportEntity.Column c : keys) {

                    String oid = (String) c.getValue();
                    ImportEntity.Column fdidForOid = columnMap.get(c);

                    logger.trace("Processing (key oid) Oid={}, Field Name={}, Field Value={}", oid,
                            fdidForOid.getField().getName(), fdidForOid.getValue());

                    //Save object metadata
                    final String value = (String) fdidForOid.getValue();

                    //Either an acid or a string:
                    if (getTableType(fdidAsInt(f.getName())).equals(TABLETYPE.OBJECT_ACID)) {
                        //1a.persist acid:
                        final AuthorityControl acidObj = new AuthorityControl();
                        acidObj.setFdid(fdidAsInt(f.getName()));
                        acidObj.setUserId(userId);
                        acidObj.setDate(new Date());
                        acidObj.setValue(value);
                        int acid = authorityControlDAO.save(acidObj);
                        logger.trace("Saved entity={}", acidObj.toString());

                        //1b. persist object acid
                        final ObjectAcid objAcid = new ObjectAcid();
                        objAcid.setFdid(fdidAsInt(f.getName()));
                        if (oid != null) { //TODO
                            objAcid.setObjectId(Integer.parseInt(oid));
                        }

                        objAcid.setValue(acid); //TODO acid PK
                        objAcid.setUserId(userId);
                        objAcid.setDate(new Date());
                        objectAcidDAO.save(objAcid);
                    } else {
                        final ObjectString objString = new ObjectStringBuilder().createObjectString();
                        objString.setFdid(fdidAsInt(f.getName()));
                        objString.setUserId(userId);
                        objString.setDate(new Date());
                        objString.setValue(value);

                        if (oid != null) { //TODO
                            objString.setOid(Integer.parseInt(oid));
                        }

                        objectStringDAO.save(objString);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error writing to object metadata tables={}", e);
        }
    }

    /**
     * Retruns the type of table the metadata should be written to
     * @param fdid int fdid
     * @return table type (e.g. object_string or object_acid)
     */
    public TABLETYPE getTableType(int fdid) {

        if (isString(fdid)) {
            return TABLETYPE.OBJECT_STRING;
        }

        return TABLETYPE.OBJECT_ACID;
    }

    /**
     * Helps determine fdid to table mapping
     * @param fdid int fdid
     * @return whether a value is a string
     */
    //FIXME externalize or make it configurable
    public static boolean isString(int fdid) {
        if (fdid == 58 || fdid == 70 || fdid == 71 || fdid == 77 || fdid == 78 || fdid == 79 || fdid == 82
                || fdid == 104 || fdid == 127 || fdid == 152 || fdid == 156) {
            return true;
        }

        return false;
    }

    private enum TABLETYPE {
        OBJECT_STRING,
        OBJECT_ACID,
        OBJECT_LONGSTRING
    }

    /**
     * converter helper
     * FiXME this depends on the the fdids are loaded (currently through a file fdid.test.propeties at webapp start up)
     *
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     * @see edu.yale.library.ladybird.engine.model.FieldConstantRules#convertStringToFieldConstant(String)
     *      for similiar functionality
     */
    private Integer fdidAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String[] parsedString = s.split("fdid=");
            return Integer.parseInt(parsedString[1].replace("}", ""));
        }
    }

    /**
     * helper
     *
     * @param importEntityContext import job context
     * @return user id
     */
    private int getUserId(final ImportEntityContext importEntityContext) {
        final Monitor monitor = importEntityContext.getMonitor();
        final User user = monitor.getUser();
        final int userId = user.getUserId();
        return userId;
    }

}
