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

import com.google.common.collect.ImmutableList;
import io.trino.client.NodeVersion;
import io.trino.metadata.InternalNode;
import io.trino.spi.HostAddress;
import io.trino.spi.Node;
import io.trino.testing.TestingNodeManager;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestReplicatedHostAddressProvider
{
    @Test
    public void testReturnsAllWorkerNodes()
    {
        TestingNodeManager nodeManager = new TestingNodeManager(false);
        nodeManager.addNode(node("worker-1"));
        nodeManager.addNode(node("worker-2"));
        nodeManager.addNode(node("worker-3"));

        ReplicatedHostAddressProvider provider = new ReplicatedHostAddressProvider(nodeManager);

        List<HostAddress> hosts = provider.getHosts("test-path", ImmutableList.of());

        assertThat(hosts).hasSize(3);
        assertThat(hosts.stream().map(HostAddress::getHostText))
                .containsExactlyInAnyOrder("worker-1", "worker-2", "worker-3");
    }

    @Test
    public void testReturnsDefaultAddressesWhenNoWorkers()
    {
        TestingNodeManager nodeManager = new TestingNodeManager(false);
        ReplicatedHostAddressProvider provider = new ReplicatedHostAddressProvider(nodeManager);

        List<HostAddress> defaultAddresses = ImmutableList.of(
                HostAddress.fromString("default-1:8080"),
                HostAddress.fromString("default-2:8080"));

        List<HostAddress> hosts = provider.getHosts("test-path", defaultAddresses);

        assertThat(hosts).isEqualTo(defaultAddresses);
    }

    @Test
    public void testConsistentResultsForSamePath()
    {
        TestingNodeManager nodeManager = new TestingNodeManager(false);
        nodeManager.addNode(node("worker-1"));
        nodeManager.addNode(node("worker-2"));

        ReplicatedHostAddressProvider provider = new ReplicatedHostAddressProvider(nodeManager);

        List<HostAddress> hosts1 = provider.getHosts("test-path", ImmutableList.of());
        List<HostAddress> hosts2 = provider.getHosts("test-path", ImmutableList.of());

        assertThat(hosts1).isEqualTo(hosts2);
    }

    @Test
    public void testDifferentPathsSameResult()
    {
        TestingNodeManager nodeManager = new TestingNodeManager(false);
        nodeManager.addNode(node("worker-1"));
        nodeManager.addNode(node("worker-2"));

        ReplicatedHostAddressProvider provider = new ReplicatedHostAddressProvider(nodeManager);

        List<HostAddress> hosts1 = provider.getHosts("path-1", ImmutableList.of());
        List<HostAddress> hosts2 = provider.getHosts("path-2", ImmutableList.of());

        assertThat(hosts1).isEqualTo(hosts2);
    }

    private static Node node(String nodeName)
    {
        return new InternalNode(nodeName, URI.create("http://" + nodeName + "/"), NodeVersion.UNKNOWN, false);
    }
}
