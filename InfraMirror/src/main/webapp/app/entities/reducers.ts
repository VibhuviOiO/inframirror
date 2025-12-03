import region from 'app/entities/region/region.reducer';
import datacenter from 'app/entities/datacenter/datacenter.reducer';
import agent from 'app/entities/agent/agent.reducer';
import instance from 'app/entities/instance/instance.reducer';
import schedule from 'app/entities/schedule/schedule.reducer';
import httpMonitor from 'app/entities/http-monitor/http-monitor.reducer';
import httpHeartbeat from 'app/entities/http-heartbeat/http-heartbeat.reducer';
import pingHeartbeat from 'app/entities/ping-heartbeat/ping-heartbeat.reducer';
import apiKey from 'app/entities/api-key/api-key.reducer';
import auditTrail from 'app/entities/audit-trail/audit-trail.reducer';
import tag from 'app/entities/tag/tag.reducer';
import sessionLog from 'app/entities/session-log/session-log.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  region,
  datacenter,
  agent,
  instance,
  schedule,
  httpMonitor,
  httpHeartbeat,
  pingHeartbeat,
  apiKey,
  auditTrail,
  tag,
  sessionLog,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
