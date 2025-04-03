package cloud.noetica.jacocolog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.tools.ExecFileLoader;

public class CountersExtractor {

    private final Log log;
    private final String includes;

    public CountersExtractor(Log log, String includes) {
        this.log = log;
        this.includes = includes;
    }

    JacocoCounters extract(MavenProject project) {
        if (!Files.exists(Paths.get(project.getBuild().getDirectory()))) {
            log.debug("Project is not yet built");
            return null;
        }
        DirectoryScanner ds = new DirectoryScanner();
        String[] includes = { this.includes };
        ds.setIncludes(includes);
        ds.setBasedir(project.getBuild().getDirectory());
        ds.scan();
        String[] files = ds.getIncludedFiles();
        log.debug("Found Jacoco files:");
        for (int i = 0; i < files.length; i++) {
            log.debug("- " + files[i]);
        }

        if (files.length == 0) {
            return null;
        }
        ExecutionDataStore store = extract(
                Arrays.stream(files)
                        .map(f -> Paths.get(project.getBuild().getDirectory(), f).toFile())
                        .toArray(File[]::new));

        return JacocoCounters.of(
                buildCounters(new File(project.getBuild().getOutputDirectory()), store));
    }

    private ExecutionDataStore extract(File... files) {
        ExecutionDataStore store = new ExecutionDataStore();
        for (File file : files) {
            ExecFileLoader loader = new ExecFileLoader();
            try {
                loader.load(file);
                loader.getExecutionDataStore().getContents().forEach(store::put);
            } catch (IOException e) {
                log.debug("An exception occured while reading file : " + file, e);
            }
        }
        return store;
    }

    private IBundleCoverage buildCounters(
            File classDirectory,
            ExecutionDataStore data) {
        // Create a CoverageBuilder and an Analyzer.
        CoverageBuilder coverageBuilder = new CoverageBuilder();
        if (classDirectory.isDirectory()) {
            final Analyzer analyzer = new Analyzer(data, coverageBuilder);
            try {
                analyzer.analyzeAll(classDirectory);
            } catch (IOException e) {
                log.debug(
                        "An exception occured while analyzing files : " + e.getMessage());
            }
        }

        // Retrieve bundle coverage (which aggregates coverage from all classes).
        return coverageBuilder.getBundle("Bundle");
    }
}
