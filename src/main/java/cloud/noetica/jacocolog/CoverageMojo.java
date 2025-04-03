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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

/**
 * Goal which reads the JaCoCo report and logs coverage.
 */
@Mojo(name = "coverage", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true, aggregator = true)
public class CoverageMojo extends AbstractMojo {

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  /**
   * Whether the Mojo should
   */
  @Parameter(property = "overall")
  private OverallCoverageConfig overall;

  /**
   * Whether the plugin should
   */
  @Parameter(property = "includes", defaultValue = "**\\jacoco.exec")
  private String includes;

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
  private CoverageAggregatorLifecycleParticipant hook;

  @Override
  public void execute() throws MojoExecutionException {
    CountersLogger logger = new CountersLogger(
        getLog(),
        digits,
        new LinkedHashSet<>(counters));

    if (project.isExecutionRoot() && this.overall.isEnable()) {
      hook.enable();
      hook.setLog(getLog());
      hook.setIncludes(this.overall.getIncludes());
      hook.setLogger(logger);
    }

    CountersExtractor extractor = new CountersExtractor(getLog(), includes);
    JacocoCounters report = extractor.extract(project);
    if (report != null) {
      hook.record(project.getName(), report);
      logger.log(report);
    }
  }

  public static class OverallCoverageConfig {
    private boolean enable;
    private Set<String> includes = new LinkedHashSet<>();

    public OverallCoverageConfig() {
    }

    public boolean isEnable() {
      return enable;
    }

    public void setEnable(boolean enable) {
      this.enable = enable;
    }

    public Set<String> getIncludes() {
      return includes;
    }

    public void setIncludes(Set<String> includes) {
      this.includes = includes;
    }
  }
}
