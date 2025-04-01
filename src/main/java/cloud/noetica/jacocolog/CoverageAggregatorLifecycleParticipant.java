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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;

@Named
@Singleton
public class CoverageAggregatorLifecycleParticipant extends AbstractMavenLifecycleParticipant {

    private Map<String, JacocoCounters> reports = new HashMap<>();

    private Log log;

    private CountersLogger logger;

    /**
     * Whether the plugin should log overall coverage at the end
     */
    private boolean enable = false;

    /**
     * What projects to include in overall coverage report
     */
    private Set<String> includes;

    public void setLog(Log log) {
        this.log = log;
    }

    public void setLogger(CountersLogger logger) {
        this.logger = logger;
    }

    public void enable() {
        this.enable = true;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    /**
     * Record Maven project report for overall coverage logging
     * 
     * @param projectName - Maven project name
     * @param report      - project's computed coverage report
     */
    public void record(String projectName, JacocoCounters report) {
        this.reports.put(projectName, report);
    }

    @Override
    public void afterSessionEnd(MavenSession session) throws MavenExecutionException {
        if (!enable) {
            return;
        }

        this.log.info("--- Overall coverage ---");

        if (reports.size() <= 0) {
            this.log.info("No reports to aggregate");
            return;
        }

        final Set<String> projects;
        if (includes != null && !includes.isEmpty()) {
            projects = reports
                    .keySet()
                    .stream()
                    .filter(p -> includes.contains(p))
                    .collect(Collectors.toSet());
        } else {
            projects = reports.keySet();
        }

        // Check ignored projects
        includes
                .stream()
                .filter(name -> !projects.contains(name))
                .forEach(projectName -> this.log
                        .warn(
                                "Project \"" +
                                        projectName +
                                        "\" not found in current build session"));

        projects.stream().map(p -> reports.get(p)).reduce((JacocoCounters a, JacocoCounters b) -> a.merge(b))
                .ifPresent(report -> logger.log(report));
        reports.clear();
    }
}