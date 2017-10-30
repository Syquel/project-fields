package com.mrozekma.atlassian.bitbucket.servlet;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectSupplier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

import com.atlassian.sal.api.user.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ProjectFieldsServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(ProjectFieldsServlet.class);

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

    public ProjectFieldsServlet(UserManager userManager, LoginUriProvider loginUriProvider, ProjectSupplier projectSupplier, PermissionValidationService permissionValidationService, I18nResolver i18n) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.projectSupplier = projectSupplier;
        this.permissionValidationService = permissionValidationService;
        this.i18n = i18n;
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

        resp.setContentType("text/html");
        resp.getWriter().write(String.format("<html><body>Hello World -- %s</body></html>", this.userManager.getRemoteUser()));
    }

}

/*
[INFO] bitbucket.web.project.id.notfound -> No project with ID ''{0}'' exists.
[INFO] bitbucket.web.project.tabs.repositories -> Repositories
[INFO] bitbucket.web.project.settings.tab.addons.section -> Add-Ons
[INFO] bitbucket.web.project.personal.permissions.not.modifiable -> You cannot modify the permissions of a personal project.
[INFO] bitbucket.web.project.deleted -> The project {0} has been deleted.
[INFO] bitbucket.web.project.settings.tab.workflow.section -> Workflow
[INFO] bitbucket.web.project.audit.heading -> Audit log
[INFO] bitbucket.web.project.settings.tab.hooks.window.title -> Hooks for {0}
[INFO] bitbucket.web.project.selector.no.results -> No projects
[INFO] bitbucket.web.project.help.development.desc -> Customize {0} via the API and plugin framework.
[INFO] bitbucket.web.project.settings.tab.hooks -> Hooks
[INFO] bitbucket.web.project.settings.mergestrategy.description.html -> Merge strategies affect the way the commit history appears after a merge has occurred. Choose which merge strategies to allow for pull requests for all repositories in this project, and set a default merge strategy. <a href="{0}">Learn more about pull request merge strategies</a>.
[INFO] bitbucket.web.project.delete.unable.detail -> All repositories must be removed from this project before it can be deleted.
[INFO] bitbucket.web.project.help.workflows.desc -> Learn about Git workflows for enterprise teams.
[INFO] bitbucket.web.project.edit.personalunmodifiable -> Settings for personal projects cannot be edited.
[INFO] bitbucket.web.project.audit.desc -> Shows the most important audit events affecting this project (up to a maximum of the {0} most recent events)
[INFO] bitbucket.web.project.permission.licensedusers -> Default permission
[INFO] bitbucket.web.project.overview.window.title -> {0} project overview
[INFO] bitbucket.web.project.settings.tab.hooks.tooltip -> Configure hooks for this project
[INFO] bitbucket.web.project.key.help.text -> Eg. AT (for a project named Atlassian)
[INFO] bitbucket.web.project.help.getting.started.desc -> All the information you need to get started in {0}.
[INFO] bitbucket.web.project.delete.unable -> The project {0}{1}{2} cannot be deleted.
[INFO] bitbucket.web.project.key.description -> Changing this project''s key will change the clone URL of all contained repositories. You can update each repository''s remote with the following command: git remote set-url REMOTE_NAME NEW_URL
[INFO] bitbucket.web.project.help.administration.desc -> Configure {0} for your enterprise teams.
[INFO] bitbucket.web.project.create.accessdenied -> You do not have permission to create a project
[INFO] bitbucket.web.project.selector.no.more.results -> No more projects
[INFO] bitbucket.web.project.delete.confirm -> Are you sure that you want to delete {0}{1}{2}?
[INFO] bitbucket.web.project.help.development.title -> API
[INFO] bitbucket.web.project.help.administration.title -> Administering {0}
[INFO] bitbucket.web.project.settings.tab.settings -> Project details
[INFO] bitbucket.web.project.settings.mergestrategy.window.title -> Merge strategies for {0}
[INFO] bitbucket.web.project.key.html -> Project key
[INFO] bitbucket.web.project.audit.title -> Audit Log for {0}
[INFO] bitbucket.web.project.settings.tab.audit.tooltip -> See any recently persisted audit events for this project
[INFO] bitbucket.web.project.help.getting.started.title -> Getting started
[INFO] bitbucket.web.project.permission.group.noresults -> No groups have been given explicit permissions to this project
[INFO] bitbucket.web.project.heading -> Projects
[INFO] bitbucket.web.project.delete.confirm.detail -> Deleting cannot be undone and this project will be permanently removed.
[INFO] bitbucket.web.project.name.html -> Project name
[INFO] bitbucket.web.project.edit.permissions -> Project permissions
[INFO] bitbucket.web.project.permission.licensedusers.read -> Read
[INFO] bitbucket.web.project.settings.tab.merge.checks -> Merge checks
[INFO] bitbucket.web.project.permission.licensedusers.desc -> Specify the default level of access to this project for logged-in users. Override this for specific user and group access below.
[INFO] bitbucket.web.project.selector.default -> Select project
[INFO] bitbucket.web.project.list.createproject.link -> Create project
[INFO] bitbucket.web.project.settings.window.title -> {0} project details
[INFO] bitbucket.web.project.edit.accessdenied -> You do not have permission to edit the {0} project
[INFO] bitbucket.web.project.permission.licensedusers.noaccess -> No access
[INFO] bitbucket.web.project.avatar.picker.title -> Upload a project avatar
[INFO] bitbucket.web.project.list.window.title -> Projects
[INFO] bitbucket.web.project.settings.tab.permissions.tooltip -> Configure project-level permissions for this repository
[INFO] bitbucket.web.project.settings.tab.permissions.section -> Security
[INFO] bitbucket.web.project.col.key -> Key
[INFO] bitbucket.web.project.create.title -> Create project
[INFO] bitbucket.web.project.delete -> Delete project
[INFO] bitbucket.web.project.permission.licensedusers.noaccess.disabledreason -> Cannot prevent access to this project as public access is enabled
[INFO] bitbucket.web.project.settings.tab.pullrequest.section -> Pull requests
[INFO] bitbucket.web.project.permissions.window.title -> {0} project permissions
[INFO] bitbucket.web.project.nodescription -> No description
[INFO] bitbucket.web.project.settings.tab.merge.checks.tooltip -> Configure merge checks for this project
[INFO] bitbucket.web.project.key.notfound -> No project with key ''{0}'' exists.
[INFO] bitbucket.web.project.description.html -> Description
[INFO] bitbucket.web.project.settings.tab.mergestrategy.tooltip -> Configure default merge strategies for this project
[INFO] bitbucket.web.project.permission.public.desc -> Allow users without a {0} account to clone and browse repositories within this project.
[INFO] bitbucket.web.project.col.description -> Description
[INFO] bitbucket.web.project.settings.tab.mergestrategy -> Merge strategies
[INFO] bitbucket.web.project.settings.tab.merge.checks.window.title -> Merge checks for {0}
[INFO] bitbucket.web.project.col.project -> Project
[INFO] bitbucket.web.project.settings.tab.permissions -> Project permissions
[INFO] bitbucket.web.project.edit.settings -> Project details
[INFO] bitbucket.web.project.overview.createrepo.link -> Create repository
[INFO] bitbucket.web.project.create.project -> Create a project
[INFO] bitbucket.web.project.tabs.settings -> Project settings
[INFO] bitbucket.web.project.permission.public.allow -> Enable
[INFO] bitbucket.web.project.settings.tab.branch.permissions.tooltip -> Configure branch-level permissions for this project
[INFO] bitbucket.web.project.tab.audit -> Audit log
[INFO] bitbucket.web.project.help.workflows.title -> Branching and forking
[INFO] bitbucket.web.project.permission.licensedusers.readwrite -> Write
[INFO] bitbucket.web.project.permission.user.noresults -> No users have been given explicit permissions to this project
[INFO] bitbucket.web.project.settings.tab.settings.tooltip -> Configure the details for this project
[INFO] bitbucket.web.project.permissions.learn.more -> Learn more
[INFO] bitbucket.web.project.permission.public -> Public access
 */