import branding from 'app/entities/branding/branding.reducer';
import region from 'app/entities/region/region.reducer';
import datacenter from 'app/entities/datacenter/datacenter.reducer';
import agent from 'app/entities/agent/agent.reducer';
import auditTrail from 'app/entities/audit-trail/audit-trail.reducer';
import apiKey from 'app/entities/api-key/api-key.reducer';
import httpMonitor from 'app/entities/http-monitor/http-monitor.reducer';
import httpHeartbeat from 'app/entities/http-heartbeat/http-heartbeat.reducer';
import agentMonitor from 'app/entities/agent-monitor/agent-monitor.reducer';
import agentLock from 'app/entities/agent-lock/agent-lock.reducer';
import instance from 'app/entities/instance/instance.reducer';
import instanceHeartbeat from 'app/entities/instance-heartbeat/instance-heartbeat.reducer';
import serviceInstance from 'app/entities/service-instance/service-instance.reducer';
import serviceHeartbeat from 'app/entities/service-heartbeat/service-heartbeat.reducer';
import statusPage from 'app/entities/status-page/status-page.reducer';
import statusDependency from 'app/entities/status-dependency/status-dependency.reducer';
import monitoredService from 'app/entities/monitored-service/monitored-service.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  branding,
  region,
  datacenter,
  agent,
  auditTrail,
  apiKey,
  httpMonitor,
  httpHeartbeat,
  agentMonitor,
  agentLock,
  instance,
  instanceHeartbeat,
  serviceInstance,
  serviceHeartbeat,
  statusPage,
  statusDependency,
  monitoredService,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
