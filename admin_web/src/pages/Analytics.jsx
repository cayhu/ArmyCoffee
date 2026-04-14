import React, { useState, useEffect } from 'react';
import { db } from '../firebase';
import { collection, getDocs, query, where } from 'firebase/firestore';
import {
  TrendingUp,
  Users,
  DollarSign,
  ShoppingBag,
  ArrowUpRight,
  ArrowDownRight,
  Download
} from 'lucide-react';
import * as XLSX from 'xlsx';

const Analytics = () => {
  const [stats, setStats] = useState({
    totalRevenue: 0,
    totalOrders: 0,
    todayRevenue: 0,
    todayOrders: 0
  });

  const exportToExcel = () => {
    const data = [
      { "Chỉ số": "Tổng doanh thu", "Giá trị": formatCurrency(stats.totalRevenue) },
      { "Chỉ số": "Tổng đơn hàng", "Giá trị": stats.totalOrders },
      { "Chỉ số": "Doanh thu hôm nay", "Giá trị": formatCurrency(stats.todayRevenue) },
      { "Chỉ số": "Đơn hàng hôm nay", "Giá trị": stats.todayOrders },
    ];

    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "BaoCaoDoanhThu");
    XLSX.writeFile(wb, `Bao_Cao_Army_Coffee_${new Date().toLocaleDateString().replace(/\//g, '-')}.xlsx`);
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    const querySnapshot = await getDocs(collection(db, "orders"));
    let totalRev = 0;
    let todayRev = 0;
    let todayCount = 0;

    const today = new Date().setHours(0,0,0,0);

    querySnapshot.forEach((doc) => {
      const data = doc.data();
      totalRev += data.totalAmount || 0;

      if (data.createdAt >= today) {
        todayRev += data.totalAmount || 0;
        todayCount++;
      }
    });

    setStats({
      totalRevenue: totalRev,
      totalOrders: querySnapshot.size,
      todayRevenue: todayRev,
      todayOrders: todayCount
    });
  };

  const statCards = [
    {
      label: 'Tổng doanh thu',
      value: stats.totalRevenue,
      icon: DollarSign,
      color: 'bg-blue-500',
      isCurrency: true,
      trend: '+12.5%'
    },
    {
      label: 'Đơn hàng mới hôm nay',
      value: stats.todayOrders,
      icon: ShoppingBag,
      color: 'bg-orange-500',
      trend: '+5'
    },
    {
      label: 'Doanh thu hôm nay',
      value: stats.todayRevenue,
      icon: TrendingUp,
      color: 'bg-green-500',
      isCurrency: true,
      trend: '+18%'
    },
    {
      label: 'Tổng khách hàng',
      value: stats.totalOrders, // Tạm tính theo số đơn hàng
      icon: Users,
      color: 'bg-purple-500',
      trend: '+24'
    },
  ];

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold">Thống kê kinh doanh</h1>
        <button
          onClick={exportToExcel}
          className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all"
        >
          <Download size={20} /> Xuất báo cáo Excel
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {statCards.map((card, idx) => (
          <div key={idx} className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <div className="flex justify-between items-start mb-4">
              <div className={`p-3 rounded-xl ${card.color} bg-opacity-10`}>
                <card.icon className={`${card.color.replace('bg-', 'text-')}`} size={24} />
              </div>
              <span className="flex items-center text-green-500 text-xs font-bold bg-green-50 px-2 py-1 rounded-full">
                {card.trend} <ArrowUpRight size={14} />
              </span>
            </div>
            <p className="text-gray-500 text-sm font-medium">{card.label}</p>
            <h3 className="text-2xl font-bold mt-1">
              {card.isCurrency ? formatCurrency(card.value) : card.value}
            </h3>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 min-h-[300px]">
          <h3 className="font-bold mb-4">Biểu đồ tăng trưởng</h3>
          <div className="flex items-center justify-center h-full text-gray-400 italic">
            [Biểu đồ doanh thu 7 ngày gần nhất]
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <h3 className="font-bold mb-4">Món uống bán chạy nhất</h3>
          <div className="space-y-4">
             {/* Fake top products list */}
             {[
               { name: 'Cà phê Muối', sales: 45, price: 35000 },
               { name: 'Bạc Xỉu', sales: 38, price: 30000 },
               { name: 'Trà Đào Cam Sả', sales: 25, price: 45000 }
             ].map((item, i) => (
               <div key={i} className="flex items-center justify-between">
                 <div className="flex items-center gap-3">
                   <div className="w-8 h-8 bg-gray-100 rounded flex items-center justify-center text-xs font-bold">{i+1}</div>
                   <span className="font-medium">{item.name}</span>
                 </div>
                 <span className="text-sm text-gray-500">{item.sales} lượt mua</span>
               </div>
             ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
