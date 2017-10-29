package it.com.mrozekma.atlassian.bitbucket.projectFields.rest;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.mrozekma.atlassian.bitbucket.projectFields.rest.ProjectFieldsRestResource;
import com.mrozekma.atlassian.bitbucket.projectFields.rest.ProjectFieldsRestResourceModel;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

public class ProjectFieldsRestResourceFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/projectfieldsrestresource/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        ProjectFieldsRestResourceModel message = resource.get(ProjectFieldsRestResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
