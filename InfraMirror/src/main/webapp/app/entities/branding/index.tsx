import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Branding from './branding';
import BrandingDetail from './branding-detail';
import BrandingUpdate from './branding-update';
import BrandingDeleteDialog from './branding-delete-dialog';

const BrandingRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Branding />} />
    <Route path="new" element={<BrandingUpdate />} />
    <Route path=":id">
      <Route index element={<BrandingDetail />} />
      <Route path="edit" element={<BrandingUpdate />} />
      <Route path="delete" element={<BrandingDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BrandingRoutes;
