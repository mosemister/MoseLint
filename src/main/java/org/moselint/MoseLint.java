package org.moselint;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;

public class MoseLint {

    private final @Nullable
    String projectRoot;

    private static MoseLint main;

    private MoseLint(@Nullable String projectRoot) {
        this.projectRoot = projectRoot;
    }

    public File getClassRoot() {
        File root = new File(getProjectRoot(), "src");
        File mavenRoot = new File(root, "main/java");
        if (mavenRoot.exists()) {
            return mavenRoot;
        }
        return root;

    }

    public File getProjectRoot() {
        return new File(getProjectRootPath());
    }

    public String getProjectRootPath() {
        if (this.projectRoot != null) {
            return this.projectRoot;
        }
        try {
            return new File(MoseLint.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public static MoseLint getInstance() {
        return main;
    }

    public static void main(String[] args) {

    }
}
