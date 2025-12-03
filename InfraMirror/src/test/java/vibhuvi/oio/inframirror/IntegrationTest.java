package vibhuvi.oio.inframirror;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import vibhuvi.oio.inframirror.config.AsyncSyncConfiguration;
import vibhuvi.oio.inframirror.config.EmbeddedElasticsearch;
import vibhuvi.oio.inframirror.config.EmbeddedRedis;
import vibhuvi.oio.inframirror.config.EmbeddedSQL;
import vibhuvi.oio.inframirror.config.JacksonConfiguration;
import vibhuvi.oio.inframirror.config.TestSecurityConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { InfraMirrorApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
