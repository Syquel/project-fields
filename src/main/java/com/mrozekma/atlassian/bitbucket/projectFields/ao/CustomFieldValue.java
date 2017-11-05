package com.mrozekma.atlassian.bitbucket.projectFields.ao;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;

public interface CustomFieldValue extends Entity {
    @NotNull
    String getProjectKey();
    void setProjectKey(String key);

    @NotNull
    int getFieldId();
    void setFieldId(int id);

    String getValue();
    void setValue(String value);
}
