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
import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@ViewScoped
@SuppressWarnings("unchecked")
public class ObjectMetadataView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    /** fdid value map*/
    private Map<Integer, String> map;

    /** Path to image */
    private String image;

    /** for file image info */
    private ObjectFile objectFile;

    /** for metadata */
    private edu.yale.library.ladybird.entity.Object object;

    /** for metadata */
    private ObjectString objectString;

    /** for metadata */
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
        //logger.debug("Init ObjectMedataView");
        try {
            final String oidStr = Faces.getRequestParameter("oid");
            final int oid = Integer.parseInt(oidStr);

            objectFile = objectFileDAO.findByOid(oid);

            map = populateFieldMap(oid); //map contains fdid values
        } catch (Exception e) {
            logger.error("Error={}", e.getMessage());
        }
    }

    /** Loads field definitions from dao */
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
    }


    public String getValueByOidAndFdid(int oid, int fdid) {
        try {
            //1. Find acid value for this oid (assuming that's what's needed here)
            logger.debug("Finding ObjectAcid by oid={} and fdid={}", oid, fdid);

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
     * converter helper
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     *
     * @see edu.yale.library.ladybird.engine.model.FieldConstantRules#convertStringToFieldConstant(String)
     * for similiar functionality
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
            Object o =  objectDAO.findByOid(oid);
            return o.isParent();
        } catch (Exception e) {
            logger.error("Error finding parent attribute", e.getMessage());
        }
        return false;
    }

    //Getters and setters -------------------------------------------------------------------
    /** converts request parameter to file path */
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

    /** Should be moved */
    public int getCount() {
        return objectFileDAO.count();
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}


