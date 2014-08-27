package edu.yale.library.ladybird.web.view;

import com.google.common.collect.Lists;
import edu.yale.library.ladybird.engine.file.ImageMagickProcessor;
import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.entity.AuthorityControlBuilder;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectAcidBuilder;
import edu.yale.library.ladybird.entity.ObjectBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
import edu.yale.library.ladybird.entity.ObjectStringBuilder;
import edu.yale.library.ladybird.entity.Project;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Used to create a new object
 */
@ManagedBean
@SessionScoped
public class NewObjectView extends AbstractView implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(NewObjectView.class);

    private List<FieldDefinitionValue> fieldDefinitionvalueList;

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


    @PostConstruct
    public void init() {
        initFields();
        logger.trace("Init NewObjectView");

        if (fieldDefinitionvalueList == null || fieldDefinitionvalueList.isEmpty()) {
            fieldDefinitionvalueList = getEmptyFdidValues();
        }
    }

    private List<FieldDefinitionValue> getEmptyFdidValues() {
        final List<FieldDefinitionValue> fdidValues = new ArrayList<>();

        try {
            List<FieldDefinition> list = fdidDAO.findAll();

            for (FieldDefinition f : list) {
                fdidValues.add(new FieldDefinitionValue(f, new String[]{""}));
            }
        } catch (Exception e) {
            logger.error("Error building empty map", e);
        }

        return fdidValues;
    }

    /**
     * TODO DUPLICATE
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

    public void newFdid(int fdid) {
        addFdidtoFDV(fdid);
    }

    public void addFdidtoFDV(final int fdid) {
        for (FieldDefinitionValue f : fieldDefinitionvalueList) {
            if (f.getFdid().getFdid() == fdid) {
                final String[] ex = f.getValues();
                final List<String> list = Lists.newArrayList(ex);
                list.add("");
                final String[] newValueArray = list.toArray(new String[list.size()]);
                f.setValues(newValueArray);
            }
        }
    }

    /**
     * Add new oid metadata
     * TODO tx - should fail the addition if one of the values fails to happen.
     * TODO need FDV to object* converter. or at least write a test.
     */
    public String editOidMetadata() {
        try {
            final Date date = new Date(); //same date for all values

            final int userId = auth.getCurrentUserId();
            final Project project = auth.getDefaultProjectForCurrentUser();

            if (project == null) {
                logger.error("No default project for user={}", userId);
                return fail();
            }

            //get thumbnail:
            byte[] thumbnail = getThumbnail();

            if (thumbnail == null || thumbnail.length == 0) {
                logger.error("Thumbnail null. Cannot save object");
                return fail();
            }


            final Object newObject = new ObjectBuilder().setDate(new Date()).setParent(false)
                    .setUserId(auth.getCurrentUserId())
                    .setProjectId(auth.getDefaultProjectForCurrentUser().getProjectId()).createObject();
            int oid = objectDAO.save(newObject);

            logger.debug("Saved oid={}", oid);

            //save object file:
            final ObjectFile objectFile = new ObjectFileBuilder().setDate(date).setOid(oid).setUserId(userId).setThumbnail(thumbnail).createObjectFile();
            objectFileDAO.save(objectFile);

            //save object string:
            ObjectStringBuilder osb = new ObjectStringBuilder();
            ObjectAcidBuilder oab = new ObjectAcidBuilder();

            for (FieldDefinitionValue f : fieldDefinitionvalueList) {
                final int fdid = f.getFdid().getFdid();
                final String[] values = f.getValues();

                for (final String value : values) {
                    if (FieldConstantUtil.isString(fdid)) {
                        objectStringDAO.save(osb.setDate(date).setFdid(fdid).setUserId(userId).setOid(oid).setValue(value).createObjectString());
                    } else {
                        int acid = authorityControlDAO.save(new AuthorityControlBuilder().setValue(value).setDate(date)
                                .setUserId(userId).setFdid(fdid).createAuthorityControl());
                        objectAcidDAO.save(oab.setDate(date).setFdid(fdid).setUserId(userId).setObjectId(oid)
                                .setValue(acid).createObjectAcid());
                    }
                }
            }
            logger.debug("Saved metadata for oid={}. Object string size={}. Object acid size={}",
                    oid, objectStringDAO.findByOid(oid).size(), objectAcidDAO.findByOid(oid).size());

            //N.B. empty fieldDefinitionvaluelist, since we're using @SessionScoped:
            fieldDefinitionvalueList = getEmptyFdidValues();

            return ok();
        } catch (Exception e) {
            fieldDefinitionvalueList = getEmptyFdidValues();
            logger.error("Error creating new object={}", e); //TODO this is a mass exception
            return fail();
        }
    }

    private byte[] getThumbnail() {
        try {
            return getBytes(ImageMagickProcessor.getBlankImagePath());
        } catch (IOException e) {
            logger.error("Error getting thumbnail", e);
            return null;
        }
    }

    private byte[] getBytes(String filePath) throws IOException {
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            logger.error("Error getting bytes for path={}", filePath, e);
            throw e;
        }
        return bytes;
    }

    //TODO see if it can be replaced with engine's FDV
    public class FieldDefinitionValue implements Serializable {
        private FieldDefinition fdid;
        String[] values;

        public FieldDefinition getFdid() {
            return fdid;
        }

        public void setFdid(FieldDefinition fdid) {
            this.fdid = fdid;
        }

        public String[] getValues() {
            return values;
        }

        public void setValues(String[] values) {
            this.values = values;
        }

        public FieldDefinitionValue(FieldDefinition fdid, String[] values) {
            this.fdid = fdid;
            this.values = values;
        }

        @Override
        public String toString() {
            return "FieldDefinitionValue{"
                    + "fdid=" + fdid
                    + ", values=" + Arrays.toString(values)
                    + '}';
        }
    }




    //Getters and setters -------------------------------------------------------------------
    public List<FieldDefinitionValue> getFieldDefinitionvalueList() {
        return fieldDefinitionvalueList;
    }

    public void setFieldDefinitionvalueList(List<FieldDefinitionValue> list) {
        this.fieldDefinitionvalueList = list;
    }

}
