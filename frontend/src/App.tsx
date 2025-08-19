import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Index from "./pages/Index";


import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import Applications from "./pages/Applications";
import ApplicationDeployments from "./pages/ApplicationDeployments";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import ServiceCatalog from "./pages/ServiceCatalog";
import ServiceCatalogPage from "./pages/ServiceCatalog";
import ServiceTypes from "./pages/ServiceTypes";
import ServiceOwners from "./pages/ServiceOwners";
import ServiceDependencies from "./pages/ServiceDependencies";
import EnvironmentsPage from "./pages/Environments";
import TeamsPage from "./pages/Teams";
import ApplicationCatalogPage from "./pages/ApplicationCatalog";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Index />} />
          {/* Datacenters */}
          <Route path="/datacenters" element={<DatacentersPage />} />
          {/* Settings (with submenus handled inside) */}
          <Route path="/settings" element={<SettingsPage />}>
            <Route path="regions" element={<RegionsPage />} />
            <Route path="environments" element={<EnvironmentsPage />} />
            <Route path="service-types" element={<ServiceTypes />} />
            <Route path="teams" element={<TeamsPage />} />
            {/* Add more submenus as needed */}
            <Route index element={<RegionsPage />} />
          </Route>
          <Route path="/service-catalogs" element={<ServiceCatalog />} />
          <Route path="/application-catalogs" element={<ApplicationCatalogPage />} />
          <Route path="/service-catalogs" element={<ServiceCatalogPage />} />
          <Route path="/infrastructure/datacenters/enabled-services" element={<DatacentersEnabledServices />} />
          <Route path="/infrastructure/datacenters/instances" element={<DatacentersInstances />} />
          {/* Services */}
          {/* Applications */}
          <Route path="applications" element={<Applications />} />
          <Route path="applications/deployments" element={<ApplicationDeployments />} />
          {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;