import Index from "./pages/Index";
import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import Applications from "./pages/Applications";
import ApplicationDeployments from "./pages/ApplicationDeployments";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import { ServiceCatalogPageWithLayout } from "./pages/ServiceCatalog";
import ServiceTypes from "./pages/ServiceTypes";
import ServiceOwners from "./pages/ServiceOwners";
import ServiceDependencies from "./pages/ServiceDependencies";
import EnvironmentsPage from "./pages/Environments";
import TeamsPage from "./pages/Teams";
import ApplicationCatalogPageWithLayout from "./pages/ApplicationCatalog";

const routes = [
  {
    path: "/",
    element: <Index />,
  },
  {
    path: "/datacenters",
    element: <DatacentersPage />,
  },
  {
    path: "/settings",
    element: <SettingsPage />,
    children: [
      { path: "regions", element: <RegionsPage /> },
      { path: "environments", element: <EnvironmentsPage /> },
      { path: "service-types", element: <ServiceTypes /> },
      { path: "teams", element: <TeamsPage /> },
      { index: true, element: <RegionsPage /> },
    ],
  },
  { path: "/service-catalog", element: <ServiceCatalogPageWithLayout /> },
  { path: "/application-catalog", element: <ApplicationCatalogPageWithLayout /> },
  { path: "/infrastructure/datacenters/enabled-services", element: <DatacentersEnabledServices /> },
  { path: "/infrastructure/datacenters/instances", element: <DatacentersInstances /> },
  { path: "applications", element: <Applications /> },
  { path: "applications/deployments", element: <ApplicationDeployments /> },
  { path: "*", element: <NotFound /> },
];

export default routes;
