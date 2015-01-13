package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectDAO objectDAO = new ObjectHibernateDAO();

    enum ACTION {
        CONFIRM("CONFIRM");

        String name;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        ACTION(String name) {
            this.name = name;
        }
    }

    public void process(final ImportValue importValue) {
        try {
            Map<Import.Column, Import.Column> columnMap =
                    importValue.getColumnValuesWithOIds(FunctionConstants.F00);

            Set<Import.Column> keySet = columnMap.keySet();

            List<Object> objectsToDelete = new ArrayList<>();

            for (Import.Column<String> col : keySet) {
                int oid = Integer.parseInt(col.getValue().toString());

                //TODO validate OID, version metadata, hydra stuff etc

                ACTION operation = ACTION.valueOf(columnMap.get(col).getValue().toString());

                if (operation.equals(ACTION.CONFIRM)) {
                    logger.debug("Add to delete list object={}", oid);
                    Object object = objectDAO.findByOid(oid);
                    objectsToDelete.add(object);
                }
            }

            //Delete objects:
            objectDAO.delete(objectsToDelete);
        } catch (Exception e) {
            logger.error("Error={}", e);
        }
    }
}
