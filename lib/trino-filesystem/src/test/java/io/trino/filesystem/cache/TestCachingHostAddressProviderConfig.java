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

import org.junit.jupiter.api.Test;

import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.CONSISTENT_HASH;
import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.DEFAULT;
import static io.trino.filesystem.cache.CachingHostAddressProviderConfig.ProviderType.REPLICATED;
import static org.assertj.core.api.Assertions.assertThat;

public class TestCachingHostAddressProviderConfig
{
    @Test
    public void testDefaults()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig();

        assertThat(config.getProviderType()).isEqualTo(CONSISTENT_HASH);
        assertThat(config.getPreferredHostsCount()).isEqualTo(2);
    }

    @Test
    public void testSetProviderType()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig();

        config.setProviderType(CONSISTENT_HASH);
        assertThat(config.getProviderType()).isEqualTo(CONSISTENT_HASH);

        config.setProviderType(REPLICATED);
        assertThat(config.getProviderType()).isEqualTo(REPLICATED);

        config.setProviderType(DEFAULT);
        assertThat(config.getProviderType()).isEqualTo(DEFAULT);
    }

    @Test
    public void testSetPreferredHostsCount()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig();

        config.setPreferredHostsCount(5);
        assertThat(config.getPreferredHostsCount()).isEqualTo(5);

        config.setPreferredHostsCount(1);
        assertThat(config.getPreferredHostsCount()).isEqualTo(1);

        config.setPreferredHostsCount(10);
        assertThat(config.getPreferredHostsCount()).isEqualTo(10);
    }

    @Test
    public void testFluentInterface()
    {
        CachingHostAddressProviderConfig config = new CachingHostAddressProviderConfig()
                .setProviderType(CONSISTENT_HASH)
                .setPreferredHostsCount(7);

        assertThat(config.getProviderType()).isEqualTo(CONSISTENT_HASH);
        assertThat(config.getPreferredHostsCount()).isEqualTo(7);
    }
}
