package com.mrozekma.atlassian.bitbucket.projectFields.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectSupplier;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.gson.*;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomField;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomFieldValue;
import com.mrozekma.atlassian.bitbucket.projectFields.impl.License;
import net.java.ao.Query;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectFieldsData implements WebResourceDataProvider, Jsonable {
    @ComponentImport
    private final UserManager userManager;

    @ComponentImport
    private final PermissionService permissionService;

    @ComponentImport
    private final ProjectSupplier projectSupplier;

    @ComponentImport
    private final ActiveObjects activeObjects;

    @ComponentImport
    private final PluginLicenseManager pluginLicenseManager;

    private final Gson gson = new Gson();

    public ProjectFieldsData(UserManager userManager, PermissionService permissionService, ProjectSupplier projectSupplier, ActiveObjects activeObjects, PluginLicenseManager pluginLicenseManager) {
        this.userManager = userManager;
        this.permissionService = permissionService;
        this.projectSupplier = projectSupplier;
        this.activeObjects = activeObjects;
        this.pluginLicenseManager = pluginLicenseManager;
    }

    @Override public Jsonable get() {
        return this;
    }

    @Override public void write(Writer writer) throws IOException {
        /*
        {
          "fields": [
            {
              "name": "field1",
              "description": "description 1"
            },
            {
              "name": "field2",
              "description": "description 2"
            }
          ],
          "projects": {
            "PROJECT1": {
              "field1": "value 1",
              "field2": "value 2",
              ...
            },
            ...
          }
        }
        */
        final JsonObject json = new JsonObject();

        if(License.isValid(this.pluginLicenseManager)) {
            // Cache field ID -> field, and store the fields in the 'fields' key of the JSON object
            final Map<Integer, CustomField> fields = new HashMap<>();
            final JsonArray jsonFields = new JsonArray();
            json.add("fields", jsonFields);
            for(CustomField field : this.activeObjects.find(CustomField.class, Query.select().where("VISIBLE = ?", true).order("SEQ asc"))) {
                final JsonObject fieldJSON = new JsonObject();
                fieldJSON.addProperty("name", field.getName());
                fieldJSON.addProperty("description", field.getDescription());
                jsonFields.add(fieldJSON);
                fields.put(field.getID(), field);
            }

            // Cache project key -> read access (lazy)
            final Map<String, Boolean> projectKeys = new HashMap<>();

            final JsonObject projects = new JsonObject();
            json.add("projects", projects);
            final UserProfile user = this.userManager.getRemoteUser();

            for(CustomFieldValue value : this.activeObjects.find(CustomFieldValue.class)) {
                if(projectKeys.computeIfAbsent(value.getProjectKey(), key -> {
                    try {
                        return this.permissionService.hasProjectPermission(this.projectSupplier.getByKey(key), Permission.PROJECT_READ);
                    } catch(AuthorisationException e) {
                        return false;
                    }
                })) {
                    final String projectKey = value.getProjectKey();
                    if(fields.containsKey(value.getFieldId())) {
                        if(!projects.has(projectKey)) {
                            projects.add(projectKey, new JsonObject());
                        }
                        projects.getAsJsonObject(projectKey).addProperty(fields.get(value.getFieldId()).getName(), value.getValue());
                    }
                }
            }
        } else {
            json.add("fields", new JsonArray());
            json.add("projects", new JsonObject());
        }

        writer.write(this.gson.toJson(json));
    }
}
