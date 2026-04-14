import React, { useState, useEffect } from 'react';
import { db } from '../firebase';
import { collection, query, onSnapshot, addDoc, updateDoc, deleteDoc, doc } from 'firebase/firestore';
import { Package, Plus, Edit2, Trash2, AlertTriangle } from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';

const InventoryManager = () => {
  const [items, setItems] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newItem, setNewItem] = useState({ name: '', quantity: 0, unit: 'kg', minThreshold: 1 });

  useEffect(() => {
    const q = query(collection(db, "inventory"));
    const unsubscribe = onSnapshot(q, (snapshot) => {
      setItems(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    });
    return () => unsubscribe();
  }, []);

  const handleAddItem = async (e) => {
    e.preventDefault();
    try {
      await addDoc(collection(db, "inventory"), newItem);
      setShowAddModal(false);
      setNewItem({ name: '', quantity: 0, unit: 'kg', minThreshold: 1 });
      toast.success("Đã thêm nguyên liệu");
    } catch (error) {
      toast.error("Lỗi: " + error.message);
    }
  };

  const updateQuantity = async (id, current, change) => {
    const newQty = Math.max(0, current + change);
    await updateDoc(doc(db, "inventory", id), { quantity: newQty });
  };

  return (
    <div className="p-6">
      <Toaster />
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold flex items-center gap-2">
          <Package className="text-orange-600" /> Quản lý Kho nguyên liệu
        </h1>
        <button
          onClick={() => setShowAddModal(true)}
          className="bg-orange-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-orange-700"
        >
          <Plus size={20} /> Thêm nguyên liệu
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {items.map(item => (
          <div key={item.id} className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
            <div className="flex justify-between items-start mb-4">
              <h3 className="font-bold text-lg">{item.name}</h3>
              {item.quantity <= item.minThreshold && (
                <div className="flex items-center gap-1 text-red-500 bg-red-50 px-2 py-1 rounded text-xs font-bold animate-pulse">
                  <AlertTriangle size={14} /> Sắp hết hàng
                </div>
              )}
            </div>

            <div className="flex items-center justify-between bg-gray-50 p-4 rounded-xl mb-4">
              <div>
                <p className="text-sm text-gray-500">Tồn kho</p>
                <p className="text-2xl font-bold text-gray-800">{item.quantity} <span className="text-sm font-normal text-gray-400">{item.unit}</span></p>
              </div>
              <div className="flex gap-2">
                <button onClick={() => updateQuantity(item.id, item.quantity, -1)} className="w-8 h-8 bg-white border border-gray-200 rounded flex items-center justify-center hover:bg-gray-100">-</button>
                <button onClick={() => updateQuantity(item.id, item.quantity, 1)} className="w-8 h-8 bg-orange-600 text-white rounded flex items-center justify-center hover:bg-orange-700">+</button>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-2 border-t border-gray-50">
               <button className="text-gray-400 hover:text-blue-600"><Edit2 size={16} /></button>
               <button
                 onClick={() => deleteDoc(doc(db, "inventory", item.id))}
                 className="text-gray-400 hover:text-red-600"
               >
                 <Trash2 size={16} />
               </button>
            </div>
          </div>
        ))}
      </div>

      {/* Modal Thêm nguyên liệu */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Thêm nguyên liệu mới</h2>
            <form onSubmit={handleAddItem} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Tên nguyên liệu</label>
                <input required value={newItem.name} onChange={e => setNewItem({...newItem, name: e.target.value})} className="mt-1 block w-full border border-gray-300 rounded-lg p-2" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Số lượng</label>
                  <input type="number" value={newItem.quantity} onChange={e => setNewItem({...newItem, quantity: parseFloat(e.target.value)})} className="mt-1 block w-full border border-gray-300 rounded-lg p-2" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Đơn vị</label>
                  <input value={newItem.unit} onChange={e => setNewItem({...newItem, unit: e.target.value})} className="mt-1 block w-full border border-gray-300 rounded-lg p-2" placeholder="kg, lít, hộp..." />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Ngưỡng báo động (min)</label>
                <input type="number" value={newItem.minThreshold} onChange={e => setNewItem({...newItem, minThreshold: parseFloat(e.target.value)})} className="mt-1 block w-full border border-gray-300 rounded-lg p-2" />
              </div>
              <div className="flex gap-3 pt-4">
                <button type="button" onClick={() => setShowAddModal(false)} className="flex-1 px-4 py-2 border border-gray-300 rounded-lg">Hủy</button>
                <button type="submit" className="flex-1 px-4 py-2 bg-orange-600 text-white rounded-lg">Lưu</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default InventoryManager;
