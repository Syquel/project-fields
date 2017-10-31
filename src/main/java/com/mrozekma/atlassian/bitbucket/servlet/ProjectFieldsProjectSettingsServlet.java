package com.mrozekma.atlassian.bitbucket.servlet;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public ProjectFieldsProjectSettingsServlet(UserManager userManager, LoginUriProvider loginUriProvider, ProjectSupplier projectSupplier, PermissionValidationService permissionValidationService, I18nResolver i18n, SoyTemplateRenderer soyRenderer, PageBuilderService pageBuilder) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.projectSupplier = projectSupplier;
        this.permissionValidationService = permissionValidationService;
        this.i18n = i18n;
        this.soyRenderer = soyRenderer;
        this.pageBuilder = pageBuilder;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
//        for(Map.Entry<String, String> e : i18n.getAllTranslationsForPrefix("").entrySet()) {
//            if(e.getValue().contains("dead link"))
//            System.out.format("%s -> %s\n", e.getKey(), e.getValue());
//        }

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

        this.pageBuilder.assembler().resources().requireContext("com.mrozekma.atlassian.bitbucket.projectFields.project-settings");
        resp.setContentType("text/html");
//        resp.getWriter().write(String.format("<html><body>Hello World -- %s</body></html>", this.userManager.getRemoteUser()));

        final Map<String, Object> data = new HashMap<String, Object>() {{
            put("project", project);
        }};
        this.soyRenderer.render(resp.getWriter(), "com.mrozekma.atlassian.bitbucket.projectFields:project-fields-soy", "com.mrozekma.atlassian.bitbucket.pluginFields.project_settings", data);
    }
}
