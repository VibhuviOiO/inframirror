package vibhuvi.oio.inframirror.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HttpHeartbeatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static HttpHeartbeat getHttpHeartbeatSample1() {
        return new HttpHeartbeat()
            .id(1L)
            .responseTimeMs(1)
            .responseSizeBytes(1)
            .responseStatusCode(1)
            .responseContentType("responseContentType1")
            .responseServer("responseServer1")
            .responseCacheStatus("responseCacheStatus1")
            .dnsLookupMs(1)
            .dnsResolvedIp("dnsResolvedIp1")
            .tcpConnectMs(1)
            .tlsHandshakeMs(1)
            .sslCertificateIssuer("sslCertificateIssuer1")
            .sslDaysUntilExpiry(1)
            .timeToFirstByteMs(1)
            .warningThresholdMs(1)
            .criticalThresholdMs(1)
            .errorType("errorType1")
            .httpVersion("httpVersion1")
            .contentEncoding("contentEncoding1")
            .transferEncoding("transferEncoding1")
            .responseBodyHash("responseBodyHash1")
            .responseBodyUncompressedBytes(1)
            .cacheControl("cacheControl1")
            .etag("etag1")
            .cacheAge(1)
            .cdnProvider("cdnProvider1")
            .cdnPop("cdnPop1");
    }

    public static HttpHeartbeat getHttpHeartbeatSample2() {
        return new HttpHeartbeat()
            .id(2L)
            .responseTimeMs(2)
            .responseSizeBytes(2)
            .responseStatusCode(2)
            .responseContentType("responseContentType2")
            .responseServer("responseServer2")
            .responseCacheStatus("responseCacheStatus2")
            .dnsLookupMs(2)
            .dnsResolvedIp("dnsResolvedIp2")
            .tcpConnectMs(2)
            .tlsHandshakeMs(2)
            .sslCertificateIssuer("sslCertificateIssuer2")
            .sslDaysUntilExpiry(2)
            .timeToFirstByteMs(2)
            .warningThresholdMs(2)
            .criticalThresholdMs(2)
            .errorType("errorType2")
            .httpVersion("httpVersion2")
            .contentEncoding("contentEncoding2")
            .transferEncoding("transferEncoding2")
            .responseBodyHash("responseBodyHash2")
            .responseBodyUncompressedBytes(2)
            .cacheControl("cacheControl2")
            .etag("etag2")
            .cacheAge(2)
            .cdnProvider("cdnProvider2")
            .cdnPop("cdnPop2");
    }

    public static HttpHeartbeat getHttpHeartbeatRandomSampleGenerator() {
        return new HttpHeartbeat()
            .id(longCount.incrementAndGet())
            .responseTimeMs(intCount.incrementAndGet())
            .responseSizeBytes(intCount.incrementAndGet())
            .responseStatusCode(intCount.incrementAndGet())
            .responseContentType(UUID.randomUUID().toString())
            .responseServer(UUID.randomUUID().toString())
            .responseCacheStatus(UUID.randomUUID().toString())
            .dnsLookupMs(intCount.incrementAndGet())
            .dnsResolvedIp(UUID.randomUUID().toString())
            .tcpConnectMs(intCount.incrementAndGet())
            .tlsHandshakeMs(intCount.incrementAndGet())
            .sslCertificateIssuer(UUID.randomUUID().toString())
            .sslDaysUntilExpiry(intCount.incrementAndGet())
            .timeToFirstByteMs(intCount.incrementAndGet())
            .warningThresholdMs(intCount.incrementAndGet())
            .criticalThresholdMs(intCount.incrementAndGet())
            .errorType(UUID.randomUUID().toString())
            .httpVersion(UUID.randomUUID().toString())
            .contentEncoding(UUID.randomUUID().toString())
            .transferEncoding(UUID.randomUUID().toString())
            .responseBodyHash(UUID.randomUUID().toString())
            .responseBodyUncompressedBytes(intCount.incrementAndGet())
            .cacheControl(UUID.randomUUID().toString())
            .etag(UUID.randomUUID().toString())
            .cacheAge(intCount.incrementAndGet())
            .cdnProvider(UUID.randomUUID().toString())
            .cdnPop(UUID.randomUUID().toString());
    }
}
