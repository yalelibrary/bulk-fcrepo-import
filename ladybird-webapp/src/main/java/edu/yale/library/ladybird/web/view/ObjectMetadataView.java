package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.engine.metadata.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.metadata.MetadataEditor;
import edu.yale.library.ladybird.engine.metadata.Rollbacker;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.entity.ObjectEventBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectEventDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectMetadataView extends AbstractView {

    private final Logger logger = getLogger(this.getClass());

    private List<FieldDefinitionValue> fieldDefinitionvalueList;

    /**
     * Field is for file image info
     */
    private ObjectFile objectFile;

    private edu.yale.library.ladybird.entity.Object object;

    @Inject
    private ObjectDAO objectDAO;

    @Inject
    private ObjectFileDAO objectFileDAO;

    @Inject
    private ObjectStringDAO objectStringDAO;

    @Inject
    private ObjectAcidDAO objectAcidDAO;

    @Inject
    private AuthorityControlDAO authorityControlDAO;

    @Inject
    private FieldDefinitionDAO fdidDAO;

    @Inject
    private ObjectStringVersionDAO objectStringVersionDAO;

    @Inject
    private ObjectAcidVersionDAO objectAcidVersionDAO;

    @Inject
    private ObjectVersionDAO objectVersionDAO;

    @Inject
    private ObjectEventDAO objectEventDAO;

    @Inject
    private EventTypeDAO eventTypeDAO;

    @Inject
    private AuthUtil auth;

    /**Making versioned items uneditable */
    private boolean readOnly = false;

    @PostConstruct
    public void init() {
        initFields();
        dao = objectDAO;

        try {
            final String oidStr = Faces.getRequestParameter("oid");
            final int oid = Integer.parseInt(oidStr);
            objectFile = objectFileDAO.findByOid(oid);
            final String versionId = Faces.getRequestParameter("version");

            if (fieldDefinitionvalueList == null && versionId == null) {
                fieldDefinitionvalueList = getFdidValues(oid);
            } else if (fieldDefinitionvalueList == null && !versionId.isEmpty()) {
                int version = Integer.parseInt(versionId);
                fieldDefinitionvalueList = getFdidValues(oid, version);
                readOnly = true;
            }
        } catch (Exception e) {
            logger.error("Error init bean", e.getMessage());
        }
    }

    private List<FieldDefinitionValue> getFdidValues(int oid) {
        final List<FieldDefinitionValue> fdidValues = new ArrayList<>();

        try {
            List<FieldDefinition> list = fdidDAO.findAll();

            for (FieldDefinition f : list) {
                fdidValues.add(new FieldDefinitionValue(f, getValuesByOidAndFdid(oid, f.getFdid())));
            }
        } catch (Exception e) {
            logger.error("Error building fdid map for oid={}", oid, e);
        }

        return fdidValues;
    }

    private List<FieldDefinitionValue> getFdidValues(int oid, int versionId) {
        final List<FieldDefinitionValue> fdidValues = new ArrayList<>();

        try {
            List<FieldDefinition> list = fdidDAO.findAll();

            for (FieldDefinition f : list) {
                fdidValues.add(new FieldDefinitionValue(f, getValuesByOidAndFdid(oid, f.getFdid(), versionId)));
            }

            //logger.debug("Fdid values={}", fdidValues);
        } catch (Exception e) {
            logger.error("Error building fdid map for oid={} version={}", oid, versionId, e);
        }

        return fdidValues;
    }

    /**
     * Populates object metadata. Pulls from object_acid and object_string table(s). It pulls multiple values
     *
     * @param oid  oid
     * @param fdid fdid
     * @return value for an oid for a fdid
     */
    public List<String> getValuesByOidAndFdid(int oid, int fdid) {
        List<String> values = new ArrayList();

        try {
            if (!ObjectWriter.isString(fdid)) { //not a string:
                //1. Find acid value for this oid
                final List<ObjectAcid> objectAcid = objectAcidDAO.findListByOidAndFdid(oid, fdid);

                for (ObjectAcid oa: objectAcid) {
                    int acid = oa.getValue();

                    //2. Get string value corresponding to this acid
                    final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);
                    values.add(authorityControl.getValue());
                }
            } else { //a string:
                final List<ObjectString> objectStringList = objectStringDAO.findListByOidAndFdid(oid, fdid);

                for (ObjectString obs: objectStringList) {
                    values.add(obs.getValue());
                }
            }

            return values;
        } catch (Exception e) {
            logger.error("Error finding entity or value=", e);
        }
        return new ArrayList<>();
    }

       /**
     * Populates object metadata. Pulls rom object_acid and object_string table(s).
     * If an exception occures during retreival, an unknown error is thrown.
     *
     * @param oid  oid
     * @param fdid fdid
     * @return value for an oid for a fdid
     */
    public List<String> getValuesByOidAndFdid(int oid, int fdid, int version) {
        List<String> values = new ArrayList();

        try {
            if (!ObjectWriter.isString(fdid)) {
                final List<ObjectAcidVersion> objectAcidVersions = objectAcidVersionDAO.findListByOidAndFdidAndVersion(oid, fdid, version);

                //logger.debug("Object Acid Versions size={}", objectAcidVersions.size());

                if (objectAcidVersions.isEmpty()) {
                    logger.debug("Acid version empty");
                }

                for (ObjectAcidVersion oav: objectAcidVersions) {
                    int acid = oav.getValue();
                    final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);
                    values.add(authorityControl.getValue());
                }

            } else { //a string:
                final List<ObjectStringVersion> objStr = objectStringVersionDAO.findListByOidAndFdidAndVersion(oid, fdid, version);

                //logger.debug("Object String version size={}", objStr.size());

                if (objStr.isEmpty()) {
                    logger.debug("Full list={}", objectStringVersionDAO.findAll());
                }

                for (ObjectStringVersion osv: objStr) {
                    values.add(osv.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("Error finding entity or value. No acid value={} for fdid={} for versionId={}",
                    oid, fdid, version, e.getMessage());
        }
        return values;
    }

    /**
     * Saves audit events
     * @param oid oid
     * @param userId userid
     */
    private void saveAuditEvent(int oid, int userId) {
        try {
            ObjectEvent objectEvent = new ObjectEventBuilder().setEventType(getEditEvent())
                    .setDate(new Date()).setOid(oid).setUserId(userId).createObjectEvent();
            objectEventDAO.save(objectEvent);
        } catch (Exception e) {
            logger.error("Error saving audit event", e);
            throw e;
        }
    }

    public EventType getEditEvent() {
        return eventTypeDAO.findByEditEvent();
    }

    /**
     * Updates object metadata
     */
    public String updateOidMetadata() {
        final int oid = Integer.parseInt(Faces.getRequestParameter("oid"));
        //logger.debug("Updating oid metadata for oid={} with values={}", oid, fieldDefinitionvalueList);

        MetadataEditor metadataEditor = new MetadataEditor();
        try {
            //Save and update list
            metadataEditor.updateOidMetadata(oid, auth.getCurrentUserId(), fieldDefinitionvalueList);
            //Audit event (creates direct db entry, doesn't post it):
            saveAuditEvent(oid, auth.getCurrentUserId());
        } catch (Exception e) {
            logger.error("Error updating or versioning values for oid={}", oid, e);
            return fail();
        }
        return ok();
    }

    /**
     * Rolls back an object. Older copy is preserved.
     */
    public String rollback() {
        try {
            if (isParamNull("oid") || isParamNull("version")) {
                logger.error("Params oid or version null!");
                return fail();
            }

            int oid = Integer.parseInt(Faces.getRequestParameter("oid"));
            int version = Integer.parseInt(Faces.getRequestParameter("version"));

            logger.debug("User={} requesting rollbacking oid={} to version={}", auth.getCurrentUserId(), oid, version);

            Rollbacker rollbacker = new Rollbacker();
            rollbacker.rollback(oid, version, auth.getCurrentUserId());

            logger.debug("Done rolling back object");
            return ok();
        } catch (Exception e) {
            logger.error("Error rolling back", e);
        }
        return fail();
    }

    /**
     * Returns fdid handle
     *
     * @param fdid int value of fdid, e.g. 69
     * @return handle, e.g. Name or empty string
     */
    public String getFdidHandle(int fdid) {
        try {
            FieldDefinition fdidObj = fdidDAO.findByFdid(fdid);
            return fdidObj.getHandle();
        } catch (Exception e) {
            logger.trace("Error finding value for fdid={}", fdid, e);
            return "";
        }
    }

    public int getChildCount(int oid) {
        return objectDAO.childCount(oid);
    }

    /**
     * Returns false if prop not set or the acutal value.
     * That's ok since it's called directly by JSF (javax.el)
     * @param oid
     * @return
     */
    public boolean isParent(int oid) {
        try {
            Object o = objectDAO.findByOid(oid);
            return o == null ? false : o.isParent();
        } catch (Exception e) {
            logger.error("Error finding parent attribute for oid={}", oid, e.getMessage());
            throw e;
        }
    }

    //Getters and setters -------------------------------------------------------------------
    /**
     * converts request parameter to file path
     */
    public String getImage() {
        if (objectFile == null) {
            return "";
        }

        return objectFile.getFilePath();
    }

    public edu.yale.library.ladybird.entity.Object getObject() {
        return object;
    }

    public void setObject(edu.yale.library.ladybird.entity.Object object) {
        this.object = object;
    }

    public ObjectFile getObjectFile() {
        return objectFile;
    }

    public void setObjectFile(ObjectFile objectFile) {
        this.objectFile = objectFile;
    }

    //TODO move?
    public int getCount() {
        return objectFileDAO.count();
    }

    public List<FieldDefinitionValue> getFieldDefinitionvalueList() {
        return fieldDefinitionvalueList;
    }

    public void setFieldDefinitionvalueList(List<FieldDefinitionValue> fieldDefinitionvalueList) {
        this.fieldDefinitionvalueList = fieldDefinitionvalueList;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}


