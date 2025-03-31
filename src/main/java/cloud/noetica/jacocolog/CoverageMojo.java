/*
 *   Copyright (c) 2025 The maven-jacoco-log contributors
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package cloud.noetica.jacocolog;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import java.util.List;
import javax.inject.Inject;

/**
 * Goal which reads the JaCoCo report and logs coverage.
 */
@Mojo(name = "coverage", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, aggregator = true)
public class CoverageMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    /**
     * Coverage counters to log in Maven Build logs
     */
    @Parameter(property = "counters", defaultValue = "CLASS,METHOD,BRANCH,LINE,INSTRUCTION,COMPLEXITY")
    private List<CounterEntity> counters;

    /**
     * Coverage counters to log in Maven Build logs
     */
    @Parameter(property = "digits", defaultValue = "2")
    private int digits;

    @Inject
    CountersExtractor extractor;

    @Inject
    CountersLogger logger;

    @Override
    public void execute() throws MojoExecutionException {
        logger.setDigits(digits);
        logger.setCounters(counters.toArray(new CounterEntity[counters.size()]));
        JacocoCounters report = extractor.extract(project);
        if (report != null) {
            logger.log(report);
        }
    }
}
