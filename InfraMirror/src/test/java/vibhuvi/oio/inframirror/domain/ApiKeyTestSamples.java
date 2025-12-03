package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApiKeyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ApiKey getApiKeySample1() {
        return new ApiKey()
            .id(1L)
            .name("name1")
            .description("description1")
            .keyHash("keyHash1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static ApiKey getApiKeySample2() {
        return new ApiKey()
            .id(2L)
            .name("name2")
            .description("description2")
            .keyHash("keyHash2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static ApiKey getApiKeyRandomSampleGenerator() {
        return new ApiKey()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .keyHash(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
