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
package io.trino.plugin.iceberg.cache;

import com.google.inject.Inject;
import io.trino.filesystem.TrinoInputFile;
import io.trino.filesystem.cache.CacheKeyProvider;
import io.trino.plugin.iceberg.IcebergConfig;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class IcebergCacheKeyProvider
        implements CacheKeyProvider
{
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("/([^/]+)/([^/]+)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/(?:data|metadata)/");

    private final List<String> fileSystemCacheTables;

    @Inject
    public IcebergCacheKeyProvider(IcebergConfig icebergConfig)
    {
        this.fileSystemCacheTables = requireNonNull(icebergConfig, "icebergConfig is null").getFileSystemCacheTables();
    }

    @Override
    public Optional<String> getCacheKey(TrinoInputFile inputFile)
    {
        String path = inputFile.location().path();
        if (path.endsWith(".trinoSchema") || path.contains("/.trinoPermissions/")) {
            // Needed to avoid caching files from FileHiveMetastore on coordinator during tests
            return Optional.empty();
        }

        // If specific tables are configured for caching, only cache files from those tables
        if (!this.fileSystemCacheTables.isEmpty()) {
            String tableName = extractTableNameFromPath(path);
            if (tableName != null && !this.fileSystemCacheTables.contains(tableName)) {
                // File belongs to a table not in the cache list - don't cache
                return Optional.empty();
            }
        }

        // Iceberg data and metadata files are immutable
        return Optional.of(path);
    }

    private String extractTableNameFromPath(String path)
    {
        Matcher matcher = TABLE_NAME_PATTERN.matcher(path);
        if (matcher.find()) {
            String schemaName = matcher.group(1);
            String tableName = matcher.group(2);
            return schemaName + "." + tableName;
        }
        return null;
    }
}
