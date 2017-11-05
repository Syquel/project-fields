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
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomField;
import com.mrozekma.atlassian.bitbucket.projectFields.ao.CustomFieldValue;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ProjectFieldsJavascriptServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(ProjectFieldsJavascriptServlet.class);

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

    public ProjectFieldsJavascriptServlet(UserManager userManager, LoginUriProvider loginUriProvider, ProjectSupplier projectSupplier, PermissionValidationService permissionValidationService, I18nResolver i18n, SoyTemplateRenderer soyRenderer, PageBuilderService pageBuilder, ActiveObjects activeObjects) {
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
        resp.setContentType("text/javascript");

        final Map<String, Object> data = new HashMap<String, Object>() {{
        }};
//        this.soyRenderer.render(resp.getWriter(), "com.mrozekma.atlassian.bitbucket.projectFields:project-fields-soy", "com.mrozekma.atlassian.bitbucket.pluginFields.project_settings", data);
        resp.getWriter().println("alert('test');");
    }
}
