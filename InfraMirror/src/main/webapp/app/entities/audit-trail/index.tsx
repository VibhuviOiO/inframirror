import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AuditTrail from './audit-trail';
import AuditTrailDetail from './audit-trail-detail';
import AuditTrailUpdate from './audit-trail-update';
import AuditTrailDeleteDialog from './audit-trail-delete-dialog';

const AuditTrailRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AuditTrail />} />
    <Route path="new" element={<AuditTrailUpdate />} />
    <Route path=":id">
      <Route index element={<AuditTrailDetail />} />
      <Route path="edit" element={<AuditTrailUpdate />} />
      <Route path="delete" element={<AuditTrailDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuditTrailRoutes;
