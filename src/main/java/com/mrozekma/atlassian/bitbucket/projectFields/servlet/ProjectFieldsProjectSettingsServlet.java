package com.mrozekma.atlassian.bitbucket.projectFields.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectSupplier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomField;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomFieldValue;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectFieldsProjectSettingsServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(ProjectFieldsProjectSettingsServlet.class);

    @ComponentImport
    private final UserManager userManager;

    @ComponentImport
    private final LoginUriProvider loginUriProvider;

    @ComponentImport
    private final ProjectSupplier projectSupplier;

    @ComponentImport
    private final PermissionValidationService permissionValidationService;

    @ComponentImport
    private final I18nResolver i18n;

    @ComponentImport
    private final SoyTemplateRenderer soyRenderer;

    @ComponentImport
    private final PageBuilderService pageBuilder;

    @ComponentImport
    private final ActiveObjects activeObjects;

    public ProjectFieldsProjectSettingsServlet(UserManager userManager, LoginUriProvider loginUriProvider, ProjectSupplier projectSupplier, PermissionValidationService permissionValidationService, I18nResolver i18n, SoyTemplateRenderer soyRenderer, PageBuilderService pageBuilder, ActiveObjects activeObjects) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.projectSupplier = projectSupplier;
        this.permissionValidationService = permissionValidationService;
        this.i18n = i18n;
        this.soyRenderer = soyRenderer;
        this.pageBuilder = pageBuilder;
        this.activeObjects = activeObjects;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handle(req, resp, null);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Map<Integer, String> vals = new HashMap<>();
        for(Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            final String k = e.getKey();
            if(!k.startsWith("val_")) {
                throw new IllegalArgumentException(k);
            }
            final int id = Integer.parseInt(k.substring(4));

            String v = e.getValue()[0];
            if(!v.isEmpty()) {
//                resp.getWriter().format("%d: %s\n", id, v);
                vals.put(id, v);
            }
        }

        this.handle(req, resp, vals);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp, Map<Integer, String> updates) throws ServletException, IOException {
        final String[] parts = req.getPathInfo().split("/");
        if(parts.length != 2 || !parts[0].isEmpty()) {
            resp.sendError(404);
            return;
        }

        final Project project = this.projectSupplier.getByKey(parts[1]);
        if(project == null) {
            resp.sendError(404, this.i18n.getText("bitbucket.web.project.key.notfound", parts[1]));
            return;
        }

        this.permissionValidationService.validateForProject(project, Permission.PROJECT_ADMIN);
        boolean isSysadmin = this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());

        final List<Map<String, Object>> values = new LinkedList<>();
        this.activeObjects.executeInTransaction(() -> {
            for(CustomField field : this.activeObjects.find(CustomField.class)) {
                final String[] options = field.getOptions().isEmpty() ? null : field.getOptions().split("\r?\n");
                final boolean editable = (field.isEditableByProjectAdmins() || isSysadmin);

                // User specified a value that isn't one of the options
                if(updates != null && options != null && updates.containsKey(field.getID()) && !Arrays.asList(options).contains(updates.get(field.getID()))) {
                    updates.put(field.getID(), null);
                }

                CustomFieldValue val = null;
                {
                    final CustomFieldValue[] vals = this.activeObjects.find(CustomFieldValue.class, Query.select().where("PROJECT_KEY = ? AND FIELD_ID = ?", project.getKey(), field.getID()));
                    if(vals.length > 0) {
                        val = vals[0];
                        if(editable && updates != null) {
                            if(updates.containsKey(field.getID())) {
                                // Update value
                                final String newVal = updates.get(field.getID());
                                if(newVal != null) {
                                    val.setValue(newVal);
                                }
                            } else {
                                // Delete value
                                this.activeObjects.delete(val);
                                val = null;
                            }
                        }
                    } else if(editable && updates != null && updates.containsKey(field.getID())) {
                        // Create value
                        final String newVal = updates.get(field.getID());
                        if(newVal != null) {
                            val = this.activeObjects.create(
                                    CustomFieldValue.class,
                                    new DBParam("PROJECT_KEY", project.getKey()),
                                    new DBParam("FIELD_ID", field.getID()),
                                    new DBParam("VALUE", newVal)
                            );
                        }
                    }
                }

                final Map<String, Object> m = new HashMap<>();
                m.put("field", field);
                m.put("options", options);
                m.put("value", (val == null) ? "" : val.getValue());
                m.put("editable", editable);
                values.add(m);
            }
            return null;
        });

        this.pageBuilder.assembler().resources()
                .requireContext("com.mrozekma.atlassian.bitbucket.projectFields.project-settings")
                .requireWebResource("com.atlassian.auiplugin:aui-select");

        resp.setContentType("text/html");

        final Map<String, Object> data = new HashMap<String, Object>() {{
            put("project", project);
            put("fields", values);
            put("configUrl", isSysadmin ? req.getContextPath() + "/plugins/servlet/project-fields-admin" : null);
            if(updates != null) {
                put("message", new ResultMessage("success", i18n.getText("bitbucket.web.changes.saved")).toMap());
            }
        }};
        this.soyRenderer.render(resp.getWriter(), "com.mrozekma.atlassian.bitbucket.projectFields:project-fields-soy", "com.mrozekma.atlassian.bitbucket.pluginFields.project_settings", data);
    }
}
