import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import HttpHeartbeat from './http-heartbeat';
import HttpHeartbeatDetail from './http-heartbeat-detail';

const HttpHeartbeatRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<HttpHeartbeat />} />
    <Route path=":id">
      <Route index element={<HttpHeartbeatDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default HttpHeartbeatRoutes;
