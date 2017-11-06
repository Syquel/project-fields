package com.mrozekma.atlassian.bitbucket.projectFields.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.NotNull;

public interface CustomField extends Entity {
    @NotNull
    int getSeq();
    void setSeq(int seq);

    @NotNull
    String getName();
    void setName(String name);

//    @NotNull
    // This won't be null, but it *can* be empty, and Atlassian treats them identically (for Oracle compatibility, I think)
    String getDescription();
    void setDescription(String description);

    String getOptions();
    void setOptions(String options);

    @Accessor("visible")
    boolean isVisibleInProjectList();
    @Mutator("visible")
    void setVisibleInProjectList(boolean visible);

    @Accessor("editable")
    boolean isEditableByProjectAdmins();
    @Mutator("editable")
    void setEditableByProjectAdmins(boolean editable);
}
