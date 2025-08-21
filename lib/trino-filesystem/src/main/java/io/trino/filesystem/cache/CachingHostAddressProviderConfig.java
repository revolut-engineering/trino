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

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

public class CachingHostAddressProviderConfig
{
    public enum ProviderType
    {
        DEFAULT,
        CONSISTENT_HASH,
        REPLICATED
    }

    private ProviderType providerType = ProviderType.CONSISTENT_HASH;
    private int preferredHostsCount = 2;

    @Config("fs.cache.host-address-provider")
    @ConfigDescription("Type of caching host address provider (DEFAULT, CONSISTENT_HASH, REPLICATED)")
    public CachingHostAddressProviderConfig setProviderType(ProviderType providerType)
    {
        this.providerType = providerType;
        return this;
    }

    public ProviderType getProviderType()
    {
        return this.providerType;
    }

    @Config("fs.cache.preferred-hosts-count")
    @ConfigDescription("Number of preferred hosts for consistent hashing")
    public CachingHostAddressProviderConfig setPreferredHostsCount(int preferredHostsCount)
    {
        this.preferredHostsCount = preferredHostsCount;
        return this;
    }

    public int getPreferredHostsCount()
    {
        return this.preferredHostsCount;
    }
}
