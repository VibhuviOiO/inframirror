import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SessionLog from './session-log';
import SessionLogDetail from './session-log-detail';

const SessionLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SessionLog />} />
    <Route path=":id">
      <Route index element={<SessionLogDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SessionLogRoutes;
