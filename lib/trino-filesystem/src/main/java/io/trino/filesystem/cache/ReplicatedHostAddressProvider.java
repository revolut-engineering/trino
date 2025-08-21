/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.filesystem.cache;

import com.google.inject.Inject;
import io.trino.spi.HostAddress;
import io.trino.spi.Node;
import io.trino.spi.NodeManager;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

public class ReplicatedHostAddressProvider
        implements CachingHostAddressProvider
{
    private final NodeManager nodeManager;

    @Inject
    public ReplicatedHostAddressProvider(NodeManager nodeManager)
    {
        this.nodeManager = requireNonNull(nodeManager, "nodeManager is null");
    }

    @Override
    public List<HostAddress> getHosts(String splitPath, List<HostAddress> defaultAddresses)
    {
        List<HostAddress> workerAddresses = nodeManager.getWorkerNodes().stream()
                .map(Node::getHostAndPort)
                .collect(toImmutableList());

        if (workerAddresses.isEmpty()) {
            return defaultAddresses;
        }

        return workerAddresses;
    }
}
