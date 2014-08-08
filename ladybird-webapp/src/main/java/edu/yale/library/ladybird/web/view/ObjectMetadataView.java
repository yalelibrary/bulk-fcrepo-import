package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.engine.imports.ObjectWriter;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.entity.ObjectVersionBuilder;
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
import edu.yale.library.ladybird.web.view.template.FieldDefinitionValue;
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

//TODO size check: how many acid values are created upon any edit.
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

    /**
     * For unknown metadata field value (e.g. when an error occurs)
     */
    private static final String UNK_VALUE = "N/A";

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
                logger.trace("Getting version data for version={}", version);
                fieldDefinitionvalueList = getFdidValues(oid, version);
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
                fdidValues.add(new FieldDefinitionValue(f, getValueByOidAndFdid(oid, f.getFdid())));
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
                fdidValues.add(new FieldDefinitionValue(f, getValueByOidAndFdid(oid, f.getFdid(), versionId)));
            }
        } catch (Exception e) {
            logger.error("Error building fdid map for oid={} version={}", oid, versionId, e);
        }

        return fdidValues;
    }

    /**
     * Populates object metadata. Pullsf rom object_acid and object_string table(s).
     *
     * @param oid  oid
     * @param fdid fdid
     * @return value for an oid for a fdid
     */
    public String getValueByOidAndFdid(int oid, int fdid) {
        try {
            if (!ObjectWriter.isString(fdid)) { //not a string:
                //1. Find acid value for this oid
                final ObjectAcid objectAcid = objectAcidDAO.findByOidAndFdid(oid, fdid);
                int acid = objectAcid.getValue();

                //2. Get string value corresponding to this acid
                final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);
                return authorityControl.getValue();
            } else { //a string:
                final ObjectString objectString = objectStringDAO.findByOidAndFdid(oid, fdid);
                return objectString.getValue();
            }
        } catch (Exception e) {
            logger.error("Error finding entity or value=", e);
        }
        return "N/A";
    }

    /**
     * Populates object metadata. Pulls rom object_acid and object_string table(s).
     * If an exception occures during retreival, an unknown error is thrown.
     *
     * @param oid  oid
     * @param fdid fdid
     * @return value for an oid for a fdid
     */
    public String getValueByOidAndFdid(int oid, int fdid, int version) {
        try {
            if (!ObjectWriter.isString(fdid)) {
                //1. Find acid value for this oid
                logger.trace("Finding ObjectAcid by oid={} and fdid={}", oid, fdid);

                final ObjectAcidVersion objectAcid = objectAcidVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);

                if (objectAcid == null) {
                    logger.debug("No acid value={} for fdid={} for versionId={}", oid, fdid, version);
                    logger.trace("Full acid stack={}", objectAcidVersionDAO.findAll().toString());
                    return UNK_VALUE;
                }

                int acid = objectAcid.getValue();

                //2. Get acid value
                final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);

                if (authorityControl == null) {
                    logger.error("AC null. No acid value={} for fdid={} for versionId={}", oid, fdid, version);
                    return UNK_VALUE;
                }

                return authorityControl.getValue();
            } else { //a string:
                final ObjectStringVersion objStr = objectStringVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);

                if (objStr == null) {
                    logger.debug("No string value={} for fdid={} for versionId={}", oid, fdid, version);
                    return UNK_VALUE;
                }
                return objStr.getValue();
            }
        } catch (Exception e) {
            logger.error("Error finding entity or value. No acid value={} for fdid={} for versionId={}",
                    oid, fdid, version, e.getMessage());
        }
        return UNK_VALUE;
    }

    /**
     * Updates object metadata
     * FIXME transcations handling.
     * TODO extract functionality to enable testing
     */
    public String updateOidMetadata() {
        logger.trace("Updating oid metadata for oid={}", Faces.getRequestParameter("oid"));

        final int oid = Integer.parseInt(Faces.getRequestParameter("oid"));
        final List<ObjectString> stringsToUpdate = new ArrayList<>();
        final List<ObjectAcid> objectAcidsToUpdate = new ArrayList<>();
        final List<ObjectString> stringsVersions = new ArrayList<>();
        final List<ObjectAcid> objectAcidVersions = new ArrayList<>();

        try {
            for (FieldDefinitionValue field : fieldDefinitionvalueList) {
                int fdid = field.getFdid().getFdid();

                if (ObjectWriter.isString(fdid)) {
                    final ObjectString objectString = objectStringDAO.findByOidAndFdid(oid, fdid);

                    //add to version list before updating
                    stringsVersions.add(new ObjectStringBuilder().setCopy(objectString).createObjectString());

                    objectString.setValue(field.getValue());
                    stringsToUpdate.add(objectString);
                } else { //assuming acid!
                    final ObjectAcid objectAcid = objectAcidDAO.findByOidAndFdid(oid, field.getFdid().getFdid());

                    //add to version list before updating
                    objectAcidVersions.add(new ObjectAcid(objectAcid));

                    int acidInt = objectAcid.getValue();
                    final AuthorityControl oldAcid = authorityControlDAO.findByAcid(acidInt);

                    //update only if the field has been changed
                    if (!oldAcid.getValue().equalsIgnoreCase(field.getValue())) {
                        //TODO out of tx (coordinate with objectAcidsToUpdate)
                        final AuthorityControl newAuthorityControl = new AuthorityControl(oldAcid);
                        newAuthorityControl.setValue(field.getValue());
                        int newAcidInt = authorityControlDAO.save(newAuthorityControl);

                        //set object acid to point to this new acid
                        objectAcid.setValue(newAcidInt);
                        //1. make sure to write this object acid:
                        objectAcidsToUpdate.add(objectAcid);
                    }
                }
            }
            //Save and update lists:
            //TODO in a tx. Must roll back if error!
            objectAcidDAO.saveOrUpdateList(objectAcidsToUpdate);
            objectStringDAO.saveOrUpdateList(stringsToUpdate);
            versionAcid(objectAcidVersions);
            versionStrings(stringsVersions);
            //Save object version:
            final ObjectVersion objVersion = new ObjectVersionBuilder().setCreationDate(new Date())
                    .setNotes("User Edit").setOid(oid).setUserId(auth.getCurrentUserId())
                    .setVersionId(getLastVersion(oid) + 1).createObjectVersion();
            objectVersionDAO.save(objVersion);

            //Audit event (creates direct db entry, doesn't post it):
            ObjectEvent objectEvent = new ObjectEvent();
            objectEvent.setEventType(getEditEvent());
            objectEvent.setDate(new Date());
            objectEvent.setOid(oid);
            objectEvent.setUserId(auth.getCurrentUserId());
            objectEventDAO.save(objectEvent);
        } catch (Exception e) {
            logger.error("Error updating or versioning values for oid={}", oid, e);
            return fail();
        }
        return ok();
    }

    public EventType getEditEvent() {
        return eventTypeDAO.findByEditEvent();
    }

    /**
     * Versions list of ObjectString. New versions start with 1 (Perhaps should pass version).
     * @param list an object string value
     */
    public void versionStrings(List<ObjectString> list) {
        for (ObjectString o : list) {
            try {
                ObjectStringVersion objStrVersion = new ObjectStringVersion(o);
                objStrVersion.setVersionId(getLastVersion(o.getOid()) + 1);
                objectStringVersionDAO.save(objStrVersion);
            } catch (Exception e) {
                logger.error("Error versioning object_string={}", o, e);
                throw e;
            }
        }
    }

    /**
     * Versions an objectAcid. new versions start with 1 (Perhaps should pass version).
     * @param list an object acid value
     */
    public void versionAcid(List<ObjectAcid> list) {
        for (ObjectAcid o : list) {
            try {
                ObjectAcidVersion objectVersion = new ObjectAcidVersion(o);
                objectVersion.setVersionId(getLastVersion(o.getObjectId()) + 1);
                objectAcidVersionDAO.save(objectVersion);
            } catch (Exception e) {
                logger.error("Error versioning object_acid={}", o, e);
                throw e;
            }
        }
    }

    /**
     * Returns maximum version id
     *
     * @param oid object id
     * @return latet version or 0 if no version found
     */
    public int getLastVersion(int oid) {
        return (objectVersionDAO.findByOid(oid).isEmpty()) ?  0 : objectVersionDAO.findMaxVersionByOid(oid);
    }

    /**
     * Rolls back an object. Older copy is perserved.
     * TODO tx handling
     * TODO version id control
     * TODO extract rollback to enable testing
     */
    public String rollback() {
        try {
            if (isParamNull("oid") || isParamNull("version")) {
                logger.error("Params oid or version null!");
                return fail();
            }

            int oid = Integer.parseInt(Faces.getRequestParameter("oid"));
            int version = Integer.parseInt(Faces.getRequestParameter("version"));

            logger.trace("User={} requesting rollbacking oid={} to version={}", auth.getCurrentUserId(), oid, version);

            //2. Version the current instance
            final List<FieldDefinition> flist = fdidDAO.findAll();
            final List<ObjectString> archiveStrings = new ArrayList<>();
            final List<ObjectAcid> archiveAcids = new ArrayList<>();

            for (FieldDefinition f : flist) {
                ObjectString os = objectStringDAO.findByOidAndFdid(oid, f.getFdid());
                if (os != null) {
                    archiveStrings.add(new ObjectStringBuilder().setCopy(os).createObjectString());
                } else {
                    logger.trace("No string val for oid={} fdid={}", oid, f.getFdid());
                }

                ObjectAcid objAcid = objectAcidDAO.findByOidAndFdid(oid, f.getFdid());

                if (objAcid != null) {
                    archiveAcids.add(new ObjectAcid(objAcid));
                } else {
                    logger.trace("No acid val for oid={} fdid={}", oid, f.getFdid());
                }
            }

            logger.trace("Archive acids size={}", archiveAcids.size());
            logger.trace("Archive strings size={}", archiveStrings.size());

            versionAcid(archiveAcids);
            versionStrings(archiveStrings);

            logger.trace("Done archiving current instance");

            //3. Replace object string and acid values with version's string and acid values
            List<FieldDefinition> list = fdidDAO.findAll();

            List<ObjectString> objStrToUpdate = new ArrayList<>();
            List<ObjectAcid> objAcidToUpdate = new ArrayList<>();

            for (FieldDefinition f : list) {

                int fdid = f.getFdid();

                if (ObjectWriter.isString(f.getFdid())) {
                    ObjectStringVersion historyObject = objectStringVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);
                    ObjectString objectString1 = objectStringDAO.findByOidAndFdid(oid, fdid);
                    objectString1.setValue(historyObject.getValue());
                    objStrToUpdate.add(objectString1);
                } else { //an acid
                    ObjectAcidVersion historyObjectAcid = objectAcidVersionDAO.findByOidAndFdidAndVersion(oid, fdid, version);
                    ObjectAcid objectAcid1 = objectAcidDAO.findByOidAndFdid(oid, fdid);
                    objectAcid1.setValue(historyObjectAcid.getValue());
                    objAcidToUpdate.add(objectAcid1);
                }
            }
            //4. Hit the dao, update the lists
            objectStringDAO.saveOrUpdateList(objStrToUpdate);
            objectAcidDAO.saveOrUpdateList(objAcidToUpdate);

            //5. Create a new ObjectVersion. Note that the version Id must correspond to version acid and version string.
            // Currently done in the version methods.(Doing this before can trip up the version number)
            final ObjectVersion objectVersion = new ObjectVersionBuilder().setVersionId(getLastVersion(oid) + 1)
                    .setCreationDate(new Date()).setNotes("Rollback").setOid(oid).setUserId(auth.getCurrentUserId())
                    .createObjectVersion();
            objectVersionDAO.save(objectVersion);

            logger.trace("Done rolling back object");
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

    public boolean isParent(int oid) {
        try {
            Object o = objectDAO.findByOid(oid);
            return o.isParent();
        } catch (Exception e) {
            logger.error("Error finding parent attribute", e.getMessage());
        }
        return false;
    }

    //Getters and setters -------------------------------------------------------------------

    /**
     * converts request parameter to file path
     */
    public String getImage() {
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
}


