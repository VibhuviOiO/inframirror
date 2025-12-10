import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AuditTrail from './audit-trail';

const AuditTrailRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AuditTrail />} />
  </ErrorBoundaryRoutes>
);

export default AuditTrailRoutes;
