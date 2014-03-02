package ladybird.persistence;

import edu.yale.library.ApplicationProperties;
import edu.yale.library.persistence.HibernateUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class HibernateUtilTest {

    private static final int IDX = 3;

    /**
     * Test policy
     */
    @Test
    public void configRulesShouldReturnApplicationRuleValueWhenInvoked() {
        assert (ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG
                == HibernateUtil.DBRules.proceedWith(HibernateUtil.DBConfigState.INCOMPLETE));
    }

    /**
     * Tests policy
     */
    @Test
    public void dbConfigStateShouldReturnApplicationRuleValueWhenInvoked() {
        assert (ApplicationProperties.RUN_WITH_INCOMPLETE_CONFIG
                == HibernateUtil.DBConfigState.INCOMPLETE.getConfig());
    }

    /**
     * Tests whether the relevant .properties file contains the right identifier for kicking off embedded DB.
     * The mechanism should return the same default file identifer from .properties as coded in
     * edu.yale.library.ApplicationProperties.
     * For example, "database" should return "default". If it returns something else (and the test fails), it means
     * that edu.yale.library.ApplicationProperties does not recognize this value.
     * <p/>
     * TODO This method will change as the underlying class definition is changed.
     *
     * @throws Exception
     * @see edu.yale.library.persistence.HibernateUtil.ConfigReader
     */
    @Test
    public void assertGetConfigFileMapping() throws Exception {
        Class<?> innerClass = Class.forName("edu.yale.library.persistence.HibernateUtil$ConfigReader");
        Constructor<?> constructor = innerClass.getDeclaredConstructor(edu.yale.library.persistence.HibernateUtil.class);
        constructor.setAccessible(true);
        Object child = constructor.newInstance(new HibernateUtil());
        Method method = HibernateUtil.class.getDeclaredClasses()[IDX].getDeclaredMethod("readPropertiesFromFile",
                String.class, String.class);
        method.setAccessible(true);
        assertEquals(method.invoke(child, ApplicationProperties.DATABASE_STRING_IDENTIFIER, "/"
                + ApplicationProperties.PROPS_FILE), ApplicationProperties.DATABASE_DEFAULT_IDENTIFIER);
    }
}
