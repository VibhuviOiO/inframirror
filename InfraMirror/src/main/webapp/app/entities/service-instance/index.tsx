import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ServiceInstance from './service-instance';
import ServiceInstanceDetail from './service-instance-detail';
import ServiceInstanceUpdate from './service-instance-update';
import ServiceInstanceDeleteDialog from './service-instance-delete-dialog';

const ServiceInstanceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ServiceInstance />} />
    <Route path="new" element={<ServiceInstanceUpdate />} />
    <Route path=":id">
      <Route index element={<ServiceInstanceDetail />} />
      <Route path="edit" element={<ServiceInstanceUpdate />} />
      <Route path="delete" element={<ServiceInstanceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ServiceInstanceRoutes;
