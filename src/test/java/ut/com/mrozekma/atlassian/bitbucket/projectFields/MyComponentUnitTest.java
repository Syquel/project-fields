package ut.com.mrozekma.atlassian.bitbucket.projectFields;

import org.junit.Test;
import com.mrozekma.atlassian.bitbucket.projectFields.api.MyPluginComponent;
import com.mrozekma.atlassian.bitbucket.projectFields.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}