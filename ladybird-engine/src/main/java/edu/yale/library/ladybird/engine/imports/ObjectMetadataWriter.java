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

    // TODO inject:
    final ObjectStringDAO objectStringDAO = new ObjectStringHibernateDAO();
    final ObjectAcidDAO objectAcidDAO = new ObjectAcidHibernateDAO();
    final AuthorityControlDAO authorityControlDAO = new AuthorityControlHibernateDAO();

    /**
     * Populates object metadata tables
     *
     * @param importEntityContext context
     * @see edu.yale.library.ladybird.engine.cron.DefaultExportJob#execute(org.quartz.JobExecutionContext) for call
     */
    public void write(final ImportEntityContext importEntityContext) {
        try {
            final List<ImportEntity.Row> importRows = importEntityContext.getImportJobList();
            final ImportEntityValue importEntityValue = new ImportEntityValue(importRows);
            final int userId = getUserId(importEntityContext);
            final List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();
            logger.trace("Field constants are={}", fieldConstants.toString());

            final Date currentDate = new Date();
            final int importId = importEntityContext.getImportId();

            // Go through each column (F1.. fdid=220), and persist object data (i.e. it processes vertically):
            for (final FieldConstant f : fieldConstants) {

                if (FunctionConstants.isFunction(f.getName())) { // skip functions (no metadata)
                    continue;
                }

                final int fdid = FieldDefinition.fdidAsInt(f.getName());
                logger.debug("Writing values for fdid={} for import={} ", fdid, importId);

                final Map<ImportEntity.Column, ImportEntity.Column> columnMap
                        = importEntityValue.getContentColumnValuesWithOIds(f);
                final Set<ImportEntity.Column> keys = columnMap.keySet();

                // Either an acid or a string:
                final boolean shouldWriteAsObjectAcid = shouldWriteAsObjectAcid(fdid);

                for (final ImportEntity.Column col : keys) {
                    final int oid = Integer.parseInt((String) col.getValue());
                    final ImportEntity.Column fdidForOid = columnMap.get(col);
                    final String value = (String) fdidForOid.getValue();

                    if (shouldWriteAsObjectAcid) {
                        addObjectAcid(oid, fdid, userId, currentDate, value);
                    } else {
                        addObjectString(oid, fdid, userId, currentDate, value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error writing to object metadata tables", e); // TODO
        }
    }

    private void addObjectAcid(int oid, int fdid, int userId, Date date, String value) throws Exception{
        // 1a. reference existing or new acid:
        // check if the acid already exists (to get rid of multiple acids landing into acid list view)
        final List<AuthorityControl> existingAcidList
                = authorityControlDAO.findByFdidAndStringValue(fdid, value);

        int acid;

        if (existingAcidList.isEmpty()) {
            final AuthorityControl authorityControl
                    = new AuthorityControlBuilder().setFdid(fdid).setUserId(userId).setDate(date).setValue(value)
                    .createAuthorityControl();
            acid = authorityControlDAO.save(authorityControl);
        } else if (existingAcidList.size() == 1) {
            acid = existingAcidList.get(0).getAcid();
        } else {
            logger.error("More than one acid value found for fdid={}", fdid);
            throw new Exception("Error with acid fdid multi value for fdid=" + fdid); //TODO check
        }

        //see if an objectacid already exists (this is used to replace the value when a spreadsheet update is done)
        final ObjectAcid existingObjectAcid = objectAcidDAO.findByOidAndFdid(oid, fdid);

        if (existingObjectAcid != null) {
            logger.trace("An object acid value already exists for oid={} fdid={}", oid, fdid);
            objectAcidDAO.delete(existingObjectAcid);
        }

        //1b. persist object acid // TODO acid PK
        final ObjectAcid objAcid
                = new ObjectAcidBuilder().setFdid(fdid).setObjectId(oid).setValue(acid).setUserId(userId).setDate(date)
                         .createObjectAcid();
        objectAcidDAO.save(objAcid);
    }

    private void addObjectString(int oid, int fdid, int userId, Date date, String value) {
        // see if an objectstring already exists
        // (this is used to replace the value when a spreadsheet update is done)
        final ObjectString exObjectString = objectStringDAO.findByOidAndFdid(oid, fdid);

        if (exObjectString != null) {
            logger.trace("A string value already exists for oid={} fdid={}", oid, fdid);
            objectStringDAO.delete(exObjectString);
        }

        final ObjectString objString =
                new ObjectStringBuilder().setFdid(fdid).setUserId(userId).setDate(date).setValue(value).setOid(oid)
                        .createObjectString();
        objString.setOid(oid);
        objectStringDAO.save(objString);
    }

    public boolean shouldWriteAsObjectAcid(final int fdid) {
        return getTableType(fdid).equals(SCHEMA.OBJECT_ACID);
    }

    /**
     * Retruns the type of table the metadata should be written to
     * @param fdid int fdid
     * @return table type (e.g. object_string or object_acid)
     */
    public SCHEMA getTableType(final int fdid) {
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
