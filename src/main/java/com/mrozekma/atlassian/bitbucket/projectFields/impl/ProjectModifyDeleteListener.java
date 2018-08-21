package com.mrozekma.atlassian.bitbucket.projectFields.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.event.project.ProjectDeletedEvent;
import com.atlassian.bitbucket.event.project.ProjectModifiedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomFieldValue;
import net.java.ao.Query;

import javax.inject.Named;

@Named("projectModifyDeleteListener")
public class ProjectModifyDeleteListener {
    @ComponentImport
    private final ActiveObjects activeObjects;

    public ProjectModifyDeleteListener(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @EventListener
    public void onProjectModified(ProjectModifiedEvent e) {
        final String oldKey = e.getOldValue().getKey(), newKey = e.getNewValue().getKey();
        if(!oldKey.equals(newKey)) {
            this.activeObjects.executeInTransaction(() -> {
                for(CustomFieldValue val : this.activeObjects.find(CustomFieldValue.class, Query.select().where("PROJECT_KEY = ?", oldKey))) {
                    val.setProjectKey(newKey);
                    val.save();
                }
                return null;
            });
        }
    }

    @EventListener
    public void onProjectDelete(ProjectDeletedEvent e) {
        this.activeObjects.delete(this.activeObjects.find(CustomFieldValue.class, Query.select().where("PROJECT_KEY = ?", e.getProject().getKey())));
    }
}
