package org.moselint;

import org.jetbrains.annotations.Nullable;
import org.moselint.check.Checker;
import org.moselint.check.Checkers;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MoseLint {

    private final @Nullable
    String projectRoot;

    private static MoseLint main;

    private MoseLint(@Nullable String projectRoot) {
        this.projectRoot = projectRoot;
    }

    public Set<Checker> getCheckers() {
        return Arrays
                .stream(Checkers.class.getDeclaredFields())
                .parallel()
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> field.getType().isAssignableFrom(Checker.class))
                .map(field -> {
                    try {
                        return (Checker) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toSet());
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

    public void runChecks() {

    }


    public static MoseLint getInstance() {
        return main;
    }

    public static void main(String[] args) {
        main = new MoseLint(null);
        main.runChecks();
    }
}
