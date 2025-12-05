package vibhuvi.oio.inframirror.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        Config config = new Config();
        // Fix Hibernate lazy initialization https://github.com/jhipster/generator-jhipster/issues/22889
        config.setCodec(new org.redisson.codec.SerializationCodec());
        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, vibhuvi.oio.inframirror.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Branding.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Region.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Region.class.getName() + ".datacenters", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Region.class.getName() + ".agents", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Datacenter.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Datacenter.class.getName() + ".instances", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Datacenter.class.getName() + ".monitoredServices", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Agent.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Agent.class.getName() + ".instances", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Agent.class.getName() + ".httpHeartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Agent.class.getName() + ".instanceHeartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Agent.class.getName() + ".serviceHeartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.AuditTrail.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.ApiKey.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.HttpMonitor.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.HttpMonitor.class.getName() + ".children", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.HttpMonitor.class.getName() + ".heartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.HttpHeartbeat.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.AgentMonitor.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.AgentLock.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Instance.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Instance.class.getName() + ".heartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.Instance.class.getName() + ".serviceInstances", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.InstanceHeartbeat.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.ServiceInstance.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.ServiceInstance.class.getName() + ".heartbeats", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.ServiceHeartbeat.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.StatusPage.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.StatusPage.class.getName() + ".items", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.StatusPage.class.getName() + ".statusDependencies", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.StatusPageItem.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.StatusDependency.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.MonitoredService.class.getName(), jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.MonitoredService.class.getName() + ".serviceInstances", jcacheConfiguration);
            createCache(cm, vibhuvi.oio.inframirror.domain.MonitoredService.class.getName() + ".heartbeats", jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
