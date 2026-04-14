import React, { useState, useEffect } from 'react';
import ProductManager from './ProductManager';
import TableManager from './TableManager';
import {
  LayoutDashboard,
  Coffee,
  ListOrdered,
  Users,
  Table as TableIcon,
  BarChart3,
  Settings,
  LogOut,
  Plus,
  Search,
  CheckCircle,
  XCircle,
  Download
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line
} from 'recharts';

// Mock data for charts
const revenueData = [
  { name: 'Thứ 2', revenue: 1200000 },
  { name: 'Thứ 3', revenue: 1900000 },
  { name: 'Thứ 4', revenue: 1500000 },
  { name: 'Thứ 5', revenue: 2200000 },
  { name: 'Thứ 6', revenue: 3000000 },
  { name: 'Thứ 7', revenue: 4500000 },
  { name: 'Chủ nhật', revenue: 3800000 },
];

const Dashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-slate-800 text-white flex flex-col">
        <div className="p-6 border-b border-slate-700">
          <h1 className="text-2xl font-bold text-orange-400">Army Coffee</h1>
          <p className="text-xs text-slate-400 mt-1">Hệ thống quản trị</p>
        </div>

        <nav className="flex-1 mt-6">
          <NavItem
            icon={<LayoutDashboard size={20}/>}
            label="Tổng quan"
            active={activeTab === 'overview'}
            onClick={() => setActiveTab('overview')}
          />
          <NavItem
            icon={<TableIcon size={20}/>}
            label="Quản lý bàn"
            active={activeTab === 'tables'}
            onClick={() => setActiveTab('tables')}
          />
          <NavItem
            icon={<Coffee size={20}/>}
            label="Sản phẩm"
            active={activeTab === 'products'}
            onClick={() => setActiveTab('products')}
          />
          <NavItem
            icon={<ListOrdered size={20}/>}
            label="Hóa đơn"
            active={activeTab === 'orders'}
            onClick={() => setActiveTab('orders')}
          />
          <NavItem
            icon={<BarChart3 size={20}/>}
            label="Thống kê"
            active={activeTab === 'stats'}
            onClick={() => setActiveTab('stats')}
          />
        </nav>

        <div className="p-4 border-t border-slate-700">
          <button className="flex items-center gap-3 text-slate-300 hover:text-white transition-colors w-full p-3 rounded-lg hover:bg-slate-700">
            <LogOut size={20} />
            <span>Đăng xuất</span>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-y-auto">
        {/* Header */}
        <header className="bg-white shadow-sm p-4 flex justify-between items-center sticky top-0 z-10">
          <h2 className="text-xl font-semibold text-gray-800">
            {activeTab === 'overview' && 'Tổng quan hệ thống'}
            {activeTab === 'tables' && 'Quản lý bàn'}
            {activeTab === 'products' && 'Quản lý sản phẩm'}
            {activeTab === 'orders' && 'Danh sách hóa đơn'}
            {activeTab === 'stats' && 'Báo cáo thống kê'}
          </h2>
          <div className="flex items-center gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
              <input
                type="text"
                placeholder="Tìm kiếm..."
                className="pl-10 pr-4 py-2 border rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-orange-400 w-64"
              />
            </div>
            <div className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-600 font-bold">
              AD
            </div>
          </div>
        </header>

        {/* Content Body */}
        <main className="p-6">
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Stats Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <StatCard title="Tổng doanh thu" value="18,500,000đ" subtitle="+12% so với hôm qua" color="bg-blue-500" />
                <StatCard title="Đơn hàng mới" value="24" subtitle="6 đơn đang chờ duyệt" color="bg-green-500" />
                <StatCard title="Bàn đang sử dụng" value="8/15" subtitle="3 bàn đã đặt trước" color="bg-orange-500" />
                <StatCard title="Sản phẩm bán chạy" value="Cà phê sữa" subtitle="120 ly trong tuần" color="bg-purple-500" />
              </div>

              {/* Charts Section */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-white p-6 rounded-xl shadow-sm border">
                  <h3 className="text-lg font-semibold mb-6">Doanh thu 7 ngày gần nhất</h3>
                  <div className="h-64">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={revenueData}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="revenue" fill="#f97316" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </div>
                </div>

                <div className="bg-white p-6 rounded-xl shadow-sm border">
                  <div className="flex justify-between items-center mb-6">
                    <h3 className="text-lg font-semibold">Trạng thái bàn hiện tại</h3>
                    <button className="text-sm text-blue-600 font-medium">Chi tiết</button>
                  </div>
                  <div className="grid grid-cols-4 gap-4">
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(num => (
                      <div key={num} className={`p-4 rounded-lg text-center border-2 ${num % 3 === 0 ? 'bg-red-50 border-red-200' : 'bg-green-50 border-green-200'}`}>
                        <div className={`text-xs font-bold ${num % 3 === 0 ? 'text-red-600' : 'text-green-600'}`}>
                          {num % 3 === 0 ? 'Đang dùng' : 'Trống'}
                        </div>
                        <div className="text-xl font-bold mt-1">Bàn {num}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Recent Orders Table */}
              <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
                <div className="p-6 border-b flex justify-between items-center">
                  <h3 className="text-lg font-semibold">Hóa đơn gần đây</h3>
                  <button className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg text-sm hover:bg-green-700">
                    <Download size={16} />
                    Xuất Excel
                  </button>
                </div>
                <table className="w-full text-left">
                  <thead className="bg-gray-50 text-gray-500 uppercase text-xs font-semibold">
                    <tr>
                      <th className="px-6 py-4">Mã đơn</th>
                      <th className="px-6 py-4">Khách hàng</th>
                      <th className="px-6 py-4">Sản phẩm</th>
                      <th className="px-6 py-4">Tổng tiền</th>
                      <th className="px-6 py-4">Trạng thái</th>
                      <th className="px-6 py-4">Thao tác</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y text-sm">
                    <tr className="hover:bg-gray-50">
                      <td className="px-6 py-4 font-medium">#ORD001</td>
                      <td className="px-6 py-4">Nguyễn Văn A</td>
                      <td className="px-6 py-4">Cà phê đen, Bạc xỉu...</td>
                      <td className="px-6 py-4 font-bold">85,000đ</td>
                      <td className="px-6 py-4">
                        <span className="px-3 py-1 bg-yellow-100 text-yellow-700 rounded-full text-xs font-medium">Chờ duyệt</span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex gap-2">
                          <button className="p-1.5 text-green-600 hover:bg-green-50 rounded"><CheckCircle size={18} /></button>
                          <button className="p-1.5 text-red-600 hover:bg-red-50 rounded"><XCircle size={18} /></button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'tables' && (
            <TableManager />
          )}

          {activeTab === 'products' && (
            <ProductManager />
          )}
        </main>
      </div>
    </div>
  );
};

const NavItem = ({ icon, label, active, onClick }) => (
  <button
    onClick={onClick}
    className={`w-full flex items-center gap-4 px-6 py-4 transition-all ${
      active
        ? 'bg-slate-700 text-orange-400 border-r-4 border-orange-400'
        : 'text-slate-400 hover:bg-slate-700 hover:text-white'
    }`}
  >
    {icon}
    <span className="font-medium">{label}</span>
  </button>
);

const StatCard = ({ title, value, subtitle, color }) => (
  <div className="bg-white p-6 rounded-xl shadow-sm border hover:shadow-md transition-shadow">
    <p className="text-sm font-medium text-gray-500">{title}</p>
    <h4 className="text-2xl font-bold mt-2 text-gray-800">{value}</h4>
    <div className="flex items-center gap-2 mt-2">
      <div className={`w-2 h-2 rounded-full ${color}`}></div>
      <p className="text-xs text-gray-400">{subtitle}</p>
    </div>
  </div>
);

export default Dashboard;
