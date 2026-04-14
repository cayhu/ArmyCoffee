import React, { useState, useEffect } from 'react';
import { db } from '../firebase';
import { collection, query, orderBy, onSnapshot, updateDoc, doc } from 'firebase/firestore';
import { ShoppingBag, CheckCircle, Clock, CreditCard } from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';

const OrdersManager = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const q = query(collection(db, "orders"), orderBy("createdAt", "desc"));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const newOrders = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));

      // Kiểm tra nếu có đơn hàng mới (Pending) thì phát âm thanh
      const lastOrder = newOrders[0];
      if (lastOrder && lastOrder.status === 'pending' && !loading) {
        playNotificationSound();
        toast.success(`Đơn hàng mới từ ${lastOrder.customerName}!`, {
            duration: 5000,
            icon: '☕',
        });
      }

      setOrders(newOrders);
      setLoading(false);
    });

    return () => unsubscribe();
  }, [loading]);

  const playNotificationSound = () => {
    const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2354/2354-preview.mp3');
    audio.play().catch(e => console.log("Audio play error:", e));
  };

  const updateStatus = async (orderId, newStatus) => {
    try {
      await updateDoc(doc(db, "orders", orderId), { status: newStatus });
      toast.success("Đã cập nhật trạng thái đơn hàng");
    } catch (error) {
      toast.error("Lỗi cập nhật: " + error.message);
    }
  };

  return (
    <div className="p-6">
      <Toaster position="top-right" />
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <ShoppingBag className="text-orange-600" /> Quản lý Đơn hàng
        </h1>
      </div>

      <div className="grid gap-6">
        {orders.map((order) => (
          <div key={order.id} className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row justify-between gap-4">
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                  order.status === 'pending' ? 'bg-orange-100 text-orange-600' :
                  order.status === 'completed' ? 'bg-green-100 text-green-600' : 'bg-blue-100 text-blue-600'
                }`}>
                  {order.status.toUpperCase()}
                </span>
                <span className="text-gray-400 text-sm">
                  {new Date(order.createdAt).toLocaleString()}
                </span>
              </div>
              <h3 className="font-bold text-lg mb-2">Khách hàng: {order.customerName}</h3>
              <div className="space-y-1">
                {order.items?.map((item, idx) => (
                  <p key={idx} className="text-gray-600 text-sm">
                    • {item.productName} x {item.quantity}
                  </p>
                ))}
              </div>
            </div>

            <div className="flex flex-col items-end justify-between">
              <div className="text-right">
                <p className="text-gray-500 text-sm">Tổng thanh toán</p>
                <p className="text-xl font-bold text-orange-600">
                  {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.totalAmount)}
                </p>
                <p className="text-xs text-gray-400 flex items-center justify-end gap-1 mt-1">
                  <CreditCard size={12} /> {order.paymentMethod}
                </p>
              </div>

              <div className="flex gap-2 mt-4">
                {order.status === 'pending' && (
                  <button
                    onClick={() => updateStatus(order.id, 'completed')}
                    className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm"
                  >
                    <CheckCircle size={16} /> Hoàn tất
                  </button>
                )}
                <button className="px-4 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 text-sm">
                  Chi tiết
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrdersManager;
