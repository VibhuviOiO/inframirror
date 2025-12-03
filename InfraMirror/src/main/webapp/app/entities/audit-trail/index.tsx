import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AuditTrail from './audit-trail';
import AuditTrailDetail from './audit-trail-detail';

const AuditTrailRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AuditTrail />} />
    <Route path=":id">
      <Route index element={<AuditTrailDetail />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuditTrailRoutes;
