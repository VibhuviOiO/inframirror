import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Region from './region';
import Datacenter from './datacenter';
import Agent from './agent';
import Instance from './instance';
import Schedule from './schedule';
import HttpMonitor from './http-monitor';
import HttpHeartbeat from './http-heartbeat';
import PingHeartbeat from './ping-heartbeat';
import ApiKey from './api-key';
import AuditTrail from './audit-trail';
import Tag from './tag';
import SessionLog from './session-log';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="region/*" element={<Region />} />
        <Route path="datacenter/*" element={<Datacenter />} />
        <Route path="agent/*" element={<Agent />} />
        <Route path="instance/*" element={<Instance />} />
        <Route path="schedule/*" element={<Schedule />} />
        <Route path="http-monitor/*" element={<HttpMonitor />} />
        <Route path="http-heartbeat/*" element={<HttpHeartbeat />} />
        <Route path="ping-heartbeat/*" element={<PingHeartbeat />} />
        <Route path="api-key/*" element={<ApiKey />} />
        <Route path="audit-trail/*" element={<AuditTrail />} />
        <Route path="tag/*" element={<Tag />} />
        <Route path="session-log/*" element={<SessionLog />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
