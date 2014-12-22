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

    public void write(final ImportValue importValue) {
        try {
            Map<Import.Column, Import.Column> columnMap =
                    importValue.getColumnValuesWithOIds(FunctionConstants.F11);

            Set<Import.Column> keySet = columnMap.keySet();

            for (Import.Column col : keySet) {
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
