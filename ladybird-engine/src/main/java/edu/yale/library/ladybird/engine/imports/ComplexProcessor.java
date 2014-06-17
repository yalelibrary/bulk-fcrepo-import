package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ComplexProcessor {
    private Logger logger = LoggerFactory.getLogger(ComplexProcessor.class);

    final ObjectDAO objectDAO = new ObjectHibernateDAO();

    public void process(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        final List<Object> changedObjectLit = new ArrayList<>();

        int tmp = 0;

        for (int i = 0; i < rowList.size(); i++) {
            try {
                Object object = objectDAO.findByOid(asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i)));

                Integer f1 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F1, i));
                Integer f4 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F4, i));
                //Integer f6 = asInt(importEntityValue.getRowFieldValue(FunctionConstants.F6, i));

                //Set parent oid and parent property:
                if (f4 == 0) {
                    object.setParent(true);
                    object.setP_oid(0);
                    tmp = f1;
                    changedObjectLit.add(object);
                } else {
                    object.setParent(false);
                    object.setP_oid(tmp);
                    changedObjectLit.add(object);
                }
            } catch (Exception e) {
                logger.trace(e.getMessage()); //ignore
            }
        }
        logger.debug(changedObjectLit.toString());
        objectDAO.saveOrUpdateList(changedObjectLit);
    }

    private Integer asInt(String s) {
        return Integer.parseInt(s);
    }
}
