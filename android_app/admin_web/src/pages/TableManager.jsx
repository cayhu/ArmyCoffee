import React, { useState, useEffect } from 'react';
import { db } from '../firebaseConfig';
import {
  collection,
  getDocs,
  updateDoc,
  doc,
  onSnapshot,
  query,
  orderBy
} from 'firebase/firestore';
import { Check, X, Clock, User, Coffee, Loader2 } from 'lucide-react';

const TableManager = () => {
  const [tables, setTables] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Lắng nghe dữ liệu bàn thời gian thực
    const unsubTables = onSnapshot(collection(db, "tables"), (snapshot) => {
      const tableData = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
      setTables(tableData.sort((a, b) => a.number - b.number));
      setLoading(false);
    });

    // Lắng nghe yêu cầu đặt bàn mới nhất
    const q = query(collection(db, "bookings"), orderBy("createdAt", "desc"));
    const unsubBookings = onSnapshot(q, (snapshot) => {
      const bookingData = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
      setBookings(bookingData);
    });

    return () => {
      unsubTables();
      unsubBookings();
    };
  }, []);

  const updateTableStatus = async (tableId, status) => {
    try {
      await updateDoc(doc(db, "tables", tableId), { status });
    } catch (error) {
      alert("Lỗi cập nhật: " + error.message);
    }
  };

  const handleApproveBooking = async (booking) => {
    try {
      // 1. Cập nhật trạng thái booking
      await updateDoc(doc(db, "bookings", booking.id), { status: 'approved' });
      // 2. Cập nhật trạng thái bàn sang 'booked'
      const tableRef = doc(db, "tables", booking.tableId);
      await updateDoc(tableRef, {
        status: 'booked',
        currentBooking: {
          customerName: booking.customerName,
          time: booking.time
        }
      });
    } catch (error) {
      alert("Lỗi duyệt bàn: " + error.message);
    }
  };

  if (loading) return <div className="flex items-center justify-center h-full"><Loader2 className="animate-spin text-orange-500" size={40} /></div>;

  return (
    <div className="p-6 space-y-8">
      {/* Sơ đồ bàn */}
      <section>
        <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
          <Coffee className="text-orange-600" /> Sơ đồ bàn hiện tại
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-6">
          {tables.map(table => (
            <div
              key={table.id}
              className={`relative p-6 rounded-2xl border-2 transition-all shadow-sm flex flex-col items-center justify-center gap-2
                ${table.status === 'available' ? 'bg-green-50 border-green-200' :
                  table.status === 'booked' ? 'bg-orange-50 border-orange-200' : 'bg-red-50 border-red-200'}`}
            >
              <div className="text-3xl font-black mb-1">#{table.number}</div>
              <div className={`text-xs font-bold uppercase px-3 py-1 rounded-full
                ${table.status === 'available' ? 'bg-green-200 text-green-800' :
                  table.status === 'booked' ? 'bg-orange-200 text-orange-800' : 'bg-red-200 text-red-800'}`}>
                {table.status === 'available' ? 'Trống' : table.status === 'booked' ? 'Đã đặt' : 'Đang dùng'}
              </div>

              {table.status === 'booked' && (
                <div className="mt-2 text-center text-[10px] text-gray-600">
                   <p className="font-bold">{table.currentBooking?.customerName}</p>
                   <p>{table.currentBooking?.time}</p>
                </div>
              )}

              {/* Thao tác nhanh */}
              <div className="mt-4 flex gap-2">
                {table.status !== 'available' && (
                  <button
                    onClick={() => updateTableStatus(table.id, 'available')}
                    className="p-1.5 bg-white rounded-lg shadow-sm hover:bg-gray-100 text-gray-600"
                    title="Giải phóng bàn"
                  >
                    <X size={14} />
                  </button>
                )}
                {table.status === 'available' && (
                  <button
                    onClick={() => updateTableStatus(table.id, 'occupied')}
                    className="p-1.5 bg-white rounded-lg shadow-sm hover:bg-gray-100 text-red-600"
                    title="Khách vào"
                  >
                    <User size={14} />
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Danh sách yêu cầu đặt bàn từ App */}
      <section className="bg-white rounded-2xl border shadow-sm overflow-hidden">
        <div className="p-6 border-b flex justify-between items-center bg-slate-50">
          <h3 className="text-lg font-bold flex items-center gap-2">
            <Clock className="text-blue-600" /> Yêu cầu đặt bàn chờ duyệt
          </h3>
          <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-xs font-bold">
            {bookings.filter(b => b.status === 'pending').length} yêu cầu mới
          </span>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-gray-50 text-gray-400 text-xs font-bold uppercase">
              <tr>
                <th className="px-6 py-4">Khách hàng</th>
                <th className="px-6 py-4">Bàn số</th>
                <th className="px-6 py-4">Thời gian đặt</th>
                <th className="px-6 py-4">Ghi chú</th>
                <th className="px-6 py-4 text-center">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y text-sm">
              {bookings.filter(b => b.status === 'pending').map(booking => (
                <tr key={booking.id} className="hover:bg-blue-50 transition-colors">
                  <td className="px-6 py-4 font-bold text-gray-800">{booking.customerName}</td>
                  <td className="px-6 py-4 font-black">#{booking.tableNumber}</td>
                  <td className="px-6 py-4">{booking.time}</td>
                  <td className="px-6 py-4 italic text-gray-500">{booking.note || 'Không có'}</td>
                  <td className="px-6 py-4">
                    <div className="flex justify-center gap-2">
                      <button
                        onClick={() => handleApproveBooking(booking)}
                        className="flex items-center gap-1 bg-green-600 text-white px-3 py-1.5 rounded-lg hover:bg-green-700 font-bold"
                      >
                        <Check size={16} /> Duyệt
                      </button>
                      <button
                        onClick={() => updateDoc(doc(db, "bookings", booking.id), { status: 'rejected' })}
                        className="flex items-center gap-1 bg-red-100 text-red-600 px-3 py-1.5 rounded-lg hover:bg-red-200 font-bold"
                      >
                        <X size={16} /> Từ chối
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {bookings.filter(b => b.status === 'pending').length === 0 && (
                <tr>
                  <td colSpan="5" className="px-6 py-10 text-center text-gray-400 italic">Không có yêu cầu đặt bàn nào đang chờ.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
};

export default TableManager;
