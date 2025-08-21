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

import io.trino.testing.TestingNodeManager;
import org.junit.jupiter.api.Test;

import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.CONSISTENT_HASH;
import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.DEFAULT;
import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.REPLICATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestCachingHostAddressProviderFactory
{
    private final TestingNodeManager nodeManager = new TestingNodeManager();

    @Test
    public void testCreateDefaultProvider()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig()
                .setProviderType(DEFAULT);
        CachingHostAddressProviderFactory factory = new CachingHostAddressProviderFactory(nodeManager, config);

        CachingHostAddressProvider provider = factory.get();
        assertThat(provider).isInstanceOf(DefaultCachingHostAddressProvider.class);
    }

    @Test
    public void testCreateConsistentHashProvider()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig()
                .setProviderType(CONSISTENT_HASH)
                .setPreferredHostsCount(5);
        CachingHostAddressProviderFactory factory = new CachingHostAddressProviderFactory(nodeManager, config);

        CachingHostAddressProvider provider = factory.get();
        assertThat(provider).isInstanceOf(ConsistentHashingHostAddressProvider.class);
    }

    @Test
    public void testCreateReplicatedProvider()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig()
                .setProviderType(REPLICATED);
        CachingHostAddressProviderFactory factory = new CachingHostAddressProviderFactory(nodeManager, config);

        CachingHostAddressProvider provider = factory.get();
        assertThat(provider).isInstanceOf(ReplicatedHostAddressProvider.class);
    }

    @Test
    public void testNullNodeManagerThrows()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig();

        assertThatThrownBy(() -> new CachingHostAddressProviderFactory(null, config))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("nodeManager is null");
    }

    @Test
    public void testNullConfigThrows()
    {
        assertThatThrownBy(() -> new CachingHostAddressProviderFactory(nodeManager, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("config is null");
    }

    @Test
    public void testFactoryCreatesNewInstancesEachTime()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig()
                .setProviderType(DEFAULT);
        CachingHostAddressProviderFactory factory = new CachingHostAddressProviderFactory(nodeManager, config);

        CachingHostAddressProvider provider1 = factory.get();
        CachingHostAddressProvider provider2 = factory.get();

        assertThat(provider1).isNotSameAs(provider2);
        assertThat(provider1).isInstanceOf(DefaultCachingHostAddressProvider.class);
        assertThat(provider2).isInstanceOf(DefaultCachingHostAddressProvider.class);
    }
}
