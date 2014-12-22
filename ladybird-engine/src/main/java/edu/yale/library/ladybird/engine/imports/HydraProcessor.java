package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.Hydra;
import edu.yale.library.ladybird.entity.HydraPublish;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.HydraDAO;
import edu.yale.library.ladybird.persistence.dao.HydraPublishDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.HydraHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.HydraPublishHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Processes 'PUBLISH' and 'DELETE'
 */
public class HydraProcessor {

    private Logger logger = LoggerFactory.getLogger(HydraProcessor.class);

    //TODO inject DAO(s)

    HydraDAO hydraDAO = new HydraHibernateDAO();

    ObjectFileDAO objectDAO = new ObjectFileHibernateDAO();

    HydraPublishDAO hydraPublishDAO = new HydraPublishHibernateDAO();

    enum ACTION {
        PUBLISH("PUBLISH"), DELETE("DELETE");

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

    public void write(final ImportValue importValue) {
        try {
            Map<Import.Column, Import.Column> columnMap = importValue.getColumnValuesWithOIds(FunctionConstants.F40);

            Set<Import.Column> keySet = columnMap.keySet();

            for (Import.Column col : keySet) {
                int oid = Integer.parseInt(col.getValue().toString());

                ACTION operation = ACTION.valueOf(columnMap.get(col).getValue().toString());
                if (operation == ACTION.PUBLISH) {
                    Hydra hydra = new Hydra();
                    hydra.setDate(new Date());
                    hydra.setOid(Integer.parseInt(col.getValue().toString()));
                    hydraDAO.save(hydra);
                } else if (operation == ACTION.DELETE) {
                    // TODO check business logic "hydraID?"
                    ObjectFile object = objectDAO.findByOid(oid);
                    if (object.getHydraPublishId() != null) {
                        object.setStatus("DELETED"); //"flag hydra delete date in object table"

                        HydraPublish hydraPublish = hydraPublishDAO.findByOid(oid);
                        hydraPublish.setAction("delete");
                        hydraPublishDAO.saveOrUpdateList(Collections.singletonList(hydraPublish)); //TODO
                    } //else { //no "hydraID"
                    //}
                } else {
                    throw new UnsupportedOperationException("Unknown operation");
                }
            }
        } catch (Exception e) {
            logger.error("Eror processing hydra column. Error={}", e);
            throw e;
        }
    }
}
