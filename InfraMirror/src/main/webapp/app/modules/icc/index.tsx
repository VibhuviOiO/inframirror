import React from 'react';
import { Route } from 'react-router-dom';
import ICCIntegrationList from './icc-integration-list';

const ICCRoutes = () => (
  <>
    <Route path="icc" element={<ICCIntegrationList />} />
  </>
);

export default ICCRoutes;
