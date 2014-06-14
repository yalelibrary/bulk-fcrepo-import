package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class HydraPointerProcessor {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectDAO objectDAO = new ObjectHibernateDAO();

    public void write(final ImportEntityValue importEntityValue) {
        try {
            Map<ImportEntity.Column, ImportEntity.Column> columnMap =
                    importEntityValue.getColumnValuesWithOIds(FunctionConstants.F11);

            Set<ImportEntity.Column> keySet = columnMap.keySet();

            for (ImportEntity.Column col : keySet) {
                int oid = Integer.parseInt(col.getValue().toString());

                Object object = objectDAO.findByOid(oid);
                object.setRoid(Integer.parseInt(columnMap.get(col).getValue().toString())); //TODO check business logic
                objectDAO.saveOrUpdateList(Collections.singletonList(object));
            }
        } catch (Exception e) {
            logger.error("Error={}", e);
        }
    }
}
