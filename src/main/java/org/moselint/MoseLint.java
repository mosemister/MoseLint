package org.moselint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moselint.check.Checker;
import org.moselint.check.Checkers;
import org.moselint.exception.CheckException;
import org.moselint.exception.CheckExceptionContext;
import org.moselint.utils.NumberEntryCollector;
import org.openblock.creator.code.Codeable;
import org.openblock.creator.code.clazz.IClass;
import org.openblock.creator.code.function.IFunction;
import org.openblock.creator.code.statement.Statement;
import org.openblock.creator.code.variable.field.Field;
import org.openblock.creator.impl.custom.clazz.AbstractCustomClass;
import org.openblock.creator.impl.custom.clazz.reader.CustomClassReader;
import org.openblock.creator.project.Project;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MoseLint {

    private final @Nullable
    String projectRoot;

    private static MoseLint main;

    private MoseLint(@Nullable String projectRoot) {
        this.projectRoot = projectRoot;
    }

    public Set<Checker> getCheckers() {
        System.out.println("Checkers: ");
        return Arrays
                .stream(Checkers.class.getDeclaredFields())
                .parallel()
                .filter(field -> {
                    System.out.println("Field: " + field.getName());
                    return Modifier.isStatic(field.getModifiers());
                })
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

    public Map.Entry<Integer, Integer> runChecks() throws IOException {
        Set<Map.Entry<Path, CustomClassReader>> files = Files
                .walk(this.getClassRoot().toPath())
                .filter(path -> path.toFile().isFile())
                .map(path -> {
                    try {
                        return new AbstractMap.SimpleImmutableEntry<>(path, Files.lines(path).collect(Collectors.joining("\n")));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new AbstractMap.SimpleImmutableEntry<>(path, "");
                    }
                })
                .filter(entry -> !entry.getValue().isBlank())
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), new CustomClassReader(entry.getValue())))
                .collect(Collectors.toSet());
        System.out.println("Found " + files.size() + " files");
        Project<AbstractCustomClass> project = new Project<>("Temp");
        Map<CustomClassReader, AbstractCustomClass> map = new HashMap<>();
        for (Map.Entry<Path, CustomClassReader> entry : files) {
            try {
                CustomClassReader reader = entry.getValue();
                AbstractCustomClass clazz = reader.readStageOne(project);
                project.register(clazz);
                map.put(reader, clazz);
            } catch (Exception e) {
                writeReadErrorReport(entry.getKey().toFile(), "Class basic read error", e.getMessage());
                e.printStackTrace();
            }
        }

        map.forEach((reader, clazz) -> reader.readStageTwo(project, clazz));
        map.forEach((reader, clazz) -> reader.readStageThree(project, clazz));

        return project
                .getClasses()
                .stream()
                .map(clazz -> {
                    Set<Checker> checkers = MoseLint.getInstance().getCheckers();
                    @NotNull List<Field> fields = clazz.getFields();
                    @NotNull Set<IFunction> functions = clazz.getFunctions();

                    Map.Entry<Integer, Integer> mapFields = fields.stream().map(field -> check(clazz, field, checkers)).collect(new NumberEntryCollector<>(Number::intValue, Number::intValue));
                    Map.Entry<Integer, Integer> mapFunctions = functions.stream().map(field -> check(clazz, field, checkers)).collect(new NumberEntryCollector<>(Number::intValue, Number::intValue));
                    int checks = mapFields.getKey() + mapFunctions.getKey();
                    int fails = mapFunctions.getKey() + mapFunctions.getValue();

                    return new AbstractMap.SimpleImmutableEntry<>(checks, fails);
                })
                .collect(new NumberEntryCollector<>(Number::intValue, Number::intValue));
    }

    private Map.Entry<Integer, Integer> check(IClass clazz, Codeable codeable, Collection<Checker> checkers) {
        return checkers.stream().filter(c -> c.canCheck(codeable)).map(c -> {
            int failed = 0;
            int checks = 1;
            try {
                c.isValid(codeable);
            } catch (CheckException e) {
                writeErrorReport(clazz.getFullName(), codeable, c, e.getContext());
                failed = 1;
            }
            if (codeable instanceof Statement statement) {
                Map.Entry<Integer, Integer> result = statement
                        .getCodeBlock()
                        .stream()
                        .map(cod -> check(clazz, cod, checkers))
                        .collect(new NumberEntryCollector<>(Number::intValue, Number::intValue));
                checks = checks + result.getKey();
                failed = failed + result.getValue();
            }
            return new AbstractMap.SimpleImmutableEntry<>(checks, failed);
        }).collect(new NumberEntryCollector<>(Number::intValue, Number::intValue));
    }

    private void writeReadErrorReport(@NotNull File file, @NotNull String message, @NotNull String error) {
        InputStream inputStream = getClass().getResourceAsStream("/template.html");
        if (inputStream == null) {
            throw new RuntimeException("Could not find template.html");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String htmlTemplateFile = br.lines().collect(Collectors.joining("\n"));
        htmlTemplateFile = htmlTemplateFile.replaceAll("template@title", message);
        htmlTemplateFile = htmlTemplateFile.replaceAll("template@message", error);

        htmlTemplateFile = htmlTemplateFile.replaceAll("template@suggestions", "");
        htmlTemplateFile = htmlTemplateFile.replaceAll("template@errors", "");
        System.out.println("|--------------------------");
        System.out.println("At: " + file.toPath());
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvv");
        System.out.println(htmlTemplateFile);

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(htmlTemplateFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeErrorReport(@NotNull String name, @NotNull Codeable codeable, @NotNull Checker checker, CheckExceptionContext... context) {
        for (CheckExceptionContext eContext : context) {
            InputStream inputStream = getClass().getResourceAsStream("template.html");
            if (inputStream == null) {
                throw new RuntimeException("Could not find template.html");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String htmlTemplateFile = br.lines().collect(Collectors.joining("\n"));
            htmlTemplateFile = htmlTemplateFile.replaceAll("template@title", checker.getDisplayName());
            htmlTemplateFile = htmlTemplateFile.replaceAll("template@message", eContext.getMessage());

            String suggestions = eContext.getSuggestions().stream().map(suggestionCode -> {
                String code = suggestionCode.writeCode(1);
                return "<pre class=\"suggestion\"><code>" + code + "</code></pre>";
            }).collect(Collectors.joining("<br>"));

            List<Codeable> errorsCode = eContext.getErrors();
            if (errorsCode.isEmpty()) {
                errorsCode = Collections.singletonList(codeable);
            }
            String errors = errorsCode.stream().map(suggestionCode -> {
                String code = suggestionCode.writeCode(1);
                return "<pre class=\"error\"><code>" + code + "</code></pre>";
            }).collect(Collectors.joining("<br>"));

            htmlTemplateFile = htmlTemplateFile.replaceAll("template@suggestions", suggestions);
            htmlTemplateFile = htmlTemplateFile.replaceAll("template@errors", errors);

            System.out.println("|--------------------------");
            System.out.println("At: " + name);
            System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvv");
            System.out.println(htmlTemplateFile);

            File file = new File("MoseLint/result/" + name.replaceAll("\\.", "/") + ".html");
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(htmlTemplateFile);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

        }
    }


    public static MoseLint getInstance() {
        return main;
    }

    public static void main(String[] args) {
        String projectRoot = null;
        for (String arg : args) {
            if (arg.toLowerCase().startsWith("projectroot-")) {
                projectRoot = arg.toLowerCase().substring(12);
            }
        }


        main = new MoseLint(projectRoot);
        try {
            Map.Entry<Integer, Integer> failedChecks = main.runChecks();
            System.out.println(failedChecks.getKey() + " checks ran");
            if (failedChecks.getValue() == 0) {
                System.out.println("All checks passed with no suggestions");
            } else {
                System.err.println(failedChecks + " checks failed, reported in 'MoseLint/results/'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
