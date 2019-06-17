package com.friday.ar.plugin.file;


import com.friday.ar.plugin.Plugin;

import org.jetbrains.annotations.NotNull;

public class Manifest {
    private String pluginName;
    private String author;
    private String version;

    public Manifest(@NotNull String pluginName, String author, @NotNull String version) {
        this.pluginName = pluginName;
        this.author = author;
        this.version = version;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Plugin toPlugin() {
        Plugin returnPlugin = new Plugin();
        returnPlugin.setName(pluginName);
        returnPlugin.setAuthorName(author);
        returnPlugin.setVersionName(version);
        return returnPlugin;
    }

    /**
     * Represents an exception thrown when a plugins' manifest is invalid causing security issues
     *
     * @see Manifest
     **/
    public static class ManifestSecurityException extends Exception {
        public ManifestSecurityException(String message) {
            super("The given manifest is not valid:" + message);
        }

        /**
         * This exception indicates that an important field is missing in the manifest and the plugin could not be loaded.
         */
        public static class MissingFieldException extends ManifestSecurityException {
            public MissingFieldException(String message) {
                super("A field is missing:" + message);
            }

            /**
             * @param context Context of the missing field, for example the containing fields' name.
             * @param message Details about the missing field.
             */
            public MissingFieldException(String context, String message) {
                super("Occurred in context '" + context + "'; A field is missing:" + message);
            }
        }
    }
}
