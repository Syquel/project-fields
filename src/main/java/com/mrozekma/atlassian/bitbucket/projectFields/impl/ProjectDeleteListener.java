package com.mrozekma.atlassian.bitbucket.projectFields.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.event.project.ProjectDeletedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomFieldValue;
import net.java.ao.Query;

import javax.inject.Named;

@Named("projectDeleteListener")
public class ProjectDeleteListener {
    @ComponentImport
    private final ActiveObjects activeObjects;

    public ProjectDeleteListener(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @EventListener
    public void onProjectDelete(ProjectDeletedEvent e) {
        this.activeObjects.delete(this.activeObjects.find(CustomFieldValue.class, Query.select().where("PROJECT_KEY = ?", e.getProject().getKey())));
    }
}
