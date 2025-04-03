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

import java.util.Arrays;
import java.util.EnumMap;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.internal.analysis.CounterImpl;

public class JacocoCounters {

    private EnumMap<CounterEntity, ICounter> counters = new EnumMap<CounterEntity, ICounter>(CounterEntity.class);

    private JacocoCounters() {
    }

    public static JacocoCounters of(IBundleCoverage bundle) {
        // Extract counters
        JacocoCounters counters = new JacocoCounters();
        counters.counters.put(CounterEntity.CLASS, bundle.getInstructionCounter());
        counters.counters.put(CounterEntity.METHOD, bundle.getMethodCounter());
        counters.counters.put(CounterEntity.BRANCH, bundle.getBranchCounter());
        counters.counters.put(CounterEntity.LINE, bundle.getLineCounter());
        counters.counters.put(
                CounterEntity.INSTRUCTION,
                bundle.getInstructionCounter());
        counters.counters.put(
                CounterEntity.COMPLEXITY,
                bundle.getComplexityCounter());
        return counters;
    }

    public EnumMap<CounterEntity, ICounter> getCounters() {
        return counters;
    }

    public JacocoCounters merge(JacocoCounters other) {
        Arrays.stream(CounterEntity.values())
                .forEach(e -> this.counters.merge(e, other.getCounters().get(e), (ICounter base, ICounter incoming) -> {
                    if (base == null) {
                        return incoming;
                    }
                    if (incoming == null) {
                        return base;
                    }

                    int mergedMissed = base.getMissedCount() + incoming.getMissedCount();
                    int mergedCovered = base.getCoveredCount() + incoming.getCoveredCount();

                    // Create a new CounterImpl instance with the summed values.
                    return CounterImpl.getInstance(mergedMissed, mergedCovered);
                }));
        return this;
    }
}
