// App.js
import React, { useState } from 'react';
import Navbar from './Navbar';
import AdminList from './AdminList';
import BanList from './BanList';
import OperationLog from './OperationLog';
import { QueryClient, QueryClientProvider } from 'react-query';

function RoomManager() {
  const [activeTab, setActiveTab] = useState(0);

  const handleChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <Navbar activeTab={activeTab} handleChange={handleChange} />
      {activeTab === 0 && <AdminList />}
      {activeTab === 1 && <BanList />}
      {activeTab === 2 && <OperationLog />}
    </QueryClientProvider>
  );
}

export default RoomManager;
