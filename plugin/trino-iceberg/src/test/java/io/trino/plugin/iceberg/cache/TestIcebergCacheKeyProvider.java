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

import com.google.common.collect.ImmutableList;
import io.trino.filesystem.Location;
import io.trino.filesystem.TrinoInputFile;
import io.trino.plugin.iceberg.IcebergConfig;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TestIcebergCacheKeyProvider
{
    @Test
    public void testCacheAllTablesWhenEmpty()
    {
        IcebergConfig config = new IcebergConfig();
        IcebergCacheKeyProvider provider = new IcebergCacheKeyProvider(config);

        TrinoInputFile inputFile = createTestInputFile("file:///catalog/myschema/mytable-12345678-1234-1234-1234-123456789012/data/file.parquet");
        Optional<String> cacheKey = provider.getCacheKey(inputFile);

        assertThat(cacheKey).isPresent();
        assertThat(cacheKey.get()).isEqualTo("catalog/myschema/mytable-12345678-1234-1234-1234-123456789012/data/file.parquet");
    }

    @Test
    public void testCacheOnlySpecifiedTables()
    {
        IcebergConfig config = new IcebergConfig();
        config.setFileSystemCacheTables(ImmutableList.of("myschema.mytable", "otherschema.othertable"));
        IcebergCacheKeyProvider provider = new IcebergCacheKeyProvider(config);

        // Should cache files from specified table
        TrinoInputFile allowedFile = createTestInputFile("file:///catalog/myschema/mytable-12345678-1234-1234-1234-123456789012/data/file.parquet");
        Optional<String> allowedCacheKey = provider.getCacheKey(allowedFile);
        assertThat(allowedCacheKey).isPresent();

        // Should not cache files from non-specified table
        TrinoInputFile blockedFile = createTestInputFile("file:///catalog/myschema/anothertable-12345678-1234-1234-1234-123456789012/data/file.parquet");
        Optional<String> blockedCacheKey = provider.getCacheKey(blockedFile);
        assertThat(blockedCacheKey).isEmpty();
    }

    @Test
    public void testSkipTrinoMetadataFiles()
    {
        IcebergConfig config = new IcebergConfig();
        IcebergCacheKeyProvider provider = new IcebergCacheKeyProvider(config);

        TrinoInputFile schemaFile = createTestInputFile("file:///path/to/file.trinoSchema");
        assertThat(provider.getCacheKey(schemaFile)).isEmpty();

        TrinoInputFile permissionsFile = createTestInputFile("file:///path/.trinoPermissions/file");
        assertThat(provider.getCacheKey(permissionsFile)).isEmpty();
    }

    @Test
    public void testTableNameExtractionWithUuidPaths()
    {
        IcebergConfig config = new IcebergConfig();
        config.setFileSystemCacheTables(ImmutableList.of("schema.table"));
        IcebergCacheKeyProvider provider = new IcebergCacheKeyProvider(config);

        TrinoInputFile specificFormatFile = createTestInputFile("file:///catalog/schema/table-12345678-1234-1234-1234-123456789012/metadata/file-m1.avro");
        assertThat(provider.getCacheKey(specificFormatFile)).isPresent();
    }

    private TrinoInputFile createTestInputFile(String path)
    {
        Location location = Location.of(path);
        return new TrinoInputFile()
        {
            @Override
            public Location location()
            {
                return location;
            }

            @Override
            public long length()
            {
                return 1000L;
            }

            @Override
            public Instant lastModified()
            {
                return Instant.now();
            }

            @Override
            public boolean exists()
            {
                return true;
            }

            @Override
            public io.trino.filesystem.TrinoInput newInput()
            {
                throw new UnsupportedOperationException("Not implemented for test");
            }

            @Override
            public io.trino.filesystem.TrinoInputStream newStream()
            {
                throw new UnsupportedOperationException("Not implemented for test");
            }
        };
    }
}
