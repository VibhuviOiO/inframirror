import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Branding from './branding';
import Region from './region';
import Datacenter from './datacenter';
import Agent from './agent';
import AuditTrail from './audit-trail';
import ApiKey from './api-key';
import HttpMonitor from './http-monitor';
import HttpHeartbeat from './http-heartbeat';
import AgentMonitor from './agent-monitor';
import AgentLock from './agent-lock';
import Instance from './instance';
import InstanceHeartbeat from './instance-heartbeat';
import Service from './service';
import ServiceInstance from './service-instance';
import ServiceHeartbeat from './service-heartbeat';
import StatusPage from './status-page';
import StatusPageItem from './status-page-item';
import StatusDependency from './status-dependency';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="branding/*" element={<Branding />} />
        <Route path="region/*" element={<Region />} />
        <Route path="datacenter/*" element={<Datacenter />} />
        <Route path="agent/*" element={<Agent />} />
        <Route path="audit-trail/*" element={<AuditTrail />} />
        <Route path="api-key/*" element={<ApiKey />} />
        <Route path="http-monitor/*" element={<HttpMonitor />} />
        <Route path="http-heartbeat/*" element={<HttpHeartbeat />} />
        <Route path="agent-monitor/*" element={<AgentMonitor />} />
        <Route path="agent-lock/*" element={<AgentLock />} />
        <Route path="instance/*" element={<Instance />} />
        <Route path="instance-heartbeat/*" element={<InstanceHeartbeat />} />
        <Route path="service/*" element={<Service />} />
        <Route path="service-instance/*" element={<ServiceInstance />} />
        <Route path="service-heartbeat/*" element={<ServiceHeartbeat />} />
        <Route path="status-page/*" element={<StatusPage />} />
        <Route path="status-page-item/*" element={<StatusPageItem />} />
        <Route path="status-dependency/*" element={<StatusDependency />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
