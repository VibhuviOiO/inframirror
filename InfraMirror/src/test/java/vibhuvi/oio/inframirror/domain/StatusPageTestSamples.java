package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatusPageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static StatusPage getStatusPageSample1() {
        return new StatusPage()
            .id(1L)
            .name("name1")
            .slug("slug1")
            .description("description1")
            .customDomain("customDomain1")
            .logoUrl("logoUrl1")
            .themeColor("themeColor1")
            .headerText("headerText1")
            .footerText("footerText1")
            .autoRefreshSeconds(1);
    }

    public static StatusPage getStatusPageSample2() {
        return new StatusPage()
            .id(2L)
            .name("name2")
            .slug("slug2")
            .description("description2")
            .customDomain("customDomain2")
            .logoUrl("logoUrl2")
            .themeColor("themeColor2")
            .headerText("headerText2")
            .footerText("footerText2")
            .autoRefreshSeconds(2);
    }

    public static StatusPage getStatusPageRandomSampleGenerator() {
        return new StatusPage()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .customDomain(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString())
            .themeColor(UUID.randomUUID().toString())
            .headerText(UUID.randomUUID().toString())
            .footerText(UUID.randomUUID().toString())
            .autoRefreshSeconds(intCount.incrementAndGet());
    }
}
