package com.mrozekma.atlassian.bitbucket.projectFields.impl;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;

import java.util.Optional;

public class License {
    public static Optional<String> getErrorKey(PluginLicenseManager pluginLicenseManager) {
        final Option<PluginLicense> license = pluginLicenseManager.getLicense();
        if(license.isDefined()) {
            final Option<LicenseError> err = license.get().getError();
            if(err.isDefined()) {
                switch(err.get()) {
                case EXPIRED:
                    return Optional.of("upm.plugin.license.status.expired");
                case TYPE_MISMATCH:
                    return Optional.of("upm.plugin.license.status.type_mismatch");
                case USER_MISMATCH:
                    return Optional.of("upm.plugin.license.status.user_mismatch");
                case EDITION_MISMATCH:
                    return Optional.of("upm.plugin.license.status.edition_mismatch.users");
                case ROLE_EXCEEDED:
                    // This takes some arguments I don't know how to fill in
//                    return Optional.of("upm.plugin.license.status.role_exceeded");
                    return Optional.of("Role exceeded");
                case ROLE_UNDEFINED:
                    return Optional.of("upm.plugin.license.status.role_undefined");
                case VERSION_MISMATCH:
                    return Optional.of("upm.plugin.license.status.version_mismatch");
                default:
                    return Optional.of("upm.plugin.license.status.unknown");
                }
            } else if(!license.get().isValid()) {
                return Optional.of("upm.plugin.license.info.text.unlicensed");
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.of("upm.plugin.license.info.text.unlicensed");
        }
    }

    public static Optional<String> getError(PluginLicenseManager pluginLicenseManager, I18nResolver i18n) {
        return getErrorKey(pluginLicenseManager).map(i18n::getText);
    }

    public static Optional<String> getErrorBox(PluginLicenseManager pluginLicenseManager, I18nResolver i18n) {
        return getError(pluginLicenseManager, i18n).map(err ->
                "<div class=\"aui-message aui-message-error\">\n" +
                "    <p class=\"title\">License error: " + err + "</p>\n" +
                "    Fields will not show up in the project list or project settings.\n" +
                "</div>");
    }

    public static boolean isValid(PluginLicenseManager pluginLicenseManager) {
        return !getErrorKey(pluginLicenseManager).isPresent();
    }
}
