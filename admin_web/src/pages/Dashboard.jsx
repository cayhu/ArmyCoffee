import React, { useState } from 'react';
import {
  LayoutDashboard,
  Coffee,
  Table as TableIcon,
  ShoppingBag,
  Settings,
  LogOut,
  BarChart3,
  Package
} from 'lucide-react';
import ProductManager from './ProductManager';
import TableManager from './TableManager';
import OrdersManager from './OrdersManager';
import Analytics from './Analytics';
import InventoryManager from './InventoryManager';

const Dashboard = () => {
  const [activeTab, setActiveTab] = useState('dashboard'); // Mặc định mở Analytics

  const menuItems = [
    { id: 'dashboard', label: 'Thống kê', icon: BarChart3 },
    { id: 'orders', label: 'Đơn hàng', icon: ShoppingBag },
    { id: 'products', label: 'Thực đơn', icon: Coffee },
    { id: 'tables', label: 'Phòng & Bàn', icon: TableIcon },
    { id: 'inventory', label: 'Kho hàng', icon: Package },
    { id: 'settings', label: 'Cài đặt', icon: Settings },
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard': return <Analytics />;
      case 'orders': return <OrdersManager />;
      case 'products': return <ProductManager />;
      case 'tables': return <TableManager />;
      case 'inventory': return <InventoryManager />;
      default: return (
        <div className="p-6">
          <h1 className="text-2xl font-bold mb-4">Chào mừng trở lại, Admin!</h1>
          <p className="text-gray-600">Chọn một mục từ menu bên trái để bắt đầu quản lý.</p>
        </div>
      );
    }
  };

  return (
    <div className="flex h-screen bg-gray-50 overflow-hidden">
      {/* Sidebar */}
      <div className="w-64 bg-white border-r border-gray-200 flex flex-col">
        <div className="p-6 flex items-center gap-3 border-b border-gray-100">
          <div className="w-10 h-10 bg-orange-600 rounded-lg flex items-center justify-center">
            <Coffee className="text-white" size={24} />
          </div>
          <span className="font-bold text-xl text-gray-800">Army Coffee</span>
        </div>

        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          {menuItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                activeTab === item.id
                  ? 'bg-orange-50 text-orange-600 font-medium'
                  : 'text-gray-500 hover:bg-gray-100'
              }`}
            >
              <item.icon size={20} />
              {item.label}
            </button>
          ))}
        </nav>

        <div className="p-4 border-t border-gray-100">
          <button className="w-full flex items-center gap-3 px-4 py-3 text-red-500 hover:bg-red-50 rounded-xl transition-all">
            <LogOut size={20} />
            Đăng xuất
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-y-auto bg-gray-50">
        {renderContent()}
      </div>
    </div>
  );
};

export default Dashboard;
