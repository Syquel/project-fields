package ut.com.mrozekma.atlassian.bitbucket.projectFields.rest;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.mrozekma.atlassian.bitbucket.projectFields.rest.ProjectFieldsRestResource;
import com.mrozekma.atlassian.bitbucket.projectFields.rest.ProjectFieldsRestResourceModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericEntity;

public class ProjectFieldsRestResourceTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        ProjectFieldsRestResource resource = new ProjectFieldsRestResource();

        Response response = resource.getMessage(1234);
        final ProjectFieldsRestResourceModel message = (ProjectFieldsRestResourceModel) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
