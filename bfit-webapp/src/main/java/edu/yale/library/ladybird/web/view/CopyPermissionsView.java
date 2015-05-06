package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for copying permissions
 * @see UserProjectView
 * @see AssignProjectFieldView
 * @see UserProjectFieldView
 *
 * TODO converters
 *
 * @author Osman Din
 */

@ManagedBean
@ViewScoped
public class CopyPermissionsView extends AbstractView {

    private Logger logger = LoggerFactory.getLogger(CopyPermissionsView.class);

    private int sourceUser;

    private int destUser;

    private int project;

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

    @Inject
    private UserProjectDAO userProjectDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    /**
     * TODO test
     * Copies metadata auth.
     * @return NavigationCase outcome. ok if all well. failes if an error occures e.g. due to project assignment error.
     */
    public String copy() {
        logger.debug("Copying permsisions from userId={} to userId={} for projectId={}", sourceUser, destUser, project);

        if (sourceUser == destUser) {
            logger.error("User permissions cannot be self assigned");
            return NavigationCase.FAIL.toString();
        }

        //Check if both source and dest users are assigned to this project in the first place
        if (userProjectDAO.findByUserAndProject(sourceUser, project).isEmpty()
            || userProjectDAO.findByUserAndProject(destUser, project).isEmpty()) {
            logger.error("Either source or destination user have not been assigned to this project");
            return NavigationCase.FAIL.toString();
        }


        try {
            //1. get all permissions for source user, and put them in a map
            final List<UserProjectField> sourceUserProjFieldList = userProjectFieldDAO.findByUserAndProject(sourceUser, project);

            //logger.debug("Source user project field list size={}", sourceUserProjFieldList.size());

            if (sourceUserProjFieldList.isEmpty()) {
                logger.error("User permsissions cannot be copied. Source list empty. No metadata access assignments.");
                return NavigationCase.FAIL.toString();
            }

            final Map<Integer, String> sourceFdidRoleMap = new HashMap<>();

            for (UserProjectField userProjectField: sourceUserProjFieldList) {
                int fdid = userProjectField.getFdid();
                String role = userProjectField.getRole();
                sourceFdidRoleMap.put(fdid, role);
            }

            final List<UserProjectField> destUserProjFieldList = userProjectFieldDAO.findByUserAndProject(destUser, project);

            if (destUserProjFieldList.isEmpty()) {
                logger.error("User permsissions cannot be copied. Dest list empty");
                return NavigationCase.FAIL.toString();
            }

            //logger.debug("Dest user project field list size={}", destUserProjFieldList.size());

            //2. Update corresponding entries
            for (UserProjectField destUserProj: destUserProjFieldList) {
                 String roleInSource = sourceFdidRoleMap.get(destUserProj.getFdid());
                 destUserProj.setRole(roleInSource);
            }

            userProjectFieldDAO.saveOrUpdateList(destUserProjFieldList);

            return ok();

        } catch (Exception e) {
            logger.error("Error copying permissions", e);
            return fail();
        }
    }

    public int getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(int sourceUser) {
        this.sourceUser = sourceUser;
    }

    public int getDestUser() {
        return destUser;
    }

    public void setDestUser(int destUser) {
        this.destUser = destUser;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }
}
