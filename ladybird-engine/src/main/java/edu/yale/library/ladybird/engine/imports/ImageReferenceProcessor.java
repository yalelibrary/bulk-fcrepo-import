package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ImageReferenceProcessor {

    private final Logger logger = LoggerFactory.getLogger(ImageReferenceProcessor.class);

    ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();

    public void write(final ImportEntityValue importEntityValue) {
        try {
            Map<ImportEntity.Column, ImportEntity.Column> columnMap =
                    importEntityValue.getColumnValuesWithOIds(FunctionConstants.F300);

            Set<ImportEntity.Column> keySet = columnMap.keySet();

            for (ImportEntity.Column col : keySet) {
                int oid = Integer.parseInt(col.getValue().toString());

                ObjectFile objectFile = objectFileDAO.findByOid(oid);
                String valueToUpdate = columnMap.get(col).getValue().toString(); //TODO check requirement
                objectFile.setFilePath(valueToUpdate);

                objectFileDAO.saveOrUpdateList(Collections.singletonList(objectFile));
            }
        } catch (Exception e) {
            logger.error("Error={}", e);
        }
    }

}
