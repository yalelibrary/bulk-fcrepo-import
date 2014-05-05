package edu.yale.library.ladybird.web.view;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.sun.faces.htmlunit.HtmlUnitFacesTestCase;
import edu.yale.library.ladybird.web.AbstractWarTest;
import junit.framework.TestSuite;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.List;

public class UserViewIT extends HtmlUnitFacesTestCase {

    private static final String FORM = "UserForm";
    private static final String PATH = "/pages/secure/user_manager_form.xhtml";

    public UserViewIT(String name) {
        super(name);
    }

    @BeforeClass
    public static void setup() throws MalformedURLException {
        try {
            AbstractWarTest.setupContainer();
        } catch (RuntimeException e) {
            fail("Error starting test container");
        }
    }

    @Test
    public void testTestResult() throws Exception {
        final String testUserNameStr = "alice0";
        final String testEmailStr = "alice@yale.edu";
        final String testNameStr = "alice";

        setUp();
        client.setJavaScriptEnabled(false);
        HtmlPage page = getPage(PATH);

        final HtmlTextInput userNameInputText = page.getFormByName(FORM).getInputByName(FORM + ":username");
        userNameInputText.setValueAttribute(testUserNameStr);

        final HtmlTextInput nameInputText = page.getFormByName(FORM).getInputByName(FORM + ":name");
        nameInputText.setValueAttribute(testNameStr);

        final HtmlTextInput userEmailInputText = page.getFormByName(FORM).getInputByName(FORM + ":email");
        userEmailInputText.setValueAttribute(testEmailStr);

        final List list = getAllElementsOfGivenClass(page, null, HtmlSubmitInput.class);
        final HtmlSubmitInput button = (HtmlSubmitInput) list.get(0);
        page = button.click();

        final String pageAsText = page.asText();

        assertTrue(pageAsText.contains(testEmailStr));

        assertFalse(pageAsText.contains("Submit"));

        HtmlElement htmlElement = page.getElementById(FORM + ":UserDatatable:0:email");
        assertEquals("Element value mismatch", htmlElement.getTextContent(), testEmailStr);

        HtmlElement htmlElementUserName = page.getElementById(FORM + ":UserDatatable:0:username");
        assertEquals("Element value mismatch", htmlElementUserName.getTextContent(), testUserNameStr);

        HtmlElement htmlElementName = page.getElementById(FORM + ":UserDatatable:0:name");
        assertEquals("Element value mismatch", htmlElementName.getTextContent(), testNameStr);
    }

    public void tearDown() {
        super.tearDown();
    }

    public static junit.framework.Test suite() {
        return (new TestSuite(UserViewIT.class));
    }

}
