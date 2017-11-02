package com.mrozekma.atlassian.bitbucket.projectFields.servlet;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.ProjectSupplier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomField;
import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectFieldsGlobalSettingsServlet extends HttpServlet{
    private static class ResultMessage {
        final String type, message;

        ResultMessage(String type, String message) {
            this.type = type;
            this.message = message;
        }

        Map<String, String> toMap() {
            return new HashMap<String, String>() {{
                put("type", type);
                put("message", message);
            }};
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ProjectFieldsGlobalSettingsServlet.class);

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

    public ProjectFieldsGlobalSettingsServlet(UserManager userManager, LoginUriProvider loginUriProvider, ProjectSupplier projectSupplier, PermissionValidationService permissionValidationService, I18nResolver i18n, SoyTemplateRenderer soyRenderer, PageBuilderService pageBuilder, ActiveObjects activeObjects) {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.permissionValidationService.validateForGlobal(Permission.ADMIN);

        /*
        this.activeObjects.deleteWithSQL(CustomField.class, "1 = 1");
        this.activeObjects.create(CustomField.class, new DBParam("SEQ", 1), new DBParam("NAME", "First"), new DBParam("DESCRIPTION", ""), new DBParam("OPTIONS", "opt1\nopt2\nopt3")).getID();
        this.activeObjects.create(CustomField.class, new DBParam("SEQ", 2), new DBParam("NAME", "Second"), new DBParam("DESCRIPTION", ""), new DBParam("OPTIONS", null)).getID();
        this.activeObjects.create(CustomField.class, new DBParam("SEQ", 3), new DBParam("NAME", "Third"), new DBParam("DESCRIPTION", "Test description for the third custom field."), new DBParam("OPTIONS", null)).getID();
        */

        this.renderTemplate(resp, Optional.empty());
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO Audit logging
        this.permissionValidationService.validateForGlobal(Permission.ADMIN);

        boolean changed = this.activeObjects.executeInTransaction(() -> {
            boolean[] rtn = {false};

            // POST data: seq_#, name_#, options_#, description_# (where # is either the ID or something starting with 'new')
            // Also has the key 'delete' containing the ID of every deleted field

            // Delete
            Optional.ofNullable(req.getParameterValues("delete")).ifPresent(toDelete -> {
                for(String id : toDelete) {
                    rtn[0] = true;
                    activeObjects.deleteWithSQL(CustomField.class, "ID = ?", Integer.parseInt(id));
                }
            });

            // Update
            for(CustomField field : activeObjects.find(CustomField.class)) {
                final int id = field.getID();

                final int seq = Integer.parseInt(req.getParameter("seq_" + id));
                if(seq != field.getSeq()) {
                    rtn[0] = true;
                    field.setSeq(seq);
                }

                final String name = req.getParameter("name_" + id);
                if(!name.equals(field.getName())) {
                    rtn[0] = true;
                    field.setName(name);
                }

                //TODO Clear option for any projects that no longer match
                final String options = req.getParameter("options_" + id);
                if(!options.equals(field.getOptions())) {
                    rtn[0] = true;
                    field.setOptions(options);
                }

                final String description = req.getParameter("description_" + id);
                if(!description.equals(field.getDescription())) {
                    rtn[0] = true;
                    field.setDescription(description);
                }

                field.save();
            }

            // Add
            Optional.ofNullable(req.getParameterValues("add")).ifPresent(toAdd -> {
                for(String id : toAdd) {
                    rtn[0] = true;
                    activeObjects.create(
                            CustomField.class,
                            new DBParam("SEQ", Integer.parseInt(req.getParameter("seq_" + id))),
                            new DBParam("NAME", req.getParameter("name_" + id)),
                            new DBParam("OPTIONS", req.getParameter("options_" + id)),
                            new DBParam("DESCRIPTION", req.getParameter("description_" + id))
                    );
                }
            });

            return rtn[0];
        });

        final ResultMessage message = changed
                ? new ResultMessage("success", "Your changes have been saved.")
                : new ResultMessage("info", "No changes found.");
        this.renderTemplate(resp, Optional.of(message));
    }

    private void renderTemplate(HttpServletResponse resp, Optional<ResultMessage> resultMessage) throws IOException {
        this.pageBuilder.assembler().resources().requireContext("com.mrozekma.atlassian.bitbucket.projectFields.global-settings");
        resp.setContentType("text/html");

        final CustomField[] fields = activeObjects.find(CustomField.class, Query.select().order("SEQ ASC"));
        final Map<String, Object> data = new HashMap<String, Object>() {{
            put("fields", fields);
            put("field_usage", new HashMap<String, Integer>() {{
                for(CustomField field : fields) {
                    //TODO
                    //activeObjects.count(CustomField.class, Query.select().where(""))
                    put("" + field.getID(), field.getSeq() == 2 ? 0 : 3);
                }
            }});
            resultMessage.ifPresent(message -> put("message", message.toMap()));
        }};
        this.soyRenderer.render(resp.getWriter(), "com.mrozekma.atlassian.bitbucket.projectFields:project-fields-soy", "com.mrozekma.atlassian.bitbucket.pluginFields.global_settings", data);
    }
}
