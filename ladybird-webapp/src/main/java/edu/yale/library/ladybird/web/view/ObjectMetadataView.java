package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.web.view.template.FieldDefinitionValue;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectMetadataView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    /**
     * fdid value map
     */
    //private Map<Integer, String> map;

    private List<FieldDefinitionValue> fieldDefinitionvalueList;

    /**
     * Path to image
     */
    private String image;

    /**
     * for file image info
     */
    private ObjectFile objectFile;

    /**
     * for metadata
     */
    private edu.yale.library.ladybird.entity.Object object;

    /**
     * for metadata
     */
    private ObjectString objectString;

    /**
     * for metadata
     */
    private ObjectAcid objectAcid;

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
    private FieldDefinitionDAO fieldDefinitionDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = objectDAO;
        logger.debug("Init ObjectMedataView");
        try {
            final String oidStr = Faces.getRequestParameter("oid");
            final int oid = Integer.parseInt(oidStr);

            objectFile = objectFileDAO.findByOid(oid);

            if (fieldDefinitionvalueList == null) {
                logger.debug("Field definition value null");
                fieldDefinitionvalueList = buildFieldDefnList(oid);
            }
        } catch (Exception e) {
            logger.error("Error={}", e.getMessage());
        }
    }

    /*
    private Map populateFieldMap(int oid) {
        final Map<Integer, String> map = new HashMap<>();

        try {
            List<FieldDefinition> list = fieldDefinitionDAO.findAll();

            for (FieldDefinition f : list) {
                map.put(f.getFdid(), getValueByOidAndFdid(oid, f.getFdid()));
            }
        } catch (Exception e) {
            logger.debug("Error loading fdid={}", e);
        }

        return map;
    } */

    private List<FieldDefinitionValue> buildFieldDefnList(int oid) {
        final List<FieldDefinitionValue> fieldDefinitionvalueList = new ArrayList<>();

        try {
            List<FieldDefinition> list = fieldDefinitionDAO.findAll();

            for (FieldDefinition f : list) {
                fieldDefinitionvalueList.add(new FieldDefinitionValue(f, getValueByOidAndFdid(oid, f.getFdid())));
            }
        } catch (Exception e) {
            logger.debug("Error loading fdid={}", e);
        }

        return fieldDefinitionvalueList;
    }


    public String getValueByOidAndFdid(int oid, int fdid) {
        try {
            //1. Find acid value for this oid (assuming that's what's needed here)
            logger.trace("Finding ObjectAcid by oid={} and fdid={}", oid, fdid);

            final ObjectAcid objectAcid = objectAcidDAO.findByOidAndFdid(oid, fdid);
            //logger.debug("Found entry={}", objectAcid.toString());
            int acid = objectAcid.getValue();

            //2. Get acid value
            //logger.debug("Find AuthorityControl by acid={}", acid);
            final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);
            //logger.debug("Found entry={}", authorityControl.toString());
            return authorityControl.getValue();

        } catch (Exception e) {
            logger.error("Error finding entity={}", e);
        }
        return "N/A";
    }

    /**
     * Returns fdid handle
     *
     * @param fdid int value of fdid, e.g. 69
     * @return handle, e.g. Name or empty string
     */
    public String getFdidHandle(int fdid) {
        try {
            FieldDefinition fieldDefinition = fieldDefinitionDAO.findByFdid(fdid);
            return fieldDefinition.getHandle();
        } catch (Exception e) {
            logger.trace("Error finding fdid value={}", fdid, e);
            return "";
        }
    }

    /**
     * Updates oid metadata
     */
    public String updateOidMetadata() {
        logger.trace("Updating oid metadata for oid={}", Faces.getRequestParameter("oid"));

        final int oid = Integer.parseInt(Faces.getRequestParameter("oid"));

        final List<AuthorityControl> listToUpdate = new ArrayList<>();

        logger.trace(fieldDefinitionvalueList.toString());

        try {
            for (FieldDefinitionValue fieldDefinitionValue : fieldDefinitionvalueList) {
                logger.trace("Chedking for fdid={} and oid={}", fieldDefinitionValue.getFdid().getFdid(), oid);

                ObjectAcid objectAcid = objectAcidDAO.findByOidAndFdid(oid, fieldDefinitionValue.getFdid().getFdid());
                int acid = objectAcid.getValue();
                final AuthorityControl authorityControl = authorityControlDAO.findByAcid(acid);

                //update only if the field has been changed
                if (!authorityControl.getValue().equalsIgnoreCase(fieldDefinitionValue.getValue())) {
                    authorityControl.setValue(fieldDefinitionValue.getValue());
                    listToUpdate.add(authorityControl);
                    logger.debug("Updating for fdid={} and oid={}. Value={}", fieldDefinitionValue.getFdid().getFdid(), oid, fieldDefinitionValue.getValue());
                }
            }
            authorityControlDAO.saveOrUpdateList(listToUpdate);
        } catch (Exception e) {
            logger.error("Error saving for oid={}", oid, e);
            logger.debug("Full object acid={}", objectAcidDAO.findAll().toString());
            return fail();
        }

        return ok();
    }

    /**
     * converter helper
     *
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     * @see edu.yale.library.ladybird.engine.model.FieldConstantRules#convertStringToFieldConstant(String)
     *      for similiar functionality
     * @see edu.yale.library.ladybird.engine.imports.ObjectWriter#fdidAsInt(String) for duplicate
     */
    private Integer fdidAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String[] parsedString = s.split("fdid=");
            return Integer.parseInt(parsedString[1].replace("}", ""));
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

    public void setImage(String image) {
        this.image = image;
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

    public ObjectString getObjectString() {
        return objectString;
    }

    public void setObjectString(ObjectString objectString) {
        this.objectString = objectString;
    }

    public ObjectAcid getObjectAcid() {
        return objectAcid;
    }

    public void setObjectAcid(ObjectAcid objectAcid) {
        this.objectAcid = objectAcid;
    }

    /**
     * Should be moved
     */
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


