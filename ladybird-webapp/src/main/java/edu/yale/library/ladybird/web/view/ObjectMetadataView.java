package edu.yale.library.ladybird.web.view;


//import edu.yale.library.ladybird.entity.*;
import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;

import org.omnifaces.util.Faces;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@ViewScoped
@SuppressWarnings("unchecked")
public class ObjectMetadataView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

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


    @PostConstruct
    public void init() {
        initFields();
        dao = objectDAO;
        //logger.debug("Init ObjectMedataView");
        try {
            String oidStr = Faces.getRequestParameter("oid");
            //logger.debug("Request oid={}", oidStr);

            objectFile = objectFileDAO.findByOid(Integer.parseInt(oidStr));
            //logger.debug("Object File={}", objectFile);

        } catch (Exception e) {
            logger.error("Error finding ObjectFile entity");
        }
    }

    //TODO this should be replaced with List<FieldDefintionValue>
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
}


