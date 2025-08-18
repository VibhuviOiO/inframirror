import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Index from "./pages/Index";
import NotFound from "./pages/NotFound";
import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import ServiceCatalog from "./pages/ServiceCatalog";
import ServiceTypes from "./pages/ServiceTypes";
import ServiceOwners from "./pages/ServiceOwners";
import ServiceDependencies from "./pages/ServiceDependencies";
import Applications from "./pages/Applications";
import ApplicationDeployments from "./pages/ApplicationDeployments";

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
          <Route path="/infrastructure/datacenters" element={<DatacentersPage />} />
          <Route path="/infrastructure/datacenters/enabled-services" element={<DatacentersEnabledServices />} />
          <Route path="/infrastructure/datacenters/instances" element={<DatacentersInstances />} />
          {/* Services */}
          <Route path="services/catalog" element={<ServiceCatalog />} />
          <Route path="services/types" element={<ServiceTypes />} />
          <Route path="services/owners" element={<ServiceOwners />} />
          <Route path="services/dependencies" element={<ServiceDependencies />} />
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