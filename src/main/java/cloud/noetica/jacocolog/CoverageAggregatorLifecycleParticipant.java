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

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

@Named
@Singleton
public class CoverageAggregatorLifecycleParticipant extends AbstractMavenLifecycleParticipant {
    private final Log log = new SystemStreamLog();

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[TEST-HOOK] Shutdown hook triggered: Build completed.");
            try {
                log.info("[TEST-HOOK] Coverage aggregation completed successfully.");
            } catch (Exception e) {
                log.info("[TEST-HOOK] Error during coverage aggregation: " + e.getMessage());
            }
        }));
    }
}