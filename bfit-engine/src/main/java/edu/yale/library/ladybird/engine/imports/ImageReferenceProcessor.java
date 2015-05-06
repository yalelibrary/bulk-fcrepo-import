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

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImageReferenceProcessor {

    private final Logger logger = LoggerFactory.getLogger(ImageReferenceProcessor.class);

    private ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();

    public void write(final ImportValue importValue) {
        try {
            Map<Import.Column, Import.Column> columnMap =
                    importValue.getColumnValuesWithOIds(FunctionConstants.F300);

            Set<Import.Column> keySet = columnMap.keySet();

            for (Import.Column col : keySet) {
                int oid = Integer.parseInt(col.getValue().toString());

                ObjectFile objectFile = objectFileDAO.findByOid(oid);
                String valueToUpdate = columnMap.get(col).getValue().toString(); //TODO check
                objectFile.setFilePath(valueToUpdate);

                objectFileDAO.saveOrUpdateList(Collections.singletonList(objectFile));
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

}
