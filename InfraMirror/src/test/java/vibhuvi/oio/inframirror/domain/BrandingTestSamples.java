package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BrandingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Branding getBrandingSample1() {
        return new Branding()
            .id(1L)
            .title("title1")
            .description("description1")
            .keywords("keywords1")
            .author("author1")
            .faviconPath("faviconPath1")
            .logoPath("logoPath1")
            .logoWidth(1)
            .logoHeight(1)
            .footerTitle("footerTitle1");
    }

    public static Branding getBrandingSample2() {
        return new Branding()
            .id(2L)
            .title("title2")
            .description("description2")
            .keywords("keywords2")
            .author("author2")
            .faviconPath("faviconPath2")
            .logoPath("logoPath2")
            .logoWidth(2)
            .logoHeight(2)
            .footerTitle("footerTitle2");
    }

    public static Branding getBrandingRandomSampleGenerator() {
        return new Branding()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .keywords(UUID.randomUUID().toString())
            .author(UUID.randomUUID().toString())
            .faviconPath(UUID.randomUUID().toString())
            .logoPath(UUID.randomUUID().toString())
            .logoWidth(intCount.incrementAndGet())
            .logoHeight(intCount.incrementAndGet())
            .footerTitle(UUID.randomUUID().toString());
    }
}
