import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { AuthModal } from './components/AuthModal';

import { HomePage } from './pages/HomePage';
import { ServicesPage } from './pages/ServicesPage';
import { JobsPage } from './pages/JobsPage';
import { EquipmentPage } from './pages/EquipmentPage';
import { ComplaintsPage } from './pages/ComplaintsPage';
import { SchemesPage } from './pages/SchemesPage';
import { EmergencyDirectoryPage } from './pages/EmergencyDirectoryPage';
import { AdminDashboardPage } from './pages/AdminDashboardPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <div className="app-container">
            <Navbar />
            <main className="main-content">
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/services" element={<ServicesPage />} />
                <Route path="/jobs" element={<JobsPage />} />
                <Route path="/equipment" element={<EquipmentPage />} />
                <Route path="/complaints" element={<ComplaintsPage />} />
                <Route path="/schemes" element={<SchemesPage />} />
                <Route path="/emergency" element={<EmergencyDirectoryPage />} />
                <Route path="/admin" element={<AdminDashboardPage />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
            <Footer />
            <AuthModal />
          </div>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
};

export default App;
