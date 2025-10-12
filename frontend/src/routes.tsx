import Index from "./pages/Index";
import DatacentersPage from "./pages/Datacenters";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import EnvironmentsPage from "./pages/Environments";
import HostsPage from "./pages/Hosts";
import ClustersPageWithLayout from "./pages/Clusters";
import IntegrationPageWithLayout from "./pages/Integration";
import { Service } from "dockerode";
import ServicesPageWithLayout from "./pages/Services";
import PostgresOpsPageWithLayout from "./pages/PostgresOps";
import DockerOperationsPageWithLayout from "./pages/DockerOperations";

// Monitoring Pages
import MonitoringDashboard from "./pages/MonitoringDashboard";
import Monitors from "./pages/Monitors";
import MonitorDetail from "./pages/MonitorDetail";
import HTTPMonitors from "./pages/HTTPMonitors";
import TCPMonitors from "./pages/TCPMonitors";
import PINGMonitors from "./pages/PINGMonitors";
import DNSMonitors from "./pages/DNSMonitors";

const routes = [
  {
    path: "/",
    element: <Index />,
  },
  {
    path: "/datacenters",
    element: <DatacentersPage />,
  },
  { path: "/integrations", element: <IntegrationPageWithLayout /> },
  { path: "/hosts", element: <HostsPage /> },
  { path: "/clusters", element: <ClustersPageWithLayout /> },
  { path: "/services", element: <ServicesPageWithLayout /> },
  {
    path: "/settings",
    element: <SettingsPage />,
    children: [
      { path: "regions", element: <RegionsPage /> },
      { path: "environments", element: <EnvironmentsPage /> },
      { index: true, element: <RegionsPage /> },
    ],
  },
  { path: "/docker-operations", element: <DockerOperationsPageWithLayout /> },
  { path: "/postgres-operations", element: <PostgresOpsPageWithLayout /> },
  
  // Monitoring Routes
  { path: "/monitoring", element: <MonitoringDashboard /> },
  { path: "/monitors", element: <Monitors /> },
  { path: "/monitors/http", element: <HTTPMonitors /> },
  { path: "/monitors/tcp", element: <TCPMonitors /> },
  { path: "/monitors/ping", element: <PINGMonitors /> },
  { path: "/monitors/dns", element: <DNSMonitors /> },
  { path: "/monitors/:id", element: <MonitorDetail /> },
  
  { path: "*", element: <NotFound /> },
];

export default routes;
