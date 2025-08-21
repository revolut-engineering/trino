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
import com.google.inject.Provider;
import io.trino.spi.NodeManager;

import static java.util.Objects.requireNonNull;

public class CachingHostAddressProviderFactory
        implements Provider<CachingHostAddressProvider>
{
    private final NodeManager nodeManager;
    private final CachingHostAddressProviderConfig config;

    @Inject
    public CachingHostAddressProviderFactory(NodeManager nodeManager, CachingHostAddressProviderConfig config)
    {
        this.nodeManager = requireNonNull(nodeManager, "nodeManager is null");
        this.config = requireNonNull(config, "config is null");
    }

    @Override
    public CachingHostAddressProvider get()
    {
        return switch (config.getProviderType()) {
            case DEFAULT -> new DefaultCachingHostAddressProvider();
            case CONSISTENT_HASH -> new ConsistentHashingHostAddressProvider(nodeManager, config);
            case REPLICATED -> new ReplicatedHostAddressProvider(nodeManager);
        };
    }
}
